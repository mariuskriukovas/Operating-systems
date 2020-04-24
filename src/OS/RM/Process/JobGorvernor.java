package OS.RM.Process;

import OS.Interfaces.Memory;
import OS.RM.CPU;
import OS.Tools.Constants;
import OS.Tools.Word;

import java.util.*;
import java.util.concurrent.BlockingDeque;

import static OS.Tools.Constants.*;
import static OS.Tools.Constants.SYSTEM_MODE.SUPERVISOR_MODE;

public class JobGorvernor {
    private final CPU cpu;
    private final JobToSwap jobToSwap;
    private final HashMap<String,VirtualMachine> virtualMachines;
    private final HashMap<String,SaveCPUState> virtualMachinesMemory;
    private final SaveCPUState saveCPUState;

    private final int TIMERTIME = 1000;

    private final Deque<Integer> memoryStack;
    private final Deque<String> waitingTasks;


    public JobGorvernor(CPU cpu){
        this.cpu = cpu;
        jobToSwap = new JobToSwap(cpu);
        virtualMachines = new HashMap<>(10);
        virtualMachinesMemory = new HashMap<>(10);
        saveCPUState = new SaveCPUState();


        memoryStack = new ArrayDeque<Integer>();
        waitingTasks = new ArrayDeque<String>();

        int internalMemoryLength = 16;
        for (int i = 0; i< internalMemoryLength; i = i+4){
            memoryStack.push(i);
        }
    }

    //System.out.println("Bloku pradzia "+"------------------ > "+ pap);


    public Constants.PROCESS_STATUS createVirtualMachine(String fileName) {
        cpu.showProcess(PROCESS.JobGorvernor);
        if(waitingTasks.contains(fileName))
        {
            cpu.showPreviousProcess();
            return PROCESS_STATUS.FAILED;
        }
        if(memoryStack.isEmpty()){
            waitingTasks.push(fileName);
        }else {
            cpu.showPreviousProcess();
            return createProcess(fileName);
        }
        cpu.showPreviousProcess();
        return Constants.PROCESS_STATUS.COMPLETED;
    }

    private Constants.PROCESS_STATUS createProcess(String fileName) {
        try {
            cpu.setMODE(SUPERVISOR_MODE);
            if(memoryStack.isEmpty())throw new Exception("N0T ENOUGH INTERNALL MEMORY FOR NEW VIRTUAL MACHINE");
            int internalBlockBegin = memoryStack.pop();

            //padaryti kad irgi mestu exceptiona
            jobToSwap.uploadTaskToExternalMemory(fileName);
            int externalBlockBegin = jobToSwap.getTaskLocation(fileName);

            createMemoryTable(internalBlockBegin, externalBlockBegin);
            //  int internalBlockBegin --> RL
            cpu.setRL(new Word(internalBlockBegin));
            cpu.getLoader().loadVirtualMachineMemory();

            System.out.println("Nuo" + " " + cpu.getPTRValue(0) + " iki " + cpu.getPTRValue(255));
            System.out.println("Internal block begin ----> "+ internalBlockBegin + " externall block begin " + externalBlockBegin);
            virtualMachines.put(fileName, new VirtualMachine(cpu,internalBlockBegin,externalBlockBegin,fileName));
            virtualMachinesMemory.put(fileName,new SaveCPUState());

            return Constants.PROCESS_STATUS.COMPLETED;

        } catch (Exception e) {
            e.printStackTrace();
            return Constants.PROCESS_STATUS.FAILED;
        }

    }

