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

### 3.2 Clarke Wright Savings Algorithm
The Clarke and Wright (CW) Savings Algorithm is one of the most famous heuristics used to solve the Vehicle Routing Problem (VRP). In this section of the report we will analyse this algorithm, but also 3 improvements that were prsented in the literature. These 3 improved algorithms are the Modified Clarke Wright Savings Algoritm (MCW), Improved Clarke Wright Savings Algoritm (ICW) and the Modified Improved Clarke Wright Savings Algoritm (MICW).
#### 3.2.1 Standard Clarke Wright Savings Algoritm
The Clarke and Wright Savings Algorithm operates through a sequential logic that optimises for cost by merging individual trips. The process begins by assuming that every customer is served by a dedicated vehicle making a separate round trip from the depot. To improve this initial setup, the algorithm calculates the potential "savings" for every possible pair of customers by determining how much distance is eliminated if one vehicle visits both in a single trip instead of two separate trips.

Once these values are calculated, all customer pairs are sorted into a ranked list from the highest savings to the lowest. The algorithm then iterates through this list, merging routes whenever it encounters a pair that can be connected without exceeding the vehicle's capacity or violating other constraints. This cycle repeats until no further merges can be made, resulting in an optimized set of routes where the most significant distance reductions were prioritized first.

The results of the Clarke Wright Savings Algorithm are presented in the following table.

| Instance | Optimal Cost | CW (Cost) | Time (ms) | Gap (%) |
| :--- | :---: | :---: | :---: | :---: |
| **A-n16-k5.vrp** | 190\* | 504.70 | 0.60 | 165.6% |
| **A-n32-k5.vrp** | 784 | 843.69 | 1.23 | 7.6% |
| **A-n33-k6.vrp** | 742 | 776.26 | 0.81 | 4.6% |
| **A-n37-k5.vrp** | 669 | 707.81 | 0.94 | 5.8% |
| **A-n39-k5.vrp** | 822 | 901.99 | 1.02 | 9.7% |
| **A-n45-k7.vrp** | 1146 | 1199.98 | 1.51 | 4.7% |
| **A-n53-k7.vrp** | 1010 | 1099.45 | 1.55 | 8.9% |
| **A-n55-k9.vrp** | 1073 | 1099.84 | 1.66 | 2.5% |
| **A-n64-k9.vrp** | 1401 | 1486.92 | 2.17 | 6.1% |
| **A-n65-k9.vrp** | 1174 | 1239.42 | 1.92 | 5.6% |
| **A-n69-k9.vrp** | 1159 | 1210.78 | 2.18 | 4.5% |
| **A-n80-k10.vrp** | 1763 | 1860.94 | 2.69 | 5.6% |
| **A-n100-k10.vrp** | 2041 | 2288.44 | 17.79 | 12.1% |
| **A-n130-k10.vrp** | 1491 | 2923.98 | 16.18 | 96.1% |

We can see that if we omit the two extreme cases (as was mentioned in the note of 3.1) the CW algorithm preforms quite well. The gap stays between 2.5% and 12.1%. On top of the competitive performance, the CW algorithm is very fast, often only a couple of miliseconds. To further illustrate its's performance we may compare it to the LNS in the following table.

| Instance | Optimal Cost | CW (Cost) | LNS (Cost) | Diff Cost | Diff Time (ms) | CW gap (%) | LNS gap (%) |
| :--- | :---: | :---: | :---: | :---: |:---: | :---: | :---: |
| **A-n16-k5.vrp** | 190\* | 504.70 | 524.78 | \-20.08 | +47.61 | 165.6% | 176.2% |
| **A-n32-k5.vrp** | 784 | 843.69 | 812.29 | +31.40 | +162.90 | 7.6% | 3.6% |
| **A-n33-k6.vrp** | 742 | 776.26 | 761.84 | +14.42 | +200.33 | 4.6% | 2.7% |
| **A-n37-k5.vrp** | 669 | 707.81 | 699.05 | +8.76 | +268.80 | 5.8% | 4.5% |
| **A-n39-k5.vrp** | 822 | 901.99 | 847.68 | +54.31 | +224.88 | 9.7% | 3.1% |
| **A-n45-k7.vrp** | 1146 | 1199.98 | 1185.08 | +14.90 | +227.72 | 4.7% | 3.4% |
| **A-n53-k7.vrp** | 1010 | 1099.45 | 1103.20 | \-3.75 | +436.20 | 8.9% | 9.2% |
| **A-n55-k9.vrp** | 1073 | 1099.84 | 1128.64 | \-28.80 | +391.22 | 2.5% | 5.2% |
| **A-n64-k9.vrp** | 1401 | 1486.92 | 1492.84 | \-5.92 | +688.43 | 6.1% | 6.6% |
| **A-n65-k9.vrp** | 1174 | 1239.42 | 1287.97 | \-48.55 | +506.28 | 5.6% | 9.7% |
| **A-n69-k9.vrp** | 1159 | 1210.78 | 1216.54 | \-5.76 | +756.06 | 4.5% | 5.0% |
| **A-n80-k10.vrp** | 1763 | 1860.94 | 1934.74 | \-73.80 | +748.75 | 5.6% | 9.7% |
| **A-n100-k10.vrp** | 2041 | 2288.44 | 2436.32 | \-147.88 | +1495.04 | 12.1% | 19.4% |
| **A-n130-k10.vrp** | 1491 | 2923.98 | 3054.66 | \-130.68 | +2908.54 | 96.1% | 104.9% |
| **Average** | — | — | — | **\-24.10** | **+647.34** | **24.25%** | **26.18%** |

