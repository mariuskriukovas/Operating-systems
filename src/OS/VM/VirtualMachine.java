package OS.VM;

import OS.RM.Process.Parser;
import OS.RM.CPU;
import OS.Tools.Constants;
import OS.Tools.Word;
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
//        this.screen = screen;
//        screen.getScreenForVirtualMachine().setIncButtonFunction(() -> doYourMagicStepByStep());
//        screen.getScreenForVirtualMachine().setNodeBugButtonFunction(() -> doYourMagic());

        processID = ID;
        try {
            this.cpu = cpu;
            interpretator = new Interpretator(cpu);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Integer doYourMagic(){
        cpu.setMODE(USER_MODE);
        try{
            while (!cpu.getSI().equals(SYSTEM_INTERRUPTION.HALT)) {
                cpu.saveRegisterState();
                    cpu.setRL(cpu.getIC().copy());
                    cpu.interrupt().GETCS();
                    String command = cpu.getRL().getASCIIFormat();
                cpu.restoreRegisterState();
                interpretator.execute(command);
                cpu.increaseIC();
            }
        }catch (Exception e)
        {
             e.printStackTrace();
            return -1;
        }
        return 1;
    }

    public Integer doYourMagicStepByStep() {
        cpu.setMODE(USER_MODE);
        if (!cpu.getSI().equals(SYSTEM_INTERRUPTION.HALT)) {
            try {
                cpu.saveRegisterState();
                    cpu.setRL(cpu.getIC().copy());
                    cpu.interrupt().GETCS();
                    String command = cpu.getRL().getASCIIFormat();
                cpu.restoreRegisterState();
                interpretator.execute(command);
                cpu.increaseIC();
            }catch (Exception e){
                e.printStackTrace();
            }
            return 1;
        }
        return -1;
    }
}