package OS.VM;

import OS.RM.CPU;
import OS.Tools.Constants;
import OS.Tools.Constants.*;
import OS.Tools.Word;

import java.math.BigInteger;

public class Interpretator
{
    private final CPU cpu;
    private final Stack stack;
    private final String name;

    public Interpretator(CPU cpu, String name)
    {
        this.name = name;
        this.cpu = cpu;
        stack = new Stack(cpu);
    }

    public void execute(String command) throws Exception {
        cpu.showProcess(name);
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
        cpu.showPreviousProcess();
    }

    private void ADD()
    {
        System.out.println("ADD()");
        try {
            stack.Pop();
            long op1 = cpu.getRL().getNumber();
            stack.Pop();
            long op2 = cpu.getRL().getNumber();
            long result = op1 + op2;
            long manyF = Long.parseLong("ffffff",16);
            if (result > manyF) {
                //ADD to RX
                long rl = (result % manyF);
                cpu.setRL(new Word(rl));
                long rh = (result /manyF);
                cpu.setRH(new Word(rh));
                cpu.setC(CONDITIONAL_MODE.MORE);
            } else {
                cpu.setRL(new Word(result));
                cpu.setC(CONDITIONAL_MODE.LESS);
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
            stack.Pop();
            long op1 = cpu.getRL().getNumber();
            stack.Pop();
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
            stack.Pop();
            long op1 = cpu.getRL().getNumber();
            stack.Pop();
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
                cpu.setC(CONDITIONAL_MODE.MORE);
            } else {
                cpu.setRL(new Word(Long.parseLong(result.toString())));
                cpu.setC(CONDITIONAL_MODE.LESS);
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
            stack.Pop();
            long op1 = cpu.getRL().getNumber();
            stack.Pop();
            long op2 = cpu.getRL().getNumber();
            if(op2 == 0)throw new Exception("Division by zero");
            long div = op1 / op2;
            long mod = op1 % op2;
            cpu.setRL(new Word(div));
            cpu.setRH(new Word(mod));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void CM()
    {
        System.out.println("CM()");
        try {
            // Stack ---> RL
            stack.Pop();
            Word w1 = cpu.getRL().copy();

            // Stack ---> RL
            stack.Pop();
            Word w2 = cpu.getRL().copy();

            if(w1.getNumber() == w2.getNumber())
            {
                cpu.setC(CONDITIONAL_MODE.EQUAL);
            } else{
                if(w1.getNumber() < w2.getNumber())
                {
                    cpu.setC(CONDITIONAL_MODE.LESS);
                }else {
                    cpu.setC(CONDITIONAL_MODE.MORE);
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void CMR()
    {
        System.out.println("CMR()");
        System.out.println("RH"+cpu.getRH());
        System.out.println("RL"+cpu.getRL());

        try {
            Word w1 = cpu.getRL();
            Word w2 =  cpu.getRH();
            if(w1.getNumber() == w2.getNumber())
            {
                cpu.setC(CONDITIONAL_MODE.EQUAL);
            } else{
                if(w1.getNumber() < w2.getNumber())
                {
                    cpu.setC(CONDITIONAL_MODE.LESS);
                }else {
                    cpu.setC(CONDITIONAL_MODE.MORE);
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void JUMP()
    {
        System.out.println("JUMP()");
        try {
            stack.Pop();
            cpu.setIC(cpu.getRL());
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void JUMPR()
    {
        System.out.println("JUMPR()");
        try {
            cpu.setIC(cpu.getRL());
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void JA()
    {
        System.out.println("JA()");
        try {
            if(cpu.getC() == CONDITIONAL_MODE.MORE)
            {
                JUMP();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void JAR()
    {
        System.out.println("JAR()");
        try {
            if(cpu.getC() == CONDITIONAL_MODE.MORE)
            {
                JUMPR();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void JB()
    {
        System.out.println("JB()");
        try {
            if(cpu.getC() == CONDITIONAL_MODE.LESS)
            {
                JUMP();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void JBR()
    {
        System.out.println("JBR()");
        try {
            if(cpu.getC() == CONDITIONAL_MODE.LESS)
            {
                JUMPR();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void JE()
    {
        System.out.println("JE()");
        try {
            if(cpu.getC() == CONDITIONAL_MODE.EQUAL)
            {
                JUMP();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void JER()
    {
        System.out.println("JER");
        try {
            if(cpu.getC() == CONDITIONAL_MODE.EQUAL)
            {
                JUMPR();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void PUSH()
    {
        System.out.println("PUSH()");
        try {
            stack.Push();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void POP()
    {
        System.out.println("POP()");
        try {
            stack.Pop();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void SWAP()
    {
        System.out.println("SWAP()");
        try {
            Word rh = new Word(cpu.getRH().getNumber());
            cpu.setRH(cpu.getRL());
            cpu.setRL(rh);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    RL ---> value
    private void LOADB() throws Exception {

        //    Word address, ---> RL
        //    RL ---> value
        cpu.setRL(cpu.getIC().copy());
        cpu.getSwapping().GETCS();
        String virtualAddress = cpu.getRL().getASCIIFormat().substring(2);

        System.out.println("LOADB()");
        try {
            Word address =  new Word(virtualAddress, Word.WORD_TYPE.NUMERIC);
            cpu.setRL(address);
            cpu.getSwapping().GETDS();
        }catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("PO LOAD --------> "+ cpu.getRL().getHEXFormat());
    }
    //    RL ---> value
    private void LOADW() throws Exception {
        System.out.println("LOADW()");
        LOADB();
        String value = cpu.getRL().getASCIIFormat();
        cpu.setRL(new Word(value, Word.WORD_TYPE.NUMERIC));
    }

    private void SAVE() throws Exception {

        Word value = cpu.getRL().copy();

        //    Word address, ---> RL
        //    RL ---> value
        cpu.setRL(cpu.getIC().copy());
        cpu.getSwapping().GETCS();
        String virtualAddress = cpu.getRL().getASCIIFormat().substring(2);

        System.out.println("SAVE()");
        try {
            Word address = new Word(virtualAddress, Word.WORD_TYPE.NUMERIC);

            //    Word address, ---> RL
            //    Word value  ---> RH
            cpu.setRH(value);
            cpu.setRL(address);
            cpu.getSwapping().SETDS();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void GET() throws Exception {
        System.out.println("GET()");
        //String virtualAddress = cpu.getCSValue(cpu.getIC()).getASCIIFormat().substring(2);
        //    Word address, ---> RL
        //    RL ---> value
        cpu.setRL(cpu.getIC().copy());
        cpu.getSwapping().GETCS();
        String virtualAddress = cpu.getRL().getASCIIFormat().substring(2);
        //address --> RL
        cpu.setRL(new Word(virtualAddress, Word.WORD_TYPE.NUMERIC));
        cpu.getPrintLine().read();
    }
    private void PUT() throws Exception {
        //    Word address, ---> RL
        //    RL ---> value
        cpu.setRL(cpu.getIC().copy());
        cpu.getSwapping().GETCS();
        String virtualAddress = cpu.getRL().getASCIIFormat().substring(2);

        System.out.println("PUT()");
        //address --> RL
        cpu.setRL(new Word(virtualAddress, Word.WORD_TYPE.NUMERIC));
        cpu.getPrintLine().print();
    }
    private void HALT() throws Exception {
        System.out.println("HALT()");
        cpu.setSI(SYSTEM_INTERRUPTION.HALT);
    }

    private void PRINTR(){
        System.out.println("PRINTR()");
        cpu.getPrintLine().printRegisters();
    }


}
