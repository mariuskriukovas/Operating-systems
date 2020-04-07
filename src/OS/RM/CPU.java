package OS.RM;

import OS.Interfaces.Memory;
import OS.RM.Process.Loader;
import OS.Tools.Constants;
import OS.Tools.Constants.SYSTEM_INTERRUPTION;
import OS.Tools.Word;
import UI.OSFrame;
import UI.RMPanel;
import UI.VMPanel;

import java.util.ArrayList;

import static OS.Tools.Constants.*;
import static OS.Tools.Constants.SYSTEM_INTERRUPTION.*;


public class CPU {
    //Real Machine part
    private CONDITIONAL_MODE C = CONDITIONAL_MODE.NONE;
    private SYSTEM_MODE MODE = SYSTEM_MODE.NONE;

    private final PROGRAM_INTERRUPTION PI = PROGRAM_INTERRUPTION.NONE;
    private SYSTEM_INTERRUPTION SI = SYSTEM_INTERRUPTION.NONE;
    private final Integer TI = 0;

    private final Word PTR = new Word(0);


    //Virtual Machine Part
    private final Word IC = new Word(0);
    private final Word SP = new Word(0);

    private final Word[] RX = new Word[2];
    private final Word RH = new Word(0);
    private final Word RL = new Word(0);

    private final Word SS = new Word(0);
    private final Word DS = new Word(0);
    private final Word CS = new Word(0);

    private final Word SSB = new Word(0);
    private final Word DSB = new Word(0);
    private final Word CSB = new Word(0);

    private final Memory internalMemory;
    private final Memory externalMemory;

    private final RMPanel RMScreen;
    private final VMPanel VMScreen;

    private final Loader loader;
    private final Interruption interruption;


    CPU(Memory internal, Memory external, OSFrame screen) throws Exception {
        this.externalMemory = external;
        this.internalMemory = internal;
        loader = new Loader(this);
        interruption = new Interruption(this);

        RMScreen = screen.getScreenForRealMachine();
        VMScreen = screen.getScreenForVirtualMachine();
    }

    public Interruption interrupt()
    {
        return interruption;
    }

    private ArrayList<Object> OSStack = new ArrayList<Object>(10);

