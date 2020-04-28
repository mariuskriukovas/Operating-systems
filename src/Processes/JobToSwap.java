package Processes;

import Components.Memory;
import RealMachine.RealMachine;
import Tools.Constants;
import Tools.Word;

import java.util.ArrayList;

public class JobToSwap {
    //JobToSwap – užduoties patalpinimas išorinėje atmintyje

    private final RealMachine realMachine;

    public JobToSwap(RealMachine realMachine)
    {
        this.realMachine = realMachine;
    }

    //    STACK_SEGMENT = 0;
    //    DATA_SEGMENT = 21760; 5500
    //    CODE_SEGMENT = 43520; AA00

    public void uploadTaskToExternalMemory(String task,  int externalMemoryBegin)
    {
        realMachine.getParser().parseFile(task);
        int dataSegBegin = Constants.DATA_SEGMENT + (externalMemoryBegin*256);
        int codeSegBegin = Constants.CODE_SEGMENT + (externalMemoryBegin*256);

        //System.out.println("dataSegBegin-----> "+dataSegBegin/256);
        //System.out.println("codeSegBegin-----> "+codeSegBegin/256);

        ArrayList<Parser.Command> codeSegment = realMachine.getParser().getCodeSegment();
        ArrayList<Parser.Command> dataSegment = realMachine.getParser().getDataSegment();

        Memory externalMemory = realMachine.getExternalMemory();

        for(Parser.Command c : codeSegment)
        {
            int adr = c.getPosition() + codeSegBegin;
            try {
//                System.out.println(new Word(adr).getHEXFormat());
//                cpu.writeToExternalMemory(new Word(adr), new Word(c.getValue(), Word.WORD_TYPE.SYMBOLIC));
                externalMemory.setWord(new Word(c.getValue(), Word.WORD_TYPE.SYMBOLIC), adr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for(Parser.Command c : dataSegment)
        {
            int adr = c.getPosition() + dataSegBegin;
            try {
//                System.out.println(new Word(adr).getHEXFormat());
//                cpu.writeToExternalMemory(new Word(adr), new Word(c.getValue(), Word.WORD_TYPE.NUMERIC));
                externalMemory.setWord(new Word(c.getValue(), Word.WORD_TYPE.NUMERIC), adr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
