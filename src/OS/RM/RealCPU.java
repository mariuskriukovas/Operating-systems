package OS.RM;

import OS.Interfaces.Memory;
import OS.Tools.ByteWord;
import OS.Tools.ChannelDevice;
import OS.Tools.Constants;
import OS.Tools.Word;

public class RealCPU {

    private final ByteWord MODE = new ByteWord(Constants.INTERRUPTION.NONE);
    private final ByteWord C = new ByteWord(Constants.INTERRUPTION.NONE);

    private final ByteWord TI = new ByteWord(Constants.INTERRUPTION.NONE);
    private final ByteWord PI = new ByteWord(Constants.INTERRUPTION.NONE);
    private final ByteWord SI = new ByteWord(Constants.INTERRUPTION.NONE);

    private final Word SS = new Word(Constants.STACK_SEGMENT);
    private final Word DS = new Word(Constants.DATA_SEGMENT);
    private final Word CS = new Word(Constants.CODE_SEGMENT);
    private final Word PTR = new Word(0);

    private int currentDSBlock = 0;
    private int currentSSBlock = 0;
    private int currentCSBlock = 0;

    private final Memory internalMemory;
    private final Memory externalMemory;

    RealCPU(Memory internal, Memory external) throws Exception {
        this.externalMemory = external;
        this.internalMemory = internal;
    }


    public void createMemoryTable(int internalBlockBegin, int externalBlockBegin) throws Exception {
        setPTR(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin)));
        for (int i = 0; i < Constants.BLOCK_LENGTH; i++) {
            setPTRValue(i, new Word(externalMemory.getBlockBeginAddress(externalBlockBegin + i)));
        }
    }

    public void loadVirtualMachineMemory(int internalBlockBegin,
                                         int previousCSBlock,
                                         int previousDSBlock,
                                         int previousSSBlock) {
        try {

            currentCSBlock = previousCSBlock;
            currentDSBlock = previousDSBlock;
            currentSSBlock = previousSSBlock;

            setPTR(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin)));
            setSS(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin + 1)));
            setDS(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin + 2)));
            setCS(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin + 3)));

            loadRegisterBlock(CS, currentCSBlock);
            loadRegisterBlock(DS, currentDSBlock);
            loadRegisterBlock(SS, currentSSBlock);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int[] saveVirtualMachineMemory() {
        try {
            saveRegisterBlock(CS, currentCSBlock);
            saveRegisterBlock(DS, currentDSBlock);
            saveRegisterBlock(SS, currentSSBlock);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new int[]{currentCSBlock, currentDSBlock, currentSSBlock};
    }

    public Word getPTR() throws Exception {
        return new Word(PTR.getNumber());
    }

    public void setPTR(Word word) {
        PTR.setWord(word);
    }

    public Word getPTRValue(int block) throws Exception {
        return internalMemory.getWord(PTR.add(block));
    }

    public void setPTRValue(int block, Word word) throws Exception {
        internalMemory.setWord(word, PTR.add(block));
    }

    public void setDS(Word word) {
        DS.setWord(word);
    }

    public void setSS(Word word) {
        SS.setWord(word);
    }

    public void setCS(Word word) {
        CS.setWord(word);
    }

    public Word getDS() {
        return DS;
    }

    public Word getSS() {
        return SS;
    }

    public Word getCS() {
        return CS;
    }

    private int getBlockFromAddress(Word address) {
        return Integer.parseInt(address.getHEXFormat().substring(0, 4), 16);
    }

    private int getWordFromAddress(Word address) {
        return Integer.parseInt(address.getHEXFormat().substring(4), 16);
    }

    private void loadRegisterBlock(Word register, int block) throws Exception {
        //save this, load new
        int regBlock = getBlockFromAddress(register);
        ChannelDevice.copyBlock(externalMemory, internalMemory, block, regBlock);
    }

    private void saveRegisterBlock(Word register, int block) throws Exception {
        //save this, load new
        int regBlock = getBlockFromAddress(register);
        ChannelDevice.copyBlock(internalMemory, externalMemory, regBlock, block);
    }

    private int prepareBlock(Word virtualAddress, Word register, int currentBlock) throws Exception {
        int block = getBlockFromAddress(virtualAddress);
        if (currentBlock != block) {
            //System.out.println("---->" + "UZKRAUNA I DS : " + block + " blokas");
            saveRegisterBlock(register, currentBlock);
            loadRegisterBlock(register, block);
            return block;
        }
//    System.out.println("Segmentas ---->" + "CS");
//    System.out.println("Virtualus adresas ---->" + virtualAddress.getHEXFormat());
//    System.out.println("Realus adresas ---->" + new Word(CS.getNumber() + word).getHEXFormat());
//    System.out.println("Isorines atminties blokas ---->" + block);
        return currentBlock;
    }

    public void setCS(Word virtualAddress, Word value) throws Exception {
        currentCSBlock = prepareBlock(virtualAddress, CS, currentCSBlock);
        int word = getWordFromAddress(virtualAddress);
        internalMemory.setWord(value, CS.getNumber() + word);
    }

    public Word getCS(Word virtualAddress) throws Exception {
        currentCSBlock = prepareBlock(virtualAddress, CS, currentCSBlock);
        int word = getWordFromAddress(virtualAddress);
        return new Word(CS.getNumber() + word);
    }

    public Word getCSValue(Word virtualAddress) throws Exception {
        currentCSBlock = prepareBlock(virtualAddress, CS, currentCSBlock);
        return internalMemory.getWord(getCS(virtualAddress));
    }

    public void setSS(Word virtualAddress, Word value) throws Exception {
        currentSSBlock = prepareBlock(virtualAddress, SS, currentSSBlock);
        int word = getWordFromAddress(virtualAddress);
        internalMemory.setWord(value, SS.getNumber() + word);
    }

    public Word getSS(Word virtualAddress) throws Exception {
        currentSSBlock = prepareBlock(virtualAddress, SS, currentSSBlock);
        int word = getWordFromAddress(virtualAddress);
        return new Word(SS.getNumber() + word);
    }

    public Word getSSValue(Word virtualAddress) throws Exception {
        currentSSBlock = prepareBlock(virtualAddress, SS, currentSSBlock);
        return internalMemory.getWord(getSS(virtualAddress));
    }

    public void setDS(Word virtualAddress, Word value) throws Exception {
        currentDSBlock = prepareBlock(virtualAddress, DS, currentDSBlock);
        int word = getWordFromAddress(virtualAddress);
        internalMemory.setWord(value, DS.getNumber() + word);
    }

    public Word getDS(Word virtualAddress) throws Exception {
        currentDSBlock = prepareBlock(virtualAddress, DS, currentDSBlock);
        int word = getWordFromAddress(virtualAddress);
        return new Word(DS.getNumber() + word);
    }

    public Word getDSValue(Word virtualAddress) throws Exception {
        currentDSBlock = prepareBlock(virtualAddress, DS, currentDSBlock);
        return internalMemory.getWord(getDS(virtualAddress));
    }
}
