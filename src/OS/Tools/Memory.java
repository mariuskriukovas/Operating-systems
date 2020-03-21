package OS.Tools;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Memory implements OS.Interfaces.Memory
{
    private int BLOCK_NUMBER = 0;
    private int BLOCK_LENGTH = 0;
    private int WORD_NUMBER =  0;
    private  Word[][] memory = null;

    public Memory(int BLOCK_NUMBER, int BLOCK_LENGTH){

        this.BLOCK_LENGTH = BLOCK_LENGTH;
        this.BLOCK_NUMBER = BLOCK_NUMBER;
        this.WORD_NUMBER = BLOCK_NUMBER*BLOCK_LENGTH ;
        memory = new Word[BLOCK_NUMBER][BLOCK_LENGTH];

        for (int i = 0;i<BLOCK_NUMBER; i++)
        {
            for (int j = 0;j<BLOCK_LENGTH; j++)
            {
                try {
                    memory[i][j] = new Word(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean checkIfBlockEmpty(int block) throws Exception {
        if (block>=BLOCK_NUMBER) throw new Exception("Not existing block");
        for (int i=0;i<BLOCK_LENGTH;i++){
            if(memory[block][i].getNumber()!=0)return false;
        }
        return true;
    }

    public void cleanBlock(int block) throws Exception {
        if (block>=BLOCK_NUMBER) throw new Exception("Not existing block");
        for (int i=0;i<BLOCK_LENGTH;i++){
           memory[block][i].setWord(new Word(0));
        }
    }

    public Word[] getBlock(int block)throws Exception {
        if (block>=BLOCK_NUMBER) throw new Exception("Not existing block");
        return memory[block];
    }
    public void setBlock(int block, Word[] data)throws Exception {
        if (block>=BLOCK_NUMBER) throw new Exception("Not existing block");
        if (data.length!=BLOCK_LENGTH) throw new Exception("BAD block length");
        memory[block]= data;
    }

    public int getMax(){
        return this.WORD_NUMBER;
    }
    public int getBlockBeginAddress(int block){
        return block*BLOCK_LENGTH;
    }
    public int [] getBlockAndWord(long virtualAddress) throws Exception
    {
        if(virtualAddress<0 || virtualAddress>=WORD_NUMBER)
        {
            System.out.println(" - - - -" + virtualAddress);
            throw new Exception("Not existing address");
        }
        int block = (int) (virtualAddress/BLOCK_LENGTH);
        int word = (int) (virtualAddress % BLOCK_LENGTH);
        return  new int[]{block,word};
    }

    public Word getWord(long virtualAddress) throws Exception
    {
        int address[] = getBlockAndWord(virtualAddress);
        int block = address[0];
        int word = address[1];
        return memory[block][word];
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
                writer.print(memory[i][j].toString());
                writer.print("   ");
            }
            writer.println("");
        }
        writer.close();
    }

}
