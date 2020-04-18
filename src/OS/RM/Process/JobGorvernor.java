package OS.RM.Process;

import OS.Interfaces.Memory;
import OS.RM.CPU;
import OS.Tools.Constants;
import OS.Tools.Word;

import java.util.*;

import static OS.Tools.Constants.SYSTEM_MODE.SUPERVISOR_MODE;

public class JobGorvernor {
    private final CPU cpu;
    private final JobToSwap jobToSwap;
    private final HashMap<String,VirtualMachine> virtualMachines;
    private final HashMap<String,SaveCPUState> virtualMachinesMemory;
    private final SaveCPUState saveCPUState;

    private final int TIMERTIME = 10;


    private final Deque<Integer> memoryStack;
    private final Deque<VirtualMachine> VMStack;


    public JobGorvernor(CPU cpu){
        this.cpu = cpu;
        jobToSwap = new JobToSwap(cpu);
        virtualMachines = new HashMap<>(10);
        virtualMachinesMemory = new HashMap<>(10);
        saveCPUState = new SaveCPUState();


        memoryStack = new ArrayDeque<Integer>();
        VMStack = new ArrayDeque<VirtualMachine>();

        int internalMemoryLength = 16;
        for (int i = 0; i< internalMemoryLength; i = i+4){
            memoryStack.push(i);
        }
    }

//                System.out.println("Bloku pradzia "+"------------------ > "+ pap);


    public Constants.PROCESS_STATUS createVirtualMachine(String fileName) {
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

            virtualMachines.put(fileName, new VirtualMachine(fileName, cpu));
            virtualMachinesMemory.put(fileName,new SaveCPUState());

            VMStack.push(new VirtualMachine(fileName, cpu));
            return Constants.PROCESS_STATUS.COMPLETED;

        } catch (Exception e) {
            e.printStackTrace();
            return Constants.PROCESS_STATUS.FAILED;
        }

    }

//    public Constants.PROCESS_STATUS run(String fileName) {
//        try {
//            cpu.setMODE(SUPERVISOR_MODE);
//            cpu.setTI(12);
//            VirtualMachine vm =  virtualMachines.get(fileName);
//            vm.doYourMagic();
//
//            return Constants.PROCESS_STATUS.COMPLETED;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Constants.PROCESS_STATUS.FAILED;
//        }
//    }


    public void run(Object key)
    {
        try {
            cpu.setMODE(SUPERVISOR_MODE);
            if(virtualMachinesMemory.containsKey(key))
            {
                if(virtualMachinesMemory.get(key).getCPUState())virtualMachinesMemory.get(key).restoreCPUState();
                cpu.setTI(TIMERTIME);
                VirtualMachine vm =  virtualMachines.get(key);
                vm.doYourMagic();
                switch (cpu.getSI()){
                    case HALT:
                        virtualMachines.remove(key);
                        virtualMachinesMemory.remove(key);
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
        try {
            boolean alive = true;
            while (alive) {
                Object[] keys = virtualMachines.keySet().toArray();
                for (Object key : keys) {
                    saveCPUState.saveCPUState();
                    run(key);
                    saveCPUState.restoreCPUState();
                }
                if(virtualMachines.isEmpty())alive = false;
            }
            return Constants.PROCESS_STATUS.COMPLETED;

        } catch (Exception e) {
            e.printStackTrace();
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
