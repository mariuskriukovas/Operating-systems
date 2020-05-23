package VirtualMachine;

import Components.CPU;
import Tools.Constants;
import Tools.Constants.PROGRAM_INTERRUPTION;
import Tools.Exceptions;
import Tools.Exceptions.InstructionPointerException;
import Tools.Exceptions.ProgramInteruptionException;
import Tools.Exceptions.WrongAddressException;
import Tools.Word;

import java.math.BigInteger;

import static Tools.Constants.CONDITIONAL_MODE.EQUAL;
import static Tools.Constants.CONDITIONAL_MODE.LESS;
import static Tools.Constants.CONDITIONAL_MODE.MORE;
import static Tools.Constants.PROGRAM_INTERRUPTION.DIVISION_BY_ZERO;
import static Tools.Constants.SYSTEM_INTERRUPTION.HALT;
import static Tools.Constants.SYSTEM_INTERRUPTION.PRINTLINE_GET;
import static Tools.Constants.SYSTEM_INTERRUPTION.PRINTLINE_PUT;
import static Tools.Constants.SYSTEM_INTERRUPTION.PRINTLINE_PUT_R;
import static Tools.Word.WORD_TYPE.NUMERIC;
import static java.lang.Long.parseLong;

public class Interpretator {
    private final CPU cpu;

    public Interpretator(CPU cpu) {
        this.cpu = cpu;
    }

    public void execute(String command) {
        if (command.contains("ADD")) {
            ADD();
        } else if (command.contains("SUB")) {
            SUB();
        } else if (command.contains("MUL")) {
            MUL();
        } else if (command.contains("DIV")) {
            DIV();
        } else if (command.contains("CMR")) {
            CMR();
        } else if (command.contains("CM")) {
            CM();
        } else if (command.contains("JUMPR")) {
            JUMPR();
        } else if (command.contains("JUMP")) {
            JUMP();
        } else if (command.contains("JAR")) {
            JAR();
        } else if (command.contains("JA")) {
            JA();
        } else if (command.contains("JBR")) {
            JBR();
        } else if (command.contains("JB")) {
            JB();
        } else if (command.contains("JER")) {
            JER();
        } else if (command.contains("JE")) {
            JE();
        } else if (command.contains("PUSH")) {
            PUSH();
        } else if (command.contains("POP")) {
            POP();
        } else if (command.contains("SWAP")) {
            SWAP();
        } else if (command.contains("LB")) {
            LOADB();
        } else if (command.contains("LW")) {
            LOADW();
        } else if (command.contains("SV")) {
            SAVE();
        } else if (command.contains("GT")) {
            GET();
        } else if (command.contains("PT")) {
            PUT();
        } else if (command.contains("HALT")) {
            HALT();
        } else if (command.contains("PRINTR")) {
            PRINTR();
        } else {
            System.out.print("Not found");
        }
    }

    private void PUSH() {
        Word value = cpu.getRL().copy();
        Word address = cpu.getSP().copy();
        try {
            cpu.setSS(address, value);
            cpu.increaseSP();
        } catch (ProgramInteruptionException e) {
            e.printStackTrace();
            PROGRAM_INTERRUPTION interruption = e.getReason();
            cpu.setPI(interruption);
        }
    }

    private void POP() {
        try {
            cpu.decreaseSP();
            Word address = cpu.getSP().copy();
            Word value = cpu.getSS(address);
            if (value == null) return;
            cpu.setRL(value);
        } catch (ProgramInteruptionException e) {
            e.printStackTrace();
            PROGRAM_INTERRUPTION interruption = e.getReason();
            cpu.setPI(interruption);
        }
    }

