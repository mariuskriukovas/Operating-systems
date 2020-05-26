package Components;

import Components.UI.OSFrame;
import Components.UI.RMPanel;

import Components.UI.VMPanel;
import Processes.RealMachine;
import Tools.Constants.*;
import Tools.Exceptions;
import Tools.Word;

import java.util.HashMap;

import static Tools.Constants.*;
import static Tools.Constants.PROGRAM_INTERRUPTION.*;


public class CPU {

    private CONDITIONAL_MODE C = CONDITIONAL_MODE.NONE;
    private SYSTEM_MODE MODE = SYSTEM_MODE.SUPERVISOR_MODE;
    private  PROGRAM_INTERRUPTION PI = PROGRAM_INTERRUPTION.NONE;
    private SYSTEM_INTERRUPTION SI = SYSTEM_INTERRUPTION.NONE;

    private  Integer TI = 0;

    private final Word PTR = new Word(0);

    private final Word IC = new Word(0);
    private final Word SP = new Word(0);

    private final Word RH = new Word(0);
    private final Word RL = new Word(0);

    private final Word SS = new Word(0);
    private final Word DS = new Word(0);
    private final Word CS = new Word(0);

    private final Memory internalMemory;
    private final Memory externalMemory;

    private final RMPanel RMScreen;
    private final VMPanel VMScreen;
    private final RealMachine realMachine;

    public CPU(RealMachine realMachine)
    {
        this.realMachine = realMachine;
        this.externalMemory = realMachine.getExternalMemory();
        this.internalMemory = realMachine.getInternalMemory();

        RMScreen = realMachine.getScreen().getScreenForRealMachine();
        VMScreen = realMachine.getScreen().getScreenForVirtualMachine();
        descriptors = new HashMap<>(4);
    }




    //Real Machine parthttps://www.youtube.com/watch?v=aJB6s-IWGH4&fbclid=IwAR3sRvdIWGlZyWjnXsPqn7tr6CbpiSTpR-btr9k9JgXer3YCcmAaViorVdU

// ONLY SETTERS RESPONSIBLE FOR GRAPHICS

    public Word getPTR()
    {
        return new Word(PTR.getNumber());
    }

    public void setPTR(Word word) {
        PTR.setWord(word);
        RMScreen.setPTRRegister(PTR);
    }

    public Word getPTRValue(int block) throws Exception {
        return internalMemory.getWord(PTR.add(block));
    }

    public void setPTRValue(int block, Word word) throws Exception {
        internalMemory.setWord(word, PTR.add(block));
    }

    private int id;

    public int getExternalMemoryBegin() {
        return id;
    }

    public Word getDS() {
        return DS;
    }

    private enum Segment{
        SS,
        DS,
        CS
    }

    //----------------------------------------------------------------------------------
    // JM1256 IF (OLD_CS != NEW_CS) SI = 4 -> test()
    // AD12 -> test() if (SI + PI != 0 || TI == 0) MODE = 1
    private boolean test(Segment segment, Word virtualAddress)
    {
        int desirableBlock = virtualAddress.getBlockFromAddress();
        int loadedBlock = -1;
        switch (segment)
        {
            case SS:
                loadedBlock = SS.getWordFromAddress() - STACK_SEGMENT/256;
                if(loadedBlock!=desirableBlock){

                    setSI(SYSTEM_INTERRUPTION.SWAPING_SS);
                    //realMachine.getSwapping().setSS(desirableBlock);
                    return true;
                }
                break;
            case DS:
                loadedBlock = DS.getWordFromAddress() - DATA_SEGMENT/256;
                if(loadedBlock!=desirableBlock){
                    System.out.println("getWORDFromAddress: "+loadedBlock);
                    System.out.println("getBlockFromAddress: "+virtualAddress.getBlockFromAddress());
                    System.out.println("ds : : "+DS);

                    setSI(SYSTEM_INTERRUPTION.SWAPING_DS);
                    //realMachine.getSwapping().setDS(desirableBlock);
                    return true;
                    //System.out.println("ds : : "+DS);
                }
                break;
            case CS:
                loadedBlock = CS.getWordFromAddress() - CODE_SEGMENT/256;
                if(loadedBlock!=desirableBlock){

                    setSI(SYSTEM_INTERRUPTION.SWAPING_CS);
                    //realMachine.getSwapping().setCS(desirableBlock);
                    return true;
                }
                break;
        }
        return false;
    }

