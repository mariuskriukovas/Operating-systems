package OS.VM;

import OS.Tools.Word;

public class Stack {

    private final CPU cpu;

    Stack(CPU cpu) {
        this.cpu = cpu;
    }

    public void Push() throws Exception {
        cpu.setSSValue(cpu.getRL());
        cpu.increaseSP();
    }

    public void Pop() throws Exception {
        cpu.decreaseSP();
        cpu.setRL(cpu.getSSValue());
    }

    public Word getNthElement(int n) throws Exception {
        if (n > cpu.getSP().getNumber()) throw new Exception("NO ELEMENTS IN STACK");
        return cpu.getSSValue(n);
    }

}
