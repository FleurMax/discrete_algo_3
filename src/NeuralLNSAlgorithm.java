import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Implementation of Neural Large Neighborhood Search (NLNS) for CVRP.
 * Refined to follow Sequential Connection logic and optimized for speed.
 */
public class NeuralLNSAlgorithm implements VRPAlgorithm {

    private final Random random = new Random();

    @Override
    public List<List<Customer>> solve(VRPProblem problem) {
        if (problem.getCustomers().isEmpty()) return new ArrayList<>();

        List<List<Customer>> pi = initialSolution(problem);
        List<List<Customer>> piStar = deepCopy(pi);
        
        double piStarCost = problem.calculateTotalCost(piStar);
        double ts = piStarCost * 0.05; 
        double tr = ts;
        double tm = ts * 0.001; 
        double delta = 0.98; // Slightly faster cooling
        int Z = 20; 
        int batchSize = 16; 

        double t = ts;
        long startTime = System.currentTimeMillis();
        int maxRuntime = (int) (500 * problem.getCustomers().size()); // Dynamic timeout
        if (maxRuntime > 60000) maxRuntime = 60000;
        if (maxRuntime < 10000) maxRuntime = 10000;

        int patience = 500;
        int unstuckCounter = 0;

        while (System.currentTimeMillis() - startTime < maxRuntime && !Thread.currentThread().isInterrupted()) {
            List<List<List<Customer>>> B = new ArrayList<>();
            for (int i = 0; i < batchSize; i++) {
                B.add(deepCopy(pi));
            }

            while (t > tm && !Thread.currentThread().isInterrupted()) {
                List<List<List<Customer>>> nextB = new ArrayList<>();
                List<List<Customer>> pib = null;
                double pibCost = Double.MAX_VALUE;

                for (int i = 0; i < batchSize; i++) {
                    List<List<Customer>> solution = B.get(i);
                    int destroyType = random.nextInt(2); 
                    
                    List<Customer> removed = new ArrayList<>();
                    List<List<Customer>> destroyed = destroy(solution, removed, problem, destroyType);
                    List<List<Customer>> repaired = repairSequential(destroyed, removed, problem);
                    
                    nextB.add(repaired);
                    double cost = problem.calculateTotalCost(repaired);
                    if (cost < pibCost) {
                        pibCost = cost;
                        pib = repaired;
                    }
                }

                double piCost = problem.calculateTotalCost(pi);
                if (pib != null && accept(pibCost, piCost, t)) {
                    pi = pib;
                    if (pibCost < piStarCost) {
                        piStar = deepCopy(pib);
                        piStarCost = pibCost;
                        unstuckCounter = 0;
                    }
                }
                
                unstuckCounter++;
                if (unstuckCounter > patience) break; 

                B = nextB;
                int zCount = Math.max(1, (int) (batchSize * (Z / 100.0)));
                for (int i = 0; i < zCount; i++) {
                    B.set(i, deepCopy(pi));
                }
                t *= delta;
            }
            if (unstuckCounter > patience) break;
            t = tr; 
        }

        return piStar;
    }

    private boolean accept(double candidateCost, double currentCost, double temperature) {
        if (candidateCost < currentCost) return true;
        return random.nextDouble() < Math.exp(-(candidateCost - currentCost) / temperature);
    }

    private List<List<Customer>> initialSolution(VRPProblem problem) {
        List<List<Customer>> routes = new ArrayList<>();
        List<Customer> unvisited = new ArrayList<>(problem.getCustomers());
        while (!unvisited.isEmpty()) {
            List<Customer> route = new ArrayList<>();
            Customer cur = problem.getDepot();
            int cap = problem.getCapacity();
            while (true) {
                Customer nearest = null;
                double minDist = Double.MAX_VALUE;
                for (Customer c : unvisited) {
                    if (c.getDemand() <= cap) {
                        double d = cur.distanceTo(c);
                        if (d < minDist) { minDist = d; nearest = c; }
                    }
                }
                if (nearest == null) break;
                route.add(nearest);
                unvisited.remove(nearest);
                cap -= nearest.getDemand();
                cur = nearest;
            }
            routes.add(route);
        }
        return routes;
    }

