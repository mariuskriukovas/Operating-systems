import Processes.ProcessPlaner;
import Processes.RealMachine;
import Resources.ResourceDistributor;

public class Main {

    public static void main(String[] args) {
        ProcessPlaner processPlaner = new ProcessPlaner();
        ResourceDistributor resourceDistributor = new ResourceDistributor(processPlaner);
        RealMachine realMachine = new RealMachine(processPlaner, resourceDistributor);
    }
}
