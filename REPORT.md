# Dit is niet het finaal verslag, Leto moet nog iets uploaden en Maxim gaat nog een tabel bij zijn stuk zetten. Dit is zuiver om de volledige GitHub te uploaden.

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
| **A-n16-k5.vrp** | 190\* | 504.70 | 524.78 | \-20.08 | \-47.61 | 165.6% | 176.2% |
| **A-n32-k5.vrp** | 784 | 843.69 | 812.29 | +31.40 | \-162.90 | 7.6% | 3.6% |
| **A-n33-k6.vrp** | 742 | 776.26 | 761.84 | +14.42 | \-200.33 | 4.6% | 2.7% |
| **A-n37-k5.vrp** | 669 | 707.81 | 699.05 | +8.76 | \-268.80 | 5.8% | 4.5% |
| **A-n39-k5.vrp** | 822 | 901.99 | 847.68 | +54.31 | \-224.88 | 9.7% | 3.1% |
| **A-n45-k7.vrp** | 1146 | 1199.98 | 1185.08 | +14.90 | \-227.72 | 4.7% | 3.4% |
| **A-n53-k7.vrp** | 1010 | 1099.45 | 1103.20 | \-3.75 | \-436.20 | 8.9% | 9.2% |
| **A-n55-k9.vrp** | 1073 | 1099.84 | 1128.64 | \-28.80 | \-391.22 | 2.5% | 5.2% |
| **A-n64-k9.vrp** | 1401 | 1486.92 | 1492.84 | \-5.92 | \-688.43 | 6.1% | 6.6% |
| **A-n65-k9.vrp** | 1174 | 1239.42 | 1287.97 | \-48.55 | \-506.28 | 5.6% | 9.7% |
| **A-n69-k9.vrp** | 1159 | 1210.78 | 1216.54 | \-5.76 | \-756.06 | 4.5% | 5.0% |
| **A-n80-k10.vrp** | 1763 | 1860.94 | 1934.74 | \-73.80 | \-748.75 | 5.6% | 9.7% |
| **A-n100-k10.vrp** | 2041 | 2288.44 | 2436.32 | \-147.88 | \-1495.04 | 12.1% | 19.4% |
| **A-n130-k10.vrp** | 1491 | 2923.98 | 3054.66 | \-130.68 | \-2908.54 | 96.1% | 104.9% |
| **Average** | — | — | — | **\-24.10** | **\-647.34** | **24.25%** | **26.18%** |

This comparison shows that CW algorithm dominates the LNS algorithm on speed every and outperforms it on cost on average.

#### 3.2.2 Modified Clarke Wright Savings Algoritm
The first alternative CW algorithm is the Modified Clarke Wright Savings Algoritm (MCW). The modification happens only in the calculation of the potential savings. It add parameters $\lambda$, $\mu$ and $\nu$, where $\lambda$,  the route shape parameter, is a parameter that controls the relative significance of direct arc between two customers, $\mu$, the weighted parameter, is the asymmetry between two customers with respect to their distances to the depot and $\nu$, the customer demand parameter, includes the demand of customers on a vehicle’s capacity. Based on literature informed bounds, we chose $\lambda = 1.5$, $\mu = 0.5$ and $\nu = 1$. 

The results of the Modified Clarke Wright Savings Algoritm are presented in the following table.

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

We can see that if we omit the two extreme cases (as was mentioned in the note of 3.1) the MCW algorithm again preforms quite well. The gap stays between 0.7% and 11.1%, which is an improvement on the CW algorithm. Thz MCW algorithm is still very fast, often staying at only a couple of miliseconds, but the more complex savings computation does demand setuptime than CW did. To further compare the CW and the MCW algoritm we may may refer to the following table.

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

Here we are affirmed in belief that the MCW genrally outperforms the CW algoritm.