    public void saveRegisterState()
    {
        try {
            OSStack.add(new Word(getRH().getNumber()));
            OSStack.add(new Word(getRL().getNumber()));
            OSStack.add(getSI());
            OSStack.add(getMODE());
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void restoreRegisterState()
    {
        try {
            setMODE((SYSTEM_MODE) OSStack.remove(OSStack.size()-1));
            setSI((SYSTEM_INTERRUPTION) OSStack.remove(OSStack.size()-1));
            setRL((Word) OSStack.remove(OSStack.size()-1));
            setRH((Word) OSStack.remove(OSStack.size()-1));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //Real Machine part

    public Word getPTR() throws Exception {
        RMScreen.setPTRRegister(PTR);
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

    public Word getDS() throws Exception {
        switch (MODE)
        {
            case SUPERVISOR_MODE:
                RMScreen.setDSRegister(DS);
                return DS;
            case USER_MODE:
                RMScreen.setDSRegister(new Word(DATA_SEGMENT));
                return new Word(DATA_SEGMENT);
            default:
                return new Word(0);
        }
    }

    public Word getSS() throws Exception {
        switch (MODE)
        {
            case SUPERVISOR_MODE:
                RMScreen.setSSRegister(SS);
                return SS;
            case USER_MODE:
                RMScreen.setSSRegister(new Word(STACK_SEGMENT));
                return new Word(STACK_SEGMENT);
            default:
                return new Word(0);
        }
    }

    public Word getCS() throws Exception {

        switch (MODE)
        {
            case SUPERVISOR_MODE:
                RMScreen.setCSRegister(CS);
                return CS;
            case USER_MODE:
                RMScreen.setCSRegister(new Word(CODE_SEGMENT));
                return new Word(CODE_SEGMENT);
            default:
                return null;
        }
    }

    public Word getSSB() {
        RMScreen.setSSBRegister(SSB);
        return SSB;
    }

    public Word getDSB() {
        RMScreen.setDSBRegister(DSB);
        return DSB;
    }

    public Word getCSB() {
        RMScreen.setCSBRegister(CSB);
        return CSB;
    }

    public void setSSB(Word word) {
        RMScreen.setSSBRegister(SSB);
        SSB.setWord(word);
    }

    public void setDSB(Word word) {
        RMScreen.setDSBRegister(DSB);
        DSB.setWord(word);
    }

    public void setCSB(Word word) {
        RMScreen.setCSBRegister(CSB);
        CSB.setWord(word);
    }

    public void setDS(Word word) {
        DS.setWord(word);
        RMScreen.setDSRegister(DS);
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
        RMScreen.setMODERegister(MODE);
        return MODE;
    }

    public int getTI() {
        RMScreen.setTIRegister(TI);
        return TI;
    }

    public PROGRAM_INTERRUPTION getPI() {
        RMScreen.setPIRegister(PI);
        return PI;
    }

    public SYSTEM_INTERRUPTION getSI() {
        RMScreen.setSIRegister(SI);
        return SI;
    }

    public void setSI(SYSTEM_INTERRUPTION flag) {
        SI = flag;
        RMScreen.setSIRegister(SI);
    }

    //Virtual Machine Part

    public Word getSP() throws Exception {
        VMScreen.setStackPointer(SP);
        return SP;
    }

    public void setSP(Word word) {
        SP.setWord(word);
        VMScreen.setStackPointer(SP);
    }

    public void increaseSP() throws Exception {
        SP.setWord(SP.add(1));
        VMScreen.setStackPointer(SP);
    }

    public void decreaseSP() throws Exception {
        SP.setWord(SP.add(-1));
        VMScreen.setStackPointer(SP);
    }

    public Word getRL() {
        VMScreen.setRLRegister(RL);
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
        VMScreen.setInstructionCounter(IC);
        return IC;
    }

    public void setIC(Word word) {
        IC.setWord(word);
        VMScreen.setInstructionCounter(IC);
    }

    public void increaseIC() throws Exception {
        IC.setWord(IC.add(1));
        VMScreen.setInstructionCounter(IC);
    }

    public CONDITIONAL_MODE getC() {
        VMScreen.setCRegister(C);
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
        return internalMemory.getWord(address);
    }

    //int internalBlockBegin --> RL
    //int externalBlockBegin --> RH

    public void createMemoryTable(){
        int internalBlockBegin = (int) RL.getNumber();
        int externalBlockBegin =  (int) RH.getNumber();

        try {
            setPTR(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin)));
            for (int i = 0; i < Constants.BLOCK_LENGTH; i++) {
                setPTRValue(i, new Word(externalMemory.getBlockBeginAddress(externalBlockBegin + i)));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //  int internalBlockBegin --> RL
    public void loadVirtualMachineMemory() {
        int internalBlockBegin = (int) RL.getNumber();
        try {
            setPTR(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin)));
            setSS(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin + 1)));
            setDS(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin + 2)));
            setCS(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin + 3)));

            setSSB(new Word(STACK_SEGMENT/256));
            setCSB(new Word(CODE_SEGMENT/256));
            setDSB(new Word(DATA_SEGMENT/256));

            //    int fromBlock -->RL
            //    int toBlock --> RH
            setRL(new Word( getPTRValue( (int) CSB.getNumber()).getBlockFromAddress() ));
            setRH(new Word( getCS().getBlockFromAddress() )) ;
            loader.loadToInternalMemory();

            setRL(new Word( getPTRValue((int)SSB.getNumber()).getBlockFromAddress() ));
            setRH(new Word( getSS().getBlockFromAddress() )) ;
            loader.loadToInternalMemory();

