package VirtualMachine;

import Components.CPU;
import RealMachine.RealMachine;
import Tools.Constants;
import Tools.Exceptions;
import Tools.Word;

import java.math.BigInteger;

public class Interpretator
{
    private final CPU cpu;
    private final RealMachine realMachine;

    public Interpretator(RealMachine realMachine)
    {
        this.realMachine = realMachine;
        this.cpu = realMachine.getCpu();
    }

    public void execute(String command){
        if(command.contains("ADD")) {
            ADD();
        }else if(command.contains("SUB")){
            SUB();
        }else if(command.contains("MUL")){
            MUL();
        }else if(command.contains("DIV")){
            DIV();
        }else if(command.contains("CMR")){
            CMR();
        }else if(command.contains("CM")){
            CM();
        }else if(command.contains("JUMPR")){
            JUMPR();
        }else if(command.contains("JUMP")){
            JUMP();
        }else if(command.contains("JAR")){
            JAR();
        }else if(command.contains("JA")){
            JA();
        }else if(command.contains("JBR")){
            JBR();
        }else if(command.contains("JB")){
            JB();
        }else if(command.contains("JER")){
            JER();
        }else if(command.contains("JE")){
            JE();
        }else if(command.contains("PUSH")){
            PUSH();
        }else if(command.contains("POP")){
            POP();
        }else if(command.contains("SWAP")){
            SWAP();
        }else if(command.contains("LB")){
            LOADB();
        }else if(command.contains("LW")){
            LOADW();
        }else if(command.contains("SV")){
            SAVE();
        }else if(command.contains("GT")){
            GET();
        }else if(command.contains("PT")){
            PUT();
        }else if(command.contains("HALT")){
            HALT();
        }else if(command.contains("PRINTR")){
            PRINTR();
        }else {
            System.out.print("Not found");
        }
    }

    //    RL ---> value
    //    SP ---> [SS:SP]
    //    SP ---> SP+1
    private void PUSH()
    {
        Word value = cpu.getRL().copy();
        Word address = cpu.getSP().copy();
        try {
            cpu.setSS(address,value);
            cpu.increaseSP();
        } catch (Exceptions.ProgramInteruptionException e) {
            e.printStackTrace();
            Constants.PROGRAM_INTERRUPTION interruption =  e.getReason();
            cpu.setPI(interruption);
        }
    }

    //    SP ---> [SS:SP]
    //    SP ---> SP-1
    //    value ---> RL
    private void POP()
    {
        try {
            cpu.decreaseSP();
            Word address = cpu.getSP().copy();
            Word value = cpu.getSS(address);
            cpu.setRL(value);
        } catch (Exceptions.ProgramInteruptionException e) {
            e.printStackTrace();
            Constants.PROGRAM_INTERRUPTION interruption =  e.getReason();
            cpu.setPI(interruption);
        }
    }


