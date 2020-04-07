package OS.RM;

import OS.RM.Process.JobToSwap;
import OS.Tools.Constants;
import OS.Tools.Memory;
import OS.Tools.Word;
import OS.VM.Stack;
import OS.VM.VirtualMachine;
import UI.OSFrame;

import java.util.ArrayList;

import static OS.Tools.Constants.SYSTEM_MODE.*;


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

    RealMachine(OSFrame screen) {
        this.screen = screen;
        externalMemory = new Memory(65536, 256);
        internalMemory = new Memory(16,256);
        virtualMachines = new ArrayList<VirtualMachine>(10);
        screen.setVisible(true);
        screen.setReady(true);

        try {
            cpu = new CPU(internalMemory, externalMemory, screen);
            jobToSwap = new JobToSwap(cpu);
        } catch (Exception e) {
            e.printStackTrace();
        }
        createVirtualMachine("prog.txt").doYourMagic();
//        virtualMachines.add(createVirtualMachine("prog.txt"));
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

        return new VirtualMachine(fileName,cpu);
    }

//----------------------------------------------------------------------------------

    // JM1256 IF (OLD_CS != NEW_CS) SI = 4 -> test()
    // AD12 -> test() if (SI + PI != 0 || TI == 0) MODE = 1

}
