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
import java.util.List;

import static OS.Tools.Constants.CODE_SEGMENT;
import static OS.Tools.Constants.CONDITIONAL_MODE;
import static OS.Tools.Constants.DATA_SEGMENT;
import static OS.Tools.Constants.PROGRAM_INTERRUPTION;
import static OS.Tools.Constants.STACK_SEGMENT;
import static OS.Tools.Constants.SYSTEM_MODE;


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
    private OSFrame screen;


    CPU(Memory internal, Memory external) throws Exception {
        this.externalMemory = external;
        this.internalMemory = internal;
        loader = new Loader(this);
        interruption = new Interruption(this);

        this.screen = new OSFrame(this);
        screen.setVisible(true);
        screen.setReady(true);
        RMScreen = screen.getScreenForRealMachine();
        VMScreen = screen.getScreenForVirtualMachine();
    }

    public Interruption interrupt() {
        return interruption;
    }

    private ArrayList<Object> OSStack = new ArrayList<Object>(10);

    public void saveRegisterState() {
        try {
            OSStack.add(new Word(getRH().getNumber()));
            OSStack.add(new Word(getRL().getNumber()));
            OSStack.add(getSI());
            OSStack.add(getMODE());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restoreRegisterState() {
        try {
            setMODE((SYSTEM_MODE) OSStack.remove(OSStack.size() - 1));
            setSI((SYSTEM_INTERRUPTION) OSStack.remove(OSStack.size() - 1));
            setRL((Word) OSStack.remove(OSStack.size() - 1));
            setRH((Word) OSStack.remove(OSStack.size() - 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Real Machine parthttps://www.youtube.com/watch?v=aJB6s-IWGH4&fbclid=IwAR3sRvdIWGlZyWjnXsPqn7tr6CbpiSTpR-btr9k9JgXer3YCcmAaViorVdU

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
        switch (MODE) {
            case SUPERVISOR_MODE:
                RMScreen.setDSBRegister(DS);
                return DS;
            case USER_MODE:
                RMScreen.setDSBRegister(new Word(DATA_SEGMENT));
                return new Word(DATA_SEGMENT);
            default:
                return new Word(0);
        }
    }

    public Word getSS() throws Exception {
        switch (MODE) {
            case SUPERVISOR_MODE:
                RMScreen.setSSBRegister(SS);
                return SS;
            case USER_MODE:
                RMScreen.setSSBRegister(new Word(STACK_SEGMENT));
                return new Word(STACK_SEGMENT);
            default:
                return new Word(0);
        }
    }

    public Word getCS() throws Exception {

        switch (MODE) {
            case SUPERVISOR_MODE:
                RMScreen.setCSBRegister(CS);
                return CS;
            case USER_MODE:
                RMScreen.setCSBRegister(new Word(CODE_SEGMENT));
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
        RMScreen.setDSBRegister(DS);
    }

    public void setSS(Word word) {
        SS.setWord(word);
        RMScreen.setSSBRegister(SS);
    }

    public void setCS(Word word) {
        CS.setWord(word);
        RMScreen.setCSBRegister(CS);
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
        SP.setWord(SP.add(- 1));
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
        System.out.println("PPP");
        int block = address.getBlockFromAddress();
        System.out.println("PPP2");
        try {
            if (block == DS.getBlockFromAddress()) {
                System.out.println("DS.getBlockFromAddress()");
                VMScreen.setDataSegment(internalMemory.getBlock(block));
            }
            if (block == CS.getBlockFromAddress()) {
                System.out.println("CS.getBlockFromAddress()");
                VMScreen.setCodeSegment(internalMemory.getBlock(block));
            }
            if (block == SS.getBlockFromAddress()) {
                System.out.println("SS.getBlockFromAddress()");
                VMScreen.setStackSegment(internalMemory.getBlock(block));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Word getFromInternalMemory(Word address) throws Exception {
        int block = address.getBlockFromAddress();
        try {
            if (block == DS.getBlockFromAddress()) VMScreen.setDataSegment(internalMemory.getBlock(block));
            if (block == CS.getBlockFromAddress()) VMScreen.setCodeSegment(internalMemory.getBlock(block));
            if (block == SS.getBlockFromAddress()) VMScreen.setStackSegment(internalMemory.getBlock(block));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return internalMemory.getWord(address);
    }

    //int internalBlockBegin --> RL
    //int externalBlockBegin --> RH

    public void createMemoryTable() {
        int internalBlockBegin = (int) RL.getNumber();
        int externalBlockBegin = (int) RH.getNumber();

        try {
            setPTR(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin)));
            for (int i = 0; i < Constants.BLOCK_LENGTH; i++) {
                setPTRValue(i, new Word(externalMemory.getBlockBeginAddress(externalBlockBegin + i)));
            }
        } catch (Exception e) {
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

            setSSB(new Word(STACK_SEGMENT / 256));
            setCSB(new Word(CODE_SEGMENT / 256));
            setDSB(new Word(DATA_SEGMENT / 256));

            //    int fromBlock -->RL
            //    int toBlock --> RH
            setRL(new Word(getPTRValue((int) CSB.getNumber()).getBlockFromAddress()));
            setRH(new Word(getCS().getBlockFromAddress()));
            loader.loadToInternalMemory();

            setRL(new Word(getPTRValue((int) SSB.getNumber()).getBlockFromAddress()));
            setRH(new Word(getSS().getBlockFromAddress()));
            loader.loadToInternalMemory();

            setRL(new Word(getPTRValue((int) DSB.getNumber()).getBlockFromAddress()));
            setRH(new Word(getDS().getBlockFromAddress()));
            loader.loadToInternalMemory();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMODE(SYSTEM_MODE flag) {
        MODE = flag;
    }

    public Loader getLoader() {
        return loader;
    }

    public VMPanel getVMScreen() {
        return VMScreen;
    }

    public RMPanel getRMScreen() {
        return RMScreen;
    }

    public void writeDS(List<String> lines) throws Exception {
        System.out.println("lines1");
        setRL(new Word(151));
        long address = getRL().getNumber();
        System.out.println("lines2 " + address);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.length() != 6) {
                while (line.length() != 6) {
                    line = line + " ";
                }
            }
            System.out.println("line " + line);
            try {
                System.out.println("before interupt1");
                setRL(new Word(address + i));
                System.out.println("before interupt2 " + line);
                setRH(new Word(line, Word.WORD_TYPE.SYMBOLIC));
                System.out.println("before interupt");
                interrupt().SETDS();
                System.out.println("after interupt");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Finish writing");
        for (int i = 0; i < lines.size(); i++) {
            externalMemory.getWord(address + i);
        }
    }

    public Memory getInternalMemory() {
        return internalMemory;
    }

    public Memory getExternalMemory() {
        return externalMemory;
    }
}