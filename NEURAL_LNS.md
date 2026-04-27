# Neural Large Neighborhood Search (NLNS) for CVRP

This document provides a detailed explanation of the Neural Large Neighborhood Search (NLNS) algorithm implemented by Maxim Milet for solving the Capacitated Vehicle Routing Problem (CVRP).

## 1. Problem Description: CVRP
The **Capacitated Vehicle Routing Problem (CVRP)** is a combinatorial optimization problem where the goal is to find an optimal set of routes for a fleet of vehicles to deliver goods to a specific set of customers.

**Constraints:**
- Each route starts and ends at a central depot.
- Each customer must be visited exactly once.
- The total demand of customers in any route must not exceed the vehicle's capacity.
- The objective is to minimize the total distance traveled.

## 2. Algorithm Overview: NLNS
**Neural Large Neighborhood Search (NLNS)** is a metaheuristic that combines the traditional Large Neighborhood Search (LNS) framework with concepts from Deep Learning, specifically the **Attention Mechanism** and **Pointer Networks**.

In this implementation, we use a **surrogate heuristic** that mimics the behavior of a trained Neural NLNS model. This allows for high-quality solutions without the overhead of running a full neural network in the inner loop.

### High-Level Steps:
1. **Initial Solution**: Generate a feasible starting solution using a Greedy Nearest Neighbor heuristic.
2. **Destroy Phase**: Remove a subset of customers from the current solution to create a "partial" solution.
3. **Repair Phase**: Re-insert the removed customers using a **Sequential Connection** logic, which simulates an Attention-based Pointer Network.
4. **Acceptance Phase**: Use **Simulated Annealing** to decide whether to accept the new solution.
5. **Iteration**: Repeat the process until the termination criteria (time or stagnation) are met.

## 3. Algorithm Steps in Detail

### 3.1 Destroy Operators
The algorithm uses two types of destroy operators to introduce diversity:
- **Shaw Relatedness Removal**: Removes customers that are "related" based on distance and demand similarity. This targets clusters of customers that might be better served in different routes.
- **Tour-based Removal**: Removes entire routes or large segments of routes that are geographically close to a random reference point.

### 3.2 Repair Operator: Sequential Connection
The core of NLNS is the **Sequential Connection** repair. Instead of simple greedy insertion, it treats the problem as connecting "segments".
- A segment can be a single removed customer or a contiguous part of an existing route.
- The repair process uses **Regret-2 logic** as a surrogate for Attention. It calculates the "regret" (the difference between the best and second-best connection) for each segment and connects the one with the highest regret first.
- This approach "learns" the most critical connections first, similar to how an attention mechanism focuses on the most important nodes.

### 3.3 Acceptance: Simulated Annealing
To escape local optima, NLNS uses Simulated Annealing. A worse solution may be accepted with a probability $P = e^{-\Delta / T}$, where $\Delta$ is the cost increase and $T$ is the current temperature. The temperature cools over time, making the search more restrictive as it converges.

## 4. Code Walkthrough (Maxim's Implementation)

The implementation is located in `src/NeuralLNSAlgorithm.java`.

### Key Components:

#### The Main Loop (`solve`)
The `solve` method manages the SA iterations and the restart logic. It maintains `pi` (current solution) and `piStar` (best found solution). It uses a `batchSize` to explore multiple neighborhood moves in parallel and selects the best one.

- **Restarting SA**: The algorithm resets the temperature `t = tr` if the stagnation limit `patience` is reached, allowing the search to jump out of deep local optima.
- **Dynamic Runtime**: The algorithm scales its search time based on the problem size, ensuring larger instances get more exploration.

#### Destroy Operators (`destroy`)
1. **Shaw Relatedness**: 
   - Picks a random "seed" customer.
   - Calculates a relatedness score for all other customers based on:
     - Distance to the seed.
     - Difference in demand.
   - Removes the most related customers. This is effective because customers that are close together and have similar demands are often interchangeable or should be in the same route.
2. **Tour-based Removal**:
   - Picks a random reference point.
   - Sorts existing routes by their average proximity to this point.
   - Removes the closest routes entirely. This allows the algorithm to completely restructure a specific geographic area.

#### Simulated Annealing Parameters
- `ts` (Start Temperature): $0.05 \times \text{Initial Cost}$.
- `tm` (Min Temperature): $0.001 \times \text{Start Temperature}$.
- `delta` (Cooling Rate): $0.98$ per iteration.
- `Z` (Restart best ratio): $20\%$. In each batch, 20% of the solutions are reset to the current best `pi`.

## 5. Original Implementation (Maxim Milet)

Below is the original implementation by Maxim Milet, which utilized a basic Sequential Connection repair and a tighter cooling schedule.

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Implementation of Neural Large Neighborhood Search (NLNS) for CVRP.
 * Refined to follow Sequential Connection logic and optimized for speed.
 */
