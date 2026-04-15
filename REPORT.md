# Vehicle Routing Problem: Benchmark Report

## 1. Introduction
This project benchmarks algorithms for solving variants of the Vehicle Routing Problem (VRP). The VRP is a well-known NP-hard combinatorial optimization problem aiming to design optimal delivery routes from a central depot to a set of widely dispersed customers, subject to various constraints such as vehicle capacities (CVRP) or time windows (VRPTW). This project explores approximate algorithms and metaheuristics due to the difficulty of solving large instances exactly. 

The variants include:
- **CVRP (Capacitated VRP)**: Vehicles have a uniform limited capacity.
- **VRPTW (VRP with Time Windows)**: Customers must be served within specific time frames.
- **PVRP (Periodic VRP)**: Planning is done over a longer time horizon (e.g. multiple days).
- **DVRP (Dynamic VRP)**: Some information (locations, demands) is revealed or changes during the day.

We benchmark several algorithms on standardized DIMACS / CVRPLIB instances.

## 2. Groep
| Naam | GitHub | Rol / Algoritme |
|------|--------|-----------------|
| Maxim Milet | [@FleurMax](https://github.com/FleurMax) | *TBD* |
| Persoon 2 | | *TBD* |
| Persoon 3 | | *TBD* |
| Persoon 4 | | *TBD* |

## 3. Algorithm Performances

*In progress: To be updated as algorithms are implemented.*

### 3.1 Algorithm 1 (e.g. Clarke and Wright Savings)
Description of the algorithm and its parameters.

| Instance | Dimension | Capacity | Optimal Cost | Alg Results (Cost) | Time | Gap |
| :--- | :---: | :---: | :---: | :---: | :---: | :--- |
| **A-n32-k5.vrp** | 32 | 100 | - | - | - | - |

*(Fill tables as results are aggregated)*

### 3.2 Algorithm 2 (e.g. Ant Colony System)
Description of the ant colony metaheuristic approach...

### 3.3 Algorithm 3 (e.g. Iterated Variable Neighborhood Descent)
Description of local search and mutation operators...

### 3.4 Algorithm 4 (e.g. Restricted Dynamic Programming or Neural LNS)
Description of the framework...

## 4. Conclusion
*Conclusion detailing the trade-offs between solution quality (Gap to optimal) and computational resources (time & memory) required by the different heuristics.*