#### 3.2.2 Modified Clarke Wright Savings Algoritm

| Instance | Optimal Cost | MCW (Cost) | Time (ms) | Gap (%) |
| :--- | :---: | :---: | :---: | :---: |
| **A-n16-k5.vrp** | 190\* | 508.15 | 0.54 | 167.4% |
| **A-n32-k5.vrp** | 784 | 835.01 | 1.03 | 6.5% |
| **A-n33-k6.vrp** | 742 | 747.32 | 1.11 | 0.7% |
| **A-n37-k5.vrp** | 669 | 724.24 | 1.25 | 8.3% |
| **A-n39-k5.vrp** | 822 | 866.48 | 1.72 | 5.4% |
| **A-n45-k7.vrp** | 1146 | 1194.41 | 1.78 | 4.2% |
| **A-n53-k7.vrp** | 1010 | 1073.16 | 2.22 | 6.3% |
| **A-n55-k9.vrp** | 1073 | 1117.87 | 2.30 | 4.2% |
| **A-n64-k9.vrp** | 1401 | 1501.19 | 2.91 | 7.2% |
| **A-n65-k9.vrp** | 1174 | 1254.99 | 2.99 | 6.9% |
| **A-n69-k9.vrp** | 1159 | 1230.44 | 3.27 | 6.2% |
| **A-n80-k10.vrp** | 1763 | 1846.39 | 4.39 | 4.7% |
| **A-n100-k10.vrp** | 2041 | 2280.72 | 19.99 | 11.7% |
| **A-n130-k10.vrp** | 1491 | 2888.83 | 14.75 | 93.8% |


| Instance | Optimal Cost | CW (Cost) | CW gap (%) | MCW (Cost) | MCW gap (%) | Difference (Cost) |
| :--- | :---: | :---: | :---: | :---: |:---: | :---: | 
| **A-n16-k5.vrp** | 190\* | 504.70 | 165.6% | 508.15 | 167.4% | +3.45 |
| **A-n32-k5.vrp** | 784 | 843.69 | 7.6% | 835.01 | 6.5% | \-8.68 |
| **A-n33-k6.vrp** | 742 | 776.26 | 4.6% | 747.32 | 0.7% | \-28.94 |
| **A-n37-k5.vrp** | 669 | 707.81 | 5.8% | 724.24 | 8.3% | +16.43 |
| **A-n39-k5.vrp** | 822 | 901.99 | 9.7% | 866.48 | 5.4% | \-35.51 |
| **A-n45-k7.vrp** | 1146 | 1199.98 | 4.7% | 1194.41 | 4.2% | \-5.57 |
| **A-n53-k7.vrp** | 1010 | 1099.45 | 8.9% | 1073.16 | 6.3% | \-26.29 |
| **A-n55-k9.vrp** | 1073 | 1099.84 | 2.5% | 1117.87 | 4.2% | +18.03 |
| **A-n64-k9.vrp** | 1401 | 1486.92 | 6.1% | 1501.19 | 7.2% | +14.27 |
| **A-n65-k9.vrp** | 1174 | 1239.42 | 5.6% | 1254.99 | 6.9% | +15.57 |
| **A-n69-k9.vrp** | 1159 | 1210.78 | 4.5% | 1230.44 | 6.2% | +19.66 |
| **A-n80-k10.vrp** | 1763 | 1860.94 | 5.6% | 1846.39 | 4.7% | \-14.55 |
| **A-n100-k10.vrp** | 2041 | 2288.44 | 12.1% | 2280.72 | 11.7% | \-7.72 |
| **A-n130-k10.vrp** | 1491 | 2923.98 | 96.1% | 2888.83 | 93.8% | \-35.15 |
| **Average** | — | — | **24.25%** | — | **23.82%** | **\-6.79** |

#### 3.2.3 Improved Clarke Wright Savings Algoritm
depth = 100.000
k= 25

