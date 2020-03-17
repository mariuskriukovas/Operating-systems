package OS.VM;

import java.util.ArrayList;
import java.util.HashMap;

public class VirtualMachine
{
    private CPU cpu = null;
    private Memory memory = null;
    private ChannelDevice channelDevice;
    private Interpretator interpretator;

    private HashMap<String, String> commandDictionary = null;

    VirtualMachine(String sourceCode)
    {
        try {
            memory = new Memory();
            cpu = new CPU(memory);
            interpretator = new Interpretator(cpu,memory);
            Parser parser = new Parser(sourceCode);
            commandDictionary = parser.getCommands();
            channelDevice = new ChannelDevice();

            uploadDataSegment();
            uploadCodeSegment();

            doYourMagic();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void doYourMagic() throws Exception {
        HashMap<String, String> vocabulary  = new HashMap<String, String>(100);
        for(String key : commandDictionary.keySet()) {
            vocabulary.put(commandDictionary.get(key), key);
        }
        while (!cpu.getSI().equals(Constants.INTERRUPTION.HALT)){
            String hex = cpu.getCSValue(cpu.getIC()).getFirstHalf();
            System.out.println(" ---------------- "+ vocabulary.get(hex));
            interpretator.execute(vocabulary.get(hex));
            cpu.increaseIC();
        }
    }

    private void uploadDataSegment() throws Exception {
        channelDevice.readFile("data_seg.txt");
        int i = 0;
        while (channelDevice.hasNext()) {
            cpu.setDS(new Word(i), new Word(channelDevice.readNextWord(), Word.WORD_TYPE.NUMERIC));
            i++;
        }
    }

    private void uploadCodeSegment() throws Exception {
        channelDevice.readFile("code_seg.txt");
        int i = 0;
        while (channelDevice.hasNext()) {
            String nextByte = channelDevice.readNextByte();
            int value = Integer.parseInt(nextByte,16);
            //Commands who has virtual address
            if(value>=64 && value<80)
            {
                String bytes = nextByte+channelDevice.readNextByte()+ channelDevice.readNextByte();
                cpu.setCS(new Word(i), new Word(bytes, Word.WORD_TYPE.NUMERIC));
            }else {
                cpu.setCS(new Word(i), new Word(nextByte.concat("0000"), Word.WORD_TYPE.NUMERIC));
            }
            i++;
        }
    }
}
