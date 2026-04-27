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
The `solve` method manages the SA iterations and the restart logic. It maintains `pi` (current solution) and `piStar` (best found solution). It uses a `batchSize` to explore multiple neighborhood moves in parallel (or sequentially in this implementation) and selects the best one.

#### Repair Logic (`repairSequential`)
This method implements the Sequential Connection surrogate.
```java
private List<List<Customer>> repairSequential(List<List<Customer>> partial, List<Customer> removed, VRPProblem problem) {
    // 1. Convert all partial routes and removed customers into "segments"
    // 2. While segments exist:
    //    a. For each segment, find the best and second-best partner to connect to
    //    b. Calculate Regret = SecondBest - Best
    //    c. Pick the segment with the highest Regret
    //    d. Connect it to its best partner (or the depot if best)
}
```
*Improvement added*: The implementation supports **segment flipping**, allowing the algorithm to connect a segment in reverse if it results in a shorter distance.

#### Customer Segment Class
A helper class `CustomerSegment` tracks the nodes, start/end customers, and total demand of a partial route segment.

## 5. Performance Improvements
The original implementation was refined to:
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
