# Neural Large Neighborhood Search (NLNS) for CVRP

This document provides a comprehensive technical breakdown of the Neural Large Neighborhood Search (NLNS) algorithm implemented for solving the Capacitated Vehicle Routing Problem (CVRP).

## 1. Introduction to CVRP
The **Capacitated Vehicle Routing Problem (CVRP)** involves finding the most efficient set of routes for a fleet of vehicles to deliver goods to a set of customers. Each vehicle has a maximum **capacity**, and each customer has a specific **demand**. All routes must start and end at a central **depot**.

## 2. High-Level Algorithm Overview
The **Neural Large Neighborhood Search (NLNS)** is a metaheuristic that iteratively improves a solution by "destroying" a part of it (removing customers) and then "repairing" it (re-inserting customers). 

### Why "Neural"?
The "Neural" aspect traditionally refers to using **Deep Reinforcement Learning** (specifically Pointer Networks with Attention) to learn the repair operator. Training a neural network allows it to "see" patterns in customer distributions that simple heuristics might miss.

In this implementation, we use a **surrogate heuristic**—**Sequential Connection with Regret**. This is designed to mimic the behavior of a trained attention model:
- **Attention Simulation**: Just as an attention mechanism focuses on the most "relevant" or "difficult" nodes in a graph, our surrogate uses **Regret** to identify which customers are the most critical to place *now* to avoid massive cost increases later.

---

## 3. Key Concepts

### Simulated Annealing (SA)
Simulated Annealing is a probabilistic technique for approximating the global optimum. It is inspired by the process of annealing in metallurgy.
- **The Acceptance Formula**: The probability of accepting a worse solution is calculated as $P = e^{-\Delta / T}$, where $\Delta$ is the change in cost and $T$ is the temperature.
- **Temperature ($T$)**: High temperature allows the algorithm to accept significantly worse solutions, enabling it to "jump" out of local optima (valleys in the solution space).
- **Cooling**: As $T$ decreases, the probability of accepting bad moves shrinks, effectively "freezing" the algorithm into the best local configuration it has found.

### Large Neighborhood Search (LNS)
LNS is a powerful framework because of its **flexibility**. By destroying a large portion of the solution (10-25%), the algorithm can move to a completely different "neighborhood" of solutions that would be unreachable by simple point-swaps or local moves.

---

## 4. Detailed Code Walkthrough

### 4.1 The Orchestrator: `solve()`
The `solve` method manages the global search strategy, including batching and restarts.

```java
public List<List<Customer>> solve(VRPProblem problem) {
    // ... Initialization ...
    int batchSize = 16; 
    int patience = 500; // Stagnation limit

    while (totalTime < maxRuntime) {
        // Create a batch of independent variants
        for (int i = 0; i < batchSize; i++) {
            B.add(deepCopy(pi));
        }
        
        while (t > tm) {
            // ... Destroy & Repair batch ...
            // Select the best variant from the batch (pib)
            if (accept(pibCost, piCost, t)) {
                pi = pib;
                if (pibCost < piStarCost) { 
                    piStar = deepCopy(pi); 
                    unstuckCounter = 0; // Reset patience
                }
            }
            t *= delta; // Cool down
        }
        t = tr; // Restart SA from best found so far
    }
}
```

**Deeper Insight:**
- **Batching Strategy**: By processing a batch of 16 variants at each step, the algorithm explores 16 different "neighboring" solutions simultaneously. This increases the chances of finding a significantly better move in a single iteration.
- **Restart & Stagnation**: If the algorithm doesn't find a new global best for a long time (`patience`), it "re-heats" the solution by resetting the temperature. This provides the energy needed to escape a persistent local optimum.

### 4.2 The Starting Point: `initialSolution()`
We use a **Nearest Neighbor** heuristic to ensure we start with a valid (feasible) solution.

**Deeper Insight:**
LNS is remarkably robust to the quality of the initial solution. Even if the Nearest Neighbor starting point is 50% above optimal, the destroy-repair cycles will rapidly prune and re-grow the routes into a more efficient structure.

### 4.3 Breaking the Solution: `destroy()`
Destroying the solution is about finding the right balance between **exploration** and **exploitation**.

```java
// Shaw Relatedness Logic
flat.sort(Comparator.comparingDouble(c -> 
    dist(c, seed) + Math.abs(c.getDemand() - seed.getDemand()) * 0.5));
```

**Deeper Insight:**
- **Shaw Removal** is an "Exploitative" operator. By removing nodes that are similar (close together and similar demand), it creates a hole in the solution where the nodes are most likely to be interchangeable. This allows the repair operator to fine-tune local clusters.
- **Tour-based Removal** is an "Explorative" operator. By deleting entire routes, it forces the algorithm to re-assign dozens of customers across the map, potentially leading to a completely different routing topology.

### 4.4 Rebuilding: `repairSequential()` (The Neural Surrogate)
This is where the "Regret" logic mimics a Pointer Network's Attention.

```java
// Regret Calculation
for (CustomerSegment s : segments) {
    double best = findBestConnection(s);
    double secondBest = findSecondBestConnection(s);
    double regret = secondBest - best;
    
    if (regret > maxRegret) { 
        targetSegment = s; 
    }
}
```

**Deeper Insight:**
- **Why Regret?**: A greedy inserter only looks at the `best` option. A **Regret-2** inserter looks at the **cost of NOT picking the best option**. If a segment has a high regret, it means its second-best placement is much worse than its best. Inserting it *now* is critical because waiting might "lock" its best spot, forcing it into a very expensive alternative later.
- **Attention Mimicry**: In neural models, the attention weight is high for nodes that are "hard to place." Regret provides a deterministic mathematical surrogate for this "difficulty" or "priority."
- **Segment Flipping**: Since CVRP routes are typically undirected, flipping a segment (reversing its nodes) can often reveal a shorter connection path that a directed sequential connection would miss.

---

## 5. Summary: How the Algorithm Works

Here is a technical walkthrough of the NLNS execution:

1.  **Phase 1: Construction**: A greedy Nearest Neighbor pass creates the first feasible set of routes.
2.  **Phase 2: Perturbation (Destroy)**: The algorithm picks a region of the map (Tour-based) or a set of similar customers (Shaw-based) and "forgets" their current assignments.
3.  **Phase 3: Prioritization (Regret)**: The algorithm looks at all the unassigned customers and segments. It identifies the "most desperate" segment—the one with the largest gap between its best and second-best placement.
4.  **Phase 4: Connection (Repair)**: It connects that segment to its best neighbor, possibly flipping it for a better fit. This repeats until all customers are re-assigned.
5.  **Phase 5: Selection**: The algorithm generates 16 such variations and picks the best one.
6.  **Phase 6: Acceptance (Simulated Annealing)**:
    *   **Better?** Always accept.
    *   **Worse?** Accept with probability $e^{-\Delta / T}$ to maintain diversity.
7.  **Phase 7: Convergence**: The temperature drops, making the search more focused. If no improvement is found, the system "re-heats" and restarts from the best known solution to try a different path.

Through this "Destructive-Constructive" cycle, the algorithm effectively "evolves" the solution, pruning away inefficient paths and re-wiring the network into a near-optimal configuration.
