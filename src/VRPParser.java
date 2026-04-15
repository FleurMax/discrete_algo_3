import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VRPParser {

    /**
     * Parses a CVRP instance file in standard TSPLIB/VRPLIB format.
     * @param filepath The path to the .vrp file.
     * @return A VRPProblem instance.
     * @throws IOException If the file cannot be read.
     */
    public static VRPProblem parseFile(String filepath) throws IOException {
        String name = "Unknown";
        int dimension = 0;
        int capacity = 0;
        
        List<double[]> coords = new ArrayList<>();
        List<Integer> demands = new ArrayList<>();
        int depotId = 1; // Default to 1

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            String section = "";

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("NAME")) {
                    name = line.split(":")[1].trim();
                } else if (line.startsWith("DIMENSION")) {
                    dimension = Integer.parseInt(line.split(":")[1].trim());
                    for (int i = 0; i <= dimension; i++) {
                        coords.add(new double[]{0, 0});
                        demands.add(0);
                    }
                } else if (line.startsWith("CAPACITY")) {
                    capacity = Integer.parseInt(line.split(":")[1].trim());
                } else if (line.startsWith("NODE_COORD_SECTION")) {
                    section = "COORD";
                } else if (line.startsWith("DEMAND_SECTION")) {
                    section = "DEMAND";
                } else if (line.startsWith("DEPOT_SECTION")) {
                    section = "DEPOT";
                } else if (line.startsWith("EOF")) {
                    break;
                } else {
                    if (section.equals("COORD")) {
                        String[] parts = line.split("\\s+");
                        int id = Integer.parseInt(parts[0]);
                        double x = Double.parseDouble(parts[1]);
                        double y = Double.parseDouble(parts[2]);
                        coords.set(id, new double[]{x, y});
                    } else if (section.equals("DEMAND")) {
                        String[] parts = line.split("\\s+");
                        int id = Integer.parseInt(parts[0]);
                        int dem = Integer.parseInt(parts[1]);
                        demands.set(id, dem);
                    } else if (section.equals("DEPOT")) {
                        int id = Integer.parseInt(line.trim());
                        if (id != -1) {
                            depotId = id;
                        }
                    }
                }
            }
        }

        Customer depot = new Customer(depotId, coords.get(depotId)[0], coords.get(depotId)[1], demands.get(depotId));
        List<Customer> customers = new ArrayList<>();
        for (int i = 1; i <= dimension; i++) {
            if (i != depotId) {
                customers.add(new Customer(i, coords.get(i)[0], coords.get(i)[1], demands.get(i)));
            }
        }

        return new VRPProblem(name, capacity, depot, customers);
    }
}
