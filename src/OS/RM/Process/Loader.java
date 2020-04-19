package OS.RM.Process;

import OS.Interfaces.Memory;
import OS.RM.CPU;
import OS.Tools.Constants;
import OS.Tools.Word;

import static OS.Tools.Constants.*;

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

    //int fromBlock -->RL
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

    //  int internalBlockBegin --> RL
    public void loadVirtualMachineMemory() {

        Memory internalMemory = cpu.getInternalMemory();

        int internalBlockBegin = (int) cpu.getRL().getNumber();
        try {
            cpu.setPTR(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin + 0)));
            cpu.setSS(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin + 1)));
            cpu.setDS(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin + 2)));
            cpu.setCS(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin + 3)));

            cpu.setSSB(new Word(STACK_SEGMENT / 256));
            cpu.setCSB(new Word(CODE_SEGMENT / 256));
            cpu.setDSB(new Word(DATA_SEGMENT / 256));

            //    int fromBlock -->RL
            //    int toBlock --> RH
            cpu.setRL(new Word(cpu.getPTRValue((int) cpu.getCSB().getNumber()).getBlockFromAddress()));
            cpu.setRH(new Word(cpu.getCS().getBlockFromAddress()));
            loadToInternalMemory();

            cpu.setRL(new Word(cpu.getPTRValue((int) cpu.getSSB().getNumber()).getBlockFromAddress()));
            cpu.setRH(new Word(cpu.getSS().getBlockFromAddress()));
            loadToInternalMemory();

            cpu.setRL(new Word(cpu.getPTRValue((int) cpu.getDSB().getNumber()).getBlockFromAddress()));
            cpu.setRH(new Word(cpu.getDS().getBlockFromAddress()));
            loadToInternalMemory();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
