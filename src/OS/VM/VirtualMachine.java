package OS.VM;

import OS.RM.Parser;
import OS.RM.RealCPU;
import OS.Tools.ChannelDevice;
import OS.Tools.Constants;
import OS.Tools.Word;

import java.util.ArrayList;
import java.util.HashMap;

public class VirtualMachine
{
    private CPU cpu = null;
    private ChannelDevice channelDevice;
    private Interpretator interpretator;

    public VirtualMachine(String sourceCode, RealCPU realCPU)
    {
        try {
            cpu = new CPU(realCPU);
            interpretator = new Interpretator(cpu);
            Parser parser = new Parser(sourceCode);
            channelDevice = new ChannelDevice();

            uploadDataSegment(parser.getDataSegment());
            uploadCodeSegment(parser.getCodeSegment());

            doYourMagic();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void doYourMagic() throws Exception {
        while (!cpu.getSI().equals(Constants.INTERRUPTION.HALT)){
            String command = cpu.getCSValue(cpu.getIC()).getASCIIFormat();
            interpretator.execute(command);
            cpu.increaseIC();
        }
    }

    private void uploadDataSegment(ArrayList<String> dataSegment) throws Exception {
        int i = 0;
        for (String data:dataSegment){
            cpu.setDS(new Word(i), new Word(data, Word.WORD_TYPE.NUMERIC));
            i++;
        }
    }

    private void uploadCodeSegment(ArrayList<String> codeSegment) throws Exception {
        int i = 0;
        for (String command:codeSegment){
            cpu.setCS(new Word(i), new Word(command, Word.WORD_TYPE.SYMBOLIC));
            i++;
        }
    }
}
