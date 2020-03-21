package OS.RM;

import OS.Tools.ChannelDevice;
import OS.Tools.Word;
import OS.VM.VirtualMachine;

import java.util.ArrayList;


public class RealMachine {

    static void print(Object e){
        System.out.println(e);
    }

    private ExternalMemory externalMemory;
    private InternalMemory internalMemory;
    private ArrayList<VirtualMachine> virtualMachines;
    private RealCPU realCPU;

    RealMachine(){
        externalMemory = new ExternalMemory();
        internalMemory = new InternalMemory();
        virtualMachines = new ArrayList<VirtualMachine>(10);
        try {
            realCPU = new RealCPU(internalMemory,externalMemory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        createVirtualMachine("prog.txt");

    }

    private void createVirtualMachine(String fileName){
        new VirtualMachine(fileName,realCPU);
    }

}
