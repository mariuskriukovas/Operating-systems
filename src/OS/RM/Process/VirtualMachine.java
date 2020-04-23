package OS.RM.Process;

import OS.RM.Process.Parser;
import OS.RM.CPU;
import OS.Tools.Constants;
import OS.Tools.Word;
import OS.VM.Interpretator;
import UI.OSFrame;
import OS.Tools.Constants.SYSTEM_INTERRUPTION;

import java.util.ArrayList;

import static OS.Tools.Constants.SYSTEM_MODE.SUPERVISOR_MODE;
import static OS.Tools.Constants.SYSTEM_MODE.USER_MODE;

public class VirtualMachine {

    private CPU cpu = null;
    private Interpretator interpretator;

    private final int internalBlockBegin;
    private final int externalBlockBegin;


    public VirtualMachine(CPU cpu, int internalBlockBegin, int externalBlockBegin, String name)
    {
        this.internalBlockBegin  = internalBlockBegin;
        this.externalBlockBegin = externalBlockBegin;
        try {
            this.cpu = cpu;
            interpretator = new Interpretator(cpu, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void doYourMagic(){
        cpu.showProcess(Constants.PROCESS.VirtualMachine);
        try{
            while (!cpu.getSI().equals(SYSTEM_INTERRUPTION.HALT)) {
                if (cpu.getTI()==0){
                    cpu.setSI(SYSTEM_INTERRUPTION.TIMER_INTERUPTION);
                    cpu.showPreviousProcess();
                    return;
                }
                cpu.saveRegisterState();
                    cpu.setRL(cpu.getIC().copy());
                    cpu.getSwapping().GETCS();
                    String command = cpu.getRL().getASCIIFormat();
//                    System.out.println(" ---------------  > "+command);
                cpu.restoreRegisterState();
                interpretator.execute(command);
                cpu.increaseIC();
                cpu.decTI();
            }
        }catch (Exception e)
        {
             e.printStackTrace();
        }
        cpu.showPreviousProcess();
    }

    public int getExternalBlockBegin() {
        return externalBlockBegin;
    }

    public int getInternalBlockBegin() {
        return internalBlockBegin;
    }
}