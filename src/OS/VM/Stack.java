package OS.VM;

import OS.RM.CPU;
import OS.Tools.Constants;

public class Stack {

    private final CPU cpu;

    public Stack(CPU cpu) {
        this.cpu = cpu;
    }

    public void Push() throws Exception {

        if(cpu.getVirtualSS(cpu.getSP()).getBlockFromAddress() != cpu.getSSB().getNumber())
        {
            cpu.setSI(Constants.SYSTEM_INTERRUPTION.LOADED_WRONG_SS_BLOCK);
            cpu.test();
        }
        cpu.setVirtualSSValue(cpu.getRL());
        cpu.increaseSP();
    }

    public void Pop() throws Exception {
        cpu.decreaseSP();
        if(cpu.getVirtualSS(cpu.getSP()).getBlockFromAddress() != cpu.getSSB().getNumber())
        {
            cpu.setSI(Constants.SYSTEM_INTERRUPTION.LOADED_WRONG_SS_BLOCK);
            cpu.test();
        }
        cpu.setRL(cpu.getVirtualSSValue());
    }
}
