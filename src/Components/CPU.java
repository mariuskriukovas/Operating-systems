package Components;

import Components.UI.RMPanel;
import Components.UI.VMPanel;
import RealMachine.RealMachine;
import Tools.Constants;
import Tools.Constants.*;
import Tools.Exceptions;
import Tools.SupervisorMemory;
import Tools.Word;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

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

    private final SupervisorMemory supervisorMemory;

    private final RealMachine realMachine;

    public CPU(RealMachine realMachine){
        this.realMachine = realMachine;
        this.externalMemory = realMachine.getExternalMemory();
        this.internalMemory = realMachine.getInternalMemory();
        RMScreen = realMachine.getScreen().getScreenForRealMachine();
        VMScreen = realMachine.getScreen().getScreenForVirtualMachine();
        supervisorMemory = new SupervisorMemory(realMachine, this);
        realMachine.getScreen().setVisible(true);
        realMachine.getScreen().setReady(true);
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

    private void test(Segment segment, Word virtualAddress)
    {
        int desirableBlock = virtualAddress.getBlockFromAddress();
        int loadedBlock = -1;
        switch (segment)
        {
            case SS:
                loadedBlock = SS.getWordFromAddress() - STACK_SEGMENT/256;
                if(loadedBlock!=desirableBlock){
                    realMachine.getSwapping().setSS(desirableBlock);
                }
                break;
            case DS:
                loadedBlock = DS.getWordFromAddress() - DATA_SEGMENT/256;
                if(loadedBlock!=desirableBlock){
                    System.out.println("getWORDFromAddress: "+loadedBlock);
                    System.out.println("getBlockFromAddress: "+virtualAddress.getBlockFromAddress());
                    System.out.println("ds : : "+DS);
                    realMachine.getSwapping().setDS(desirableBlock);
                    System.out.println("ds : : "+DS);
                }
                break;
            case CS:
                loadedBlock = CS.getWordFromAddress() - CODE_SEGMENT/256;
                if(loadedBlock!=desirableBlock){
                    realMachine.getSwapping().setCS(desirableBlock);
                }
                break;
        }
    }

    public Word getSS(Word virtualAddress) throws Exceptions.WrongAddressException {
        test(Segment.SS, virtualAddress);
        long address = (PTR.getNumber() + 1) * 256 + virtualAddress.getWordFromAddress();
        try {
            return internalMemory.getWord(address);
        } catch (Exception e) {
            throw new Exceptions.WrongAddressException(WRONG_SS_BLOCK_ADDRESS,virtualAddress);
        }
    }

    public Word getDS(Word virtualAddress) throws Exceptions.WrongAddressException {
        test(Segment.DS, virtualAddress);
        long address = (PTR.getNumber() + 2) * 256 + virtualAddress.getWordFromAddress();
        try {
            return internalMemory.getWord(address);
        } catch (Exception e) {
            throw new Exceptions.WrongAddressException(WRONG_DS_BLOCK_ADDRESS,virtualAddress);
        }
    }

    public Word getCS(Word virtualAddress) throws Exceptions.WrongAddressException {
        test(Segment.CS, virtualAddress);
        long address = (PTR.getNumber() + 3) * 256 + virtualAddress.getWordFromAddress();
        try {
            return internalMemory.getWord(address);
        } catch (Exception e) {
            throw new Exceptions.WrongAddressException(WRONG_CS_BLOCK_ADDRESS,virtualAddress);
        }
    }

    public Word getByVirtualAddress(Word virtualAddress) throws Exceptions.WrongAddressException{
        if(virtualAddress.getNumber()<DATA_SEGMENT) {
            return getSS(virtualAddress);
        }else if(virtualAddress.getNumber()<CODE_SEGMENT) {
            return getDS(virtualAddress);
        }else {
            return getCS(virtualAddress);
        }
    }

    public void setSS(Word virtualAddress, Word value) throws Exceptions.WrongAddressException {
        try {
            test(Segment.SS, virtualAddress);
            long address = (PTR.getNumber() + 1) * 256 + virtualAddress.getWordFromAddress();
            internalMemory.setWord(value, address);
        }catch (Exception e)
        {
            throw new Exceptions.WrongAddressException(WRONG_SS_BLOCK_ADDRESS,virtualAddress);
        }
    }

    public void setDS(Word virtualAddress, Word value) throws Exceptions.WrongAddressException {
        test(Segment.DS, virtualAddress);
        long address = (PTR.getNumber() + 2) * 256 + virtualAddress.getWordFromAddress();
        try {
            internalMemory.setWord(value, address);
        } catch (Exception e) {
            throw new Exceptions.WrongAddressException(WRONG_DS_BLOCK_ADDRESS,virtualAddress);
        }
    }

    public void setCS(Word virtualAddress, Word value) throws Exceptions.WrongAddressException {
        test(Segment.CS, virtualAddress);
        long address = (PTR.getNumber() + 3) * 256 + virtualAddress.getWordFromAddress();
        try {
            internalMemory.setWord(value, address);
        } catch (Exception e) {
            throw new Exceptions.WrongAddressException(WRONG_DS_BLOCK_ADDRESS,virtualAddress);
        }
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

    public SupervisorMemory getSupervisorMemory() {
        return supervisorMemory;
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

    public void increaseIC() throws Exceptions.InstructionPointerException {
        try {
            setIC(IC.add(1));
        } catch (Exception e) {
            throw new Exceptions.InstructionPointerException(WRONG_IC);
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