    private List<List<Customer>> destroy(List<List<Customer>> solution, List<Customer> removed, VRPProblem problem, int type) {
        int n = problem.getCustomers().size();
        int dLimit = (int) (random.nextDouble() * 0.15 * n) + 2; 
        List<List<Customer>> partial = deepCopy(solution);

        if (type == 0) { // Shaw Relatedness (Simplified)
            List<Customer> flat = new ArrayList<>();
            for (List<Customer> r : partial) flat.addAll(r);
            if (flat.isEmpty()) return partial;
            Customer seed = flat.get(random.nextInt(flat.size()));
            flat.sort(Comparator.comparingDouble(c -> 
                Math.hypot(c.getX() - seed.getX(), c.getY() - seed.getY()) + 
                Math.abs(c.getDemand() - seed.getDemand()) * 0.5));
            for (int i = 0; i < Math.min(dLimit, flat.size()); i++) {
                final Customer toRemove = flat.get(i);
                removed.add(toRemove);
                for (List<Customer> r : partial) r.removeIf(c -> c.getId() == toRemove.getId());
            }
        } else { // Tour-based
            Customer ref = problem.getCustomers().get(random.nextInt(n));
            partial.sort(Comparator.comparingDouble(r -> {
                if (r.isEmpty()) return Double.MAX_VALUE;
                double sx=0, sy=0; for(Customer c:r){sx+=c.getX();sy+=c.getY();}
                return Math.hypot(sx/r.size() - ref.getX(), sy/r.size() - ref.getY());
            }));
            int remCount = 0;
            while (!partial.isEmpty() && remCount < dLimit) {
                List<Customer> r = partial.remove(0);
                removed.addAll(r);
                remCount += r.size();
            }
        }
        partial.removeIf(List::isEmpty);
        return partial;
    }

    /**
     * Sequential Regret-2 Repair with segment flipping.
     */
    private List<List<Customer>> repairSequential(List<List<Customer>> partial, List<Customer> removed, VRPProblem problem) {
        List<CustomerSegment> segments = new ArrayList<>();
        for (List<Customer> r : partial) segments.add(new CustomerSegment(r, r.get(0), r.get(r.size() - 1), 3));
        for (Customer c : removed) segments.add(new CustomerSegment(Collections.singletonList(c), c, c, 1));

        List<List<Customer>> finalRoutes = new ArrayList<>();

        while (!segments.isEmpty()) {
            CustomerSegment refSeg = null;
            double maxRegret = -1.0;
            int bestPartnerIdx = -1;
            int refSegIdx = -1;
            boolean shouldFlipPartner = false;

            for (int i = 0; i < segments.size(); i++) {
                CustomerSegment s = segments.get(i);
                double best = Double.MAX_VALUE, secondBest = Double.MAX_VALUE;
                int localBestIdx = -1;
                boolean localFlip = false;

                for (int j = 0; j < segments.size(); j++) {
                    if (i == j) continue;
                    CustomerSegment p = segments.get(j);
                    if (s.demand + p.demand > problem.getCapacity()) continue;

                    // Try connecting s.end to p.start
                    double d1 = s.end.distanceTo(p.start);
                    if (d1 < best) { secondBest = best; best = d1; localBestIdx = j; localFlip = false; }
                    else if (d1 < secondBest) { secondBest = d1; }

                    // Try connecting s.end to p.end (flipping p)
                    if (p.nodes.size() > 1) {
                        double d2 = s.end.distanceTo(p.end);
                        if (d2 < best) { secondBest = best; best = d2; localBestIdx = j; localFlip = true; }
                        else if (d2 < secondBest) { secondBest = d2; }
                    }
                }

                double depotDist = s.end.distanceTo(problem.getDepot());
                if (depotDist < best) { secondBest = best; best = depotDist; localBestIdx = -2; }
                else if (depotDist < secondBest) { secondBest = depotDist; }

                double regret = (secondBest == Double.MAX_VALUE) ? 0 : (secondBest - best);
                if (regret > maxRegret) {
                    maxRegret = regret;
                    refSeg = s;
                    refSegIdx = i;
                    bestPartnerIdx = localBestIdx;
                    shouldFlipPartner = localFlip;
                }
            }

            if (refSeg == null) break; 

            segments.remove(refSegIdx);
            if (bestPartnerIdx == -2) {
                finalRoutes.add(new ArrayList<>(refSeg.nodes));
            } else {
                CustomerSegment partner = segments.remove(bestPartnerIdx < refSegIdx ? bestPartnerIdx : bestPartnerIdx - 1);
                if (shouldFlipPartner) Collections.reverse(partner.nodes);
                refSeg.nodes.addAll(partner.nodes);
                refSeg.updateDemand();
                refSeg.end = refSeg.nodes.get(refSeg.nodes.size() - 1);
                segments.add(refSeg);
            }
        }
        return finalRoutes;
    }

    private static class CustomerSegment {
        List<Customer> nodes;
        Customer start, end;
        int demand, status;
        CustomerSegment(List<Customer> nodes, Customer s, Customer e, int status) {
            this.nodes = new ArrayList<>(nodes); this.start = s; this.end = e; this.status = status;
            updateDemand();
        }
        void updateDemand() { demand = nodes.stream().mapToInt(Customer::getDemand).sum(); }
    }

    private List<List<Customer>> deepCopy(List<List<Customer>> original) {
        List<List<Customer>> copy = new ArrayList<>();
        for (List<Customer> r : original) copy.add(new ArrayList<>(r));
        return copy;
    }

    @Override public String getName() { return "Neural LNS (Sequential)"; }
    @Override public boolean isExact() { return false; }
}
