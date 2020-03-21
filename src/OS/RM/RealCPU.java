package OS.RM;

import OS.Interfaces.Memory;
import OS.Tools.ByteWord;
import OS.Tools.ChannelDevice;
import OS.Tools.Constants;
import OS.Tools.Word;

public class RealCPU
{

    private final ByteWord MODE = new ByteWord(Constants.INTERRUPTION.NONE);
    private final ByteWord C = new ByteWord(Constants.INTERRUPTION.NONE);

    private final ByteWord TI = new ByteWord(Constants.INTERRUPTION.NONE);
    private final ByteWord PI = new ByteWord(Constants.INTERRUPTION.NONE);
    private final ByteWord SI = new ByteWord(Constants.INTERRUPTION.NONE);

    private final Word SS = new Word(Constants.STACK_SEGMENT);
    private final Word DS = new Word(Constants.DATA_SEGMENT);
    private final Word CS = new Word(Constants.CODE_SEGMENT);
    private final Word PTR = new Word(0);

    private final Memory internalMemory;
    private final Memory externalMemory;

    RealCPU(Memory internal, Memory external) throws Exception {
        this.externalMemory = external;
        this.internalMemory = internal;

        createMemoryTable(0, 500);
        System.out.println("Nuo" + " " + getPTRValue(0) + " iki "+ getPTRValue(255));
    }

    public Word convertToRealAddress(int address) throws Exception {
        Word addr = new Word(address);
        String blockNumber = addr.getHEXFormat().substring(2,4);
        String wordNumber = addr.getHEXFormat().substring(4);
        Word realAddress = getPTRValue(Integer.parseInt(blockNumber,16));
        realAddress = realAddress.add(Integer.parseInt(wordNumber,16));
        return realAddress;
    }

    public void createMemoryTable(int internalBlockBegin,int externalBlockBegin){
        try {
            setPTR(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin)));
            for (int i = 0; i<Constants.BLOCK_LENGTH; i++){
                setPTRValue(i,new Word(externalMemory.getBlockBeginAddress(externalBlockBegin+i)));
            }
            setSS(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin + 1)));
            setDS(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin + 2)));
            setCS(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin + 3)));
            loadRegisterBlock(CS,currentCSBlock);
            loadRegisterBlock(DS,currentDSBlock);
            loadRegisterBlock(SS,currentSSBlock);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Word getPTR() throws Exception { return new Word(PTR.getNumber()); }
    public void setPTR(Word word) {PTR.setWord(word); }

    public Word getPTRValue(int block) throws Exception {
        return internalMemory.getWord(PTR.add(block));
    }
    public void setPTRValue(int block, Word word) throws Exception  {
        internalMemory.setWord(word,PTR.add(block));
    }

    public void setDS(Word word) { DS.setWord(word); }
    public void setSS(Word word) { SS.setWord(word); }
    public void setCS(Word word) { CS.setWord(word); }

    public Word getDS() { return DS; }
    public Word getSS() { return SS; }
    public Word getCS() { return CS; }

//    SS = 00 00 00 00,  DS = 00 00 55 55,  CS = 00 00 AA AA,
    private int currentDSBlock = 0;
    private int currentSSBlock = 0;
    private int currentCSBlock = 0;

    private void loadRegisterBlock(Word register, int block) throws Exception {
        //save this, load new
        int regBlock = Integer.parseInt(register.getHEXFormat().substring(0,4),16);
        ChannelDevice.copyBlock(externalMemory,internalMemory,block,regBlock);
        currentCSBlock = block;
    }

    private void saveRegisterBlock(Word register, int block) throws Exception {
        //save this, load new
        int regBlock = Integer.parseInt(register.getHEXFormat().substring(0,4),16);
        ChannelDevice.copyBlock(internalMemory,externalMemory,regBlock,block);
    }
