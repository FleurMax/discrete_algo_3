import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting VRP Benchmarks...");

        List<VRPAlgorithm> algorithms = new ArrayList<>();

        // Add your implemented algorithms here
        //algorithms.add(new DummyVRPAlgorithm());
        //algorithms.add(new ClarkeWrightAlgorithm());
        //algorithms.add(new ImprovedClarkeWrightAlgorithm.Builder().build());
        //algorithms.add(new ModifiedClarkeWrightAlgorithm.Builder().build());
        //algorithms.add(new ModifiedImprovedClarkeWrightAlgorithm.Builder().build());
        //algorithms.add(new RestrictedDPAlgorithm());
        // algorithms.add(new AntColonyAlgorithm());
        //algorithms.add(new IVNDAlgorithm());
        algorithms.add(new NeuralLNSAlgorithm());

        BenchmarkRunner runner = new BenchmarkRunner();

        // Ensure you have .vrp files in the 'test instances' folder.
        runner.run(algorithms, "test instances");


        // The following is a separate test for parameters of the ICW algorithm
        StatisticalBenchmarkRunner statRunner = new StatisticalBenchmarkRunner();
        CWTester cwTester = new CWTester();
        List<VRPAlgorithm> statAlgorithms = new ArrayList<>();


        //statAlgorithms = cwTester.depthTest(0.1,10000);
        statAlgorithms.add(new ModifiedImprovedClarkeWrightAlgorithm.Builder().depth(100000).consecutiveDepth(50000).build());
        statRunner.run(statAlgorithms, "test instances", 25);
    }
}