            setRL(new Word( getPTRValue((int)DSB.getNumber()).getBlockFromAddress() ));
            setRH(new Word( getDS().getBlockFromAddress() )) ;
            loader.loadToInternalMemory();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMODE(SYSTEM_MODE flag)
    {
        MODE = flag;
    }

    public Loader getLoader()
    {
        return loader;
    }

}




//    public void loadVirtualMachineMemory(int internalBlockBegin,
//                                         int previousCSBlock,
//                                         int previousDSBlock,
//                                         int previousSSBlock) {
//        try {
//
//            currentCSBlock = previousCSBlock;
//            currentDSBlock = previousDSBlock;
//            currentSSBlock = previousSSBlock;
//
//            setPTR(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin)));
//            setSS(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin + 1)));
//            setDS(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin + 2)));
//            setCS(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin + 3)));
//
//            loadRegisterBlock(CS, currentCSBlock);
//            loadRegisterBlock(DS, currentDSBlock);
//            loadRegisterBlock(SS, currentSSBlock);
//
//            RMScreen.setSIRegister(SI);
//            RMScreen.setTIRegister(TI);
//            RMScreen.setPIRegister(PI);
//            RMScreen.setCRegister(C);
//            RMScreen.setMODERegister(MODE);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public int[] saveVirtualMachineMemory() {
//        try {
//            saveRegisterBlock(CS, currentCSBlock);
//            saveRegisterBlock(DS, currentDSBlock);
//            saveRegisterBlock(SS, currentSSBlock);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return new int[]{currentCSBlock, currentDSBlock, currentSSBlock};
//    }
//    private void loadRegisterBlock(Word register, int block) throws Exception {
//        //save this, load new
//        int regBlock = getBlockFromAddress(register);
//        ChannelDevice.copyBlock(externalMemory, internalMemory, block, regBlock);
//    }
//
//
//    private void saveRegisterBlock(Word register, int block) throws Exception {
//        //save this, load new
//        int regBlock = getBlockFromAddress(register);
//        ChannelDevice.copyBlock(internalMemory, externalMemory, regBlock, block);
//    }
//
//    private int prepareBlock(Word virtualAddress, Word register, int currentBlock) throws Exception {
//        int block = getBlockFromAddress(virtualAddress);
//        if (currentBlock != block) {
//            //System.out.println("---->" + "UZKRAUNA I DS : " + block + " blokas");
//            saveRegisterBlock(register, currentBlock);
//            loadRegisterBlock(register, block);
//            return block;
//        }
////    System.out.println("Segmentas ---->" + "CS");
////    System.out.println("Virtualus adresas ---->" + virtualAddress.getHEXFormat());
////    System.out.println("Realus adresas ---->" + new Word(CS.getNumber() + word).getHEXFormat());
////    System.out.println("Isorines atminties blokas ---->" + block);
//        return currentBlock;
//    }
//
//    }
//public class CPU
//{

