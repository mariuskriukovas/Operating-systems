package Processes;

import Components.CPU;
import Components.Memory;
import RealMachine.RealMachine;
import Tools.Constants;
import Tools.Word;

import static Tools.Constants.*;

public class Loader {

    private final RealMachine realMachine;
    private final CPU cpu;
    private final Memory internalMemory;
    private final Memory externalMemory;

    public Loader(RealMachine realMachine)
    {
       this.realMachine = realMachine;
       cpu = realMachine.getCpu();
       internalMemory = realMachine.getInternalMemory();
       externalMemory = realMachine.getExternalMemory();
    }

    public void createMemoryTable(int internalBlockBegin, int externalBlockBegin)
    {
        Memory internalMemory = realMachine.getInternalMemory();
        int adr = internalBlockBegin*256;
        try {
            for (int i = 0; i < Constants.BLOCK_LENGTH; i++) {
                //int block = externalMemory.getBlockBeginAddress(externalBlockBegin + i);
                internalMemory.setWord(new Word(externalBlockBegin + i),adr+i);
                //cpu.setPTRValue(i, new Word(externalMemory.getBlockBeginAddress(externalBlockBegin + i)));
            }
            //cpu.setPTR(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin)));
            cpu.setPTR(new Word(internalBlockBegin));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //    int fromBlock -->RL
    //    int toBlock --> RH
    public void loadToInternalMemory()
    {
        cpu.showProcess(PROCESS.Loader);
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
        cpu.showPreviousProcess();
    }

    //int fromBlock -->RL
    //int toBlock --> RH
    public void loadToExternalMemory()
    {
        cpu.showProcess(PROCESS.Loader);
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
        cpu.showPreviousProcess();
    }

    public void saveSegmentRegisters(){
        Memory internalMemory = realMachine.getInternalMemory();

        long ptr = cpu.getPTR().getNumber()*256;

        for(int i = 0; i<256; i++){
            try {
                int segment = internalMemory.getWord(ptr+i).getByte(0);
                if(segment == 'S'){
                    System.out.println("S");
                }else if(segment == 'D'){
                    System.out.println("D");
                }else if(segment == 'C'){
                    System.out.println("C");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public long findSegmentInMemoryTable(int segment){
        Memory internalMemory = realMachine.getInternalMemory();
        long ptr = cpu.getPTR().getNumber()*256;

        for(int i = 0; i<256; i++){
            try {
                long virtualAddress = ptr+i;
                int firstByte = internalMemory.getWord(virtualAddress).getByte(0);
                if(firstByte == segment){
                    return virtualAddress;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return  -1;
    }

    //PTR --> memory table
    public void initSegmentRegisters(){
        Memory internalMemory = realMachine.getInternalMemory();
        long ptr = cpu.getPTR().getNumber()*256;
        try {
                long adr = ptr + (STACK_SEGMENT/256);
                cpu.setSS(internalMemory.getWord(adr));
                Word value =  cpu.getPTR().add(1);
                value.setByte('S', 0);
                internalMemory.setWord(value, adr);
                System.out.println(adr);

                adr = ptr + (DATA_SEGMENT/256);
                cpu.setDS(internalMemory.getWord(adr));
                value =  cpu.getPTR().add(2);
                value.setByte('D', 0);
                internalMemory.setWord(value, adr);
                System.out.println(adr);

                adr = ptr + (CODE_SEGMENT/256);
                cpu.setCS(internalMemory.getWord(adr));
                value =  cpu.getPTR().add(3);
                value.setByte('C', 0);
                internalMemory.setWord(value, adr);
                System.out.println(adr);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //  SS ----- > internalMemory
    public void uploadSSBlock(){
        try {
            Word[] ss = externalMemory.getBlock((int) cpu.getSS().getNumber());
            int ptr = (int)cpu.getPTR().getNumber();
            internalMemory.setBlock(ptr+1,ss);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //  DS ----- > internalMemory
    public void uploadDSBlock(){
        try {
            Word[] ds = externalMemory.getBlock((int) cpu.getDS().getNumber());
            int ptr = (int)cpu.getPTR().getNumber();
            internalMemory.setBlock(ptr+2,ds);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //  CS ----- > internalMemory
    public void uploadCSBlock(){
        try {
            Word[] cs = externalMemory.getBlock((int) cpu.getCS().getNumber());
            int ptr = (int)cpu.getPTR().getNumber();
            internalMemory.setBlock(ptr+3,cs);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    //  PTR ----- > internalBEGIN
    //  SS ----- > internalMemory
    //  DS ----- > internalMemory
    //  CS ----- > internalMemory
    public void loadVirtualMachineMemory() {
        uploadSSBlock();
        uploadDSBlock();
        uploadCSBlock();
    }

    //  SS ----- > externalMemory
    public void saveSSBlock() {
        try {
            int ptr = (int)cpu.getPTR().getNumber();
            Word[] ss = internalMemory.getBlock(ptr+1);
            externalMemory.setBlock((int)cpu.getSS().getNumber(),ss);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //  DS ----- > externalMemory
    public void saveDSBlock() {
        try {
            int ptr = (int)cpu.getPTR().getNumber();
            Word[] ds = internalMemory.getBlock(ptr+2);
            externalMemory.setBlock((int)cpu.getDS().getNumber(),ds);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //  CS ----- > externalMemory
    public void saveCSBlock() {
        try {
            int ptr = (int)cpu.getPTR().getNumber();
            Word[] cs = internalMemory.getBlock(ptr+3);
            externalMemory.setBlock((int)cpu.getCS().getNumber(),cs);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //  PTR ----- > internalBEGIN
    //  SS ----- > externalMemory
    //  DS ----- > externalMemory
    //  CS ----- > externalMemory
    public void saveVirtualMachineMemory() {
        saveSSBlock();
        saveDSBlock();
        saveCSBlock();
    }


}
