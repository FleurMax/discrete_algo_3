# Neural Large Neighborhood Search (NLNS) for CVRP

This document provides a comprehensive technical breakdown of the Neural Large Neighborhood Search (NLNS) algorithm implemented for solving the Capacitated Vehicle Routing Problem (CVRP).

## 1. Introduction to CVRP
The **Capacitated Vehicle Routing Problem (CVRP)** involves finding the most efficient set of routes for a fleet of vehicles to deliver goods to a set of customers. Each vehicle has a maximum **capacity**, and each customer has a specific **demand**. All routes must start and end at a central **depot**.

## 2. High-Level Algorithm Overview
The **Neural Large Neighborhood Search (NLNS)** is a metaheuristic that iteratively improves a solution by "destroying" a part of it (removing customers) and then "repairing" it (re-inserting customers). 

The "Neural" aspect traditionally involves using deep learning models (like Pointer Networks) to decide how to repair the solution. In this implementation, we use a **surrogate heuristic**—Sequential Connection with Regret—which mimics the decision-making of a trained neural model.

---

## 3. Key Concepts

### Simulated Annealing (SA)
Simulated Annealing is a probabilistic technique for approximating the global optimum of a given function. It is inspired by the process of annealing in metallurgy (cooling metal slowly to reach a low-energy crystalline state).
- **Temperature ($T$)**: A parameter that controls the probability of accepting a "worse" solution. High temperature allows "jumps" to explore new areas; low temperature makes the search more "greedy."
- **Cooling**: Gradually reducing the temperature to focus on local optimization as the search progresses.
- **Escape Local Optima**: By occasionally accepting worse solutions, SA prevents the algorithm from getting "stuck" in a sub-optimal solution.

### Large Neighborhood Search (LNS)
LNS is a framework where a large part of the solution is changed in each step. This allows the algorithm to explore a wider range of possible solutions compared to "Small Neighborhood" searches (like swapping two adjacent nodes).

---

## 4. Detailed Code Walkthrough

### 4.1 The Orchestrator: `solve()`
The `solve` method is the main engine of the algorithm. It initializes the solution and manages the iterations.

```java
public List<List<Customer>> solve(VRPProblem problem) {
    // 1. Create an initial feasible solution
    List<List<Customer>> pi = initialSolution(problem);
    List<List<Customer>> piStar = deepCopy(pi); // Best found so far
    
    // 2. Set up Simulated Annealing parameters
    double piStarCost = problem.calculateTotalCost(piStar);
    double ts = piStarCost * 0.05; // Starting temperature
    double tr = ts;               // Reset temperature for restarts
    double tm = ts * 0.001;        // Minimum temperature (stopping point)
    double delta = 0.98;           // Cooling rate
```

**Explanation:**
- The algorithm starts by generating a basic solution using `initialSolution`.
- It sets the starting temperature based on a percentage of the initial cost. This ensures the temperature is proportional to the scale of the problem.

### 4.2 The Starting Point: `initialSolution()`
We use a simple **Nearest Neighbor** heuristic to get started.

```java
private List<List<Customer>> initialSolution(VRPProblem problem) {
    List<Customer> unvisited = new ArrayList<>(problem.getCustomers());
    while (!unvisited.isEmpty()) {
        List<Customer> route = new ArrayList<>();
        Customer cur = problem.getDepot();
        int cap = problem.getCapacity();
        while (true) {
            Customer nearest = findNearestWithinCapacity(cur, unvisited, cap);
            if (nearest == null) break; // Route is full or no more customers
            route.add(nearest);
            unvisited.remove(nearest);
            cap -= nearest.getDemand();
            cur = nearest;
        }
        routes.add(route);
    }
}
```

**Explanation:**
- This greedy approach builds routes one by one. It always picks the closest customer that doesn't exceed the vehicle's remaining capacity. This creates a "feasible" (legal) but likely non-optimal starting point.

### 4.3 Breaking the Solution: `destroy()`
The goal of the destroy phase is to remove some customers so they can be re-inserted more optimally.

