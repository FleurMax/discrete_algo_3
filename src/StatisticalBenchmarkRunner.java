import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class StatisticalBenchmarkRunner {

    private static final int TIMEOUT_SECONDS = 60;

    public void run(List<VRPAlgorithm> algorithms, String instancesDirPath, int k) {
        File dir = new File(instancesDirPath);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".vrp"));

        if (files == null || files.length == 0) {
            System.err.println("No .vrp files found in " + instancesDirPath);
            return;
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String resultFile = "results/stat_benchmark_" + timestamp + ".txt";

        try {
            Files.createDirectories(Paths.get("results"));
            try (FileWriter writer = new FileWriter(resultFile)) {
                writer.write("VRP Statistical Benchmark (k = " + k + " runs per algorithm)\n");
                writer.write("=========================================================\n\n");

                for (File file : files) {
                    processInstance(file, algorithms, k, writer);
                }
                System.out.println("Benchmarking finished. Results saved to " + resultFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processInstance(File file, List<VRPAlgorithm> algorithms, int k, FileWriter writer) throws IOException {
        writer.write("Instance: " + file.getName() + "\n");
        System.out.println("Processing " + file.getName() + "...");

        VRPProblem problem;
        try {
            problem = VRPParser.parseFile(file.getAbsolutePath());
        } catch (Exception e) {
            writer.write("  -> Failed to parse\n\n");
            return;
        }

        for (VRPAlgorithm algo : algorithms) {
            System.out.println("  Running " + algo.getName() + " [" + k + " times]...");
            List<Double> costs = new ArrayList<>();
            List<Double> times = new ArrayList<>();

            for (int i = 0; i < k; i++) {
                runSingleIteration(algo, problem, costs, times);
            }

            writeStats(writer, algo.getName(), costs, times);
        }
        writer.write("\n");
    }

    private void runSingleIteration(VRPAlgorithm algo, VRPProblem problem, List<Double> costs, List<Double> times) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        long startTime = System.nanoTime();
        try {
            Future<List<List<Customer>>> future = executor.submit(() -> algo.solve(problem));
            List<List<Customer>> solution = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            long endTime = System.nanoTime();

            if (solution != null && problem.isValidSolution(solution)) {
                costs.add(problem.calculateTotalCost(solution));
                times.add((endTime - startTime) / 1_000_000.0);
            }
        } catch (Exception e) {
            // Log error or timeout if necessary
        } finally {
            executor.shutdownNow();
        }
    }

    private void writeStats(FileWriter writer, String algoName, List<Double> costs, List<Double> times) throws IOException {
        if (costs.isEmpty()) {
            writer.write(String.format("  %-25s : FAILED ALL RUNS\n", algoName));
            return;
        }

        Stats costStats = new Stats(costs);
        Stats timeStats = new Stats(times);

        writer.write("  Algorithm: " + algoName + "\n");
        writer.write(String.format("    Cost -> Avg: %.2f, Min: %.2f, Max: %.2f, StdDev: %.2f\n",
                costStats.mean, costStats.min, costStats.max, costStats.stdDev));
        writer.write(String.format("    Time -> Avg: %.2f ms, Min: %.2f ms, Max: %.2f ms\n",
                timeStats.mean, timeStats.min, timeStats.max));
    }

    // Helper class for math
    private static class Stats {
        double mean, min, max, stdDev;

        Stats(List<Double> data) {
            double sum = 0;
            min = Double.MAX_VALUE;
            max = Double.MIN_VALUE;

            for (double val : data) {
                sum += val;
                if (val < min) min = val;
                if (val > max) max = val;
            }
            mean = sum / data.size();

            double variance = 0;
            for (double val : data) {
                variance += Math.pow(val - mean, 2);
            }
            stdDev = Math.sqrt(variance / data.size());
        }
    }
}