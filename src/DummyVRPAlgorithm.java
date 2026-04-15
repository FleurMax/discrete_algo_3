import java.util.ArrayList;
import java.util.List;

public class DummyVRPAlgorithm implements VRPAlgorithm {

    @Override
    public List<List<Customer>> solve(VRPProblem problem) {
        List<List<Customer>> routes = new ArrayList<>();
        List<Customer> customers = new ArrayList<>(problem.getCustomers());
        
        // Very simple dummy algorithm: one customer per route
        for (Customer c : customers) {
            if (Thread.currentThread().isInterrupted()) return null;
            
            List<Customer> route = new ArrayList<>();
            route.add(c);
            routes.add(route);
        }
        
        return routes;
    }

    @Override
    public String getName() {
        return "Dummy Algorithm (1 route/customer)";
    }

    @Override
    public boolean isExact() {
        return false;
    }
}
