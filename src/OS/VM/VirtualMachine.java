package OS.VM;

import java.util.ArrayList;

public class VirtualMachine
{
    private CPU cpu = null;
    private Memory memory = null;
    private Commands interpretator = null;

    VirtualMachine()
    {
        try {
            memory = new Memory();
            cpu = new CPU(memory);
            interpretator = new Commands(cpu,memory);
            uploadCode();
            doYourMagic();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doYourMagic()
    {
        while (true)
        {
            try {
                String command = memory.getWord(cpu.getCS(cpu.getIC())).getASCIIFormat();

                interpretator.execute(command);
                cpu.increaseIC();
                if(command.contains("HALT"))
                {
                    return;
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadCode()
    {
        try {
            Interpretator interpretator =  new Interpretator("prog.txt");
            interpretator.read();
            interpretator.interpreter();
            ArrayList<String>dataSegment = interpretator.getDataSegment();
            for (int i = 0; i<dataSegment.size();i++)
            {
                cpu.setDS(new Word(i), new Word(dataSegment.get(i),Word.WORD_TYPE.NUMERIC));
            }
            ArrayList<String>codeSegment = interpretator.getCodeSegment();
            for (int i = 0; i<codeSegment.size();i++)
            {
                cpu.setCS(new Word(i), new Word(codeSegment.get(i), Word.WORD_TYPE.SYMBOLIC));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
