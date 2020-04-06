package OS.Tools;

public class Constants {
    public static final int WORD_LENGTH = 4;
    public static final int BLOCK_LENGTH = 256;
    public static final int BLOCK_NUMBER = 256;
    public static final int WORD_NUMBER = 65535;

    public static final int STACK_SEGMENT = 0;
    public static final int DATA_SEGMENT = 21760;
    public static final int CODE_SEGMENT = 43520;

    public static final long MAX_NUMBER = 16777215;


    public enum CONDITIONAL_MODE
    {
        NONE,
        ZERO,
        ONE,
        TWO
    }


    public enum SYSTEM_MODE
    {
        NONE,
        USER_MODE,
        SUPERVISOR_MODE,
    }

    public enum PROGRAM_INTERRUPTION
    {
        NONE,

    }

    public enum SYSTEM_INTERRUPTION
    {
        NONE,
        LOADED_WRONG_SS_BLOCK,
        LOADED_WRONG_DS_BLOCK,
        LOADED_WRONG_CS_BLOCK,
        HALT,
    }

}
