package OS.RM.Process;

import OS.RM.CPU;
import OS.Tools.Constants;
import OS.Tools.Word;

public class Loader {
    private final CPU cpu;

    public Loader(CPU cpu)
    {
        this.cpu = cpu;
    }

//    int fromBlock -->RL
//    int toBlock --> RH
    public void loadToInternalMemory()
    {
        int fromBlock  = (int) cpu.getRL().getNumber();
        int toBlock  = (int) cpu.getRH().getNumber();

        System.out.println(fromBlock);

        int internalBegin = toBlock*256;
        int externalBegin = fromBlock*256;

        for(int i = 0;i<Constants.BLOCK_LENGTH; i++)
        {
            try {
                Word word = cpu.getFromExternalMemory(new Word(i+externalBegin));
                cpu.writeToInternalMemory(new Word(i+internalBegin), word);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //    int fromBlock -->RL
    //int toBlock --> RH

    public void loadToExternalMemory()
    {
        int fromBlock  = (int) cpu.getRL().getNumber();
        int toBlock  = (int) cpu.getRH().getNumber();

        int internalBegin = fromBlock*Constants.BLOCK_LENGTH;
        int externalBegin = toBlock*Constants.BLOCK_LENGTH;

        for(int i = 0;i<Constants.BLOCK_LENGTH; i++)
        {
            try {
                Word word = cpu.getFromInternalMemory(new Word(i+internalBegin));
                cpu.writeToExternalMemory(new Word(i+externalBegin), word);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
