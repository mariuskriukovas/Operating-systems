package OS.VM;

import java.util.Arrays;


public class Word {

    public enum WORD_TYPE
    {
        NUMERIC,
        SYMBOLIC
    }

    private WORD_TYPE type;
    private int[] content = new int[6];

    Word(String word, WORD_TYPE type) throws Exception {
        this.type = type;
        if(type == WORD_TYPE.NUMERIC)createNumericWord(word);
        if(type == WORD_TYPE.SYMBOLIC)createSymbolicWord(word);
    }

    Word(int word) throws Exception {
        this.type = WORD_TYPE.NUMERIC;
        createNumericWord(word);
    }


    private String prepareWord(String word)
    {
        while (word.length()<Constants.WORD_LENGTH)
        {
            word = "0"+word;
        }
        return word;
    }

    private void  createNumericWord(String word) throws Exception {
        word = prepareWord(word);
        if (word.length()!=Constants.WORD_LENGTH)throw new Exception("Bad length");
        for (int i = 0; i<Constants.WORD_LENGTH; i++)
        {
            int number = Integer.parseInt(String.valueOf(word.charAt(i)), 16);
            if (number>15)throw new Exception("Not hex");
            content[i]= number;
        }
    }

    private void  createNumericWord(int word) throws Exception {
        if(word>Constants.MAX_NUMBER)throw new Exception("Too long");
        if(word<0)throw new Exception("Negative");

        content[0] = word/Constants.FFFFF_VALUE;
        content[1] = word/Constants.FFFF_VALUE - content[0]*Constants.F_VALUE;
        content[2] = word/Constants.FFF_VALUE - content[0]*Constants.FF_VALUE - content[1]*Constants.F_VALUE;
        content[3] = word/Constants.FF_VALUE - content[0]*Constants.FFF_VALUE - content[1]*Constants.FF_VALUE - content[2]*Constants.F_VALUE;
        content[4] = word/Constants.F_VALUE - content[0]*Constants.FFFF_VALUE - content[1]*Constants.FFF_VALUE - content[2]*Constants.FF_VALUE - content[3]*Constants.F_VALUE;
        content[5] = word%Constants.F_VALUE;
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

    public int getNumber() {
        int result = content[0]*Constants.FFFFF_VALUE
                + content[1]*Constants.FFFF_VALUE
                + content[2] * Constants.FFF_VALUE
                + content[3]*Constants.FF_VALUE
                + content[4]*Constants.F_VALUE
                + content[5];
        return result;
    }

    public String getString()
    {
        String result = "";
        for (int i = 0; i<Constants.WORD_LENGTH; i++)
        {
           result +=content[i];
        }
        return result;
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
//            if(A<Constants.F_VALUE)hex = "0"+hex;
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
