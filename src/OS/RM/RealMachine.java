package OS.RM;

import OS.Tools.Memory;

public class RealMachine {

    private Memory externalMemory;
    private Memory internalMemory;
    private CPU cpu;


    RealMachine() throws Exception {
        externalMemory = new Memory(65536, 256);
        internalMemory = new Memory(16, 256);
        try {
            cpu = new CPU(internalMemory, externalMemory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
