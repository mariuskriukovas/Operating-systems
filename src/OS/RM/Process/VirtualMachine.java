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
    private final String processID;

    private OSFrame screen;

    public VirtualMachine(String ID, CPU cpu)
    {
        processID = ID;
        try {
            this.cpu = cpu;
            interpretator = new Interpretator(cpu);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void doYourMagic(){
        cpu.setMODE(USER_MODE);
        try{
            while (!cpu.getSI().equals(SYSTEM_INTERRUPTION.HALT)) {
                if (cpu.getTI()==0){
                    cpu.setSI(SYSTEM_INTERRUPTION.TIMER_INTERUPTION);
                    return;
                }
                cpu.saveRegisterState();
                    cpu.setRL(cpu.getIC().copy());
                    cpu.getSwapping().GETCS();
                    String command = cpu.getRL().getASCIIFormat();
                    System.out.println(" ---------------  > "+command);
                cpu.restoreRegisterState();
                interpretator.execute(command);
                cpu.increaseIC();
                cpu.decTI();
            }
        }catch (Exception e)
        {
             e.printStackTrace();
        }
    }
}