#### 3.2.3 Improved Clarke Wright Savings Algoritm
Instead of modifying the savings calculation, a different approach would be to change how we pick what pair is set as a mergign candidate for a given ordered list of savings. Holland proposed one way to do this, called fitness proportionate selection or roulettewheel selection. In this method we don't just take te highest available savings value, but we select the $T$ top elements of our ordered list and randomly select one value to be the following candidate. The selection chance is proportionate to its savings value, so that on average higher savings values are chosen more often. This step is repeated multiple times and every time this 'tournament' obtains a lower cost, we change the savingslist from which the tournament gets created to this 'more optimal' tournament. The tournament size $T$ is also randomly genreated. This method too has parameters on which it depends, namely the depth, consecutive depth, which stops the algorithm early if there are no improvements for an amount of iterations. and tournament size $T$. Though exprimentation we took the depth to be 100,000, the consecutive depth to be 20000 and the tournament size to be 3. As this method is randomised we ran the algorithm 25 times per testset.

The results of the Improved Clarke Wright Savings Algoritm are presented in the following table.

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

The first thing to note is that, similarly to the previous two algorithms, the cost is competitive and falls between 2.9% and 11.8%. There is a noticable difference in computation time, but this is a result of our choice of high depth. If computation time if more relevant for your usecase, we advise you to lower it. A further comparision with the CW algorithm is presented in the following table.

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

As we can see the ICW outperforms the CW algorithm on average. It also outperforms the MCW algorithm on average, but the difference is small and the higher computation time may not justify the slight cost decrease.

#### 3.2.4 Modified Improved Clarke Wright Savings Algoritm
Finally we can combine the changes proposed in the Modified and the Improved algoritms to obtain the Modified Improved Clarke Wright Savings Algoritm(MICW). The same parameters where used as in the previous algorithms. There where further no other parameters necessary.

The results of the Modified Improved Clarke Wright Savings Algoritm are presented in the following table.
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

Again this algorith performs similarly to the previous thee algorithms, the cost again competitive and now falls between 1.5% and 11.4%. There is a noticable difference in computation time, but this is a result of our choice of high depth. If computation time if more relevant for your usecase, we advise you to lower it. A further comparision with the CW algorithm is presented in the following table.

| Instance | Optimal Cost | CW (Cost) | CW gap (%) | Ave. MICW (Cost) | Ave. MICW gap (%) | Difference (Cost) |
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

Remarkably we observe that when we combibned the two methods of imporovement and modification to one method, there is a subtractive effect. The improvements of the MICW is on average not as good as the improvements of either MCW or ICW. This may be a result of the parameter choice, but it may also indicate that the roulettewheel selection procedure is adapted for the 'pure' notion of a savings value.

#### 3.2.5  Benchmark set B
It is important to note that these methods perform differently on different benchmark sets. As such we will summerise the performances on a different bechmark set in one table. Note that we keep the hyperparameters unchanged.

