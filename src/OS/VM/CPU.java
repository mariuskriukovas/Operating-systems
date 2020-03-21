package OS.VM;

import OS.RM.RealCPU;
import OS.Tools.ByteWord;
import OS.Tools.Constants;
import OS.Tools.Word;

public class CPU
{
    private final Word IC = new Word(0);
    private final Word PTR = new Word(0);
    private final Word SP = new Word(0);

    private final Word[] RX = new Word[2];
    private final Word RH = new Word(0);
    private final Word RL = new Word(0);

    private final ByteWord MODE = new ByteWord(Constants.INTERRUPTION.NONE);
    private final ByteWord C = new ByteWord(Constants.INTERRUPTION.NONE);

    private final ByteWord TI = new ByteWord(Constants.INTERRUPTION.NONE);
    private final ByteWord PI = new ByteWord(Constants.INTERRUPTION.NONE);
    private final ByteWord SI = new ByteWord(Constants.INTERRUPTION.NONE);

    private final Word SS = new Word(Constants.STACK_SEGMENT);
    private final Word DS = new Word(Constants.DATA_SEGMENT);
    private final Word CS = new Word(Constants.CODE_SEGMENT);

    private final RealCPU realCPU;

    CPU(RealCPU realCPU) throws Exception {
        this.realCPU = realCPU;
        RX[0] = RH;
        RX[1] = RL;
    }

    public Word getSP() throws Exception {
        return SP;
    }
    public void setSP(Word word) {
        SP.setWord(word);
    }
    public void increaseSP() throws Exception {
        SP.setWord(SP.add(1));
    }
    public void decreaseSP() throws Exception {
        SP.setWord(SP.add(-1));
    }

    public Word getRL() { return RL; }
    public void setRL(Word word) { RL.setWord(word); }

    public Word getRH() { return RH; }
    public void setRH(Word word) { RH.setWord(word); }

    public Word getIC() { return IC; }
    public void setIC(Word word) { IC.setWord(word); }
    public void increaseIC() throws Exception { IC.setWord(IC.add(1)); }

//
    public Constants.INTERRUPTION getSI() { return (Constants.INTERRUPTION) SI.getValue(); }
    public void setSI(Constants.INTERRUPTION flag) { SI.setValue(flag); }

    public Constants.C_VALUES getC() { return (Constants.C_VALUES) C.getValue(); }
    public void setC(Constants.C_VALUES flag) { C.setValue(flag); }

//--------------------------------------------------------------------------------------------------------------

    public void setDS(Word virtualAddress, Word value) throws Exception {
        //System.out.println("setDS ---->" + virtualAddress);
        //System.out.println("DS value ---->" + value);
        realCPU.setDS(getDS(virtualAddress),value);
        //memory.setWord(value,DS.getNumber() + virtualAddress.getNumber());
    }
    public Word getDS(Word virtualAddress) throws Exception {
        return new Word(DS.getNumber() + virtualAddress.getNumber());
    }
    public Word getDSValue(Word virtualAddress) throws Exception {
        //System.out.println("getDS ---->" + virtualAddress);
        //System.out.println("DS value ---->" + realCPU.getDSValue(getDS(virtualAddress)));
        //return memory.getWord(getDS(virtualAddress));
        return realCPU.getDSValue(getDS(virtualAddress));
    }

    public void setCS(Word virtualAddress, Word value) throws Exception {
        realCPU.setCS(getCS(virtualAddress),value);
        //memory.setWord(value,DS.getNumber() + virtualAddress.getNumber());
    }
    public Word getCS(Word virtualAddress) throws Exception {
        return new Word(CS.getNumber() + virtualAddress.getNumber());
    }
    public Word getCSValue(Word virtualAddress) throws Exception {
        //return memory.getWord(getDS(virtualAddress));
        return realCPU.getCSValue(getCS(virtualAddress));
    }

    public void setSSValue(Word value) throws Exception {
        realCPU.setSS(getSS(getSP()),value);
        //memory.setWord(value,DS.getNumber() + virtualAddress.getNumber());
    }
    public Word getSS(Word virtualAddress) throws Exception {
        return new Word(SS.getNumber() + virtualAddress.getNumber());
    }
    public Word getSSValue(Word virtualAddress) throws Exception {
        //return memory.getWord(getDS(virtualAddress));
        return realCPU.getSSValue(getSS(virtualAddress));
    }
    public Word getSSValue() throws Exception {
        //return memory.getWord(getDS(virtualAddress));
        return realCPU.getSSValue(getSS(getSP()));
    }

    public Word getSSValue(int n) throws Exception {
        return realCPU.getSSValue(getSS(getSP().add(-1*(n+1))));
        //return memory.getWord();
    }

}
