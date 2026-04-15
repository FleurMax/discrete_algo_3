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
| Aucke Willems | [@auckew](https://github.com/auckew) | *TBD* |
| Leto Caris | [@LetoCaris](https://github.com/LetoCaris) | *TBD* |
| Senne Lievens | [@SenneLievens](https://github.com/SenneLievens) | *TBD* |

## 3. Algorithm Performances

*In progress: To be updated as algorithms are implemented.*

### 3.1 Benchmark Results (Set A)
Known optimal values for standard Augerat Set A instances.

| Instance | Dimension | Capacity | Optimal Cost | Alg Results (Cost) | Time | Gap |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| **A-n16-k5.vrp** | 16 | 35 | 190 | - | - | - |
| **A-n32-k5.vrp** | 32 | 100 | 784 | - | - | - |
| **A-n33-k5.vrp** | 33 | 100 | 661 | - | - | - |
| **A-n33-k6.vrp** | 33 | 100 | 742 | - | - | - |
| **A-n37-k5.vrp** | 37 | 100 | 669 | - | - | - |
| **A-n39-k5.vrp** | 39 | 100 | 822 | - | - | - |
| **A-n45-k7.vrp** | 45 | 100 | 1146 | - | - | - |
| **A-n53-k7.vrp** | 53 | 100 | 1010 | - | - | - |
| **A-n55-k9.vrp** | 55 | 100 | 1073 | - | - | - |
| **A-n64-k9.vrp** | 64 | 100 | 1401 | - | - | - |
| **A-n65-k9.vrp** | 65 | 100 | 1174 | - | - | - |
| **A-n69-k9.vrp** | 69 | 100 | 1159 | - | - | - |
| **A-n80-k10.vrp** | 80 | 100 | 1763 | - | - | - |

*(Fill tables as results are aggregated)*

### 3.2 Algorithm 2 (e.g. Ant Colony System)
Description of the ant colony metaheuristic approach...

### 3.3 Algorithm 3 (e.g. Iterated Variable Neighborhood Descent)
Description of local search and mutation operators...

### 3.4 Algorithm 4 (e.g. Restricted Dynamic Programming or Neural LNS)
Description of the framework...

## 4. Conclusion
*Conclusion detailing the trade-offs between solution quality (Gap to optimal) and computational resources (time & memory) required by the different heuristics.*