|Instance | Optimal Cost | CW (Cost) | CW gap (%) | MCW (Cost) | MCW gap (%)| Ave. ICW (Cost) | Ave. ICW gap (%) | Ave MICW (Cost) | Ave. MICW gap (%) |
| :--- | :---: | :---: | :---: | :---: |:---: | :---: | :---: |:---: | :---: |
| **B-n31-k5** | 672 | 681,16 | 1,36% | 681,25 | 1,38% | 684,66 | 1,88% | 684,93 | 1,92% |
| **B-n34-k5** | 788 | 794,33 | 0,80% | 840,87 | 6,71% | 798,98 | 1,39% | 856,12 | 8,64% |
| **B-n35-k5** | 955 | 978,33 | 2,44% | 979,37 | 2,55% | 981,43 | 2,77% | 986,10 | 3,26% |
| **B-n38-k6** | 805 | 832,09 | 3,37% | 830,62 | 3,18% | 836,20 | 3,88% | 833,82 | 3,58% |
| **B-n39-k5** | 549 | 566,71 | 3,23% | 570,24 | 3,87% | 572,40 | 4,26% | 575,21 | 4,77% |
| **B-n41-k6** | 829 | 898,09 | 8,33% | 887,25 | 7,03% | 897,38 | 8,25% | 892,79 | 7,70% |
| **B-n43-k6** | 742 | 781,96 | 5,39% | 767,92 | 3,50% | 767,56 | 3,44% | 772,27 | 4,08% |
| **B-n44-k7** | 909 | 937,74 | 3,16% | 932,70 | 2,61% | 941,13 | 3,53% | 941,64 | 3,59% |
| **B-n45-k5** | 751 | 757,16 | 0,82% | 756,04 | 0,67% | 765,41 | 1,92% | 761,36 | 1,38% |
| **B-n45-k6** | 678 | 727,84 | 7,35% | 723,20 | 6,67% | 732,30 | 8,01% | 731,28 | 7,86% |
| **B-n50-k7** | 741 | 748,80 | 1,05% | 755,48 | 1,95% | 752,83 | 1,60% | 758,43 | 2,35% |
| **B-n50-k8** | 1312 | 1354,03 | 3,20% | 1392,18 | 6,11% | 1359,95 | 3,65% | 1369,66 | 4,40% |
| **B-n51-k7** | 1032 | 1121,25 | 8,65% | 1135,75 | 10,05% | 1094,78 | 6,08% | 1102,53 | 6,83% |
| **B-n52-k7** | 747 | 764,90 | 2,40% | 812,39 | 8,75% | 766,56 | 2,62% | 784,68 | 5,04% |
| **B-n56-k7** | 707 | 733,74 | 3,78% | 731,41 | 3,45% | 735,71 | 4,06% | 738,63 | 4,47% |
| **B-n57-k7** | 1153 | 1239,78 | 7,53% | 1242,79 | 7,79% | 1243,27 | 7,83% | 1244,41 | 7,93% |
| **B-n57-k9** | 1598 | 1653,42 | 3,47% | 1659,90 | 3,87% | 1658,29 | 3,77% | 1661,24 | 3,96% |
| **B-n64-k9** | 861 | 921,56 | 7,03% | 924,02 | 7,32% | 923,97 | 7,31% | 927,28 | 7,70% |
| **B-n66-k9** | 1316 | 1416,42 | 7,63% | 1433,43 | 8,92% | 1412,96 | 7,37% | 1393,24 | 5,87% |
| **B-n68-k9** | 1272 | 1317,77 | 3,60% | 1320,12 | 3,78% | 1318,62 | 3,66% | 1323,20 | 4,03% |
| **B-n78-k10** | 1221 | 1264,56 | 3,57% | 1270,10 | 4,02% | 1270,77 | 4,08% | 1275,54 | 4,47% |
| **Ave. gap** | — | — | **4,19%** | — | **4,87%** | — | **4,26%** | — | **4,64%** |

We can generally note that on this testset gap between optimal cost and the obtained cost is smaller. We also observe that the classical CW performs the best, noteably better than the MCW. We assume that this may be a result of the chosen hyperparemters. Next the ICW performs slightly worse on averge, but stays competitive. As such repeated of the ICW method and keeping track of the minimum, may attain better results than the CW attains. Finally the MICW now does improve on the MCW method, which may futher motivate the hypothesis that the poorer performance of the modified versions may be a result of the chosen hyperparemters.

### 3.3 Restricted Dynamic Programming Algorithm 
#### 3.3.1 The algorithm
The Restricted DP algorithm is a construction heuristic based on the exact dynamic programming algorithm of the Traveling Salesman Problem (TSP). The method applies this framework to the VRP through the giant-tour representation (GTR): all vehicles are concatenated into a single large cycle, reducing the VRP to a sequencing problem. This allows single-route and multi-route problems to be handled in a uniform way. 
The exact DP algorithm for the VRP has a time complexity of $O(n\cdot 2^{n-m} \cdot m)$ - impractical for realistic problem sizes. Therefore, two restrictions are imposed to reduce the state space.. 
An already known restriction is allowing at most H solutions, with lowest capacity, to be expanded further in each stage of the state space. 
In addition, they introduce the $E$-restriction. This ensures for each state $(S, j)$ to only be expanded towards the $E$ nearest unvisited nodes, for which we find feasible expansions. This means, for the CVRP, a feasibility check is incorporated at each expansion.

