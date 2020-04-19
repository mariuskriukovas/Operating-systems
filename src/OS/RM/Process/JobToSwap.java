package OS.RM.Process;

import OS.RM.CPU;
import OS.Tools.Constants;
import OS.Tools.Word;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

public class JobToSwap {
    //JobToSwap – užduoties patalpinimas išorinėje atmintyje

    private final CPU realCPU;
    private final HashMap<String,Integer>taskLocation;
    private final Deque<Integer> memoryStack;


    public JobToSwap(CPU realCPU)
    {
        this.realCPU = realCPU;
        taskLocation = new HashMap<String,Integer>(10);
        memoryStack = new ArrayDeque<Integer>();

        int externalMemoryLength = 65536;
        for (int i = 0; i< externalMemoryLength; i = i+256){
            memoryStack.push(i);
        }
    }

//    STACK_SEGMENT = 0;
//    DATA_SEGMENT = 21760; 5500
//    CODE_SEGMENT = 43520; AA00

    public void uploadTaskToExternalMemory(String task){
        Parser parser = new Parser(task);
        int externalMemoryBegin =  findFreeExternalMemoryBlocks();
        int dataSegBegin = Constants.DATA_SEGMENT + (externalMemoryBegin*256);
        int codeSegBegin = Constants.CODE_SEGMENT + (externalMemoryBegin*256);

        for(Parser.Command c : parser.getCodeSegmentC())
        {
            int adr = c.getPosition() + codeSegBegin;
            try {
//                System.out.println(new Word(adr).getHEXFormat());
                realCPU.writeToExternalMemory(new Word(adr), new Word(c.getValue(), Word.WORD_TYPE.SYMBOLIC));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for(Parser.Command c : parser.getDataSegmentC())
        {
            int adr = c.getPosition() + dataSegBegin;
            try {
//                System.out.println(new Word(adr).getHEXFormat());
                realCPU.writeToExternalMemory(new Word(adr), new Word(c.getValue(), Word.WORD_TYPE.NUMERIC));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        taskLocation.put(task,externalMemoryBegin);
    }

    public int getTaskLocation(String taskID)
    {
        return taskLocation.get(taskID);
    }


    //to be implemented
    //turi iskirti tiek atminties kad pakanktu vienai VM

    private int findFreeExternalMemoryBlocks() {
        return memoryStack.pop();
    }


}
