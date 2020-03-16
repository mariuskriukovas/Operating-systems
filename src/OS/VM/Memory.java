package OS.VM;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class Memory {

    private final Word[][] vmMemory = new Word[Constants.BLOCK_NUMBER][Constants.BLOCK_LENGTH];

    public Word getWord(int virtualAddress) throws Exception
    {
        if(virtualAddress>Constants.WORD_NUMBER || virtualAddress<0)
        {
            throw new Exception("Not existing address");
        }

        int block = virtualAddress/Constants.BLOCK_NUMBER;
        int word = virtualAddress % Constants.BLOCK_NUMBER;
        return vmMemory[block][word];
    }

    public Word getWord(String virtualAddress) throws Exception
    {
        int decimalAddress = Integer.parseInt(virtualAddress, 16);
        return getWord(decimalAddress);
    }

    public Word getWord(Word virtualAddress) throws Exception
    {
        return getWord(virtualAddress.getNumber());
    }


    public void setWord(Word word, int virtualAddress) throws Exception {
        getWord(virtualAddress).setWord(word);
    }

    public void setWord(Word word, Word virtualAddress) throws Exception {
        getWord(virtualAddress.getNumber()).setWord(word);
    }

    private String createWord(int val)
    {
        String hex = Integer.toHexString(val);
        for (int i = hex.length(); i <Constants.WORD_LENGTH; i++)
        {
            hex = "0"+hex;
        }
        return hex;
    }
    Memory() {
        for (int i = 0;i<Constants.BLOCK_NUMBER; i++)
        {
            for (int j = 0;j<Constants.BLOCK_LENGTH; j++)
            {
                try {
                    vmMemory[i][j] = new Word(i*256 + j);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void print()
    {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("the-file-name.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (int i = 0;i<Constants.BLOCK_NUMBER; i++)
        {
            for (int j = 0;j<Constants.BLOCK_LENGTH; j++)
            {
                writer.print(vmMemory[i][j].toString());
                writer.print("   ");
            }
            writer.println("");
        }
        writer.close();
    }

}