public class NeuralLNSAlgorithm implements VRPAlgorithm {

    private final Random random = new Random();

    @Override
    public List<List<Customer>> solve(VRPProblem problem) {
        if (problem.getCustomers().isEmpty()) return new ArrayList<>();

        List<List<Customer>> pi = initialSolution(problem);
        List<List<Customer>> piStar = deepCopy(pi);
        
        double piStarCost = problem.calculateTotalCost(piStar);
        double ts = piStarCost * 0.05; 
        double tr = ts;
        double tm = ts * 0.001; 
        double delta = 0.98; // Slightly faster cooling
        int Z = 20; 
        int batchSize = 8; 

        double t = ts;
        long startTime = System.currentTimeMillis();
        int maxRuntime = (int) (100 * problem.getCustomers().size()); // Dynamic timeout
        if (maxRuntime > 45000) maxRuntime = 45000;
        if (maxRuntime < 5000) maxRuntime = 5000;

        int patience = 50;
        int unstuckCounter = 0;

        while (System.currentTimeMillis() - startTime < maxRuntime && !Thread.currentThread().isInterrupted()) {
            List<List<List<Customer>>> B = new ArrayList<>();
            for (int i = 0; i < batchSize; i++) {
                B.add(deepCopy(pi));
            }

            while (t > tm && !Thread.currentThread().isInterrupted()) {
                List<List<List<Customer>>> nextB = new ArrayList<>();
                List<List<Customer>> pib = null;
                double pibCost = Double.MAX_VALUE;

                for (int i = 0; i < batchSize; i++) {
                    List<List<Customer>> solution = B.get(i);
                    int destroyType = random.nextInt(2); 
                    
                    List<Customer> removed = new ArrayList<>();
                    List<List<Customer>> destroyed = destroy(solution, removed, problem, destroyType);
                    List<List<Customer>> repaired = repairSequential(destroyed, removed, problem);
                    
                    nextB.add(repaired);
                    double cost = problem.calculateTotalCost(repaired);
                    if (cost < pibCost) {
                        pibCost = cost;
                        pib = repaired;
                    }
                }

                double piCost = problem.calculateTotalCost(pi);
                if (pib != null && accept(pibCost, piCost, t)) {
                    pi = pib;
                    if (pibCost < piStarCost) {
                        piStar = deepCopy(pib);
                        piStarCost = pibCost;
                        unstuckCounter = 0;
                    }
                }
                
                unstuckCounter++;
                if (unstuckCounter > patience) break; 

                B = nextB;
                int zCount = Math.max(1, (int) (batchSize * (Z / 100.0)));
                for (int i = 0; i < zCount; i++) {
                    B.set(i, deepCopy(pi));
                }
                t *= delta;
            }
            if (unstuckCounter > patience) break;
            t = tr; 
        }

        return piStar;
    }

    private boolean accept(double candidateCost, double currentCost, double temperature) {
        if (candidateCost < currentCost) return true;
        return random.nextDouble() < Math.exp(-(candidateCost - currentCost) / temperature);
    }

    private List<List<Customer>> initialSolution(VRPProblem problem) {
        List<List<Customer>> routes = new ArrayList<>();
        List<Customer> unvisited = new ArrayList<>(problem.getCustomers());
        while (!unvisited.isEmpty()) {
            List<Customer> route = new ArrayList<>();
            Customer cur = problem.getDepot();
            int cap = problem.getCapacity();
            while (true) {
                Customer nearest = null;
                double minDist = Double.MAX_VALUE;
                for (Customer c : unvisited) {
                    if (c.getDemand() <= cap) {
                        double d = cur.distanceTo(c);
                        if (d < minDist) { minDist = d; nearest = c; }
                    }
                }
                if (nearest == null) break;
                route.add(nearest);
                unvisited.remove(nearest);
                cap -= nearest.getDemand();
                cur = nearest;
            }
            routes.add(route);
        }
        return routes;
    }

