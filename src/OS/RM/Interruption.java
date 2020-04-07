package OS.RM;

import OS.Tools.Word;

import java.util.ArrayList;

import static OS.Tools.Constants.*;
import static OS.Tools.Constants.SYSTEM_INTERRUPTION.*;
import static OS.Tools.Constants.SYSTEM_MODE.*;

public class Interruption {

    private final CPU cpu;

    Interruption(CPU cpu)
    {
        this.cpu  = cpu;
    }


    //    Word address, ---> RL
    //    Word value  ---> RH
    public void SETCS() {
        cpu.setMODE(SUPERVISOR_MODE);

        Word address = cpu.getRL().copy();
        Word value = cpu.getRH().copy();

        int word= address.getWordFromAddress();
        int block= address.getBlockFromAddress();

        if(block != cpu.getCSB().getNumber())
        {
            try {
                cpu.setSI(LOADED_WRONG_CS_BLOCK);
                //RL-->BLOCK
                cpu.setRL(new Word(block));
                TEST();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try{
            Word virtualAddress = cpu.getCS().add(word);
            cpu.writeToInternalMemory(virtualAddress, value);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        cpu.setMODE(USER_MODE);
    }

    //    Word address, ---> RL
    //    Word value  ---> RH
    public void SETDS() {

        cpu.setMODE(SUPERVISOR_MODE);
        Word address = cpu.getRL().copy();
        Word value = cpu.getRH().copy();

        int word= address.getWordFromAddress();
        int block= address.getBlockFromAddress();

        if(block != cpu.getDSB().getNumber())
        {
            try {
                cpu.setSI(LOADED_WRONG_DS_BLOCK);
                //RL-->BLOCK
                cpu.setRL(new Word(block));
                TEST();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try{
            Word virtualAddress = cpu.getDS().add(word);
            cpu.writeToInternalMemory(virtualAddress, value);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        cpu.setMODE(USER_MODE);
    }

    //    Word address, ---> RL
    //    Word value  ---> RH
    public void SETSS() {

        cpu.setMODE(SUPERVISOR_MODE);
        Word address = cpu.getRL().copy();
        Word value = cpu.getRH().copy();

        int word= address.getWordFromAddress();
        int block= address.getBlockFromAddress();

        if(block != cpu.getSSB().getNumber())
        {
            try {
                cpu.setSI(LOADED_WRONG_SS_BLOCK);
                //RL-->BLOCK
                cpu.setRL(new Word(block));
                TEST();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try{
            Word virtualAddress = cpu.getSS().add(word);
            cpu.writeToInternalMemory(virtualAddress, value);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        cpu.setMODE(USER_MODE);
    }

    //    Word address, ---> RL
    //    RL ---> value

    public void GETCS()
    {
        cpu.setMODE(SUPERVISOR_MODE);
        Word address = cpu.getRL().copy();
        int word= address.getWordFromAddress();
        int block= address.getBlockFromAddress();

        if(block != cpu.getCSB().getNumber())
        {
            try {
                cpu.setSI(LOADED_WRONG_CS_BLOCK);
                //RL-->BLOCK
                cpu.setRL(new Word(block));
                TEST();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            Word virtualAddress = cpu.getCS().add(word);
            cpu.setRL(cpu.getFromInternalMemory(virtualAddress));
        } catch (Exception e) {
            e.printStackTrace();
        }
        cpu.setMODE(USER_MODE);
    }

    //    Word address, ---> RL
    //    RL ---> value

    public void GETSS()
    {
        cpu.setMODE(SUPERVISOR_MODE);
        Word address = cpu.getRL().copy();
        int word= address.getWordFromAddress();
        int block= address.getBlockFromAddress();

        if(block != cpu.getSSB().getNumber())
        {
            try {
                cpu.setSI(LOADED_WRONG_SS_BLOCK);
                //RL-->BLOCK
                cpu.setRL(new Word(block));
                TEST();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            Word virtualAddress = cpu.getSS().add(word);
            cpu.setRL(cpu.getFromInternalMemory(virtualAddress));
        } catch (Exception e) {
            e.printStackTrace();
        }
        cpu.setMODE(USER_MODE);
    }

    //    Word address, ---> RL
    //    RL ---> value

    public void GETDS()
    {
        cpu.setMODE(SUPERVISOR_MODE);
        Word address = cpu.getRL().copy();
        int word= address.getWordFromAddress();
        int block= address.getBlockFromAddress();

        if(block != cpu.getDSB().getNumber())
        {
            try {
                cpu.setSI(LOADED_WRONG_DS_BLOCK);
                //RL-->BLOCK
                cpu.setRL(new Word(block));
                TEST();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            Word virtualAddress = cpu.getDS().add(word);
            cpu.setRL(cpu.getFromInternalMemory(virtualAddress));
        } catch (Exception e) {
            e.printStackTrace();
        }

        cpu.setMODE(USER_MODE);
    }


    //    Word newInternalBlock, ---> RL

    private void TEST() {

        int currentInternalBlock = -1;
        int currentExternalBlock = -1;
        int newInternalBlock = (int) cpu.getRL().getNumber();
        int newExternalBlock = -1;

        try {
            switch (cpu.getSI()) {
                case LOADED_WRONG_SS_BLOCK:
                    currentInternalBlock = cpu.getSS().getBlockFromAddress();
                    currentExternalBlock = cpu.getPTRValue((int) cpu.getSSB().getNumber()).getBlockFromAddress();
                    newExternalBlock = cpu.getPTRValue(newInternalBlock + (STACK_SEGMENT/256)).getBlockFromAddress();
                    cpu.setSSB(cpu.getRL());
                    break;
                case LOADED_WRONG_DS_BLOCK:
                    currentInternalBlock = cpu.getDS().getBlockFromAddress();
                    currentExternalBlock = cpu.getPTRValue((int) cpu.getDSB().getNumber()).getBlockFromAddress();
                    newExternalBlock = cpu.getPTRValue(newInternalBlock + (DATA_SEGMENT/256)).getBlockFromAddress();
                    cpu.setDSB(cpu.getRL());
                    break;
                case LOADED_WRONG_CS_BLOCK:
                    currentInternalBlock = cpu.getCS().getBlockFromAddress();
                    currentExternalBlock = cpu.getPTRValue((int) cpu.getCSB().getNumber()).getBlockFromAddress();
                    newExternalBlock = cpu.getPTRValue(newInternalBlock + (CODE_SEGMENT/256)).getBlockFromAddress();
                    cpu.setCSB(cpu.getRL());
                    break;
            }

            //    int fromBlock -->RL
            //    int toBlock --> RH
            cpu.setRL(new Word( currentInternalBlock ));
            cpu.setRH(new Word( currentExternalBlock )) ;
            cpu.getLoader().loadToExternalMemory();



            cpu.setRL(new Word( newExternalBlock ));
            cpu.setRH(new Word( currentInternalBlock )) ;
            cpu.getLoader().loadToInternalMemory();

            System.out.println(" newExternalBlock  --- > " + newExternalBlock);
            System.out.println(" currentInternalBlock   --- > " + currentInternalBlock);
            System.out.println(" uzkruana is i  --- > ");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
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
//private int getBlockByMode(int segment, Word address) {
//
//    try{
//        switch (cpu.getMODE())
//        {
//            case SUPERVISOR_MODE:
//                return address.getBlockFromAddress();
//            case USER_MODE:
//                Word virtualAddress = new Word(segment + address.getNumber());
//                return virtualAddress.getBlockFromAddress();
//        }
//    }catch (Exception e)
//    {
//        e.printStackTrace();
//    }
//    return -1;
//}
