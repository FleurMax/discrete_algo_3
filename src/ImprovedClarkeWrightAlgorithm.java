import java.util.*;

public class ImprovedClarkeWrightAlgorithm implements VRPAlgorithm{

    private List<Customer> customers;

    private boolean[][] internalArray;
    private Adjecents[][] adjacentsArray;
    private Route[][] routesArray;
    private int capacity;
    private final int tournamentMin;
    private final int tournamentMax;
    private final int depth;
    private final int consecutiveDepth;

    private Random rand;
    private class Savings{
        public final int i;
        public final int j;
        public final double savingsValue;

        public Savings(int i, int j, double savingsValue) {
            this.i = i;
            this.j = j;
            this.savingsValue = savingsValue;
        }
    }

    private class Adjecents{
        public int left;
        public int right;
        public final int id;

        public Adjecents(int id){
            this.id = id;
            left = -1;
            right = -1;
        }
        public void add(int point){
            if(left == -1) { //We use this bias throughout the code for minute speedups
                left = point;
            }
            else{
                right = point;
            }
        }
        public boolean isNonInternal(){
            return right == -1;
        }
    }

    private class Route{
        int begin;
        int end;
        int routeDemand;
        public Route(int begin, int end, int routeDemand){
            this.begin = begin;
            this.end = end;
            this.routeDemand = routeDemand;
        }

        public boolean equals(Route route){
            return this.begin == route.begin && this.end == route.end; // No flip (begin-end, end-begin) is needed
        }
        public void join(int connected, int toConnect){
            if(begin == connected){
                begin = toConnect;
            }
            else{
                end = toConnect;
            }
            routeDemand += customers.get(toConnect).getDemand();
        }
        public void merge(Route route, int thisNonInternal, int otherNonInternal){
            begin = (begin == thisNonInternal) ? end : begin;
            end = (route.end == otherNonInternal) ? route.begin : route.end;
            routeDemand += route.routeDemand;
        }
    }
    private ImprovedClarkeWrightAlgorithm(Builder builder) {
        this.tournamentMin = builder.tournamentMin;
        this.tournamentMax = builder.tournamentMax;
        this.depth = builder.depth;
        this.consecutiveDepth = builder.consecutiveDepth;
    }

    // Static inner Builder class
    public static class Builder {
        // Default values as specified in your original no-arg constructor
        private int tournamentMin = 2;
        private int tournamentMax = 5;
        private int depth = 100000;
        private int consecutiveDepth = 20000;

        public Builder() {
        }

        public Builder tournamentMin(int tournamentMin) {
            this.tournamentMin = tournamentMin;
            return this;
        }

        public Builder tournamentMax(int tournamentMax) {
            this.tournamentMax = tournamentMax;
            return this;
        }

        public Builder depth(int depth) {
            this.depth = depth;
            return this;
        }

        public Builder consecutiveDepth(int consecutiveDepth) {
            this.consecutiveDepth = consecutiveDepth;
            return this;
        }

        public ImprovedClarkeWrightAlgorithm build() {
            return new ImprovedClarkeWrightAlgorithm(this);
        }
    }

