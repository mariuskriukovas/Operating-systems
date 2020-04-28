package Processes;

import Components.CPU;
import Components.Memory;
import RealMachine.RealMachine;
import Tools.Constants;
import VirtualMachine.VirtualMachine;

import java.util.Deque;
import java.util.HashMap;

public class JobGorvernor {

    private long idGenerator = 0;
    private final int TIMERTIME = 10;

    private final RealMachine realMachine;
    private final Memory externalMemory;
    private final Memory internalMemory;
    private final Deque<String> waitingTasks;
    private final Deque<VirtualMachine> activeTasks;
    private final HashMap<Long, Long> registerStates;
    private final CPU cpu;


    public JobGorvernor(RealMachine realMachine){
        this.realMachine = realMachine;
        this.cpu = realMachine.getCpu();
        internalMemory = realMachine.getInternalMemory();
        externalMemory = realMachine.getExternalMemory();
        activeTasks = realMachine.getActiveTasks();
        waitingTasks = realMachine.getWaitingTasks();
        registerStates = new HashMap<>(100);
    }


    public Constants.PROCESS_STATUS createVirtualMachine(String fileName){
        //if we have free 4 blocks at internal memory
        if(activeTasks.size()<realMachine.getMAXRUNNING())
        {
            System.out.println("Added to active list: "+ fileName);
            int internalMemoryBegin = -1;
            int externalMemoryBegin = -1;
            try {
                //find free space
                //space starts from last to first available block groups
                internalMemoryBegin = internalMemory.getFreeSpaceBeginAddress();
                externalMemoryBegin = externalMemory.getFreeSpaceBeginAddress();
                //parse .txt file in array of commands
                //load those commands to external memory
                realMachine.getJobToSwap().uploadTaskToExternalMemory(fileName, externalMemoryBegin);
                //creating memory table in first free block of internal memory.
                //block number saved in PTR register
                realMachine.getLoader().createMemoryTable(internalMemoryBegin, externalMemoryBegin);
                //setting segment registers with first values of each block
                realMachine.getLoader().initSegmentRegisters();
                //registers values are external memory blocks who are loaded in internal memory in this sequent
                //  SS ----- > PTR + 1
                //  DS ----- > PTR + 2
                //  CS ----- > PTR + 3

                realMachine.getLoader().loadVirtualMachineMemory();
                //added to prepared tasks list
                final long vmID = generateVMID();
                activeTasks.push(new VirtualMachine(realMachine, fileName, vmID));
                //add registers to storage
                final long registerID = cpu.getRegistersStorage().saveRMRegisters();
                registerStates.put(vmID, registerID);
            }catch (Exception e){
                e.printStackTrace();
                return Constants.PROCESS_STATUS.FAILED;
            }
        }else {
            System.out.println("Added to waiting list: "+ fileName);
            waitingTasks.push(fileName);
        }
        return Constants.PROCESS_STATUS.COMPLETED;
    }

    private void prepareRegisters(long vmID){
        if(registerStates.containsKey(vmID)) {
            long registersID = registerStates.get(vmID);
            cpu.getRegistersStorage().restoreRegisters(registersID);
            registerStates.remove(vmID);
        }
        try {
            cpu.setMODE(Constants.SYSTEM_MODE.SUPERVISOR_MODE);
            cpu.setTI(TIMERTIME);
            cpu.setSI(Constants.SYSTEM_INTERRUPTION.NONE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void run(VirtualMachine virtualMachine)
    {
        prepareRegisters(virtualMachine.getID());
        virtualMachine.doYourMagic();
        switch (cpu.getSI()){
            case HALT:
                System.err.println("HALT : "+ virtualMachine.getName() + "ID : " + virtualMachine.getID());
                realMachine.getPrintLine().printHalt(virtualMachine.getName() + " " + virtualMachine.getID());
                cleanThisVirtualMachine();
                activeTasks.remove(virtualMachine);
                break;
            case TIMER_INTERUPTION:
                System.err.println("TIMER_INTERUPTION : "+ virtualMachine.getName() + "ID : " + virtualMachine.getID());
                long registersID = cpu.getRegistersStorage().saveRMRegisters();
                registerStates.put(virtualMachine.getID(),registersID);
                break;
        }
    }

    public Constants.PROCESS_STATUS runAll() {

        while (activeTasks.size()>0){
            activeTasks.stream().forEach(this::run);
            if(activeTasks.size()<realMachine.getMAXRUNNING())addMoreTasksIfPossible();
        }

        return Constants.PROCESS_STATUS.COMPLETED;
    }

    private long generateVMID(){
        idGenerator += 1;
        return idGenerator;
    }

    private void addMoreTasksIfPossible()
    {
        if(waitingTasks.size()>0){
            System.out.println("Possible");
            String fileName = waitingTasks.pop();
            createVirtualMachine(fileName);
        }
    }

    private void cleanThisVirtualMachine()
    {
        try {
            //for checking
            System.err.println("SS -----> " + cpu.getSS() + "DS -----> " + cpu.getDS() + "CS -----> " + cpu.getCS());
            realMachine.getLoader().saveVirtualMachineMemory();
            int ptr = (int) cpu.getPTR().getNumber();
            internalMemory.cleanSpace(ptr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.err.println("Clean");
    }
}