    private void ADD() {
        System.out.println("ADD()");
        try {
            POP();
            long op1 = cpu.getRL().getNumber();
            POP();
            long op2 = cpu.getRL().getNumber();
            long result = op1 + op2;
            long manyF = parseLong("ffffff", 16);
            if (result > manyF) {
                long rl = (result % manyF);
                cpu.setRL(new Word(rl));
                long rh = (result / manyF);
                cpu.setRH(new Word(rh));
                cpu.setC(MORE);
            } else {
                cpu.setRL(new Word(result));
                cpu.setC(LESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SUB() {
        System.out.println("SUB()");
        try {
            POP();
            long op1 = cpu.getRL().getNumber();
            POP();
            long op2 = cpu.getRL().getNumber();
            long result = op1 - op2;
            cpu.setRL(new Word(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void MUL() {
        System.out.println("MUL()");
        try {
            POP();
            long op1 = cpu.getRL().getNumber();
            POP();
            long op2 = cpu.getRL().getNumber();
            BigInteger op1Big = new BigInteger(Long.toString(op1));
            BigInteger op2Big = new BigInteger(Long.toString(op2));
            BigInteger result = op1Big.multiply(op2Big);
            BigInteger manyF = new BigInteger(Long.toString(parseLong("ffffff", 16)));

            if (result.compareTo(manyF) == 1) {
                manyF = new BigInteger(Long.toString(parseLong("1000000", 16)));
                long rl = parseLong(result.mod(manyF).toString());
                cpu.setRL(new Word(rl));
                long rh = parseLong(result.divide(manyF).toString());
                cpu.setRH(new Word(rh));
                cpu.setC(MORE);
            } else {
                cpu.setRL(new Word(parseLong(result.toString())));
                cpu.setC(LESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void DIV() {
        System.out.println("DIV()");
        try {
            POP();
            long op1 = cpu.getRL().getNumber();
            POP();
            long op2 = cpu.getRL().getNumber();
            if (op2 == 0)
                throw new ProgramInteruptionException(DIVISION_BY_ZERO);
            long div = op1 / op2;
            long mod = op1 % op2;
            cpu.setRL(new Word(div));
            cpu.setRH(new Word(mod));
        } catch (ProgramInteruptionException e) {
            e.printStackTrace();
            PROGRAM_INTERRUPTION interruption = e.getReason();
            cpu.setPI(interruption);
        }
    }

    private void CM() {
        System.out.println("CM()");
        POP();
        Word w1 = cpu.getRL().copy();
        POP();
        Word w2 = cpu.getRL().copy();
        if (w1.getNumber() == w2.getNumber()) {
            cpu.setC(EQUAL);
        } else {
            if (w1.getNumber() < w2.getNumber()) {
                cpu.setC(LESS);
            } else {
                cpu.setC(MORE);
            }
        }
    }

    private void CMR() {
        System.out.println("CMR()");
        Word w1 = cpu.getRL();
        Word w2 = cpu.getRH();
        if (w1.getNumber() == w2.getNumber()) {
            cpu.setC(EQUAL);
        } else {
            if (w1.getNumber() < w2.getNumber()) {
                cpu.setC(LESS);
            } else {
                cpu.setC(MORE);
            }
        }
    }

    private void JUMP() {
        System.out.println("JUMP()");
        POP();
        try {
            cpu.setIC(cpu.getRL());
        } catch (InstructionPointerException e) {
            e.printStackTrace();
            PROGRAM_INTERRUPTION interruption = e.getReason();
            cpu.setPI(interruption);
        }
    }

    public String getVirtualAddress() {
        Word address = cpu.getIC().copy();
        Word value = null;
        try {
            value = cpu.getCS(address);
            return value.getASCIIFormat().substring(2);
        } catch (WrongAddressException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void JUMPA() {
        System.out.println("JUMP()");
        try {
            String virtualAddress = getVirtualAddress();
            cpu.setIC(new Word(virtualAddress, NUMERIC));
        } catch (ProgramInteruptionException e) {
            e.printStackTrace();
            PROGRAM_INTERRUPTION interruption = e.getReason();
            cpu.setPI(interruption);
        }
    }

    private void JUMPR() {
        System.out.println("JUMPR()");
        try {
            cpu.setIC(cpu.getRL());
        } catch (ProgramInteruptionException e) {
            e.printStackTrace();
            PROGRAM_INTERRUPTION interruption = e.getReason();
            cpu.setPI(interruption);
        }
    }

    private void JA() {
        System.out.println("JA()");
        if (cpu.getC() == MORE) {
            JUMPA();
        }
    }

    private void JAR() {
        System.out.println("JAR()");
        if (cpu.getC() == MORE) {
            JUMPR();
        }
    }

    private void JB() {
        System.out.println("JB()");
        if (cpu.getC() == LESS) {
            JUMPA();
        }
    }

    private void JBR() {
        System.out.println("JBR()");
        if (cpu.getC() == LESS) {
            JUMPR();
        }
    }

    private void JE() {
        System.out.println("JE()");
        if (cpu.getC() == EQUAL) {
            JUMPA();
        }
    }

    private void JER() {
        System.out.println("JER");
        if (cpu.getC() == EQUAL) {
            JUMPR();
        }
    }

    private void SWAP() {
        System.out.println("SWAP()");
        Word rh = new Word(cpu.getRH().getNumber());
        cpu.setRH(cpu.getRL());
        cpu.setRL(rh);

    }

    private void LOADB() {
        System.out.println("LOADB()");
        try {
            String virtualAddress = getVirtualAddress();
            Word address = new Word(virtualAddress, NUMERIC);
            Word value = cpu.getDS(address);
            if (value == null) return;
            cpu.setRL(value);
        } catch (ProgramInteruptionException e) {
            e.printStackTrace();
            PROGRAM_INTERRUPTION interruption = e.getReason();
            cpu.setPI(interruption);
        }
    }

    private void LOADW() {
        System.out.println("LOADW()");
        LOADB();
        String value = cpu.getRL().getASCIIFormat();
        cpu.setRL(new Word(value, NUMERIC));
    }

    private void SAVE() {
        System.out.println("SAVE()");
        try {
            String virtualAddress = getVirtualAddress();
            Word address = new Word(virtualAddress, NUMERIC);
            Word value = cpu.getRL().copy();
            cpu.setDS(address, value);
        } catch (ProgramInteruptionException e) {
            e.printStackTrace();
            PROGRAM_INTERRUPTION interruption = e.getReason();
            cpu.setPI(interruption);
        }
    }

    private void GET() {
        System.out.println("GET()");
        cpu.setSI(PRINTLINE_GET);
    }

    private void PUT() {
        System.out.println("PUT()");
        cpu.setSI(PRINTLINE_PUT);
    }

    private void HALT() {
        System.out.println("HALT()");
        cpu.setSI(HALT);
    }

    private void PRINTR() {
        System.out.println("PRINTR()");
        cpu.setSI(PRINTLINE_PUT_R);
    }
}
