package OS.RM;

import OS.Interfaces.Memory;
import OS.RM.Process.*;
import OS.Tools.Constants;
import OS.Tools.Constants.SYSTEM_INTERRUPTION;
import OS.Tools.Word;
import UI.OSFrame;
import UI.RMPanel;
import UI.VMPanel;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;

import static OS.Tools.Constants.CONDITIONAL_MODE;
import static OS.Tools.Constants.SYSTEM_MODE;


public class CPU {

    private CONDITIONAL_MODE C = CONDITIONAL_MODE.NONE;
    private SYSTEM_MODE MODE = SYSTEM_MODE.SUPERVISOR_MODE;
//    private  PROGRAM_INTERRUPTION PI = PROGRAM_INTERRUPTION.NONE;
    private SYSTEM_INTERRUPTION SI = SYSTEM_INTERRUPTION.NONE;

    private  Integer TI = 0;

    private final Word PTR = new Word(0);

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
    private final Swapping swapping;
    private final JobGorvernor jobGorvernor;
    private final PrintLine printLine;
    private final MainProc mainProc;


    private OSFrame screen;

    CPU(Memory internal, Memory external) throws Exception {
        this.externalMemory = external;
        this.internalMemory = internal;
        loader = new Loader(this);
        swapping = new Swapping(this);

        jobGorvernor = new JobGorvernor(this);
        this.screen = new OSFrame(this);
        screen.setVisible(true);
        screen.setReady(true);
        RMScreen = screen.getScreenForRealMachine();
        VMScreen = screen.getScreenForVirtualMachine();

        printLine = new PrintLine(this);
        mainProc = new MainProc(this);

        process.push(Constants.PROCESS.MainProcess);
    }


    public Swapping getSwapping() {
        return swapping;
    }

    public JobGorvernor getJobGorvernor(){return jobGorvernor;}

    private ArrayList<Object> OSStack = new ArrayList<Object>(10);

    public void saveRegisterState() {
        try {
            OSStack.add(getRH().copy());
            OSStack.add(getRL().copy());
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

// ONLY SETTERS RESPONSIBLE FOR GRAPHICS

    public Word getPTR() throws Exception {
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
        return DS;
//        switch (MODE) {
//            case SUPERVISOR_MODE:
//                return DS;
//            case USER_MODE:
//                return new Word(DATA_SEGMENT);
//            default:
//                return new Word(0);
//        }
    }

    public Word getSS() throws Exception {
        return SS;
//        switch (MODE) {
//            case SUPERVISOR_MODE:
//                return SS;
//            case USER_MODE:
//                return new Word(STACK_SEGMENT);
//            default:
//                return new Word(0);
//        }
    }

    public Word getCS() throws Exception {

        return CS;
//        switch (MODE) {
//            case SUPERVISOR_MODE:
//                return CS;
//            case USER_MODE:
//                return new Word(CODE_SEGMENT);
//            default:
//                return null;
//        }
    }

    public Word getSSB() {
        return SSB;
    }

    public Word getDSB() {
        return DSB;
    }

    public Word getCSB() {
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
        VMScreen.setDSRegister(DS);
    }

    public void setSS(Word word) {
        SS.setWord(word);
        VMScreen.setSSRegister(SS);
    }

    public void setCS(Word word) {
        CS.setWord(word);
        VMScreen.setCSRegister(CS);
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

//    public PROGRAM_INTERRUPTION getPI() {
////        RMScreen.setPIRegister(PI);
//        return PI;
//    }
//
//    public void setPI(PROGRAM_INTERRUPTION flag) {
//        PI = flag;
//        RMScreen.setPIRegister(PI);
//    }

    public SYSTEM_INTERRUPTION getSI() {
//        RMScreen.setSIRegister(SI);
        return SI;
    }

    public void setSI(SYSTEM_INTERRUPTION flag) {
        SI = flag;
        RMScreen.setSIRegister(SI);
    }

    //Virtual Machine Part

    public Word getSP() throws Exception {
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

    public void setIC(Word word) {
        IC.setWord(word);
        VMScreen.setInstructionCounter(IC);
    }

    public void increaseIC() throws Exception {
        IC.setWord(IC.add(1));
        VMScreen.setInstructionCounter(IC);
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

    private final Deque<Object> process = new ArrayDeque<Object>();

    private void checkProcess(Object flag){
        boolean isSystemProcess = Arrays.stream(Constants.PROCESS.values()).anyMatch(x->x == flag);
//        System.out.println(flag +" "+  isSystemProcess + " "+ process.size());
        if(MODE!=SYSTEM_MODE.SUPERVISOR_MODE)
        {
            if(isSystemProcess){
                setMODE(SYSTEM_MODE.SUPERVISOR_MODE);
            }
        }else {
            if(!isSystemProcess){
                setMODE(SYSTEM_MODE.USER_MODE);
            }
        }
    }

    public void showProcess(Object flag) {
        checkProcess(flag);
        process.push(flag);
        RMScreen.setActiveProcess(flag.toString());
    }

    public void showPreviousProcess() {
        process.pop();
        Object flag = process.getFirst();
        checkProcess(flag);
        RMScreen.setActiveProcess(flag.toString());
    }



    public Loader getLoader() {
        return loader;
    }

    public PrintLine getPrintLine()
    {
        return printLine;
    }

    public VMPanel getVMScreen() {
        return VMScreen;
    }

    public RMPanel getRMScreen() {
        return RMScreen;
    }


    public void setActiveProcess(String process){
        RMScreen.setActiveProcess(process);
    }

    public Memory getInternalMemory() {
        return internalMemory;
    }

    public Memory getExternalMemory() {
        return externalMemory;
    }
}

// Procesu matematika
//• SWAPING atsakingas uz svapingo mechanizmo palaikyma



// Procesu matematika
//
//• StartStop – šakninis procesas, sukuriantis bei naikinantis sisteminius procesus ir resursus.
//• ReadFromInterface – užduoties nuskaitymo iš įvedimo srauto procesas
//• JCL – užduoties programos, jos antraštės išskyrimas iš užduoties, ir jų organizavimas kaip resursu
//• JobToSwap – užduoties patalpinimas išorinėje atmintyje
//• MainProc – Procesas valdantis JobGorvernor procesus.
//• JobGorvernor – virtualios mašinos proceso tėvas, tvarkantis virtualios mašinos proceso
//        darbą
// • Loader – iš išorinės atminties duomenys perkeliami į vartotojo atmintį
//• Virtual Machine – procesas atsakantis už vartotojiškos programos vykdymą.
//• Interrupt – procesas, apdorojantis virtualios mašinos pertraukimą sukėlusią situaciją.
//• PrintLine – į išvedimo įrenginį pasiunčiama eilutė iš supervizorinės atminties.