    private void run(Object key)
    {
        try {
            cpu.setMODE(SUPERVISOR_MODE);
            if(virtualMachinesMemory.containsKey(key))
            {
                if(virtualMachinesMemory.get(key).getCPUState())virtualMachinesMemory.get(key).restoreCPUState();
                cpu.setTI(TIMERTIME);
                VirtualMachine vm =  virtualMachines.get(key);
                //  int internalBlockBegin --> RL
                cpu.setRL(new Word(vm.getInternalBlockBegin()));
                cpu.getLoader().loadVirtualMachineMemory();
                vm.doYourMagic();
                switch (cpu.getSI()){
                    case HALT:
                        System.out.println("REMOVE  " + key);
                        virtualMachines.remove(key);
                        virtualMachinesMemory.remove(key);
                        cleanInternalMemory(vm.getInternalBlockBegin());
                        memoryStack.push(vm.getInternalBlockBegin());
                        //clean external memory as well
                        break;
                    case TIMER_INTERUPTION:
                        virtualMachinesMemory.get(key).saveCPUState();
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Constants.PROCESS_STATUS runAll() {
        cpu.showProcess(PROCESS.JobGorvernor);
        try {
            boolean alive = true;
            while (alive) {
                Object[] keys = virtualMachines.keySet().toArray();
                for (Object key : keys) {
                    saveCPUState.saveCPUState();
                    run(key);
                    saveCPUState.restoreCPUState();
                }
                if(waitingTasks.size()>0)
                {
                    for (int i = 0; i<3; i++)
                    {
                        if(waitingTasks.size()>0)
                        {
                            createVirtualMachine(waitingTasks.pop());
                        }
                    }
                    return runAll();
                }
                if(virtualMachines.isEmpty()){
                    alive = false;
                    System.out.println("FALSE");
                }
            }
            cpu.showPreviousProcess();
            return Constants.PROCESS_STATUS.COMPLETED;
        } catch (Exception e) {
            e.printStackTrace();
            cpu.showPreviousProcess();
            return Constants.PROCESS_STATUS.FAILED;
        }
    }



    private void createMemoryTable(int internalBlockBegin, int externalBlockBegin)
    {
        Memory internalMemory = cpu.getInternalMemory();
        Memory externalMemory = cpu.getExternalMemory();

        try {
            cpu.setPTR(new Word(internalMemory.getBlockBeginAddress(internalBlockBegin)));
            for (int i = 0; i < Constants.BLOCK_LENGTH; i++) {
                cpu.setPTRValue(i, new Word(externalMemory.getBlockBeginAddress(externalBlockBegin + i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cleanInternalMemory(int internalBlockBegin)
    {
        System.out.println("Clean internal memory" + internalBlockBegin );
        try {
            int internalCS = cpu.getCS().getBlockFromAddress();
            int internalDS = cpu.getDS().getBlockFromAddress();
            int internalSS = cpu.getSS().getBlockFromAddress();

            int csb =  (CODE_SEGMENT / 256) + (int) cpu.getCSB().getNumber();
            int dsb =  (DATA_SEGMENT / 256) + (int) cpu.getCSB().getNumber();
            int ssb =  (STACK_SEGMENT / 256) + (int) cpu.getCSB().getNumber();

            csb = cpu.getPTRValue(csb).getBlockFromAddress();
            dsb = cpu.getPTRValue(dsb).getBlockFromAddress();
            ssb = cpu.getPTRValue(ssb).getBlockFromAddress();

            System.out.println("CS currentInternalBlock ------------------------- > " + internalCS);
            System.out.println("CS currentExternalBlock ------------------------- > " + csb);
            System.out.println("DS currentInternalBlock ------------------------- > " + internalDS);
            System.out.println("DS currentExternalBlock ------------------------- > " + dsb);
            System.out.println("SS currentInternalBlock ------------------------- > " + internalSS);
            System.out.println("SS currentExternalBlock ------------------------- > " + ssb);
            //reiktu juos dar surasyti i isorine del viso pikto

            cpu.setRL(new Word(internalCS));
            cpu.setRH(new Word(csb));
            cpu.getLoader().loadToExternalMemory();

            cpu.setRL(new Word(internalDS));
            cpu.setRH(new Word(dsb));
            cpu.getLoader().loadToExternalMemory();

            cpu.setRL(new Word(internalSS));
            cpu.setRH(new Word(ssb));
            cpu.getLoader().loadToExternalMemory();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        Memory internalMemory = cpu.getInternalMemory();
        for (int i = 0; i<4; i++ ) {
            Word[] block = new Word[Constants.BLOCK_LENGTH];
            for (int j = 0; j<Constants.BLOCK_LENGTH; j++)
            {
                try {
                    block[j] = new Word(0);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            try {
                internalMemory.setBlock(i+internalBlockBegin, block);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    class SaveCPUState{
//        private Constants.CONDITIONAL_MODE C = Constants.CONDITIONAL_MODE.NONE;
//        private Constants.SYSTEM_MODE MODE = Constants.SYSTEM_MODE.NONE;
//        private Constants.PROGRAM_INTERRUPTION PI = Constants.PROGRAM_INTERRUPTION.NONE;
//        private SYSTEM_INTERRUPTION SI = SYSTEM_INTERRUPTION.NONE;
//        private  Integer TI = 0;

        private Constants.CONDITIONAL_MODE C;
        private Constants.SYSTEM_INTERRUPTION SI;

        private  Word PTR = null;
        private  Word IC = null;
        private  Word SP = null;
        private  Word RH = null;
        private  Word RL = null;
        private  Word SS = null;
        private  Word DS = null;
        private  Word CS = null;
        private  Word SSB = null;
        private  Word DSB = null;
        private  Word CSB = null;

        private boolean saved;

        SaveCPUState(){
            saved = false;
        }

        public void saveCPUState()
        {
            saved = true;
            try {
                PTR = cpu.getPTR().copy();
                IC = cpu.getIC().copy();
                SP = cpu.getSP().copy();
                RH = cpu.getRH().copy();
                RL = cpu.getRL().copy();
                SS = cpu.getSS().copy();
                DS= cpu.getDS().copy();
                CS = cpu.getCS().copy();
                SSB = cpu.getSSB().copy();
                DSB = cpu.getDSB().copy();
                CSB = cpu.getCSB().copy();

                //nezinau kol kas pointeris ar objektas
                C  = cpu.getC();
                SI  = cpu.getSI();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public void restoreCPUState()
        {
            saved = false;
            try {
                cpu.setPTR(PTR);
                cpu.setIC(IC);
                cpu.setSP(SP);
                cpu.setRH(RH);
                cpu.setRL(RL);
                cpu.setSS(SS);
                cpu.setDS(DS);
                cpu.setCS(CS);
                cpu.setSSB(SSB);
                cpu.setDSB(DSB);
                cpu.setCSB(CSB);

                //nezinau kol kas pointeris ar objektas
                cpu.setC(C);
                cpu.setSI(SI);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public boolean getCPUState(){
            return saved;
        }

    }

}
