import java.util.List;

/**
 * Represents an instance of the Capacitated Vehicle Routing Problem (CVRP).
 */
public class VRPProblem {
    private final String name;
    private final int capacity;
    private final Customer depot;
    private final List<Customer> customers;

    public VRPProblem(String name, int capacity, Customer depot, List<Customer> customers) {
        this.name = name;
        this.capacity = capacity;
        this.depot = depot;
        this.customers = customers;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public Customer getDepot() {
        return depot;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    /**
     * Helper method to compute the cost of a full solution.
     * @param routes List of routes, where each route is a list of customers in visited order.
     * @return Total distance of all routes.
     */
    public double calculateTotalCost(List<List<Customer>> routes) {
        if (routes == null) return Double.POSITIVE_INFINITY;
        
        double totalCost = 0;
        for (List<Customer> route : routes) {
            if (route.isEmpty()) continue;
            
            Customer current = depot;
            for (Customer c : route) {
                totalCost += current.distanceTo(c);
                current = c;
            }
            totalCost += current.distanceTo(depot);
        }
        return totalCost;
    }

    /**
     * Validates if a solution respects capacity constraints and visits all customers exactly once.
     * @param routes The proposed solution.
     * @return true if valid, false otherwise.
     */
    public boolean isValidSolution(List<List<Customer>> routes) {
        if (routes == null) return false;

        boolean[] visited = new boolean[customers.size() + 1]; // assuming IDs are 1 to N, depot is 0 or 1
        
        for (List<Customer> route : routes) {
            int routeDemand = 0;
            for (Customer c : route) {
                routeDemand += c.getDemand();
                if (c.getId() >= 0 && c.getId() < visited.length) {
                    if (visited[c.getId()]) {
                        System.err.println("Customer " + c.getId() + " visited multiple times.");
                        return false; 
                    }
                    visited[c.getId()] = true;
                }
            }
            if (routeDemand > capacity) {
                System.err.println("Route exceeded capacity: " + routeDemand + " > " + capacity);
                return false;
            }
        }

        for (Customer c : customers) {
            if (c.getId() >= 0 && c.getId() < visited.length && !visited[c.getId()]) {
                System.err.println("Customer " + c.getId() + " was not visited.");
                return false;
            }
        }
        
        return true;
    }
}
