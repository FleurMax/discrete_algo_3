import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class ClarkeWrightAlgorithm implements VRPAlgorithm{

    private List<Customer> customers;

    private boolean[] internalArray;
    private Adjecents[] adjacentsArray;
    private Route[] routesArray;
    private int capacity;
    private class Savings{
        public int i;
        public int j;
        public double savingsValue;

        public Savings(int i, int j, double savingsValue) {
            this.i = i;
            this.j = j;
            this.savingsValue = savingsValue;
        }
    }

    private class Adjecents{
        public int left;
        public int right;
        public int id;

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
    @Override
    public List<List<Customer>> solve(VRPProblem problem) {

        List<List<Customer>> finalRoutes = new ArrayList<>(); //This will be the final route
        customers = problem.getCustomers(); //A list of all the customers in the problem
        capacity = problem.getCapacity();;
        internalArray = new boolean[customers.size()]; //An array that gives true if its internal. As internals never become noninternal, this is fast
        adjacentsArray = new Adjecents[customers.size()]; //An array that keeps a left and right adjacent point
        routesArray = new Route[customers.size()]; //An array that keeps track of what route each point is in.
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
        while(!savingsQueue.isEmpty()){
            update(savingsQueue.poll());
        }
        finalRoutes = makeSolution();

        return finalRoutes;

    }

    public void update(Savings s){
        int idI = s.i;
        int idJ = s.j;
        Customer ci = customers.get(idI);
        Customer cj =  customers.get(idJ);
        //If unassigned, assign them
        if(routesArray[idI] == null && routesArray[idJ] == null){
            if(ci.getDemand() + cj.getDemand() <= capacity){
                adjacentsArray[idI] = new Adjecents(idI);
                adjacentsArray[idI].add(idJ);
                adjacentsArray[idJ] = new Adjecents(idJ);
                adjacentsArray[idJ].add(idI);

                Route r = new Route(idI, idJ, ci.getDemand() +cj.getDemand());
                routesArray[idI] = r;
                routesArray[idJ] = r;
            }
        }
        //If one is unassigned and the other non-interal, connect it
        //i is unassigned, j is non-interal
        else if(routesArray[idI] == null && adjacentsArray[idJ] != null && adjacentsArray[idJ].isNonInternal()){
            if(ci.getDemand() + routesArray[idJ].routeDemand <= capacity) {
                adjacentsArray[idI] = new Adjecents(idI);
                adjacentsArray[idI].add(idJ);
                adjacentsArray[idJ].add(idI);
                routesArray[idJ].join(idJ, idI);
                routesArray[idI] = routesArray[idJ];
                internalArray[idJ] = true;
            }
        }
        //j is unassigned, i is non-interal
        else if(routesArray[idJ] == null && adjacentsArray[idI] != null &&  adjacentsArray[idI].isNonInternal()){
            if(cj.getDemand() + routesArray[idI].routeDemand <= capacity) {
                adjacentsArray[idJ] = new Adjecents(idJ);
                adjacentsArray[idJ].add(idI);
                adjacentsArray[idI].add(idJ);
                routesArray[idI].join(idI,idJ);
                routesArray[idJ] = routesArray[idI];
                internalArray[idI] = true;
            }
        }
        //If they are both non-internal
        else if (adjacentsArray[idI] != null && adjacentsArray[idJ] != null && adjacentsArray[idI].isNonInternal() && adjacentsArray[idJ].isNonInternal() && !routesArray[idI].equals(routesArray[idJ])) {
            if (routesArray[idI].routeDemand + routesArray[idJ].routeDemand <= capacity){
                adjacentsArray[idJ].add(idI);
                adjacentsArray[idI].add(idJ);

                // Find far end of I
                int farEndI = (routesArray[idI].begin == idI) ? routesArray[idI].end : routesArray[idI].begin;
                // Find far end of J
                int farEndJ = (routesArray[idJ].begin == idJ) ? routesArray[idJ].end : routesArray[idJ].begin;

                // Update the Route object to span from farEndI to farEndJ
                routesArray[farEndI].merge(routesArray[farEndJ], idI, idJ);
                routesArray[farEndJ] = routesArray[farEndI];

                //Note that we do not update the internal points, this is okay, because addition and merging happens at the non-internals
                //The final route constructions will have to
                internalArray[idI] = true;
                internalArray[idJ] = true;
            }
        }
    }

    public List<List<Customer>> makeSolution(){
        int previous;
        Adjecents next;
        List<List<Customer>> solutionRoutes = new ArrayList<>();

        for (int i = 0; i < customers.size(); i++) {
            // We only start building a route from an endpoint (non-internal)
            if (!internalArray[i]) {
                previous = i;
                List<Customer> routeCustomerList = new ArrayList<>();
                routeCustomerList.add(customers.get(previous));

                // At a solo point no adjacent object is made, so this allows for a seperate handling
                if(adjacentsArray[previous] == null){
                    internalArray[i] = true;
                    solutionRoutes.add(routeCustomerList);
                    continue;
                }
                int nextId = adjacentsArray[previous].left;


                next = adjacentsArray[nextId];

                while (!next.isNonInternal()) {
                    routeCustomerList.add(customers.get(next.id));

                    nextId = (next.left == previous) ? next.right : next.left;
                    previous = next.id;
                    next = adjacentsArray[nextId];
                }

                // Add the final endpoint of the chain
                routeCustomerList.add(customers.get(next.id));
                solutionRoutes.add(routeCustomerList);

                // Mark the OTHER endpoint as false so we don't start the route again from the other side
                internalArray[next.id] = true;
                //internalArray[i] = true; //Does the current need to be marked of?
            }
        }
        return solutionRoutes;
    }

    @Override
    public String getName() {
        return "Clarke and Wright savings algorithm (Senne)";
    }

    @Override
    public boolean isExact() {
        return false;
    }
}
