package OS.Interfaces;

import OS.Tools.Constants;
import OS.Tools.Word;

public interface Memory {

    public int [] getBlockAndWord(long virtualAddress) throws Exception;
    public Word getWord(long virtualAddress) throws Exception;
    public Word getWord(String virtualAddress) throws Exception;
    public Word getWord(Word virtualAddress) throws Exception;
    public void setWord(Word word, long virtualAddress) throws Exception;
    public void setWord(Word word, Word virtualAddress) throws Exception;
    public boolean checkIfBlockEmpty(int block) throws Exception;
    public void cleanBlock(int block) throws Exception;
    public void setBlock(int block, Word[] data)throws Exception;
    public Word[] getBlock(int block)throws Exception;
    public int getBlockBeginAddress(int block);
}
