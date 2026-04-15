import java.util.List;

/**
 * Common interface that every Vehicle Routing Problem algorithm must implement.
 *
 * <p>
 * Usage pattern:
 * 
 * <pre>
 * VRPAlgorithm algo = new MyAlgorithm();
 * List<List<Customer>> routes = algo.solve(problem);
 * System.out.println(algo.getName() + " found solution with " + routes.size() + " routes.");
 * </pre>
 *
 * <p>
 * Implementations should periodically check {@code Thread.currentThread().isInterrupted()}
 * to ensure that the algorithm can be terminated by the benchmarker if a timeout occurs.
 */
public interface VRPAlgorithm {

    /**
     * Solves the Vehicle Routing Problem for the given instance.
     *
     * <p>
     * The algorithm must service all customer demands without exceeding vehicle capacities,
     * starting and ending at the depot.
     *
     * @param problem The VRP instance containing depot, customers, and capacity.
     * @return A list of routes, where each route is a list of {@link Customer}s serviced by a vehicle.
     */
    List<List<Customer>> solve(VRPProblem problem);

    /**
     * Human-readable name of this algorithm, used in benchmark reports.
     * Examples: {@code "Clarke and Wright Savings"}, {@code "Ant Colony System"}.
     *
     * @return Non-null, non-empty string.
     */
    String getName();

    /**
     * Indicates whether this algorithm guarantees an optimal solution.
     *
     * <ul>
     * <li>{@code true} — exact algorithm (e.g. Branch and Cut).</li>
     * <li>{@code false} — heuristic or metaheuristic; may return a near-optimal solution.</li>
     * </ul>
     *
     * @return {@code true} for exact algorithms, {@code false} for heuristics.
     */
    boolean isExact();
}
