package Processes;

import Components.CPU;
import Components.Memory;
import RealMachine.RealMachine;
import Tools.Constants;
import Tools.Word;

import static Tools.Constants.CODE_SEGMENT;
import static Tools.Constants.DATA_SEGMENT;
import static Tools.Constants.STACK_SEGMENT;

public class Swapping {


    private final CPU cpu;
    private final RealMachine realMachine;
    private final Memory internalMemory;
    private final Memory externalMemory;

    public Swapping(RealMachine realMachine) {
        this.realMachine = realMachine;
        this.cpu = realMachine.getCpu();
        internalMemory = realMachine.getInternalMemory();
        externalMemory = realMachine.getExternalMemory();
    }



    public void setSS(int newSSBlock){
        try {
            //save old SS block values
            realMachine.getLoader().saveSSBlock();
            //save old SS register value back to memory table
            long addressInMemoryTable = realMachine.getLoader().findSegmentInMemoryTable('S');
            internalMemory.setWord(cpu.getSS(),addressInMemoryTable);
            //find and set new SS block
            int ptr = (int) cpu.getPTR().getNumber() * 256;
            long adr = newSSBlock +  ptr + (STACK_SEGMENT/256);
            cpu.setSS(internalMemory.getWord(adr));
            //save cahanges in memory table
            Word value =  cpu.getPTR().add(1);
            value.setByte('S', 0);
            internalMemory.setWord(value, adr);
            //upload new SS block to internalMemory
            realMachine.getLoader().uploadSSBlock();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setDS(int newDSBlock){
        try {
            //save old DS block values
            realMachine.getLoader().saveDSBlock();
            //save old DS register value back to memory table
            long addressInMemoryTable = realMachine.getLoader().findSegmentInMemoryTable('D');
            internalMemory.setWord(cpu.getDS(),addressInMemoryTable);
            //find and set new DS block
            int ptr = (int) cpu.getPTR().getNumber() * 256;
            long adr = newDSBlock +  ptr + (DATA_SEGMENT/256);
            cpu.setDS(internalMemory.getWord(adr));
            //save cahanges in memory table
            Word value =  cpu.getPTR().add(2);
            value.setByte('D', 0);
            internalMemory.setWord(value, adr);
            //upload new DS block to internalMemory
            realMachine.getLoader().uploadDSBlock();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setCS(int newCSBlock){
        try {
            //save old CS block values
            realMachine.getLoader().saveCSBlock();
            //save old CS register value back to memory table
            long addressInMemoryTable = realMachine.getLoader().findSegmentInMemoryTable('C');
            internalMemory.setWord(cpu.getCS(),addressInMemoryTable);
            //find and set new CS block
            int ptr = (int) cpu.getPTR().getNumber() * 256;
            long adr = newCSBlock +  ptr + (CODE_SEGMENT/256);
            cpu.setCS(internalMemory.getWord(adr));
            //save cahanges in memory table
            Word value =  cpu.getPTR().add(3);
            value.setByte('C', 0);
            internalMemory.setWord(value, adr);
            //upload new CS block to internalMemory
            realMachine.getLoader().uploadCSBlock();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


//    //    Word address, ---> RL
//    //    Word value  ---> RH
//    public void SETCS() {
//        cpu.showProcess(Constants.PROCESS.Swapping);
//
//        Word address = cpu.getRL().copy();
//        Word value = cpu.getRH().copy();
//
//        int word = address.getWordFromAddress();
//        int block = address.getBlockFromAddress();
//
//        if (block != cpu.getCSB().getNumber()) {
//            try {
//                cpu.setSI(LOADED_WRONG_CS_BLOCK);
//                //RL-->BLOCK
//                cpu.setRL(new Word(block));
//                TEST();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        try {
//            Word virtualAddress = cpu.getCS().add(word);
//            cpu.writeToInternalMemory(virtualAddress, value);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        cpu.showPreviousProcess();
//    }

    public void SETDS()
    {
//        cpu.showProcess(Constants.PROCESS.Swapping);
//
//        Word address = cpu.getRL().copy();
//        Word value = cpu.getRH().copy();
//
//        int word = address.getWordFromAddress();
//        int block = address.getBlockFromAddress();
//
//        if (block != cpu.getDSB().getNumber())
//        {
//            try {
//                cpu.setSI(LOADED_WRONG_DS_BLOCK);
//                //RL-->BLOCK
//                cpu.setRL(new Word(block));
//                TEST();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        try {
//            Word virtualAddress = cpu.getDS().add(word);
//            cpu.writeToInternalMemory(virtualAddress, value);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        cpu.showPreviousProcess();
    }
//
//    //    Word address, ---> RL
//    //    Word value  ---> RH
//    public void SETSS() {
//        cpu.showProcess(Constants.PROCESS.Swapping);
//
//        Word address = cpu.getRL().copy();
//        Word value = cpu.getRH().copy();
//
//        int word = address.getWordFromAddress();
//        int block = address.getBlockFromAddress();
//
//        if (block != cpu.getSSB().getNumber()) {
//            try {
//                cpu.setSI(LOADED_WRONG_SS_BLOCK);
//                //RL-->BLOCK
//                cpu.setRL(new Word(block));
//                TEST();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        try {
//            Word virtualAddress = cpu.getSS().add(word);
//            cpu.writeToInternalMemory(virtualAddress, value);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        cpu.showPreviousProcess();
//    }
//
//    //    Word address, ---> RL
//    //    RL ---> value
//
//    public void GETCS() {
//        cpu.showProcess(Constants.PROCESS.Swapping);
//
//        Word address = cpu.getRL().copy();
//        int word = address.getWordFromAddress();
//        int block = address.getBlockFromAddress();
//
//        if (block != cpu.getCSB().getNumber()) {
//            try {
//                cpu.setSI(LOADED_WRONG_CS_BLOCK);
//                //RL-->BLOCK
//                cpu.setRL(new Word(block));
//                TEST();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            Word virtualAddress = cpu.getCS().add(word);
//            cpu.setRL(cpu.getFromInternalMemory(virtualAddress));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        cpu.showPreviousProcess();
//    }
//
//    //    Word address, ---> RL
//    //    RL ---> value
//
//    public void GETSS() {
//        cpu.showProcess(Constants.PROCESS.Swapping);
//
//        Word address = cpu.getRL().copy();
//        int word = address.getWordFromAddress();
//        int block = address.getBlockFromAddress();
//
//        if (block != cpu.getSSB().getNumber()) {
//            try {
//                cpu.setSI(LOADED_WRONG_SS_BLOCK);
//                //RL-->BLOCK
//                cpu.setRL(new Word(block));
//                TEST();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            Word virtualAddress = cpu.getSS().add(word);
//            cpu.setRL(cpu.getFromInternalMemory(virtualAddress));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        cpu.showPreviousProcess();
//    }
//
//    //    Word address, ---> RL
//    //    RL ---> value
//
//    public void GETDS() {
//        cpu.showProcess(Constants.PROCESS.Swapping);
//
//        Word address = cpu.getRL().copy();
//        int word = address.getWordFromAddress();
//        int block = address.getBlockFromAddress();
//
//        if (block != cpu.getDSB().getNumber()) {
//            try {
//                cpu.setSI(LOADED_WRONG_DS_BLOCK);
//                //RL-->BLOCK
//                cpu.setRL(new Word(block));
//                TEST();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        try {
//            Word virtualAddress = cpu.getDS().add(word);
//            cpu.setRL(cpu.getFromInternalMemory(virtualAddress));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        cpu.showPreviousProcess();
//    }
//
//
//    //    Word newInternalBlock, ---> RL
//
//    private void TEST() {
//
//        int currentInternalBlock = - 1;
//        int currentExternalBlock = - 1;
//        int newInternalBlock = (int) cpu.getRL().getNumber();
//        int newExternalBlock = - 1;
//
//        try {
//            switch (cpu.getSI()) {
//                case LOADED_WRONG_SS_BLOCK:
//                    currentInternalBlock = cpu.getSS().getBlockFromAddress();
//                    currentExternalBlock = cpu.getPTRValue((int) cpu.getSSB().getNumber()).getBlockFromAddress();
//                    newExternalBlock = cpu.getPTRValue(newInternalBlock + (STACK_SEGMENT / 256)).getBlockFromAddress();
//                    cpu.setSSB(cpu.getRL());
//                    break;
//                case LOADED_WRONG_DS_BLOCK:
//                    currentInternalBlock = cpu.getDS().getBlockFromAddress();
//                    currentExternalBlock = cpu.getPTRValue((int) cpu.getDSB().getNumber()).getBlockFromAddress();
//                    newExternalBlock = cpu.getPTRValue(newInternalBlock + (DATA_SEGMENT / 256)).getBlockFromAddress();
//                    cpu.setDSB(cpu.getRL());
//                    break;
//                case LOADED_WRONG_CS_BLOCK:
//                    currentInternalBlock = cpu.getCS().getBlockFromAddress();
//                    currentExternalBlock = cpu.getPTRValue((int) cpu.getCSB().getNumber()).getBlockFromAddress();
//                    newExternalBlock = cpu.getPTRValue(newInternalBlock + (CODE_SEGMENT / 256)).getBlockFromAddress();
//                    cpu.setCSB(cpu.getRL());
//                    break;
//            }
//
//            //    int fromBlock -->RL
//            //    int toBlock --> RH
//            cpu.setRL(new Word(currentInternalBlock));
//            cpu.setRH(new Word(currentExternalBlock));
//            cpu.getLoader().loadToExternalMemory();
//
//
//            cpu.setRL(new Word(newExternalBlock));
//            cpu.setRH(new Word(currentInternalBlock));
//            cpu.getLoader().loadToInternalMemory();
//
////            System.out.println(" newExternalBlock  --- > " + newExternalBlock);
////            System.out.println(" currentInternalBlock   --- > " + currentInternalBlock);
////            System.out.println(" uzkruana is i  --- > ");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
}


//try {
//        System.out.println(" word  --- > " + word);
//        System.out.println(" block   --- > " + block);
//        System.out.println(" getSSB   --- > " + cpu.getSSB().getNumber());
//        System.out.println(" virtual   --- > " + cpu.getSS().add(word).getHEXFormat());
//        System.out.println(" get from internal   --- > " +  cpu.getFromInternalMemory(cpu.getSS().add(word)).getHEXFormat());
//        } catch (Exception e) {
//        e.printStackTrace();
//        }
