package OS.VM;

public class Stack {

    private final Memory memory;
    private final CPU cpu;

    Stack(Memory memory, CPU cpu)
    {
        this.memory = memory;
        this.cpu = cpu;
    }

    public void Push() throws Exception {

        memory.setWord(cpu.getRL(),cpu.getSP());
        cpu.increaseSP();
    }

    public void Pop() throws Exception {
        cpu.decreaseSP();
        cpu.setRL(memory.getWord(cpu.getSP()));
    }

    public Word getNthElement(int n) throws Exception {
        Word sp = cpu.getSP();
        return memory.getWord(sp.add(n));
    }

}
