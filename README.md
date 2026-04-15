# Vehicle Routing Problem - Project 3

Infrastructuur voor het benchmarken van (meta)heuristieken voor het Vehicle Routing Probleem (VRP), in het bijzonder het capaciteitsbeperkte probleem (CVRP).

## 👥 Groep
| Naam | GitHub | Rol / Algoritme |
|------|--------|-----------------|
| Maxim Milet | [@FleurMax](https://github.com/FleurMax) | *TBD* |
| Persoon 2 | | *TBD* |
| Persoon 3 | | *TBD* |
| Persoon 4 | | *TBD* |

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
De resultaten worden gegeneert in de `results/` map.