// ---------------------------------------------------------------------------------------------------------------------------------------
private void prepareCSBlock(Word virtualAddress) throws Exception {
    int block = Integer.parseInt(virtualAddress.getHEXFormat().substring(0, 4), 16);
    int word = Integer.parseInt(virtualAddress.getHEXFormat().substring(4), 16);
    if (currentCSBlock != block) {
//            System.out.println("---->" + "UZKRAUNA I CS : " + block + " blokas");
        saveRegisterBlock(CS, currentCSBlock);
        loadRegisterBlock(CS, block);
        currentCSBlock = block;
    }
//    System.out.println("Segmentas ---->" + "CS");
//    System.out.println("Virtualus adresas ---->" + virtualAddress.getHEXFormat());
//    System.out.println("Realus adresas ---->" + new Word(CS.getNumber() + word).getHEXFormat());
//    System.out.println("Isorines atminties blokas ---->" + block);
}

    private void prepareSSBlock(Word virtualAddress) throws Exception {
        int block = Integer.parseInt(virtualAddress.getHEXFormat().substring(0, 4), 16);
        int word = Integer.parseInt(virtualAddress.getHEXFormat().substring(4), 16);
        if (currentSSBlock != block) {
//          System.out.println("---->" + "UZKRAUNA I DS : " + block + " blokas");
            saveRegisterBlock(SS, currentSSBlock);
            loadRegisterBlock(SS, block);
            currentSSBlock = block;
        }
//        System.out.println("Segmentas ---->" + "SS");
//        System.out.println("Virtualus adresas ---->" + virtualAddress.getHEXFormat());
//        System.out.println("Realus adresas ---->" + new Word(SS.getNumber() + word).getHEXFormat());
//        System.out.println("Isorines atminties blokas ---->" + block);
    }

    private void prepareDSBlock(Word virtualAddress) throws Exception {
        int block = Integer.parseInt(virtualAddress.getHEXFormat().substring(0, 4), 16);
        int word = Integer.parseInt(virtualAddress.getHEXFormat().substring(4), 16);
        if (currentDSBlock != block) {
//            System.out.println("---->" + "UZKRAUNA I DS : " + block + " blokas");
            saveRegisterBlock(DS, currentDSBlock);
            loadRegisterBlock(DS, block);
            currentDSBlock = block;
        }
//        System.out.println("Segmentas ---->" + "DS");
//        System.out.println("Virtualus adresas ---->" + virtualAddress.getHEXFormat());
//        System.out.println("Realus adresas ---->" + new Word(DS.getNumber() + word).getHEXFormat());
//        System.out.println("Isorines atminties blokas ---->" + block);
    }

//    ----------------------------------------------------------------------------------------------------------------------------------
    public void setCS(Word virtualAddress, Word value) throws Exception {
        prepareCSBlock(virtualAddress);
        int word = Integer.parseInt(virtualAddress.getHEXFormat().substring(4),16);
        internalMemory.setWord(value,CS.getNumber() + word);
    }
    public Word getCS(Word virtualAddress) throws Exception {
        prepareCSBlock(virtualAddress);
        int word = Integer.parseInt(virtualAddress.getHEXFormat().substring(4),16);
        return new Word(CS.getNumber() + word);
    }
    public Word getCSValue(Word virtualAddress) throws Exception {
        prepareCSBlock(virtualAddress);
        return internalMemory.getWord(getCS(virtualAddress));
    }

    public void setSS(Word virtualAddress, Word value) throws Exception {
        prepareSSBlock(virtualAddress);
        int word = Integer.parseInt(virtualAddress.getHEXFormat().substring(4),16);
        internalMemory.setWord(value,SS.getNumber() + word);
    }
    public Word getSS(Word virtualAddress) throws Exception {
        prepareSSBlock(virtualAddress);
        int word = Integer.parseInt(virtualAddress.getHEXFormat().substring(4),16);
        return new Word(SS.getNumber() + word);
    }
    public Word getSSValue(Word virtualAddress) throws Exception {
        prepareSSBlock(virtualAddress);
        return internalMemory.getWord(getSS(virtualAddress));
    }

    public void setDS(Word virtualAddress, Word value) throws Exception {
        prepareDSBlock(virtualAddress);
        int word = Integer.parseInt(virtualAddress.getHEXFormat().substring(4),16);
        internalMemory.setWord(value,DS.getNumber() + word);
    }
    public Word getDS(Word virtualAddress) throws Exception {
        prepareDSBlock(virtualAddress);
        int word = Integer.parseInt(virtualAddress.getHEXFormat().substring(4),16);
        return new Word(DS.getNumber() + word);
    }
    public Word getDSValue(Word virtualAddress) throws Exception {
        prepareDSBlock(virtualAddress);
        return internalMemory.getWord(getDS(virtualAddress));
    }
}
