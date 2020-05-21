package Processes;

import Components.CPU;
import Components.Memory;
import Resources.Resource;
import Resources.ResourceDistributor;
import Resources.ResourceEnum;
import Tools.Constants;
import Components.SupervisorMemory;
import Tools.Word;
import VirtualMachine.VirtualMachine;

import static Processes.Loader.State.LOAD_VIRTUAL_MACHINE_MEMORY;
import static Processes.MainProc.State.TASK_DELETE;
import static Processes.ProcessEnum.JOB_GORVERNOR_PRIORITY;
import static Processes.ProcessEnum.Name.JOB_GORVERNOR;
import static Resources.ResourceEnum.Name.*;
import static Tools.Constants.*;
import static Tools.Constants.ANSI_BLACK;
import static VirtualMachine.VirtualMachine.VirtualMachinePriority;

public class JobGorvernor extends ProcessInterface {

    private static int JobGorvernorPriority = JOB_GORVERNOR_PRIORITY;

    private final CPU cpu;
    private final SupervisorMemory supervisorMemory;
    private final int taskID;
    private final  String taskName;
    private final int externalMemoryBegin;
    private VirtualMachine myVirtualMachine;
    private int IC = 0;


    public JobGorvernor(MainProc father, ProcessPlaner processPlaner, ResourceDistributor resourceDistributor, Resource task)
    {
        super(father, ProcessEnum.State.BLOCKED, JobGorvernorPriority, JOB_GORVERNOR,processPlaner, resourceDistributor);
        JobGorvernorPriority ++;

        RealMachine realMachine = (RealMachine) father.father;
        externalMemoryBegin = (int)task.get(3);
        cpu = new CPU(realMachine, externalMemoryBegin);

        supervisorMemory = realMachine.getSupervisorMemory();

        taskName = (String)task.get(1);
        taskID = (int)task.get(2);
        System.out.println(ANSI_BLUE + "ID --------------------------------------------------------->" + taskID +  ANSI_BLACK);
        task.getElements().pop();
        setPrepared(true);
    }

