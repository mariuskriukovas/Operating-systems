package VirtualMachine;

import Components.CPU;
import RealMachine.RealMachine;
import Tools.Constants;
import Tools.Exceptions;
import Tools.Word;

public class VirtualMachine {

    private final RealMachine realMachine;
    private final Interpretator interpretator;
    private final String name;
    private final CPU cpu;
    private boolean launchedFirstTime = true;
    private long stateID = -1;
    private final long VMID;

    public VirtualMachine(RealMachine realMachine, String name, long ID)
    {
        this.realMachine = realMachine;
        this.name = name;
        this.cpu = realMachine.getCpu();
        interpretator = new Interpretator(realMachine);
        VMID = ID;
    }


    //                if (cpu.getTI()==0){
//                    saveMyState();
//                    cpu.setSI(SYSTEM_INTERRUPTION.TIMER_INTERUPTION);
//                    cpu.showPreviousProcess();
//                    return;
//                }
//                cpu.restoreRegisterState();
//                cpu.decTI();

    private void prepareRegisters(){
        try {
            System.out.println("---------------> prepareRegisters");
            cpu.setC(Constants.CONDITIONAL_MODE.NONE);
            cpu.setRL(new Word(0));
            cpu.setRH(new Word(0));
            cpu.setIC(new Word(0));
            cpu.setSP(new Word(0));
            cpu.setMODE(Constants.SYSTEM_MODE.USER_MODE);
            cpu.setPI(Constants.PROGRAM_INTERRUPTION.NONE);
            launchedFirstTime = false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveMyState(){
        stateID = cpu.getRegistersStorage().saveVMRegisters();
    }
    private void restoreMyState(){
        cpu.getRegistersStorage().restoreRegisters(stateID);
        stateID = -1;
    }

    public void doYourMagic(){
        if(launchedFirstTime)prepareRegisters();
        else restoreMyState();
        try {
            while (!cpu.getSI().equals(Constants.SYSTEM_INTERRUPTION.HALT)) {
                Word address = cpu.getIC().copy();
                String command = cpu.getCS(address).getASCIIFormat();
                System.out.println(" ---------------  > "+command);
                interpretator.execute(command);
                cpu.increaseIC();
                cpu.decTI();
                if(cpu.getTI()==0){
                    saveMyState();
                    cpu.setSI(Constants.SYSTEM_INTERRUPTION.TIMER_INTERUPTION);
                    return;
                }
            }
        } catch (Exceptions.ProgramInteruptionException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public long getID() {
        return VMID;
    }
}