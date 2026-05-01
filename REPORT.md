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
| Maxim Milet | [@FleurMax](https://github.com/FleurMax) | *Neural Large Neighborhood Search (NLNS)* |
| Aucke Willems | [@auckew](https://github.com/auckew) | *Restricted dynamic programming (Gromicho)* |
| Leto Caris | [@LetoCaris](https://github.com/LetoCaris) | *Iterated Variable Neighborhood Descent (IVND)* |
| Senne Lievens | [@SenneLievens](https://github.com/SenneLievens) | *Improved Clarke and Wright savings algorithm* |

## 3. Algorithm Performances

*In progress: To be updated as algorithms are implemented.*

### 3.1 Benchmark Results (Set A)
Known optimal values for standard Augerat Set A instances compared against Neural LNS (Sequential).

| Instance | Optimal Cost | Neural LNS (Cost) | Time (ms) | Gap (%) |
| :--- | :---: | :---: | :---: | :---: |
| Instance | Optimal Cost | Neural LNS (Cost) | Time (ms) | Gap (%) |
| :--- | :---: | :---: | :---: | :---: |
| **A-n16-k5.vrp** | 190* | 524.78 | 48.21 | 176.2% |
| **A-n32-k5.vrp** | 784 | 812.29 | 164.13 | 3.6% |
| **A-n33-k6.vrp** | 742 | 761.84 | 201.14 | 2.7% |
| **A-n37-k5.vrp** | 669 | 699.05 | 269.74 | 4.5% |
| **A-n39-k5.vrp** | 822 | 847.68 | 225.90 | 3.1% |
| **A-n45-k7.vrp** | 1146 | 1185.08 | 229.23 | 3.4% |
| **A-n53-k7.vrp** | 1010 | 1103.20 | 437.75 | 9.2% |
| **A-n55-k9.vrp** | 1073 | 1128.64 | 392.88 | 5.2% |
| **A-n64-k9.vrp** | 1401 | 1492.84 | 690.60 | 6.6% |
| **A-n65-k9.vrp** | 1174 | 1287.97 | 508.20 | 9.7% |
| **A-n69-k9.vrp** | 1159 | 1216.54 | 758.24 | 5.0% |
| **A-n80-k10.vrp** | 1763 | 1934.74 | 751.44 | 9.7% |
| **A-n100-k10.vrp** | 2041 | 2436.32 | 1512.83 | 19.4% |
| **A-n130-k10.vrp** | 1491 | 3054.66 | 2924.72 | 104.9% |

> [!NOTE]
> *Some local instances (like A-n16) contain metadata or coordinate variants that differ from official CVRPLIB standards, leading to abnormal gaps. Results for large instances (n > 100) are heavily influenced by the heuristic surrogate's local search depth.

### 3.2 Clarke Wright Algorithm
Description of the ant colony metaheuristic approach...

### 3.3 Algorithm 3 (e.g. Iterated Variable Neighborhood Descent)
Description of local search and mutation operators...

### 3.4 Neural Large Neighborhood Search (Maxim)
Description of the framework...

## 4. Conclusion
*Conclusion detailing the trade-offs between solution quality (Gap to optimal) and computational resources (time & memory) required by the different heuristics.*
