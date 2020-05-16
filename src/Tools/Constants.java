package Tools;

public class Constants {
    public static final int WORD_LENGTH = 4;
    public static final int BLOCK_LENGTH = 256;
    public static final int BLOCK_NUMBER = 256;
    public static final int WORD_NUMBER = 65535;

    public static final int STACK_SEGMENT = 0;
    public static final int DATA_SEGMENT = 21760;
    public static final int CODE_SEGMENT = 43520;

    public static final long MAX_NUMBER = 16777215;

    public enum SYSTEM_INTERRUPTION
    {
        NONE,
        TIMER_INTERUPTION,
        HALT,
    }

    public enum PROGRAM_INTERRUPTION
    {
        NONE,
        WRONG_SP,
        NEGATIVE_SP,
        WRONG_IC,
        WRONG_SS_BLOCK_ADDRESS,
        WRONG_DS_BLOCK_ADDRESS,
        WRONG_CS_BLOCK_ADDRESS,
        DIVISION_BY_ZERO,
        SOMETHING_IS_WRONG,

        GET_VALUE_FROM_SS,
        GET_VALUE_FROM_DS,
        GET_VALUE_FROM_CS,
        SET_VALUE_TO_SS,
        SET_VALUE_TO_DS,
        SET_VALUE_TO_CS,
        READ_INPUT,
        PRINT_OUTPUT,

    }


    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public enum PROCESS
    {
        NONE,
        JobGorvernor,
        MainProcess,
        JobToSwap,
        Loader,
        Parser,
        Print,
        Swapping,
        VirtualMachine,
    }

    public enum CONDITIONAL_MODE
    {
        NONE,
        MORE,
        EQUAL,
        LESS,
    }


    public enum SYSTEM_MODE
    {
        NONE,
        USER_MODE,
        SUPERVISOR_MODE,
    }

    public enum PROCESS_STATUS
    {
        COMPLETED,
        STILL_IN_PROCESS,
        FAILED,
    }

}
