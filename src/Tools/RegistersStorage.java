package Tools;

import Components.CPU;

import java.util.HashMap;

public class RegistersStorage {

    private long id = 0;
    private final HashMap<Long, Registers> saveCPUState;
    private CPU cpu;

    public RegistersStorage(CPU cpu){
        this.cpu = cpu;
        saveCPUState = new HashMap<>(100);
    }

    private long generateID(){
        id++;
        return id;
    }

    public long saveVMRegisters(){
        long id = generateID();
        saveCPUState.put(id, new Registers(Type.VM));
        return id;
    }

    public long saveRMRegisters(){
        long id = generateID();
        saveCPUState.put(id, new Registers(Type.RM));
        return id;
    }

    public long saveAllRegisters(){
        long id = generateID();
        saveCPUState.put(id, new Registers(Type.ALL));
        return id;
    }

    public void restoreRegisters(long id){
        Registers r = saveCPUState.get(id);
        r.restoreCPUState();
        saveCPUState.remove(id);
    }


    enum Type{
        RM,
        VM,
        ALL
    }

    //private Constants.CONDITIONAL_MODE C = Constants.CONDITIONAL_MODE.NONE;
    //private Constants.SYSTEM_MODE MODE = Constants.SYSTEM_MODE.NONE;
    //private Constants.PROGRAM_INTERRUPTION PI = Constants.PROGRAM_INTERRUPTION.NONE;
    //private SYSTEM_INTERRUPTION SI = SYSTEM_INTERRUPTION.NONE;
    //private  Integer TI = 0;

    class Registers{

        private  Word IC = null;
        private  Word SP = null;
        private  Word RH = null;
        private  Word RL = null;
        private Constants.CONDITIONAL_MODE C;


        private  Word PTR = null;
        private  Word SS = null;
        private  Word DS = null;
        private  Word CS = null;
        private Constants.SYSTEM_INTERRUPTION SI;

        private final Type type;


        Registers(Type type){
            this.type = type;
            switch (type){
                case RM:
                   saveRM();
                   break;
                case VM:
                    saveVM();
                    break;
                case ALL:
                    saveRM();
                    saveVM();
                    break;
            }
        }

        //nezinau kol kas pointeris ar objektas
        //System.out.println("SAVE" + "RL :" + RL);
        //System.out.println("SAVE" +"RH :" + RH);
        //System.out.println("SAVE" +"C :" + C);

        private void saveVM(){
            RH = cpu.getRH().copy();
            RL = cpu.getRL().copy();
            IC = cpu.getIC().copy();
            SP = cpu.getSP().copy();
            C  = cpu.getC();
        }

        private void saveRM(){
            PTR = cpu.getPTR().copy();
            SS = cpu.getSS().copy();
            DS= cpu.getDS().copy();
            CS = cpu.getCS().copy();
            SI  = cpu.getSI();
        }

        private void restoreVM(){
            try {
                cpu.setRH(RH);
                cpu.setRL(RL);
                cpu.setIC(IC);
                cpu.setSP(SP);

                cpu.setC(C);
            } catch (Exceptions.InstructionPointerException e) {
                //Imposible because it was already saved one time
                e.printStackTrace();
            }

        }

        private void restoreRM(){
            cpu.setPTR(PTR);
            cpu.setSS(SS);
            cpu.setDS(DS);
            cpu.setCS(CS);

            cpu.setSI(SI);
        }


        public void restoreCPUState() {
            switch (type){
                case RM:
                    restoreRM();
                    break;
                case VM:
                    restoreVM();
                    break;
                case ALL:
                    restoreRM();
                    restoreVM();
                    break;
            }
        }
    }

}
