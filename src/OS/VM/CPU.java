package OS.VM;

import OS.RM.RealCPU;
import OS.Tools.ByteWord;
import OS.Tools.Constants;
import OS.Tools.Constants.INTERRUPTION;
import OS.Tools.Word;
import UI.VMPanel;

import java.util.ArrayList;

import static OS.Tools.Constants.CODE_SEGMENT;
import static OS.Tools.Constants.DATA_SEGMENT;
import static OS.Tools.Constants.INTERRUPTION.NONE;
import static OS.Tools.Constants.STACK_SEGMENT;

public class CPU
{
    private final Word IC = new Word(0);
    private final Word PTR = new Word(0);
    private final Word SP = new Word(0);

    private final Word[] RX = new Word[2];
    private final Word RH = new Word(0);
    private final Word RL = new Word(0);

    private final ByteWord MODE = new ByteWord(NONE);
    private final ByteWord C = new ByteWord(NONE);

    private final ByteWord TI = new ByteWord(NONE);
    private final ByteWord PI = new ByteWord(NONE);
    private final ByteWord SI = new ByteWord(NONE);

    private final Word SS = new Word(STACK_SEGMENT);
    private final Word DS = new Word(DATA_SEGMENT);
    private final Word CS = new Word(CODE_SEGMENT);

    private final RealCPU realCPU;
    private final VMPanel screen;

    CPU(RealCPU realCPU, VMPanel screen) throws Exception {
        this.screen = screen;
        this.realCPU = realCPU;
        RX[0] = RH;
        RX[1] = RL;

        screen.setSSRegister(SS);
        screen.setDSRegister(DS);
        screen.setCSRegister(CS);
        screen.setStackPointer(SP);
        screen.setDataRegisters(RL,RH);
        screen.setInstructionCounter(IC);
        screen.setCRegister(C);
    }

    public Word getSP() throws Exception {
        return SP;
    }
    public void setSP(Word word) {
        SP.setWord(word);
        screen.setStackPointer(SP);
    }
    public void increaseSP() throws Exception {
        SP.setWord(SP.add(1));
        screen.setStackPointer(SP);
    }
    public void decreaseSP() throws Exception {
        SP.setWord(SP.add(-1));
        screen.setStackPointer(SP);
    }

    public Word getRL() { return RL; }

    public void setRL(Word word) {
        RL.setWord(word);
        screen.setDataRegisters(RL,RH);
    }

    public Word getRH() { return RH; }
    public void setRH(Word word) {
        RH.setWord(word);
        screen.setDataRegisters(RL,RH);
    }

    public Word getIC() { return IC; }
    public void setIC(Word word) {
        IC.setWord(word);
        screen.setInstructionCounter(IC);
    }
    public void increaseIC() throws Exception {
        IC.setWord(IC.add(1));
        screen.setInstructionCounter(IC);
    }

//
    public INTERRUPTION getSI() { return (INTERRUPTION) SI.getValue(); }
    public void setSI(INTERRUPTION flag) {
        SI.setValue(flag);
        screen.setSIRegister(SI);
    }

    public Constants.C_VALUES getC() { return (Constants.C_VALUES) C.getValue(); }
    public void setC(Constants.C_VALUES flag) {
        C.setValue(flag);
        screen.setCRegister(C);
    }

//--------------------------------------------------------------------------------------------------------------

    public void setDS(Word virtualAddress, Word value) throws Exception {
//        System.out.println("setDS ---->" + virtualAddress);
//        System.out.println("DS value ---->" + value);
        realCPU.setDS(getDS(virtualAddress),value);
        //memory.setWord(value,DS.getNumber() + virtualAddress.getNumber());
    }
    public Word getDS(Word virtualAddress) throws Exception {
        return new Word(DS.getNumber() + virtualAddress.getNumber());
    }
    public Word getDSValue(Word virtualAddress) throws Exception {
        System.out.println("getDS ---->" + virtualAddress);
        System.out.println("DS value ---->" + realCPU.getDSValue(getDS(virtualAddress)));
        //return memory.getWord(getDS(virtualAddress));
        return realCPU.getDSValue(getDS(virtualAddress));
    }

    public void setCS(Word virtualAddress, Word value) throws Exception {
        realCPU.setCS(getCS(virtualAddress),value);
    }

    public Word getCS(Word virtualAddress) throws Exception {
        return new Word(CS.getNumber() + virtualAddress.getNumber());
    }
    public Word getCSValue(Word virtualAddress) throws Exception {
        return realCPU.getCSValue(getCS(virtualAddress));
    }

    public void setSSValue(Word value) throws Exception {
        realCPU.setSS(getSS(getSP()),value);
    }
    public Word getSS(Word virtualAddress) throws Exception {
        return new Word(SS.getNumber() + virtualAddress.getNumber());
    }
    public Word getSSValue(Word virtualAddress) throws Exception {
        return realCPU.getSSValue(getSS(virtualAddress));
    }
    public Word getSSValue() throws Exception {
        return realCPU.getSSValue(getSS(getSP()));
    }

    public Word getSSValue(int n) throws Exception {
        return realCPU.getSSValue(getSS(getSP().add(-1*(n+1))));
    }

}