#### 3.3.2 Parameters $H$ and $E$
The behaviour of RDP is primarily governed by two parameters: $H$ and $E$.
Parameter H controls the width of the beam through the state space. A low value of $H$ means the algorithm commits early to a small set of promising partial solutions, trading solution quality for speed. Increasing $H$ allows more alternative partial tours to survive into later stages. At the extreme $H=1$, the algorithm results in the nearest neighbour heuristic, while $H = \infty$ recovers the exact DP algorithm. 

Parameter $E$ controls how many neughbours each state is allowed to ewapnd to. This reflects the observation that in high-quality VRP solutions the arcs tend to connnect to nearby nodes. A smaller $E$ speeds up each stage. 

#### 3.3.3 Results on Set A
The implementation was tested on the Augerat Set A benchmark instances. The known optimal values are taken from CVRPLIB. The algortihm was run with $E = 10$ and $H= 200$. These values represent a moderate trade-off between solution quality and computation time.  

| Instance | Optimal Cost | RDP (Cost) | Time (ms) | Gap (%) |
| --- | --- | --- | --- | --- |
| **A-n16-k5.vrp** | 190* | 687.53 | 11.78 | 261.9% |
| **A-n32-k5.vrp** | 784 | 941.54 | 45.48 | 20.1% |
| **A-n33-k6.vrp** | 742 | 1082.14 | 43.20 | 45.8% |
| **A-n37-k5.vrp** | 669 | 979.32 | 58.24 | 46.4% |
| **A-n39-k5.vrp** | 822 | 1014.69 | 61.47 | 23.4% |
| **A-n45-k7.vrp** | 1146 | 1260.42 | 64.84 | 10.0% |
| **A-n53-k7.vrp** | 1010 | 1251.67 | 93.52 | 23.9% |
| **A-n55-k9.vrp** | 1073 | 1350.49 | 106.08 | 25.8% |
| **A-n64-k9.vrp** | 1401 | 1937.97 | 120.34 | 38.3% |
| **A-n65-k9.vrp** | 1174 | 1421.46 | 135.22 | 21.1% |
| **A-n69-k9.vrp** | 1159 | 1507.81 | 158.16 | 30.1% |
| **A-n80-k10.vrp** | 1763 | 2486.34 | 180.59 | 41.0% |
| **A-n100-k10.vrp** | 2041 | 2979.17 | 582.86 | 46.0% |
| **A-n130-k10.vrp** | 1491 | 3724.55 | 642.13 | 149.8% |

Ignoring the noted outliers, the algorithm scores below the Clarke-Wright vairants and Neural LNS, with gaps typically ranging between 10% and 50%. This is consistent with the findings of Gromicho et al. (2011), who report that RDP as a pure construction heuristic is less competitive on classical VRP variants, but performs significantly better when more realistic constraints are added. 

The computation times are reasonable: from more or less 12 ms for small instance (n=16) up to 643 ms for th largest instance (n=130). This is slower than Clarke-Wright, but competitive with Neural LNS.  

### 3.4 Neural Large Neighborhood Search (Maxim)
Neural Large Neighborhood Search (Neural LNS) is a metaheuristic that enhances the traditional LNS framework by employing deep learning models, such as Pointer Networks with attention mechanisms, to learn optimal destroy and repair operators from problem data. This data-driven approach enables the algorithm to discover sophisticated patterns and dependencies within solution spaces, often outperforming manually designed heuristics on complex combinatorial problems like the VRP.

### 3.5 Algorithm 3 (e.g. Iterated Variable Neighborhood Descent)
Description of local search and mutation operators...

## 4. Conclusion
*Conclusion detailing the trade-offs between solution quality (Gap to optimal) and computational resources (time & memory) required by the different heuristics.*
