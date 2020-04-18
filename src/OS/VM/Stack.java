package OS.VM;

import OS.RM.CPU;
import OS.Tools.Constants;
import OS.Tools.Word;

public class Stack {

    private final CPU cpu;

    public Stack(CPU cpu) {
        this.cpu = cpu;
    }

    //    RL ---> Stack
    public void Push() throws Exception {

        System.out.println("Rl ---------------------> " + cpu.getRL());
        //    Word address, ---> RL
        //    Word value  ---> RH
        cpu.setRH(new Word(cpu.getRL().getNumber()));
        cpu.setRL(new Word(cpu.getSP().getNumber()));
        cpu.getSwapping().SETSS();
        cpu.increaseSP();
    }

    // Stack ---> RL
    public void Pop() throws Exception {
        cpu.decreaseSP();

        //    Word address, ---> RL
        //    RL ---> value
        cpu.setRL(new Word(cpu.getSP().getNumber()));
        cpu.getSwapping().GETSS();
//        cpu.setRL(cpu.getVirtualSSValue());
    }
}
