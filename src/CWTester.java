import java.util.ArrayList;
import java.util.List;

public class CWTester {
    public List<VRPAlgorithm> tournamentSizeTest(int minimum, int testRange, String s){
        List<VRPAlgorithm> algorithms = new ArrayList<>();

        for (int i = 0; i < testRange; i++) {
            if (s.equals("ICW")) {
                algorithms.add(new ImprovedClarkeWrightAlgorithm.Builder().tournamentMin(minimum).tournamentMax(minimum + i).build());
            }
            if (s.equals("MICW")){
                algorithms.add(new ModifiedImprovedClarkeWrightAlgorithm.Builder().tournamentMin(minimum).tournamentMax(minimum + i).build());
            }
        }
        return algorithms;
    }

    public List<VRPAlgorithm> depthTest(double consecutiveFraction, int testRange, String s){
        List<VRPAlgorithm> algorithms = new ArrayList<>();
        for (int i = 2; i < testRange; i++) {
            int cd = (int) (i*consecutiveFraction);
            if(cd == 0){cd = 1;}
            if (s.equals("ICW")) {
                algorithms.add(new ImprovedClarkeWrightAlgorithm.Builder().depth(i).consecutiveDepth(cd).build());
            }
            if (s.equals("MICW")){
                algorithms.add(new ModifiedImprovedClarkeWrightAlgorithm.Builder().depth(i).consecutiveDepth(cd).build());
            }
        }
        return algorithms;
    }
}