    public Word getSS(Word virtualAddress) throws Exceptions.WrongAddressException {

        if(test(Segment.SS, virtualAddress)){
            setRL(virtualAddress);
            return null;
        }

        long address = SS.getBlockFromAddress() * 256 + virtualAddress.getWordFromAddress();
        try {
            return internalMemory.getWord(address);
        } catch (Exception e) {
            throw new Exceptions.WrongAddressException(WRONG_SS_BLOCK_ADDRESS,virtualAddress);
        }
    }

    public Word getDS(Word virtualAddress) throws Exceptions.WrongAddressException {
        if(test(Segment.DS, virtualAddress))
        {
            setRL(virtualAddress);
            return null;
        }

        long address = DS.getBlockFromAddress() * 256 + virtualAddress.getWordFromAddress();
        try {
            return internalMemory.getWord(address);
        } catch (Exception e) {
            throw new Exceptions.WrongAddressException(WRONG_DS_BLOCK_ADDRESS,virtualAddress);
        }
    }

    public Word getCS(Word virtualAddress) throws Exceptions.WrongAddressException {

        if(test(Segment.CS, virtualAddress))
        {
            setRL(virtualAddress);
            return null;
        };

        long address = CS.getBlockFromAddress() * 256 + virtualAddress.getWordFromAddress();
        try {
           return internalMemory.getWord(address);
        } catch (Exception e) {
            throw new Exceptions.WrongAddressException(WRONG_CS_BLOCK_ADDRESS,virtualAddress);
        }
    }

    public void getByVirtualAddress(Word virtualAddress) throws Exceptions.WrongAddressException{
        if(virtualAddress.getNumber()<DATA_SEGMENT) {
            getSS(virtualAddress);
        }else if(virtualAddress.getNumber()<CODE_SEGMENT) {
             getDS(virtualAddress);
        }else {
             getCS(virtualAddress);
        }
    }

    public boolean setSS(Word virtualAddress, Word value) throws Exceptions.WrongAddressException {
        try {
            if(test(Segment.SS, virtualAddress)){
                setRL(virtualAddress);
                return true;
            }

            long address = SS.getBlockFromAddress() * 256 + virtualAddress.getWordFromAddress();
            internalMemory.setWord(value, address);
            return false;
        }catch (Exception e)
        {
            throw new Exceptions.WrongAddressException(WRONG_SS_BLOCK_ADDRESS,virtualAddress);
        }
    }

    public boolean setDS(Word virtualAddress, Word value) throws Exceptions.WrongAddressException {
        if(test(Segment.DS, virtualAddress)){
            setRL(virtualAddress);
            return true;
        }

        long address = DS.getBlockFromAddress() * 256 + virtualAddress.getWordFromAddress();
        try {
            internalMemory.setWord(value, address);
        } catch (Exception e) {
            throw new Exceptions.WrongAddressException(WRONG_DS_BLOCK_ADDRESS,virtualAddress);
        }
        return false;
    }

    public boolean setCS(Word virtualAddress, Word value) throws Exceptions.WrongAddressException {
        if(test(Segment.CS, virtualAddress))
        {
            setRL(virtualAddress);
            return true;
        }

        long address = DS.getBlockFromAddress() * 256 + virtualAddress.getWordFromAddress();
        try {
            internalMemory.setWord(value, address);
        } catch (Exception e) {
            throw new Exceptions.WrongAddressException(WRONG_DS_BLOCK_ADDRESS,virtualAddress);
        }
        return false;
    }

    public void setByVirtualAddress(Word virtualAddress, Word value) throws Exceptions.WrongAddressException {
        if(virtualAddress.getNumber()<DATA_SEGMENT) {
            setSS(virtualAddress,value);
        }else if(virtualAddress.getNumber()<CODE_SEGMENT) {
            setDS(virtualAddress, value);
        }else {
            setCS(virtualAddress, value);
        }
    }

    public Word getSS(){
        return SS;
    }

    public Word getCS(){
        return CS;
    }


    public void setDS(Word word) {
        DS.setWord(word);
        RMScreen.setDSRegister(DS);
    }

