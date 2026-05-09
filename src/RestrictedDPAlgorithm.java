import java.util.*;

public class RestrictedDPAlgorithm implements VRPAlgorithm{
    private List <Customer> gtr;
    private int m;
    private double[][] dist;

    //TODO testen voor verschillende parameterwaarden
    private static final int E = 10; // max expansies per state
    private static final int H = 200; // max states te behouden per stage
    public List<List<Customer>> solve(VRPProblem problem) {
        m = problem.getCustomers().size(); // aantal klanten
        System.out.println("m = " +m + " customers");

        gtr = buildGTR(problem);
        dist = computeDistanceMatrix(gtr);

        Map<Integer, Customer> customerById = new HashMap<>();
        for(Customer c: problem.getCustomers()){
            customerById.put(c.getId(), c);
        }

        DPState initial = createInitialState(problem);

        List<DPState> states = new ArrayList<>();
        states.add(initial);

        for(int stage = 0; stage <2*m; stage++){
            List<DPState> newStates = new ArrayList<>();

            //alle mogelijke uitbreidingen toevoegen
            for(DPState s : states){
                newStates.addAll(expandState(s, problem));
            }
            if(newStates.isEmpty()) break;
            states = keepBestH(newStates); // nog eens H restrictie op final state
            System.out.println("Stage " + stage + " -> states: " + states.size());

        }


        DPState best = null;
        for(DPState s : states){
            if(s.visited.cardinality()==m){ // enkel states met alle klanten bezocht, behouden
                if(best == null || s.cost < best.cost){ //state met minimale kost behouden
                    best = s;
                }
            }
        }

        return reconstructRoutes(best, problem, customerById);
    }
//STAP 1:
    private List<Customer> buildGTR(VRPProblem problem) {
        List<Customer> g = new ArrayList<>();
        Customer depot = problem.getDepot();

        for (int v = 0; v < m; v++) {
            g.add(depot); // o_v; index 2v
            g.add(depot); // d_v; index 2v+1
        }

        g.addAll(problem.getCustomers()); // c1, ..., cn; indices 2m, ..., 2m+n-1 (=3m-1)

        return g;
    }

//STAP 2:
// DP-states definiëren ((S, j))
    // maak hiervoor heel nieuwe klasse met alle info over de state bijgehouden als veld
    private static class DPState {
        // verzameling bezochte klanten S opslaan als lijst te traag
        // BitMask veel efficientere datastructuur om meerdere booleans in 1 integer te zetten
        //(32 of 64 booleans in 1 32-bit getal)
        BitSet visited; // welke klanten zijn bezocht (BitMask)
        int last;   // index in GTR
        double cost;    // totale kost tot nu toe
        int remainingCapacity;  // resterende capaciteit
        DPState parent; // voor reconstructie
        int parentNode; // welke node werd toegevoegd

        DPState(BitSet visited, int last, double cost, int remainingCapacity,
                DPState parent, int parentNode) {
            this.visited = visited;
            this.last = last;
            this.cost = cost;
            this.remainingCapacity = remainingCapacity;
            this.parent = parent;
            this.parentNode = parentNode;
        }
    }

    //SATP 3:
    //afstanden bijhouden in matrix; O(1) tijd om op te zoeken
    // customer is locatie dus hier gewoon nxn-matrix met afstand
    // tussen elke 2 locaties
    private double[][] computeDistanceMatrix(List<Customer> g) {
        int n = g.size();
        double[][] d = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                d[i][j] = g.get(i).distanceTo(g.get(j));
            }
        }
        return d;
    }

    private DPState createInitialState(VRPProblem problem) {
        return new DPState(new BitSet(),  // geen klanten bezocht
                0,   // start op o1 (index 0 in de GTR)
                0.0, // kost = 0
                problem.getCapacity(),  // volle capaciteit
                null, // geen parent
                -1    // geen parent node
        );
    }


   // hierin zit de E-restrictie verwerkt
    private List<DPState> expandState(DPState s, VRPProblem problem){
        int offset = 2*m; // klanten index begint vanaf 2m+1
        List<DPState> expansions = new ArrayList<>();

        //1. unvisited customers verzamelen
        List<Integer> customerCandidates = new ArrayList<>();
        for(int i = offset; i < gtr.size(); i++) {
            int indexC = i - offset;
            if (!s.visited.get(indexC)) { // als klant k bezocht is
                customerCandidates.add(i);
            }
        }
        // lijst sorteren op dist (willen E nearest)
        customerCandidates.sort(Comparator.comparing(i -> dist[s.last][i]));

        // E klanten in expansions zetten
        for (int candidate : customerCandidates) { //candidate = index, customerCandidates lijst van integers
            if(expansions.size() >= E) break;
            Customer c = gtr.get(candidate);

            if(s.remainingCapacity < c.getDemand()) continue;

            int indexC = candidate - offset;
            //nieuwe state maken; de voorwaarden zijn voldaan als tot aan deze lijn in de code geraakt
            BitSet newMask = (BitSet) s.visited.clone();
            newMask.set(indexC);
            double newCost = s.cost + dist[s.last][candidate];
            int newCap = s.remainingCapacity - c.getDemand();

            DPState newS = new DPState(newMask, candidate,
                    newCost, newCap, s, candidate );
            expansions.add(newS);
        }

        // 2.  depots controleren
        // depot transisitie toevoegen indien,
        // nog ongeziene klanten (nieuwe route)
        // OF alle klanten gezien (laatste route afsluiten)
        // als we net van depot komen, dan NIET
        // dit is als (s.last <offset)
        boolean atDepot = (s.last < offset);
        if(!atDepot){
            int depotIndex = 0; //voor alle depots zelfde locatie
            double newCostD = s.cost + dist[s.last][depotIndex];
            DPState depotState = new DPState(
                    (BitSet) s.visited.clone(), depotIndex, newCostD,
                    problem .getCapacity(), s, depotIndex);
        expansions.add(depotState);
        }

        return expansions;
    }

    // willen als return alle uitbreidingen van alle states in vorige stage
    // hierin H-restrictie verwerkt met sorterne op cost
    private List<DPState> keepBestH(List<DPState> states){
        states.sort(Comparator.comparing(s -> s.cost));
        int subGrootte = Math.min(H, states.size());
        return new ArrayList<>(states.subList(0, subGrootte));
    }

    private List<List<Customer>> reconstructRoutes(DPState beste, VRPProblem problem, Map<Integer, Customer> customerById){
        List<List<Customer>> routes = new ArrayList<>();
        if(beste == null) return routes;

        List<Integer> sequence = new ArrayList<>();
        DPState current = beste;

        while(current != null && current.parent != null){
            sequence.add(current.parentNode);
            current = current.parent;
        }
        Collections.reverse(sequence);

        // verschillende routes onderscheiden
        List<Customer> currentRoute = new ArrayList<>();
        int offset = 2*m;

        for(int i : sequence){
            // depot transitie = route afsluiten
            if(i < offset){ //dan een depot-node
                if(!currentRoute.isEmpty()){
                    routes.add(currentRoute);
                    currentRoute = new ArrayList<>();
                }
            } else {
                //klant toevoegen met id van klant i n problem
                Customer c = gtr.get(i);
                Customer original = customerById.get(c.getId());
                currentRoute.add(original);
            }
        }

        if(!currentRoute.isEmpty()){
            routes.add(currentRoute);
        }
        return routes;
    }


    @Override
    public String getName() {
        return "Restricted Dynamic Programming (Aucke Willems)";
    }

    @Override
    public boolean isExact() {
        return false;
    }
}
