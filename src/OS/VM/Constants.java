package OS.VM;

public class Constants {
    public static final int WORD_LENGTH = 4;
    public static final int BLOCK_LENGTH = 256;
    public static final int BLOCK_NUMBER = 256;
    public static final int WORD_NUMBER = 65535;


    public static final int STACK_SEGMENT = 0;
    public static final int DATA_SEGMENT = 21845;
    public static final int CODE_SEGMENT = 43690;

    public static final long MAX_NUMBER = 16777215;

    public static final int F_VALUE = 16;
    public static final int FF_VALUE = 256;
    public static final int FFF_VALUE = 4096;
    public static final int FFFF_VALUE = 65536;
    public static final int FFFFF_VALUE = 1048576;

    public enum C_VALUES
    {
        EQUAL,
        LESS,
        MORE,
        SYMBOLS,
        NUMBERS,
    }

    public enum FILE_SEG
    {
        DATSEG,
        CODSEG
    }


    public enum INTERRUPTION
    {
        NONE,
        HALT,
    }
}
