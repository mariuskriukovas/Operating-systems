package OS.Tools;

import java.lang.reflect.Array;
import java.util.Arrays;

import static OS.Tools.Word.WORD_TYPE.NUMERIC;
import static OS.Tools.Word.WORD_TYPE.SYMBOLIC;


public class Word {

    public static final String EMPTY = "";

    public enum WORD_TYPE
    {
        NUMERIC,
        SYMBOLIC
    }

    private final int WORD_LENGTH = 6;
    private WORD_TYPE type;
    private int[] content = new int[WORD_LENGTH];

    public Word(String word, WORD_TYPE type) throws Exception {
        this.type = type;
        if(type == NUMERIC)createNumericWord(word);
        if(type == SYMBOLIC)createSymbolicWord(word);
    }

    public Word(long word) throws Exception {
        this.type = NUMERIC;
        createNumericWord(word);
    }


    private String prepareWord(String word)
    {
        while (word.length()<WORD_LENGTH)
        {
            word = "0"+word;
        }
        return word;
    }
    private int[] parseWord(String word)
    {
        int[] res = new int[WORD_LENGTH];
        for(int i = 0; i<word.length(); i++) {
            res[i] = Integer.parseInt(Character.toString(word.charAt(i)),16);
        }
        return res;
    }

    private void  createNumericWord(String word) throws Exception {
        word = prepareWord(word);
        int[] parsedHex = parseWord(word);
        if (Array.getLength(parsedHex)!=WORD_LENGTH)throw new Exception("Bad length");
        content = parsedHex;
    }

    private void  createNumericWord(long word) throws Exception {
            createNumericWord(Long.toHexString(word));
    }

    private void  createSymbolicWord(String word) throws Exception {
        if(word.length() != WORD_LENGTH)throw new Exception("Bad length for simbolic  word");
        for (int i = 0; i<WORD_LENGTH; i++)
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

    public int[] getContent(){
        return content;
    }

    public String getASCIIFormat() {
        String result = EMPTY;
        for (int A : content)
        {
            result += ((char)A);
        }
        return result;
    };
    public String getHEXFormat() {
        String result = EMPTY;
        for (int A : content)
        {
            String hex = Integer.toHexString(A);
            result +=hex;
        }
        return result;
    };

    public String getINTFormat() {
        String result = EMPTY;
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
                return getASCIIFormat();
        }
    }

}