    private void createMemoryTable(int internalBlockBegin, int externalBlockBegin, Memory internalMemory)
    {
        int adr = internalBlockBegin*256;
        try {
            for (int i = 0; i < Constants.BLOCK_LENGTH; i++) {
                internalMemory.setWord(new Word(externalBlockBegin + i),adr+i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cpu.setPTR(new Word(internalBlockBegin));
    }

    public RealMachine getRealMachine()
    {
        return (RealMachine) father.father;
    }


    @Override
    public void executeTask() {
        super.executeTask();
        switch (IC) {
            case 0:
                //Ar yra laisvos vidinės atminties ?
                Memory internal = (Memory) resourceDistributor.get(INTERNAL_MEMORY);
                if (internal.hasFreeSpace()){
                    IC++;
                    //Sukuriama puslapių lentelė
                    int internalMemoryBegin = internal.getFreeSpaceBeginAddress();
                    System.out.println(ANSI_BLUE + "Sukuriama puslapių lentelė --------------->" + internalMemoryBegin +  ANSI_BLACK);
                    createMemoryTable(internalMemoryBegin, externalMemoryBegin, internal);
                    initSegmentRegisters(internal);
                    resourceDistributor.disengage(ResourceEnum.Name.LOADING_PACKAGE, LOAD_VIRTUAL_MACHINE_MEMORY, cpu);
                }else {
                    IC = 0;
                    System.out.println(ANSI_BLUE + "Truksta atminties--------------->"  +  ANSI_BLACK);
                    resourceDistributor.ask(ResourceEnum.Name.EXTERNAL_MEMORY_DISENGAGED,this);
                }
                break;
            case 1:
                IC++;
                resourceDistributor.ask(ResourceEnum.Name.FROM_LOADER,this);
                break;
            case 2:
                IC++;
                //Proceso “Virtuali mašina” kūrimas
                //Atlaisvinamas “Vykdyk” resursas skirtas Virtual Machine,  su pranešimu "ID"
                //Proceso “Virtuali mašina” aktyvavimas
                myVirtualMachine = new VirtualMachine(this,processPlaner,resourceDistributor, taskName,taskID);
            case 3:
                IC++;
                myVirtualMachine.activate();
                break;
            case 4:
                IC++;
                //Blokavimasis laukiant “Pertraukimo priežastis” resurso
                resourceDistributor.ask(FROM_INTERUPT, this);
                break;
            case 5:
                executeInterrupt();
                break;
            case 6:
                IC = 3;
                resourceDistributor.ask(FROM_PRINTLINE, this);
                break;
            case 7:
                IC = 3;
                resourceDistributor.ask(FROM_SWAPING, this);
                break;
            case 9:
                IC = 33;
                resourceDistributor.ask(WAIT_UNTIL_DESTRUCTION, this);
                break;
        }
    }

    private void executeInterrupt(){
        Resource resource = resourceDistributor.get(FROM_INTERUPT);
        String action = (String) resource.get(0).toString();
        switch (action)
        {
            case "TIMER":
                this.setPriority( JobGorvernorPriority);
                JobGorvernorPriority++;
                myVirtualMachine.setPriority(VirtualMachinePriority);
                VirtualMachinePriority++;
                IC = 3;
                //processPlaner.plan();
                break;
            case "HALT":
                IC = 9;
                myVirtualMachine.setPrepared(false);
                this.setPrepared(false);
                resourceDistributor.disengage(ResourceEnum.Name.TASK_IN_DRUM,TASK_DELETE, taskID);
                break;
            case "PRINTLINE":
                IC = 6;
                resource.getElementList().remove(0);
                resourceDistributor.disengage(PRINTLINE,myVirtualMachine, resource.getElementList());
                break;
            case "PRINTLINE_NEEDS_BUFFER":
                IC = 3;
                //processPlaner.plan();
                break;
            case "SWAPING":
                IC = 7;
                resourceDistributor.disengage(SWAPPING, myVirtualMachine, resource.get(1), resource.get(2));
                break;
        }
    }

    public CPU getCpu() {
        return cpu;
    }

    //PTR --> memory table
    public void initSegmentRegisters(Memory internalMemory){
        long ptr = cpu.getPTR().getNumber()*256;
        int ss_internal = internalMemory.getFreeSpaceBeginAddress();
        int ds_internal = internalMemory.getFreeSpaceBeginAddress();
        int cs_internal = internalMemory.getFreeSpaceBeginAddress();

        System.out.println(ANSI_BLUE + "ss_internal --------------->" + ss_internal +  ANSI_BLACK);

        try {

            cpu.setSS(new Word(ss_internal*256 + (STACK_SEGMENT/256)));
            cpu.setDS(new Word(ds_internal*256 +  (DATA_SEGMENT/256)));
            cpu.setCS(new Word(cs_internal*256 +  (CODE_SEGMENT/256)));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getTaskID() {
        return taskID;
    }

    @Override
    public void destroy() {
        cleanThisVirtualMachine();
        resourceDistributor.disengage(ResourceEnum.Name.EXTERNAL_MEMORY_DISENGAGED);
    }

    public void cleanThisVirtualMachine()
    {
        try {
            //for checking
            System.err.println("SS -----> " + cpu.getSS() + "DS -----> " + cpu.getDS() + "CS -----> " + cpu.getCS());
            ((RealMachine)father.father).getLoader().saveVirtualMachineMemory(cpu);

            int ptr = (int) cpu.getPTR().getNumber();

            int ss = cpu.getSS().getBlockFromAddress();
            int ds  = cpu.getDS().getBlockFromAddress();
            int cs  = cpu.getCS().getBlockFromAddress();

            ((RealMachine)father.father).getInternalMemory().cleanSpace(ptr);
            ((RealMachine)father.father).getInternalMemory().cleanSpace(ds);
            ((RealMachine)father.father).getInternalMemory().cleanSpace(cs);
            ((RealMachine)father.father).getInternalMemory().cleanSpace(ss);

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.err.println("Clean");
    }

}
