package OS.VM;

import OS.Tools.Constants;
import OS.Tools.Word;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Memory {

    private final Word[][] vmMemory = new Word[Constants.BLOCK_NUMBER][Constants.BLOCK_LENGTH];

    Memory() {
        for (int i = 0;i<Constants.BLOCK_NUMBER; i++)
        {
            for (int j = 0;j<Constants.BLOCK_LENGTH; j++)
            {
                try {
                    vmMemory[i][j] = new Word(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Word getWord(long virtualAddress) throws Exception
    {
        if(virtualAddress>Constants.WORD_NUMBER || virtualAddress<0)
        {
            System.out.println(" - - - -" + virtualAddress);
            throw new Exception("Not existing address");
        }
        int block = (int) (virtualAddress/Constants.BLOCK_NUMBER);
        int word = (int) (virtualAddress % Constants.BLOCK_NUMBER);
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


    public void setWord(Word word, long virtualAddress) throws Exception {
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
