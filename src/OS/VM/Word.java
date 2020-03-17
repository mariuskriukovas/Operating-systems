package OS.VM;

import java.lang.reflect.Array;
import java.util.Arrays;


public class Word {

    public enum WORD_TYPE
    {
        NUMERIC,
        SYMBOLIC
    }

    private WORD_TYPE type;
    private int[] content = new int[Constants.WORD_LENGTH];

    Word(String word, WORD_TYPE type) throws Exception {
        this.type = type;
        if(type == WORD_TYPE.NUMERIC)createNumericWord(word);
        if(type == WORD_TYPE.SYMBOLIC)createSymbolicWord(word);
    }

    Word(long word) throws Exception {
        this.type = WORD_TYPE.NUMERIC;
        createNumericWord(word);
    }


    private String prepareWord(String word)
    {
        while (word.length()<(Constants.WORD_LENGTH*2))
        {
            word = "0"+word;
        }
        return word;
    }
    private int[] parseWord(String word)
    {
        int j = 0;
        int[] res = new int[Constants.WORD_LENGTH];
        for(int i = 2; i<=word.length(); i=i+2) {
            res[j] = Integer.parseInt(word.substring(i-2,i),16);
            j++;
        }
        return res;
    }

    private void  createNumericWord(String word) throws Exception {
        word = prepareWord(word);
        int[] parsedHex = parseWord(word);
        if (Array.getLength(parsedHex)!=Constants.WORD_LENGTH)throw new Exception("Bad length");
        content = parsedHex;
    }

    private void  createNumericWord(long word) throws Exception {
        createNumericWord(Long.toHexString(word));
    }

    private void  createSymbolicWord(String word) throws Exception {
        if(word.length() != Constants.WORD_LENGTH)throw new Exception("Bad length for simbolic  word");
        for (int i = 0; i<Constants.WORD_LENGTH; i++)
        {
            content[i]= word.charAt(i);
        }
    }


    public void setWord(Word word)
    {
        content = word.content;
    }
    public  Word add(int value) throws Exception {
        return new Word(getNumber()+value);
    }

    public long getNumber() {
        long result =  Long.parseLong(getHEXFormat(),16);
        return result;
    }

    public String getFirstHalf(){
        return Integer.toHexString((content[0]*Constants.FF_VALUE) + content[1]);
    }
    public String getSecondHalf(){
        return Integer.toHexString ((content[2]*Constants.FF_VALUE) + content[3]);
    }

    public int[] getContent(){
        return content;
    }

    public String getASCIIFormat() {
        String result = "";
        for (int A : content)
        {
            result += ((char)A);
        }
        return result;
    };
    public String getHEXFormat() {
        String result = "";
        for (int A : content)
        {
            String hex = Integer.toHexString(A);
            if(A<16)hex = "0"+hex;
            result +=hex;
        }
        return result;
    };

    public String getINTFormat() {
        String result = "";
        for (int A : content)
        {
            result += (A + " ");
        }
        return ("[" + result +"]");
    };

    @Override
    public String toString()
    {
        switch (type)
        {
            case NUMERIC:
                return getHEXFormat();
            case SYMBOLIC:
                return getASCIIFormat();
            default:
                return getINTFormat();
        }
    }


}
