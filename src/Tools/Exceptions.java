package Tools;


public class Exceptions {
    public static class ProgramInteruptionException extends Exception
    {
        Constants.PROGRAM_INTERRUPTION program_interruption;
        public ProgramInteruptionException(Constants.PROGRAM_INTERRUPTION program_interruption){
            super(program_interruption.name());
            this.program_interruption = program_interruption;
        }

        @Override
        public void printStackTrace() {
            super.printStackTrace();
        }

        public Constants.PROGRAM_INTERRUPTION getReason(){
            return program_interruption;
        }
    }

    public static class StackPointerException extends ProgramInteruptionException
    {
        public StackPointerException(Constants.PROGRAM_INTERRUPTION program_interruption) {
            super(program_interruption);
        }
    }

    public static class InstructionPointerException extends ProgramInteruptionException
    {
        public InstructionPointerException(Constants.PROGRAM_INTERRUPTION program_interruption) {
            super(program_interruption);
        }
    }

    public static class WrongAddressException extends ProgramInteruptionException
    {
        private final Word address;
        public WrongAddressException(Constants.PROGRAM_INTERRUPTION program_interruption, Word address) {
            super(program_interruption);
            this.address = address;
        }

        public Word getAddress() {
            return address;
        }
    }
}

