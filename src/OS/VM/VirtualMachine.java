package OS.VM;

import OS.RM.Parser;
import OS.RM.RealCPU;
import OS.Tools.ChannelDevice;
import OS.Tools.Constants;
import OS.Tools.Word;

import java.util.ArrayList;
import java.util.HashMap;

public class VirtualMachine {
    private CPU cpu = null;
    private Interpretator interpretator;
    private RealCPU realCPU = null;

    private int currentDSBlock = 0;
    private int currentSSBlock = 0;
    private int currentCSBlock = 0;
    private int internalBlockBegin = 0;

    public VirtualMachine(String sourceCode, RealCPU realCPU, int internalBlockBegin) {
        try {
            this.internalBlockBegin = internalBlockBegin;
            this.realCPU = realCPU;
            realCPU.loadVirtualMachineMemory(internalBlockBegin, currentCSBlock, currentDSBlock, currentSSBlock);

            cpu = new CPU(realCPU);
            interpretator = new Interpretator(cpu);
            Parser parser = new Parser(sourceCode);

            uploadDataSegment(parser.getDataSegment());
            uploadCodeSegment(parser.getCodeSegment());

            doYourMagic();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCurrentBlocks(int currentDSBlock, int currentSSBlock, int currentCSBlock) {
        this.currentDSBlock = currentDSBlock;
        this.currentSSBlock = currentSSBlock;
        this.currentCSBlock = currentCSBlock;
    }

    private void doYourMagic() throws Exception {
        while (!cpu.getSI().equals(Constants.INTERRUPTION.HALT)) {
            String command = cpu.getCSValue(cpu.getIC()).getASCIIFormat();
            interpretator.execute(command);
            cpu.increaseIC();
        }
    }

    private void uploadDataSegment(ArrayList<String> dataSegment) throws Exception {
        int i = 0;
        for (String data : dataSegment) {
            cpu.setDS(new Word(i), new Word(data, Word.WORD_TYPE.NUMERIC));
            i++;
        }
    }

    private void uploadCodeSegment(ArrayList<String> codeSegment) throws Exception {
        int i = 0;
        for (String command : codeSegment) {
            cpu.setCS(new Word(i), new Word(command, Word.WORD_TYPE.SYMBOLIC));
            i++;
        }
    }

    public CPU getCpu() {
        return cpu;
    }

    public void setCpu(CPU cpu) {
        this.cpu = cpu;
    }

    public Interpretator getInterpretator() {
        return interpretator;
    }

    public void setInterpretator(Interpretator interpretator) {
        this.interpretator = interpretator;
    }

    public RealCPU getRealCPU() {
        return realCPU;
    }

    public void setRealCPU(RealCPU realCPU) {
        this.realCPU = realCPU;
    }

    public int getCurrentDSBlock() {
        return currentDSBlock;
    }

    public void setCurrentDSBlock(int currentDSBlock) {
        this.currentDSBlock = currentDSBlock;
    }

    public int getCurrentSSBlock() {
        return currentSSBlock;
    }

    public void setCurrentSSBlock(int currentSSBlock) {
        this.currentSSBlock = currentSSBlock;
    }

    public int getCurrentCSBlock() {
        return currentCSBlock;
    }

    public void setCurrentCSBlock(int currentCSBlock) {
        this.currentCSBlock = currentCSBlock;
    }

    public int getInternalBlockBegin() {
        return internalBlockBegin;
    }

    public void setInternalBlockBegin(int internalBlockBegin) {
        this.internalBlockBegin = internalBlockBegin;
    }
}