| Instance | Optimal Cost | Ave. ICW (Cost) | Min. ICW (Cost) | Max. ICW (Cost) | St. Dev ICW (Cost) | Ave. Time (ms) | Gap (%) |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | ---: | 
| **A-n16-k5.vrp** | 190\* | 528.12 | 502.86 | 558.99 | 19.34 | 32.35 | 178.0% |
| **A-n32-k5.vrp** | 784 | 844.35 | 828.02 | 868.52 | 10.55 | 68.99 | 7.7% |
| **A-n33-k6.vrp** | 742 | 763.64 | 744.74 | 775.95 | 9.38 | 69.48 | 2.9% |
| **A-n37-k5.vrp** | 669 | 711.79 | 695.42 | 729.25 | 9.41 | 80.11 | 6.4% |
| **A-n39-k5.vrp** | 822 | 881.63 | 848.25 | 918.90 | 20.59 | 85.56 | 7.3% |
| **A-n45-k7.vrp** | 1146 | 1189.91 | 1166.56 | 1215.98 | 11.40 | 97.16 | 3.8% |
| **A-n53-k7.vrp** | 1010 | 1089.45 | 1060.95 | 1123.43 | 17.65 | 116.47 | 7.9% |
| **A-n55-k9.vrp** | 1073 | 1113.31 | 1099.84 | 1138.43 | 8.56 | 122.11 | 3.8% |
| **A-n64-k9.vrp** | 1401 | 1481.41 | 1464.47 | 1499.04 | 8.42 | 148.28 | 5.7% |
| **A-n65-k9.vrp** | 1174 | 1259.95 | 1229.71 | 1286.92 | 17.78 | 152.03 | 7.3% |
| **A-n69-k9.vrp** | 1159 | 1217.36 | 1192.89 | 1264.77 | 21.94 | 166.91 | 5.0% |
| **A-n80-k10.vrp** | 1763 | 1853.68 | 1828.06 | 1876.92 | 12.27 | 203.02 | 5.1% |
| **A-n100-k10.vrp** | 2041 | 2281.82 | 2260.32 | 2315.17 | 12.92 | 277.40 | 11.8% |
| **A-n130-k10.vrp** | 1491 | 2914.60 | 2881.55 | 2938.47 | 13.50 | 386.72 | 95.5% |



| Instance | Optimal Cost | CW (Cost) | CW gap (%) | Ave. ICW (Cost) | Ave. ICW gap (%) | Difference (Cost) |
| :--- | :---: | :---: | :---: | :---: |:---: | :---: |
| **A-n16-k5.vrp** | 190\* | 504.70 | 165.6% | 515.88 | 171.5% | +11.18 |
| **A-n32-k5.vrp** | 784 | 843.69 | 7.6% | 820.15 | 4.6% | \-23.54 |
| **A-n33-k6.vrp** | 742 | 776.26 | 4.6% | 750.59 | 1.2% | \-25.67 |
| **A-n37-k5.vrp** | 669 | 707.81 | 5.8% | 723.67 | 8.2% | +15.86 |
| **A-n39-k5.vrp** | 822 | 901.99 | 9.7% | 875.32 | 6.5% | \-26.67 |
| **A-n45-k7.vrp** | 1146 | 1199.98 | 4.7% | 1199.09 | 4.6% | \-0.89 |
| **A-n53-k7.vrp** | 1010 | 1099.45 | 8.9% | 1086.10 | 7.5% | \-13.35 |
| **A-n55-k9.vrp** | 1073 | 1099.84 | 2.5% | 1097.79 | 2.3% | \-2.05 |
| **A-n64-k9.vrp** | 1401 | 1486.92 | 6.1% | 1509.26 | 7.7% | +22.34 |
| **A-n65-k9.vrp** | 1174 | 1239.42 | 5.6% | 1242.80 | 5.9% | +3.38 |
| **A-n69-k9.vrp** | 1159 | 1210.78 | 4.5% | 1200.28 | 3.6% | \-10.50 |
| **A-n80-k10.vrp** | 1763 | 1860.94 | 5.6% | 1840.85 | 4.4% | \-20.09 |
| **A-n100-k10.vrp** | 2041 | 2288.44 | 12.1% | 2250.34 | 10.3% | \-38.10 |
| **A-n130-k10.vrp** | 1491 | 2923.98 | 96.1% | 2914.34 | 95.5% | \-9.64 |
| **Average** | — | — | **24.25%** | — | **23.84%** | **\-8.83** |