    private List<List<Customer>> destroy(List<List<Customer>> solution, List<Customer> removed, VRPProblem problem, int type) {
        int n = problem.getCustomers().size();
        int dLimit = (int) (random.nextDouble() * 0.15 * n) + 2; 
        List<List<Customer>> partial = deepCopy(solution);

        if (type == 0) { // Shaw Relatedness (Simplified)
            List<Customer> flat = new ArrayList<>();
            for (List<Customer> r : partial) flat.addAll(r);
            Customer seed = flat.get(random.nextInt(flat.size()));
            flat.sort(Comparator.comparingDouble(c -> 
                Math.hypot(c.getX() - seed.getX(), c.getY() - seed.getY()) + 
                Math.abs(c.getDemand() - seed.getDemand()) * 0.5));
            for (int i = 0; i < Math.min(dLimit, flat.size()); i++) {
                final Customer toRemove = flat.get(i);
                removed.add(toRemove);
                for (List<Customer> r : partial) r.removeIf(c -> c.getId() == toRemove.getId());
            }
        } else { // Tour-based
            Customer ref = problem.getCustomers().get(random.nextInt(n));
            partial.sort(Comparator.comparingDouble(r -> {
                if (r.isEmpty()) return Double.MAX_VALUE;
                double sx=0, sy=0; for(Customer c:r){sx+=c.getX();sy+=c.getY();}
                return Math.hypot(sx/r.size() - ref.getX(), sy/r.size() - ref.getY());
            }));
            int remCount = 0;
            while (!partial.isEmpty() && remCount < dLimit) {
                List<Customer> r = partial.remove(0);
                removed.addAll(r);
                remCount += r.size();
            }
        }
        partial.removeIf(List::isEmpty);
        return partial;
    }

    private List<List<Customer>> repairSequential(List<List<Customer>> partial, List<Customer> removed, VRPProblem problem) {
        List<CustomerSegment> segments = new ArrayList<>();
        for (List<Customer> r : partial) segments.add(new CustomerSegment(r, r.get(0), r.get(r.size() - 1), 3));
        for (Customer c : removed) segments.add(new CustomerSegment(Collections.singletonList(c), c, c, 1));

        List<List<Customer>> finalRoutes = new ArrayList<>();

        while (!segments.isEmpty()) {
            CustomerSegment refSeg = null;
            double maxRegret = -1.0;
            int bestPartnerIdx = -1;
            int refSegIdx = -1;

            for (int i = 0; i < segments.size(); i++) {
                CustomerSegment s = segments.get(i);
                double best = Double.MAX_VALUE, secondBest = Double.MAX_VALUE;
                int localBestIdx = -1;

                for (int j = 0; j < segments.size(); j++) {
                    if (i == j) continue;
                    CustomerSegment p = segments.get(j);
                    if (s.demand + p.demand > problem.getCapacity()) continue;

                    double d = s.end.distanceTo(p.start);
                    if (d < best) { secondBest = best; best = d; localBestIdx = j; }
                    else if (d < secondBest) { secondBest = d; }
                }

                double depotDist = s.end.distanceTo(problem.getDepot());
                if (depotDist < best) { secondBest = best; best = depotDist; localBestIdx = -2; }
                else if (depotDist < secondBest) { secondBest = depotDist; }

                double regret = (secondBest == Double.MAX_VALUE) ? 0 : (secondBest - best);
                if (regret > maxRegret) {
                    maxRegret = regret;
                    refSeg = s;
                    refSegIdx = i;
                    bestPartnerIdx = localBestIdx;
                }
            }

            if (refSeg == null) break; 

            segments.remove(refSegIdx);
            if (bestPartnerIdx == -2) {
                finalRoutes.add(new ArrayList<>(refSeg.nodes));
            } else {
                CustomerSegment partner = segments.remove(bestPartnerIdx < refSegIdx ? bestPartnerIdx : bestPartnerIdx - 1);
                refSeg.nodes.addAll(partner.nodes);
                refSeg.updateDemand();
                refSeg.end = refSeg.nodes.get(refSeg.nodes.size() - 1);
                segments.add(refSeg);
            }
        }
        return finalRoutes;
    }

    private static class CustomerSegment {
        List<Customer> nodes;
        Customer start, end;
        int demand, status;
        CustomerSegment(List<Customer> nodes, Customer s, Customer e, int status) {
            this.nodes = new ArrayList<>(nodes); this.start = s; this.end = e; this.status = status;
            updateDemand();
        }
        void updateDemand() { demand = nodes.stream().mapToInt(Customer::getDemand).sum(); }
    }

    private List<List<Customer>> deepCopy(List<List<Customer>> original) {
        List<List<Customer>> copy = new ArrayList<>();
        for (List<Customer> r : original) copy.add(new ArrayList<>(r));
        return copy;
    }

    @Override public String getName() { return "Neural LNS (Sequential)"; }
    @Override public boolean isExact() { return false; }
}
```

## 6. Performance Improvements
The implementation was refined to:
1. **Increase Search Intensity**: Increased the `patience` (stagnation limit) and `maxRuntime` to allow for deeper exploration.
2. **Optimize Repair**: Added segment flipping to the Sequential Connection logic, significantly reducing the gap on standard benchmarks.
3. **Batch Exploration**: Expanded the batch size to 16, allowing the algorithm to "see" more potential moves before committing to a step.

## 6. How to Run
To run the benchmarks and see the NLNS performance:
```powershell
# Compile
javac -d out src/*.java

# Run
java -cp out Main
```
Results will be saved in the `results/` directory.
