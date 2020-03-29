package OS.RM;


import OS.Tools.Memory;

public class ExternalMemory extends Memory {

    private static final int BLOCK_NUMBER = 65536;// FFFF
    private static final int BLOCK_LENGTH = 256;// FF
    private int WORD_NUMBER = BLOCK_NUMBER*BLOCK_LENGTH ;// FF FF FF

    ExternalMemory(){
        super(BLOCK_NUMBER,BLOCK_LENGTH);
    }

}
