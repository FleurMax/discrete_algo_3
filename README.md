# Vehicle Routing Problem - Project 3

Infrastructuur voor het benchmarken van (meta)heuristieken voor het Vehicle Routing Probleem (VRP), in het bijzonder het capaciteitsbeperkte probleem (CVRP).

## 👥 Groep
| Naam | GitHub | Rol / Algoritme |
|------|--------|-----------------|
| Maxim Milet | [@FleurMax](https://github.com/FleurMax) | *Neural Large Neighborhood Search (NLNS)* |
| Aucke Willems | [@auckew](https://github.com/auckew) | *Restricted dynamic programming (Gromicho)* |
| Leto Caris | [@LetoCaris](https://github.com/LetoCaris) | *Iterated Variable Neighborhood Descent (IVND)* |
| Senne Lievens | [@SenneLievens](https://github.com/SenneLievens) | *Improved Clarke and Wright savings algorithm* |

## 📁 Projectstructuur
- `src/Customer.java`: Locatie/klant met coördinaten en vraag (demand).
- `src/VRPProblem.java`: Data klas met capaciteit, depot en klanten.
- `src/VRPAlgorithm.java`: Interface voor de solvers.
- `src/VRPParser.java`: Parser voor CVRPLIB / TSPLIB formaat bestanden.
- `src/BenchmarkRunner.java`: Vergelijkt diverse algoritmen.
- `src/Main.java`: Startpunt van de applicatie.
- `test instances/`: Map om `.vrp` test instanties in te plaatsen (bijv. CVRPLIB Set A/B/E).
- `results/`: Gegenereerde resultaten van de benchmarks.

## 🧩 Implementatie

Elk algoritme moet de `VRPAlgorithm` interface implementeren:

```java
public interface VRPAlgorithm {
    List<List<Customer>> solve(VRPProblem problem);
    String getName();
    boolean isExact();
}
```

### De `solve` methode en Benchmarking
Elk algoritme moet de klanten opsplitsen in legale routes (`List<Customer>`), startend en eindigend aan het depot, waarbij de som van de `demand` niet groter is dan de `capacity` van de test instantie. Alle routes samen worden teruggegeven als `List<List<Customer>>`.

De `BenchmarkRunner` zorgt voor:
1. **Timing**: De tijd die je algoritme nodig heeft wordt extern gemeten.
2. **Kost**: De totale afgelegde afstand wordt berekend op basis van de ingeleverde routes.
3. **Validatie**: Oplossing wordt gecheckt op haalbaarheid (capaciteit gerespecteerd en iedereen bezocht).
4. **Timeouts**: Net zoals bij taak 2 is het aanbevolen om te checken of threads niet onderbroken zijn na een timeout.

**Template:**
```java
@Override
public List<List<Customer>> solve(VRPProblem problem) {
    List<List<Customer>> routes = new ArrayList<>();

    // Logica ...
    if (Thread.currentThread().isInterrupted()) return null;

    return routes;
}
```

> [!IMPORTANT]
> Omdat we met metaheuristieken / benaderingsalgoritmen werken kan het runnen lang duren. Bouw zeker een interrupt check in om timeouts vlot te laten werken!

## 🧪 Benchmarking
We gebruiken standaard CVRP datasets. Je kunt benchmarks grafen vinden via:
- [DIMACS Vehicle Routing Problem](http://dimacs.rutgers.edu/programs/challenge/vrp/)
- [CVRPLIB](http://vrp.galib.ii.pw.edu.pl/)

Download een aantal instanties `.vrp` (bijv. set A, set B) naar de map `test instances/`.

### Runnen
```powershell
# Compileer
javac -d out src/*.java

# Run
java -cp out Main
```
## 📈 Resultaten & Benchmarks

### 3.1 Officiële Optimale Kosten (Set A)
Deze waarden zijn de referentiepunten voor de Augerat Set A benchmarks, verkregen via [CVRPLIB](http://vrp.galgos.inf.puc-rio.br/).

| Instantie | Optimaal | Instantie | Optimaal |
| :--- | :---: | :--- | :---: |
| A-n16-k5 | 190 | A-n55-k9 | 1073 |
| A-n32-k5 | 784 | A-n64-k9 | 1401 |
| A-n33-k6 | 742 | A-n65-k9 | 1174 |
| A-n37-k5 | 669 | A-n69-k9 | 1159 |
| A-n39-k5 | 822 | A-n80-k10 | 1763 |
| A-n45-k7 | 1146 | A-n100-k10 | 2041 |
| A-n53-k7 | 1010 | A-n130-k10 | 1491 |

---

### 3.4 Neural Large Neighborhood Search (Maxim)
Dit algoritme implementeert de NLNS metaheuristiek (Hottung & Tierney, 2020) met Point/Tour destroy en een Regret-based Sequential Repair surrogate.

| Instance | Optimal | NLNS Cost | Gap (%) |
| :--- | :---: | :---: | :---: |
| **A-n16-k5** | 190 | 510.09 | 168.5% |
| **A-n32-k5** | 784 | 800.60 | 2.1% |
| **A-n33-k6** | 742 | 778.72 | 4.9% |
| **A-n37-k5** | 669 | 771.76 | 15.4% |
| **A-n39-k5** | 822 | 912.52 | 11.0% |
| **A-n45-k7** | 1146 | 1320.53 | 15.2% |
| **A-n53-k7** | 1010 | 1169.68 | 15.8% |
| **A-n55-k9** | 1073 | 1255.69 | 17.0% |
| **A-n64-k9** | 1401 | 1774.23 | 26.6% |
| **A-n65-k9** | 1174 | 1359.80 | 15.8% |
| **A-n69-k9** | 1159 | 1290.43 | 11.3% |
| **A-n80-k10** | 1763 | 2137.82 | 21.3% |
| **A-n100-k10** | 2041 | 2618.36 | 28.3% |
| **A-n130-k10** | 1491 | 3523.64 | 136.3% |

*Resultaten gegenereerd via de geoptimaliseerde Java surrogate (Regret-2 Sequential Connection).*