    private void ADD()
    {
        System.out.println("ADD()");
        try {
            POP();
            long op1 = cpu.getRL().getNumber();
            POP();
            long op2 = cpu.getRL().getNumber();
            long result = op1 + op2;
            long manyF = Long.parseLong("ffffff",16);
            if (result > manyF) {
                //ADD to RX
                long rl = (result % manyF);
                cpu.setRL(new Word(rl));
                long rh = (result /manyF);
                cpu.setRH(new Word(rh));
                cpu.setC(Constants.CONDITIONAL_MODE.MORE);
            } else {
                cpu.setRL(new Word(result));
                cpu.setC(Constants.CONDITIONAL_MODE.LESS);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void SUB()
    {
        System.out.println("SUB()");
        try {
            POP();
            long op1 = cpu.getRL().getNumber();
            POP();
            long op2 = cpu.getRL().getNumber();
            long result = op1 - op2;
            cpu.setRL(new Word(result));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void MUL()
    {
        System.out.println("MUL()");
        try {
            POP();
            long op1 = cpu.getRL().getNumber();
            POP();
            long op2 = cpu.getRL().getNumber();
            BigInteger op1Big = new BigInteger(Long.toString(op1));
            BigInteger op2Big = new BigInteger(Long.toString(op2));
            BigInteger result = op1Big.multiply(op2Big);
            BigInteger manyF = new BigInteger(Long.toString(Long.parseLong("ffffff",16)));

            if (result.compareTo(manyF)==1) {
                //ADD to RX
                manyF = new BigInteger(Long.toString(Long.parseLong("1000000",16)));
                long rl = Long.parseLong(result.mod(manyF).toString());
                cpu.setRL(new Word(rl));
                long rh = Long.parseLong(result.divide(manyF).toString());
                cpu.setRH(new Word(rh));
                cpu.setC(Constants.CONDITIONAL_MODE.MORE);
            } else {
                cpu.setRL(new Word(Long.parseLong(result.toString())));
                cpu.setC(Constants.CONDITIONAL_MODE.LESS);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void DIV()
    {
        System.out.println("DIV()");
        try {
            POP();
            long op1 = cpu.getRL().getNumber();
            POP();
            long op2 = cpu.getRL().getNumber();
            if(op2 == 0)throw new Exceptions.ProgramInteruptionException(Constants.PROGRAM_INTERRUPTION.DIVISION_BY_ZERO);
            long div = op1 / op2;
            long mod = op1 % op2;
            cpu.setRL(new Word(div));
            cpu.setRH(new Word(mod));
        } catch (Exceptions.ProgramInteruptionException e) {
            e.printStackTrace();
            Constants.PROGRAM_INTERRUPTION interruption =  e.getReason();
            cpu.setPI(interruption);
        }
    }
    private void CM()
    {
        System.out.println("CM()");
        // Stack ---> RL
        POP();
        Word w1 = cpu.getRL().copy();
        // Stack ---> RL
        POP();
        Word w2 = cpu.getRL().copy();
        if(w1.getNumber() == w2.getNumber())
        {
            cpu.setC(Constants.CONDITIONAL_MODE.EQUAL);
        } else{
            if(w1.getNumber() < w2.getNumber())
            {
                cpu.setC(Constants.CONDITIONAL_MODE.LESS);
            }else {
                cpu.setC(Constants.CONDITIONAL_MODE.MORE);
            }
        }
    }
    private void CMR()
    {
        System.out.println("CMR()");
        //System.out.println("RH"+cpu.getRH());
        //System.out.println("RL"+cpu.getRL());

        Word w1 = cpu.getRL();
        Word w2 =  cpu.getRH();
        if(w1.getNumber() == w2.getNumber())
        {
            cpu.setC(Constants.CONDITIONAL_MODE.EQUAL);
        } else{
            if(w1.getNumber() < w2.getNumber())
            {
                cpu.setC(Constants.CONDITIONAL_MODE.LESS);
            }else {
                cpu.setC(Constants.CONDITIONAL_MODE.MORE);
            }
        }
    }

    private void JUMP()
    {
        System.out.println("JUMP()");
        POP();
        try {
            cpu.setIC(cpu.getRL());
        } catch (Exceptions.InstructionPointerException e) {
            e.printStackTrace();
            Constants.PROGRAM_INTERRUPTION interruption =  e.getReason();
            cpu.setPI(interruption);
        }
    }

    private String getVirtualAddress() throws Exceptions.WrongAddressException {
        Word address = cpu.getIC().copy();
        Word value = null;
        value = cpu.getCS(address);
        return value.getASCIIFormat().substring(2);
    }

    private void JUMPA()
    {
        System.out.println("JUMP()");
        try {
            String virtualAddress = getVirtualAddress();
            cpu.setIC(new Word(virtualAddress, Word.WORD_TYPE.NUMERIC));
        }catch (Exceptions.ProgramInteruptionException e){
            e.printStackTrace();
            Constants.PROGRAM_INTERRUPTION interruption =  e.getReason();
            cpu.setPI(interruption);
        }
    }
    private void JUMPR()
    {
        System.out.println("JUMPR()");
        try {
            cpu.setIC(cpu.getRL());
        } catch (Exceptions.ProgramInteruptionException e) {
            e.printStackTrace();
            Constants.PROGRAM_INTERRUPTION interruption =  e.getReason();
            cpu.setPI(interruption);
        }
    }
    private void JA()
    {
        System.out.println("JA()");
        if(cpu.getC() == Constants.CONDITIONAL_MODE.MORE)
        {
            JUMPA();
        }
    }
    private void JAR()
    {
        System.out.println("JAR()");
        if(cpu.getC() == Constants.CONDITIONAL_MODE.MORE)
        {
            JUMPR();
        }
    }
    private void JB()
    {
        System.out.println("JB()");
        if(cpu.getC() == Constants.CONDITIONAL_MODE.LESS)
        {
            JUMPA();
        }
    }
    private void JBR()
    {
        System.out.println("JBR()");
        if(cpu.getC() == Constants.CONDITIONAL_MODE.LESS)
        {
            JUMPR();
        }
    }
    private void JE()
    {
        System.out.println("JE()");
        if(cpu.getC() == Constants.CONDITIONAL_MODE.EQUAL)
        {
            JUMPA();
        }
    }
    private void JER()
    {
        System.out.println("JER");
        if(cpu.getC() == Constants.CONDITIONAL_MODE.EQUAL)
        {
            JUMPR();
        }
    }

    private void SWAP()
    {
        System.out.println("SWAP()");
        Word rh = new Word(cpu.getRH().getNumber());
        cpu.setRH(cpu.getRL());
        cpu.setRL(rh);

    }

    //    RL ---> value
    private void LOADB(){

        System.out.println("LOADB()");
        try {
            String virtualAddress = getVirtualAddress();
            Word address =  new Word(virtualAddress, Word.WORD_TYPE.NUMERIC);
            Word value = cpu.getDS(address);
            cpu.setRL(value);
        }catch (Exceptions.ProgramInteruptionException e){
            e.printStackTrace();
            Constants.PROGRAM_INTERRUPTION interruption =  e.getReason();
            cpu.setPI(interruption);
        }
    }
    //    RL ---> value
    private void LOADW() {
        System.out.println("LOADW()");
        LOADB();
        String value = cpu.getRL().getASCIIFormat();
        cpu.setRL(new Word(value, Word.WORD_TYPE.NUMERIC));
    }

    //    RL ---> [DS:SAVE ADDR]
    private void SAVE(){
        System.out.println("SAVE()");
        try {
            String virtualAddress = getVirtualAddress();
            Word address =  new Word(virtualAddress, Word.WORD_TYPE.NUMERIC);
            Word value = cpu.getRL().copy();
            cpu.setDS(address,value);
        }catch (Exceptions.ProgramInteruptionException e){
            e.printStackTrace();
            Constants.PROGRAM_INTERRUPTION interruption =  e.getReason();
            cpu.setPI(interruption);
        }
    }

    //   getPrintLine ---> [DS:ADDR]
    private void GET(){

        System.out.println("GET()");
        try {
            String virtualAddress = getVirtualAddress();

            Word address =  new Word(virtualAddress, Word.WORD_TYPE.NUMERIC);
            realMachine.getPrintLine().read(address);

        }catch (Exceptions.ProgramInteruptionException e){
            e.printStackTrace();
            Constants.PROGRAM_INTERRUPTION interruption =  e.getReason();
            cpu.setPI(interruption);
        }
    }
    //  [DS:ADDR] ---> getPrintLine
    private void PUT(){
        System.out.println("PUT()");
        try {
            String virtualAddress = getVirtualAddress();
            Word address =  new Word(virtualAddress, Word.WORD_TYPE.NUMERIC);
            realMachine.getPrintLine().print(address);
        }catch (Exceptions.ProgramInteruptionException e){
            e.printStackTrace();
            Constants.PROGRAM_INTERRUPTION interruption =  e.getReason();
            cpu.setPI(interruption);
        }
    }
    private void HALT(){
        System.out.println("HALT()");
        cpu.setSI(Constants.SYSTEM_INTERRUPTION.HALT);
    }

    // registers ---> getPrintLine
    private void PRINTR(){
        System.out.println("PRINTR()");
        realMachine.getPrintLine().printRegisters();
    }


}