```java
private List<List<Customer>> destroy(List<List<Customer>> solution, List<Customer> removed, VRPProblem problem, int type) {
    if (type == 0) { // Shaw Relatedness Removal
        Customer seed = pickRandomCustomer(solution);
        // Sort others by proximity and demand similarity to seed
        flat.sort(Comparator.comparingDouble(c -> 
            dist(c, seed) + Math.abs(c.getDemand() - seed.getDemand()) * 0.5));
        // Remove the top N most "related" customers
        removeCustomers(partial, removed, flat.subList(0, dLimit));
    } else { // Tour-based Removal
        // Pick a random spot and remove all routes close to it
        partial.sort(Comparator.comparingDouble(r -> dist(r.center(), ref)));
        removed.addAll(partial.remove(0)); // Delete whole routes
    }
}
```

**Explanation:**
- **Shaw Removal**: Target clusters of customers. By removing nodes that are similar, we give the repair operator a chance to re-order them more efficiently within the same area.
- **Tour-based Removal**: Completely deletes a set of routes in a specific area. This forces the algorithm to "re-think" how that entire region should be served.

### 4.4 Rebuilding: `repairSequential()`
This is the "brain" of the NLNS implementation. It uses a **Regret-based Sequential Connection** strategy.

```java
private List<List<Customer>> repairSequential(List<List<Customer>> partial, List<Customer> removed, VRPProblem problem) {
    // Treat everything as "segments" (partial routes or single nodes)
    List<CustomerSegment> segments = createSegments(partial, removed);

    while (!segments.isEmpty()) {
        // Calculate "Regret" for each segment
        // Regret = (Distance to 2nd best partner) - (Distance to best partner)
        for (CustomerSegment s : segments) {
            findBestAndSecondBest(s, segments, ...);
            double regret = secondBestDist - bestDist;
            if (regret > maxRegret) { 
                target = s; // Pick segment with highest penalty for not being connected NOW
            }
        }
        // Connect the target segment to its best partner
        connect(target, bestPartner);
    }
}
```

**Explanation:**
- **Segments**: The algorithm views the partial solution as a collection of "segments" (either single customers or chunks of existing routes).
- **Regret Strategy**: Instead of picking the "cheapest" connection first (Greedy), it picks the connection that has the **highest regret**. This means "if I don't connect this segment to its best partner now, the next best option is much worse." This simulates the decision-making of a Neural Attention mechanism.
- **Segment Flipping**: (Improved feature) The algorithm checks if connecting a segment in reverse is cheaper, allowing for more flexible route construction.

### 4.5 Accepting the Move: `accept()`
This implements the Simulated Annealing logic.

```java
private boolean accept(double candidateCost, double currentCost, double temperature) {
    if (candidateCost < currentCost) return true; // Always accept better solutions
    
    // Accept worse solutions with a probability based on temperature
    double delta = candidateCost - currentCost;
    return random.nextDouble() < Math.exp(-delta / temperature);
}
```

**Explanation:**
- If the new solution is better, we always keep it.
- If it's worse, we might still keep it. The probability is high if the cost increase is small and the temperature is high. As the temperature drops (cools), we become less and less likely to accept "bad" moves.

---

## 5. Summary: How the Algorithm Works

Here is a step-by-step walkthrough of one iteration of the NLNS algorithm:

1.  **Initialize**: Start with a simple "Nearest Neighbor" solution.
2.  **Batching**: Generate a "batch" of potential new solutions (e.g., 16 variants).
3.  **Destroy**: For each variant in the batch, randomly choose a "Destroy" method (Shaw or Tour) and remove 10-15% of the customers.
4.  **Repair**: Use the **Sequential Regret** logic to re-insert the removed customers. This logic ensures that the most "constrained" connections are handled first.
5.  **Select Best**: Out of the 16 variants, find the one with the lowest cost.
6.  **Evaluate (SA)**: Compare this best variant to our current solution:
    *   If it's better, update the current solution.
    *   If it's worse, roll a die (Simulated Annealing) to decide if we should jump to it anyway to avoid local optima.
7.  **Cool Down**: Slightly reduce the temperature (`T = T * 0.98`).
8.  **Repeat**: Keep doing this until the time limit is reached or the solution stops improving.
9.  **Restart**: If we get "stuck" for too long (stagnation), reset the temperature to the original high value and continue from the best solution found so far.

By repeating these steps thousands of times, the algorithm slowly "cooks" the random initial routes into a highly optimized set of delivery paths.