    public void refresh()
    {
        boolean saveTickMode = OSFrame.TickMode;

        OSFrame.TickMode = false;
        VMScreen.setCpu(this);
        RMScreen.setPTRRegister(PTR);
        RMScreen.setDSRegister(DS);
        RMScreen.setSSRegister(SS);
        RMScreen.setCSRegister(CS);

        RMScreen.setTIRegister(TI);
        RMScreen.setSIRegister(SI);

        VMScreen.setStackPointer(SP);
        VMScreen.setRHRegister(RH);
        VMScreen.setRLRegister(RL);
        VMScreen.setCRegister(C);
        VMScreen.setPIRegister(PI);
        VMScreen.setInstructionCounter(IC);

        RMScreen.setMODERegister(MODE);

        OSFrame.TickMode = saveTickMode;
    }

    public void setSS(Word word) {
        SS.setWord(word);
        RMScreen.setSSRegister(SS);
    }

    public void setCS(Word word) {
        CS.setWord(word);
        RMScreen.setCSRegister(CS);
    }


    public SYSTEM_MODE getMODE() {
//        RMScreen.setMODERegister(MODE);
        return MODE;
    }

    public int getTI() {
//        RMScreen.setTIRegister(TI);
        return TI;
    }

    public void incTI() {
        TI ++;
        RMScreen.setTIRegister(TI);
    }

    public void decTI() {
        TI --;
        RMScreen.setTIRegister(TI);
    }

    public void setTI(int ti) {
        TI = ti;
        RMScreen.setTIRegister(TI);
    }

        public PROGRAM_INTERRUPTION getPI() {
//        RMScreen.setPIRegister(PI);
        return PI;
    }

    public void setPI(PROGRAM_INTERRUPTION flag) {
        PI = flag;
        VMScreen.setPIRegister(PI);
    }

    public SYSTEM_INTERRUPTION getSI() {
        return SI;
    }

    public void setSI(SYSTEM_INTERRUPTION flag) {
        SI = flag;
        RMScreen.setSIRegister(SI);
    }

    //Virtual Machine Part

    public Word getSP(){
        return SP;
    }

    public void setSP(Word word) {
        SP.setWord(word);
        VMScreen.setStackPointer(SP);
    }

    public void increaseSP() throws Exceptions.StackPointerException {
        try {
            SP.setWord(SP.add(1));
        } catch (Exception e) {
            throw new Exceptions.StackPointerException(WRONG_SP);
        }
        VMScreen.setStackPointer(SP);
    }

    public void decreaseSP() throws Exceptions.StackPointerException {
        try {
            SP.setWord(SP.add(- 1));
        } catch (Exception e) {
            throw new Exceptions.StackPointerException(NEGATIVE_SP);
        }
        VMScreen.setStackPointer(SP);
    }

    public Word getRL() {
        return RL;
    }

    public void setRL(Word word) {
        RL.setWord(word);
        VMScreen.setRLRegister(RL);
    }

    public Word getRH() {
        VMScreen.setRHRegister(RH);
        return RH;
    }

    public void setRH(Word word) {
        RH.setWord(word);
        VMScreen.setRHRegister(RH);
    }

    public Word getIC() {
        return IC;
    }

    public void setIC(Word word) throws Exceptions.InstructionPointerException {
        //not bigger than segment block
        if(word.getNumber()<DATA_SEGMENT){
            IC.setWord(word);
            VMScreen.setInstructionCounter(IC);
        }else{
            throw new Exceptions.InstructionPointerException(WRONG_IC);
        }
    }

    public void increaseIC() {
        try {
            setIC(IC.add(1));
        } catch (Exception e) {
            System.err.println(e.getStackTrace());
            //throw new Exceptions.InstructionPointerException(WRONG_IC);
        }
    }

    public CONDITIONAL_MODE getC() {
        return C;
    }

    public void setC(CONDITIONAL_MODE flag) {
        C = flag;
        VMScreen.setCRegister(C);
    }

    public void writeToExternalMemory(Word address, Word value) {
        try {
            externalMemory.setWord(value, address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Word getFromExternalMemory(Word address) throws Exception {
        return externalMemory.getWord(address);
    }

    public void writeToInternalMemory(Word address, Word value) {
        try {
            internalMemory.setWord(value, address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Word getFromInternalMemory(Word address) throws Exception {
        int block = address.getBlockFromAddress();
        return internalMemory.getWord(address);
    }

    public void setMODE(SYSTEM_MODE flag) {
        RMScreen.setMODERegister(flag);
        MODE = flag;
    }

    HashMap<Integer, CPU> descriptors;
    public CPU getDescriptor(int id){
        CPU descriptor = new CPU(realMachine);
        descriptor.id = id;
        descriptors.put(id, descriptor);
        return descriptor;
    }

}