    @Override
    public List<List<Customer>> solve(VRPProblem problem) {

        List<List<Customer>> finalRoutes = new ArrayList<>(); //This will be the final route
        List<List<Customer>> tempRoutes;
        customers = problem.getCustomers(); //A list of all the customers in the problem
        capacity = problem.getCapacity();

        rand = new Random();
        double bestCost = Double.MAX_VALUE;

        internalArray = new boolean[customers.size()][depth]; //An array that gives true if its internal. As internals never become noninternal, this is fast
        adjacentsArray = new Adjecents[customers.size()][depth]; //An array that keeps a left and right adjacent point
        routesArray = new Route[customers.size()][depth]; //An array that keeps track of what route each point is in.
        List<Double> depotDistance = new ArrayList<>();
        Customer depot = problem.getDepot();

        PriorityQueue<Savings> savingsQueue = new PriorityQueue<>(Comparator.comparingDouble((Savings s) -> s.savingsValue).reversed());
        //I could also not reverse it and make the savings values negative?

        //Filling the pq
        for (int i = 0; i < problem.getCustomers().size(); i++) {
            //We index the depot with -1, deal with it separately;
            //As the savings of the depot connections are 0 these are minimal and are at the end of the pq. This will be useful.
            //savingsQueue.add(new Savings(-1,i,0));

            depotDistance.add(depot.distanceTo(customers.get(i)));

            for (int j = 0; j < i; j++) {
                Savings s = new Savings(i,j,depotDistance.get(i)+depotDistance.get(j)-customers.get(i).distanceTo(customers.get(j)));
                savingsQueue.add(s);
            }
        }

        Deque<Savings> tournamentQueue = new ArrayDeque<>(); //May be passed on through iterations
        while (!savingsQueue.isEmpty()) {//Initialise with order of the pq can't be done with a constructor:/
            tournamentQueue.addLast(savingsQueue.poll());
        }

        Deque<Savings> runthroughQueue; //The Deque that is actually used to iteratively find better solutions
        Deque<Savings> copyableQueue;// A copy of the previous Deque to update the superiterative tournamentQueue;
        double currentCost;
        int d = 0;
        int k = 0;

        while(d < depth && k < consecutiveDepth) {
            runthroughQueue = tournament(tournamentQueue);
            copyableQueue = new ArrayDeque<>(runthroughQueue);
            while (!runthroughQueue.isEmpty()) {
                Savings s = runthroughQueue.removeFirst();
                update(s, d);
            }
            tempRoutes = makeSolution(d);
            currentCost = problem.calculateTotalCost(tempRoutes); //Ik kan dit sneller dmv de routes
            if(currentCost < bestCost){
                bestCost = currentCost;
                tournamentQueue = copyableQueue;
                k = 0;
                finalRoutes = tempRoutes;
            }
            else {
                k++;
            }
            d++;

        }

        return finalRoutes;
    }

    public void update(Savings s, int d){
        int idI = s.i;
        int idJ = s.j;
        Customer ci = customers.get(idI);
        Customer cj =  customers.get(idJ);
        //If unassigned, assign them
        if(routesArray[idI][d] == null && routesArray[idJ][d] == null){
            if(ci.getDemand() + cj.getDemand() <= capacity){
                adjacentsArray[idI][d] = new Adjecents(idI);
                adjacentsArray[idI][d].add(idJ);
                adjacentsArray[idJ][d] = new Adjecents(idJ);
                adjacentsArray[idJ][d].add(idI);

                Route r = new Route(idI, idJ, ci.getDemand() +cj.getDemand());
                routesArray[idI][d] = r;
                routesArray[idJ][d] = r;
            }
        }
        //If one is unassigned and the other non-interal, connect it
        //i is unassigned, j is non-interal
        else if(routesArray[idI][d] == null && adjacentsArray[idJ][d] != null && adjacentsArray[idJ][d].isNonInternal()){
            if(ci.getDemand() + routesArray[idJ][d].routeDemand <= capacity) {
                adjacentsArray[idI][d] = new Adjecents(idI);
                adjacentsArray[idI][d].add(idJ);
                adjacentsArray[idJ][d].add(idI);
                routesArray[idJ][d].join(idJ, idI);
                routesArray[idI][d] = routesArray[idJ][d];
                internalArray[idJ][d] = true;
            }
        }
        //j is unassigned, i is non-interal
        else if(routesArray[idJ][d] == null && adjacentsArray[idI][d] != null &&  adjacentsArray[idI][d].isNonInternal()){
            if(cj.getDemand() + routesArray[idI][d].routeDemand <= capacity) {
                adjacentsArray[idJ][d] = new Adjecents(idJ);
                adjacentsArray[idJ][d].add(idI);
                adjacentsArray[idI][d].add(idJ);
                routesArray[idI][d].join(idI,idJ);
                routesArray[idJ][d] = routesArray[idI][d];
                internalArray[idI][d] = true;
            }
        }
        //If they are both non-internal
        else if (adjacentsArray[idI][d] != null && adjacentsArray[idJ][d] != null && adjacentsArray[idI][d].isNonInternal() && adjacentsArray[idJ][d].isNonInternal() && !routesArray[idI][d].equals(routesArray[idJ][d])) {
            if (routesArray[idI][d].routeDemand + routesArray[idJ][d].routeDemand <= capacity){
                adjacentsArray[idJ][d].add(idI);
                adjacentsArray[idI][d].add(idJ);

                // Find far end of I
                int farEndI = (routesArray[idI][d].begin == idI) ? routesArray[idI][d].end : routesArray[idI][d].begin;
                // Find far end of J
                int farEndJ = (routesArray[idJ][d].begin == idJ) ? routesArray[idJ][d].end : routesArray[idJ][d].begin;

                // Update the Route object to span from farEndI to farEndJ
                routesArray[farEndI][d].merge(routesArray[farEndJ][d], idI, idJ);
                routesArray[farEndJ][d] = routesArray[farEndI][d];

                //Note that we do not update the internal points, this is okay, because addition and merging happens at the non-internals
                //The final route constructions will have to
                internalArray[idI][d] = true;
                internalArray[idJ][d] = true;
            }
        }
    }

