package Processes;

import Components.CPU;
import Components.Memory;
import Components.SupervisorMemory;
import Resources.Resource;
import Resources.ResourceDistributor;
import Resources.ResourceEnum;
import Tools.Constants;
import Tools.Word;
import VirtualMachine.VirtualMachine;

import static Processes.Loader.State.LOAD_VIRTUAL_MACHINE_MEMORY;
import static Processes.MainProc.State.TASK_DELETE;
import static Processes.ProcessEnum.JOB_GORVERNOR_PRIORITY;
import static Processes.ProcessEnum.Name.JOB_GORVERNOR;
import static Processes.ProcessEnum.State.BLOCKED;
import static Resources.ResourceEnum.Name.EXTERNAL_MEMORY_DISENGAGED;
import static Resources.ResourceEnum.Name.FROM_INTERUPT;
import static Resources.ResourceEnum.Name.FROM_PRINTLINE;
import static Resources.ResourceEnum.Name.FROM_SWAPING;
import static Resources.ResourceEnum.Name.INTERNAL_MEMORY;
import static Resources.ResourceEnum.Name.LOADING_PACKAGE;
import static Resources.ResourceEnum.Name.PRINTLINE;
import static Resources.ResourceEnum.Name.SWAPPING;
import static Resources.ResourceEnum.Name.TASK_IN_DRUM;
import static Resources.ResourceEnum.Name.WAIT_UNTIL_DESTRUCTION;
import static Tools.Constants.ANSI_BLACK;
import static Tools.Constants.ANSI_BLUE;
import static Tools.Constants.CODE_SEGMENT;
import static Tools.Constants.DATA_SEGMENT;
import static Tools.Constants.STACK_SEGMENT;
import static VirtualMachine.VirtualMachine.VirtualMachinePriority;

public class JobGorvernor extends ProcessInterface {

    private static int JobGorvernorPriority = JOB_GORVERNOR_PRIORITY;

    private final CPU cpu;
    private final SupervisorMemory supervisorMemory;
    private final int taskID;
    private final String taskName;
    private final int externalMemoryBegin;
    private VirtualMachine myVirtualMachine;

    public JobGorvernor(MainProc father, ProcessPlaner planner, ResourceDistributor distributor, Resource task) {
        super(father, BLOCKED, JobGorvernorPriority, JOB_GORVERNOR, planner, distributor);
        JobGorvernorPriority++;

        RealMachine realMachine = (RealMachine) father.father;
        externalMemoryBegin = (int) task.get(3);
        cpu = new CPU(realMachine, externalMemoryBegin);

        supervisorMemory = realMachine.getSupervisorMemory();

        taskName = (String) task.get(1);
        taskID = (int) task.get(2);
        System.out.println(ANSI_BLUE + "ID ------------------------------------------------>" + taskID + ANSI_BLACK);
        task.getElements().pop();
        setPrepared(true);
    }

    private void createMemoryTable(int internalBlockBegin, int externalBlockBegin, Memory internalMemory) {
        int adr = internalBlockBegin * 256;
        try {
            for (int i = 0; i < Constants.BLOCK_LENGTH; i++) {
                internalMemory.setWord(new Word(externalBlockBegin + i), adr + i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cpu.setPTR(new Word(internalBlockBegin));
    }

    public RealMachine getRealMachine() {
        return (RealMachine) father.father;
    }


    @Override
    public void executeTask() {
        super.executeTask();
        switch (IC) {
            case 0:
                Memory internal = (Memory) resourceDistributor.get(INTERNAL_MEMORY);
                if (internal.hasFreeSpace()) {
                    IC++;
                    int internalMemoryBegin = internal.getFreeSpaceBeginAddress();
                    System.out.println(ANSI_BLUE + "Sukuriama puslapių lentelė --------------->" + internalMemoryBegin + ANSI_BLACK);
                    createMemoryTable(internalMemoryBegin, externalMemoryBegin, internal);
                    initSegmentRegisters(internal);
                    resourceDistributor.disengage(LOADING_PACKAGE, LOAD_VIRTUAL_MACHINE_MEMORY, cpu);
                } else {
                    IC = 0;
                    System.out.println(ANSI_BLUE + "Truksta atminties--------------->" + ANSI_BLACK);
                    resourceDistributor.ask(EXTERNAL_MEMORY_DISENGAGED, this);
                }
                break;
            case 1:
                IC++;
                resourceDistributor.ask(ResourceEnum.Name.FROM_LOADER, this);
                break;
            case 2:
                IC++;
                myVirtualMachine = new VirtualMachine(this, processPlaner, resourceDistributor, taskName, taskID);
            case 3:
                IC++;
                myVirtualMachine.activate();
                break;
            case 4:
                IC++;
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

    private void executeInterrupt() {
        Resource resource = resourceDistributor.get(FROM_INTERUPT);
        String action = (String) resource.get(0).toString();
        switch (action) {
            case "TIMER":
                this.setPriority(JobGorvernorPriority);
                JobGorvernorPriority++;
                myVirtualMachine.setPriority(VirtualMachinePriority);
                VirtualMachinePriority++;
                IC = 3;
                break;
            case "HALT":
                IC = 9;
                myVirtualMachine.setPrepared(false);
                this.setPrepared(false);
                resourceDistributor.disengage(TASK_IN_DRUM, TASK_DELETE, taskID);
                break;
            case "PRINTLINE":
                IC = 6;
                resource.getElementList().remove(0);
                resourceDistributor.disengage(PRINTLINE, myVirtualMachine, resource.getElementList());
                break;
            case "PRINTLINE_NEEDS_BUFFER":
                IC = 3;
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

    public void initSegmentRegisters(Memory internalMemory) {
        long ptr = cpu.getPTR().getNumber() * 256;
        int ss_internal = internalMemory.getFreeSpaceBeginAddress();
        int ds_internal = internalMemory.getFreeSpaceBeginAddress();
        int cs_internal = internalMemory.getFreeSpaceBeginAddress();

        System.out.println(ANSI_BLUE + "ss_internal --------------->" + ss_internal + ANSI_BLACK);

        try {
            cpu.setSS(new Word(ss_internal * 256 + (STACK_SEGMENT / 256)));
            cpu.setDS(new Word(ds_internal * 256 + (DATA_SEGMENT / 256)));
            cpu.setCS(new Word(cs_internal * 256 + (CODE_SEGMENT / 256)));

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
        resourceDistributor.disengage(EXTERNAL_MEMORY_DISENGAGED);
    }

    public void cleanThisVirtualMachine() {
        try {
            System.err.println("SS -----> " + cpu.getSS() + "DS -----> " + cpu.getDS() + "CS -----> " + cpu.getCS());
            ((RealMachine) father.father).getLoader().saveVirtualMachineMemory(cpu);

            int ptr = (int) cpu.getPTR().getNumber();

            int ss = cpu.getSS().getBlockFromAddress();
            int ds = cpu.getDS().getBlockFromAddress();
            int cs = cpu.getCS().getBlockFromAddress();

            ((RealMachine) father.father).getInternalMemory().cleanSpace(ptr);
            ((RealMachine) father.father).getInternalMemory().cleanSpace(ds);
            ((RealMachine) father.father).getInternalMemory().cleanSpace(cs);
            ((RealMachine) father.father).getInternalMemory().cleanSpace(ss);

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.err.println("Clean");
    }

}
