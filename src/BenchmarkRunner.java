import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class BenchmarkRunner {

    private static final int TIMEOUT_SECONDS = 60;

    public void run(List<VRPAlgorithm> algorithms, String instancesDirPath) {
        File dir = new File(instancesDirPath);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".vrp"));
        
        if (files == null || files.length == 0) {
            System.err.println("No .vrp files found in " + instancesDirPath);
            return;
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String resultFile = "results/benchmark_results_" + timestamp + ".txt";

        try {
            Files.createDirectories(Paths.get("results"));
        } catch (IOException e) {
            System.err.println("Could not create results directory.");
        }

        try (FileWriter writer = new FileWriter(resultFile)) {
            writer.write("VRP Benchmark Results\n");
            writer.write("=====================\n\n");
            
            for (File file : files) {
                writer.write("Instance: " + file.getName() + "\n");
                System.out.println("Processing " + file.getName() + "...");
                
                VRPProblem problem;
                try {
                    problem = VRPParser.parseFile(file.getAbsolutePath());
                } catch (Exception e) {
                    writer.write("  -> Failed to parse\n\n");
                    continue;
                }
                
                writer.write("  Dimension: " + (problem.getCustomers().size() + 1) + ", Capacity: " + problem.getCapacity() + "\n");
                
                for (VRPAlgorithm algo : algorithms) {
                    System.out.println("  Running " + algo.getName() + "...");
                    
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    Future<List<List<Customer>>> future = executor.submit(() -> algo.solve(problem));
                    
                    long startTime = System.nanoTime();
                    List<List<Customer>> solution = null;
                    boolean timeout = false;
                    
                    try {
                        solution = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                    } catch (TimeoutException e) {
                        timeout = true;
                        future.cancel(true);
                    } catch (Exception e) {
                        writer.write(String.format("  %-25s : ERROR (%s)\n", algo.getName(), e.getMessage()));
                        executor.shutdownNow();
                        continue;
                    }
                    
                    long endTime = System.nanoTime();
                    double timeMs = (endTime - startTime) / 1_000_000.0;
                    
                    executor.shutdownNow();
                    
                    if (timeout) {
                        writer.write(String.format("  %-25s : TIMEOUT (>%ds)\n", algo.getName(), TIMEOUT_SECONDS));
                    } else if (solution == null || !problem.isValidSolution(solution)) {
                        writer.write(String.format("  %-25s : INVALID SOLUTION\n", algo.getName()));
                    } else {
                        double cost = problem.calculateTotalCost(solution);
                        writer.write(String.format("  %-25s : Cost = %.2f, Time = %.2f ms\n", algo.getName(), cost, timeMs));
                    }
                }
                writer.write("\n");
            }
            System.out.println("Benchmarking finished. Results saved to " + resultFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
