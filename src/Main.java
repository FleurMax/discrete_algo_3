import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting VRP Benchmarks...");

        List<VRPAlgorithm> algorithms = new ArrayList<>();

        // Add your implemented algorithms here
        // algorithms.add(new DummyVRPAlgorithm());
        // algorithms.add(new ClarkeWrightAlgorithm());
        // algorithms.add(new RestrictedDPAlgorithm());
        // algorithms.add(new AntColonyAlgorithm());
        // algorithms.add(new IteratedVNDAlgorithm());
        algorithms.add(new NeuralLNSAlgorithm());

        BenchmarkRunner runner = new BenchmarkRunner();

        // Ensure you have .vrp files in the 'test instances' folder.
        // Download CVRP instances from CVRPLIB (e.g. Set A, Set B, etc.)
        runner.run(algorithms, "test instances");
    }
}
