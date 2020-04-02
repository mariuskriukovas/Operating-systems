package OS.VM;

import OS.RM.Parser;
import OS.RM.RealCPU;
import OS.Tools.ChannelDevice;
import OS.Tools.Constants;
import OS.Tools.Word;
import UI.OSFrame;
import UI.RMPanel;
import UI.VMPanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class VirtualMachine {
    private CPU cpu = null;
    private Interpretator interpretator;
    private RealCPU realCPU = null;

    private int currentDSBlock = 0;
    private int currentSSBlock = 0;
    private int currentCSBlock = 0;
    private int internalBlockBegin = 0;

    private OSFrame screen;

    public VirtualMachine(String sourceCode, RealCPU realCPU, int internalBlockBegin, OSFrame screen) {
        this.screen = screen;
        screen.getScreenForRealMachine().setIncButtonFunction(
                    new Callable<>() {
                        public Integer call() {
                            return doYourMagicStepByStep();
                        }
                    }
        );

        try {
            this.internalBlockBegin = internalBlockBegin;
            this.realCPU = realCPU;
            realCPU.loadVirtualMachineMemory(internalBlockBegin, currentCSBlock, currentDSBlock, currentSSBlock);

            cpu = new CPU(realCPU, screen.getScreenForVirtualMachine());
            interpretator = new Interpretator(cpu);
            Parser parser = new Parser(sourceCode);

            uploadDataSegment(parser.getDataSegment());
            uploadCodeSegment(parser.getCodeSegment());


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

    private Integer doYourMagicStepByStep() {
        if (!cpu.getSI().equals(Constants.INTERRUPTION.HALT)) {
            try {
                String command = cpu.getCSValue(cpu.getIC()).getASCIIFormat();
                interpretator.execute(command);
                cpu.increaseIC();
            }catch (Exception e){
                e.printStackTrace();
            }
            return 1;
        }
        else return -1;
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
}