    public List<List<Customer>> makeSolution(int d){
        int previous;
        Adjecents next;
        List<List<Customer>> solutionRoutes = new ArrayList<>();

        for (int i = 0; i < customers.size(); i++) {
            // We only start building a route from an endpoint (non-internal)
            if (!internalArray[i][d]) {
                previous = i;
                List<Customer> routeCustomerList = new ArrayList<>();
                routeCustomerList.add(customers.get(previous));

                // At a solo point no adjacent object is made, so this allows for a seperate handling
                if(adjacentsArray[previous][d] == null){
                    internalArray[i][d] = true;
                    solutionRoutes.add(routeCustomerList);
                    continue;
                }

                int nextId = adjacentsArray[previous][d].left;

                next = adjacentsArray[nextId][d];

                while (!next.isNonInternal()) {
                    routeCustomerList.add(customers.get(next.id));

                    nextId = (next.left == previous) ? next.right : next.left;
                    previous = next.id;
                    next = adjacentsArray[nextId][d];
                }

                // Add the final endpoint of the chain
                routeCustomerList.add(customers.get(next.id));
                solutionRoutes.add(routeCustomerList);

                // Mark the OTHER endpoint as false so we don't start the route again from the other side
                internalArray[next.id][d] = true;
                //internalArray[i] = true; //Does the current need to be marked of?
            }
        }
        return solutionRoutes;
    }

    public Deque<Savings> tournament(Deque<Savings> queue){
        Deque<Savings> tournamentQueue = new ArrayDeque<>();
        while(!queue.isEmpty()){
            int tournamentSize = rand.nextInt(tournamentMax-tournamentMin) + tournamentMin;
            if (queue.size() < tournamentSize){
                tournamentSize = queue.size();
            }
            double rouletteSpin = rand.nextDouble();
            double totalSavings =0;
            Savings[] candidates = new Savings[tournamentSize];
            for (int i = 0; i < tournamentSize; i++) {
                candidates[i] = queue.removeFirst();
                totalSavings += candidates[i].savingsValue;
            }
            int i = 0;
            double cumProb = candidates[0].savingsValue/totalSavings;
            while(cumProb < rouletteSpin){
                i++;
                cumProb += candidates[i].savingsValue/totalSavings;
            }
            tournamentQueue.addLast(candidates[i]);
            for (int j = tournamentSize-1; 0 <= j; j--) {
                if (j!=i){
                    queue.addFirst(candidates[j]);
                }
            }
        }

        return tournamentQueue;
    }

    @Override
    public String getName() {
        return "Improved Clarke and Wright savings algorithm (Senne)";
    }

    @Override
    public boolean isExact() {
        return false;
    }
}