//
//    CPU(RealCPU realCPU, OSFrame screen) throws Exception {
//        this.screen = screen.getScreenForVirtualMachine();
//        RMScreen = screen.getScreenForRealMachine();
//        this.realCPU = realCPU;
//        RX[0] = RH;
//        RX[1] = RL;
//
//        this.screen.setSSRegister(SS);
//        this.screen.setDSRegister(DS);
//        this.screen.setCSRegister(CS);
//        this.screen.setStackPointer(SP);
//        this.screen.setDataRegisters(RL,RH);
//        this.screen.setInstructionCounter(IC);
//        this.screen.setCRegister(C);
//    }
//    }
//    public void test() {
//
//        int currentInternalBlock = -1;
//        int currentExternalBlock = -1;
//        int newExternalBlock = -1;
//
//        System.out.println("----------------->"+"test()" + " " + SI);
//        try {
//            switch (SI) {
//                case LOADED_WRONG_SS_BLOCK:
//                    //imam SP
//                    currentInternalBlock = SS.getBlockFromAddress();
//                    currentExternalBlock = getPTRValue((int) SSB.getNumber()).getBlockFromAddress();
//                    newExternalBlock = getPTRValue(getVirtualSS(SP).getBlockFromAddress()).getBlockFromAddress();
//
//                    SSB.setWord(new Word(getVirtualSS(SP).getBlockFromAddress()));
//                    break;
//                case LOADED_WRONG_DS_BLOCK:
//                    //imam RL, vistiek paskui perarys i RL
//
//                    currentInternalBlock = DS.getBlockFromAddress();
//                    currentExternalBlock = getPTRValue((int) DSB.getNumber()).getBlockFromAddress();
//                    newExternalBlock = getPTRValue(getVirtualDS(RL).getBlockFromAddress()).getBlockFromAddress();
//
//                    System.out.println("----------------->"+"previous DSB" + " " + DSB);
//                    DSB.setWord(new Word(getVirtualDS(RL).getBlockFromAddress()));
//                    System.out.println("----------------->"+"new DSB" + " " + DSB);
//                    break;
//                case LOADED_WRONG_CS_BLOCK:
//                    //zaidziam gudriai imam IC
//                    //uzkrauk AA bloka i cs
//
//                    currentInternalBlock = CS.getBlockFromAddress();
//                    currentExternalBlock = getPTRValue((int)CSB.getNumber()).getBlockFromAddress();
//                    newExternalBlock =getPTRValue(getVirtualCS(IC).getBlockFromAddress()).getBlockFromAddress();
//
//                    CSB.setWord(new Word(getVirtualCS(IC).getBlockFromAddress()));
//                    break;
//            }
//
//            //    int fromBlock -->RL
//            //    int toBlock --> RH
//            setRL(new Word( currentInternalBlock ));
//            setRH(new Word( currentExternalBlock )) ;
//            loader.loadToExternalMemory();
//
//            setRL(new Word( newExternalBlock ));
//            setRH(new Word( currentInternalBlock )) ;
//            loader.loadToInternalMemory();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//    public Word getVirtualDS(Word virtualAddress) throws Exception {
//        return new Word(DATA_SEGMENT + virtualAddress.getNumber());
//    }
//
//    public Word getVirtualCS(Word virtualAddress) throws Exception {
//        return new Word(CODE_SEGMENT + virtualAddress.getNumber());
//    }
//
//    public Word getVirtualSS(Word virtualAddress) throws Exception {
//        return new Word(STACK_SEGMENT + virtualAddress.getNumber());
//    }
//
//    public void setVirtualDS(Word virtualAddress, Word value) throws Exception {
//        //System.out.println("setDS ---->" + virtualAddress);
//        //System.out.println("DS value ---->" + value);
//        setDS(getVirtualDS(virtualAddress), value);
//    }
//
//    public Word getVirtualDSValue(Word virtualAddress) throws Exception {
//        System.out.println("getDS ---->" + virtualAddress);
//        System.out.println("DS value ---->" + getDSValue(getVirtualDS(virtualAddress)));
//        //return memory.getWord(getDS(virtualAddress));
//        return getDSValue(getVirtualDS(virtualAddress));
//    }
//
//    public void setVirtualCS(Word virtualAddress, Word value) throws Exception {
//        setCS(getVirtualCS(virtualAddress), value);
//    }
//
//    public Word getVirtualCSValue(Word virtualAddress) throws Exception {
//        return getCSValue(getVirtualCS(virtualAddress));
//    }
//
//    public void setVirtualSSValue(Word value) throws Exception {
//        setSS(getVirtualSS(getSP()), value);
//    }
//
//    public Word getVirtualSSValue(Word virtualAddress) throws Exception {
//        return getSSValue(getVirtualSS(virtualAddress));
//    }
//
//    public Word getVirtualSSValue() throws Exception {
//        return getSSValue(getVirtualSS(getSP()));
//    }
//
//    public Word getVirtualSSValue(int n) throws Exception {
//        return getSSValue(getVirtualSS(getSP().add(-1 * (n + 1))));
//    }

//---------------------------------------------------------------------------
