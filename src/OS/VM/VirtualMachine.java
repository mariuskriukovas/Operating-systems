package OS.VM;

import OS.RM.Process.Parser;
import OS.RM.CPU;
import OS.Tools.Constants;
import OS.Tools.Word;
import UI.OSFrame;
import OS.Tools.Constants.SYSTEM_INTERRUPTION;

import java.util.ArrayList;

public class VirtualMachine {

    private CPU cpu = null;
    private Interpretator interpretator;
    private final String processID;


    private OSFrame screen;

    public VirtualMachine(String ID, CPU cpu)
    {
//        this.screen = screen;
//        screen.getScreenForVirtualMachine().setIncButtonFunction(() -> doYourMagicStepByStep());
//        screen.getScreenForVirtualMachine().setNodeBugButtonFunction(() -> doYourMagic());

        processID = ID;
        try {
            this.cpu = cpu;
            interpretator = new Interpretator(cpu);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Integer doYourMagic(){
        try{
            while (!cpu.getSI().equals(SYSTEM_INTERRUPTION.HALT)) {
                check();
                String command = cpu.getVirtualCSValue(cpu.getIC()).getASCIIFormat();
                interpretator.execute(command);
                cpu.increaseIC();
            }
        }catch (Exception e)
        {
             e.printStackTrace();
            return -1;
        }
        return 1;
    }

    public Integer doYourMagicStepByStep() {
        if (!cpu.getSI().equals(SYSTEM_INTERRUPTION.HALT)) {
            try {
                check();
                String command = cpu.getVirtualCS(cpu.getIC()).getASCIIFormat();
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

    private void check(){
        try {
            if(cpu.getVirtualCS(cpu.getIC()).getBlockFromAddress() != cpu.getCSB().getNumber())
            {
                cpu.setSI(Constants.SYSTEM_INTERRUPTION.LOADED_WRONG_CS_BLOCK);
                cpu.test();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}