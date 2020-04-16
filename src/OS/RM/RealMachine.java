package OS.RM;

import OS.RM.Process.JobToSwap;
import OS.Tools.Memory;
import OS.Tools.Word;
import OS.VM.VirtualMachine;
import UI.OSFrame;

import java.util.ArrayList;

import static OS.Tools.Constants.SYSTEM_MODE.SUPERVISOR_MODE;

public class RealMachine {

    static void print(Object e) {
        System.out.println(e);
    }

    private Memory externalMemory;
    private Memory internalMemory;
    private ArrayList<VirtualMachine> virtualMachines;
    private CPU cpu;
    private JobToSwap jobToSwap;

    private OSFrame screen;

    RealMachine() throws Exception {
        externalMemory = new Memory(65536, 256);
        internalMemory = new Memory(16, 256);
        virtualMachines = new ArrayList<VirtualMachine>(10);


        try {
            cpu = new CPU(internalMemory, externalMemory);
            jobToSwap = new JobToSwap(cpu);
        } catch (Exception e) {
            e.printStackTrace();
        }
        createVirtualMachine("prog.txt").doYourMagic();
    }

    //to be implemented
    private int findFreeInternalMemoryBlocks() {
        return 0;
    }

    private VirtualMachine createVirtualMachine(String fileName) {
        cpu.setMODE(SUPERVISOR_MODE);
        int internalBlockBegin = findFreeInternalMemoryBlocks();
        jobToSwap.uploadTaskToExternalMemory(fileName);
        int externalBlockBegin = jobToSwap.getTaskLocation(fileName);
        try {
            //int internalBlockBegin --> RL
            //int externalBlockBegin --> RH
            cpu.setRL(new Word(internalBlockBegin));
            cpu.setRH(new Word(externalBlockBegin));
            cpu.createMemoryTable();
            cpu.loadVirtualMachineMemory();
            System.out.println("Nuo" + " " + cpu.getPTRValue(0) + " iki " + cpu.getPTRValue(255));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new VirtualMachine(fileName, cpu);
    }

//----------------------------------------------------------------------------------

    // JM1256 IF (OLD_CS != NEW_CS) SI = 4 -> test()
    // AD12 -> test() if (SI + PI != 0 || TI == 0) MODE = 1
}