#### 3.2.4 Modified Improved Clarke Wright Savings Algoritm
depth = 100.000
k= 25
| Instance | Optimal Cost | Ave. MICW (Cost) | Min. MICW (Cost) | Max. MICW (Cost) | St. Dev MICW (Cost) | Ave. Time (ms) | Gap (%) |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | ---: | 
| **A-n16-k5.vrp** | 190\* | 516.78 | 502.86 | 550.81 | 15.12 | 32.47 | 172.0% |
| **A-n32-k5.vrp** | 784 | 838.95 | 832.15 | 858.83 | 7.67 | 70.12 | 7.0% |
| **A-n33-k6.vrp** | 742 | 753.11 | 747.25 | 777.26 | 7.81 | 71.39 | 1.5% |
| **A-n37-k5.vrp** | 669 | 713.87 | 694.71 | 733.64 | 12.16 | 80.93 | 6.7% |
| **A-n39-k5.vrp** | 822 | 874.61 | 848.25 | 915.92 | 18.59 | 84.99 | 6.4% |
| **A-n45-k7.vrp** | 1146 | 1182.39 | 1168.69 | 1213.83 | 11.89 | 97.72 | 3.2% |
| **A-n53-k7.vrp** | 1010 | 1080.27 | 1051.84 | 1129.92 | 18.13 | 119.43 | 7.0% |
| **A-n55-k9.vrp** | 1073 | 1118.75 | 1102.31 | 1139.02 | 11.17 | 124.07 | 4.3% |
| **A-n64-k9.vrp** | 1401 | 1478.53 | 1456.58 | 1518.99 | 20.01 | 149.46 | 5.5% |
| **A-n65-k9.vrp** | 1174 | 1249.11 | 1223.56 | 1263.66 | 7.44 | 153.76 | 6.4% |
| **A-n69-k9.vrp** | 1159 | 1219.51 | 1194.80 | 1239.05 | 13.89 | 165.29 | 5.2% |
| **A-n80-k10.vrp** | 1763 | 1843.85 | 1809.29 | 1861.79 | 12.43 | 205.26 | 4.6% |
| **A-n100-k10.vrp** | 2041 | 2279.02 | 2234.55 | 2328.54 | 23.47 | 291.61 | 11.7% |
| **A-n130-k10.vrp** | 1491 | 2889.87 | 2880.78 | 2907.70 | 6.46 | 374.02 | 93.8% |



| Instance | Optimal Cost | CW (Cost) | CW gap (%) | Ave. ICW (Cost) | Ave. ICW gap (%) | Difference (Cost) |
| :--- | :---: | :---: | :---: | :---: |:---: | :---: |
| **A-n16-k5.vrp** | 190\* | 504.70 | 165.6% | 516.78 | 172.0% | +12.08 |
| **A-n32-k5.vrp** | 784 | 843.69 | 7.6% | 838.95 | 7.0% | \-4.74 |
| **A-n33-k6.vrp** | 742 | 776.26 | 4.6% | 753.11 | 1.5% | \-23.15 |
| **A-n37-k5.vrp** | 669 | 707.81 | 5.8% | 713.87 | 6.7% | +6.06 |
| **A-n39-k5.vrp** | 822 | 901.99 | 9.7% | 874.61 | 6.4% | \-27.38 |
| **A-n45-k7.vrp** | 1146 | 1199.98 | 4.7% | 1182.39 | 3.2% | \-17.59 |
| **A-n53-k7.vrp** | 1010 | 1099.45 | 8.9% | 1080.27 | 7.0% | \-19.18 |
| **A-n55-k9.vrp** | 1073 | 1099.84 | 2.5% | 1118.75 | 4.3% | +18.91 |
| **A-n64-k9.vrp** | 1401 | 1486.92 | 6.1% | 1478.53 | 5.5% | \-8.39 |
| **A-n65-k9.vrp** | 1174 | 1239.42 | 5.6% | 1249.11 | 6.4% | +9.69 |
| **A-n69-k9.vrp** | 1159 | 1210.78 | 4.5% | 1219.51 | 5.2% | +8.73 |
| **A-n80-k10.vrp** | 1763 | 1860.94 | 5.6% | 1843.85 | 4.6% | \-17.09 |
| **A-n100-k10.vrp** | 2041 | 2288.44 | 12.1% | 2279.02 | 11.7% | \-9.42 |
| **A-n130-k10.vrp** | 1491 | 2923.98 | 96.1% | 2889.87 | 93.8% | \-34.11 |
| **Average** | — | — | **24.25%** | — | **23.95%** | **\-7.54** |

remarkably when combining the two methods of imporovement and modification, we see a subtractive effect. This may be as a result of the parameter choice, but may also be because the randomisation is better with pure savingsvalues.

### 3.3 Algorithm 3 (e.g. Iterated Variable Neighborhood Descent)
Description of local search and mutation operators...

### 3.4 Neural Large Neighborhood Search (Maxim)
Description of the framework...

## 4. Conclusion
*Conclusion detailing the trade-offs between solution quality (Gap to optimal) and computational resources (time & memory) required by the different heuristics.*
