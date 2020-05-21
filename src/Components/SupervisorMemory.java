package Components;

import Components.CPU;
import Processes.Parser;
import Processes.ProcessInterface;
import Resources.Resource;
import Resources.ResourceEnum;
import Tools.Constants;
import Tools.Exceptions;
import Tools.Word;

import java.util.ArrayList;
import java.util.HashMap;


public class SupervisorMemory extends Resource
{

    private long id = 0;
    private final HashMap<Integer, Registers> saveRMState;
    private final HashMap<Integer, Registers> saveVMState;
    private final HashMap<Integer, Registers> taskState;

    private CPU cpu;
    private final ArrayList<String> fileNames;

    private final HashMap<String, ArrayList<Parser.Command>> dataSegs;
    private final HashMap<String, ArrayList<Parser.Command>> codeSegs;



    public SupervisorMemory(ProcessInterface father){
        super(father, ResourceEnum.Name.SUPERVISOR_MEMORY, ResourceEnum.Type.STATIC);
        setAvailability(true);

        fileNames = new ArrayList<String>(10);
        dataSegs = new HashMap<>(100);
        codeSegs = new HashMap<>(100);

        saveRMState = new HashMap<>(100);
        saveVMState = new HashMap<>(100);
        taskState = new HashMap<>(100);
    }

    public ArrayList<String> getFileList()
    {
       return fileNames;
    }

    public HashMap<String, ArrayList<Parser.Command>> getDataSegs() {
        return dataSegs;
    }
    public HashMap<String, ArrayList<Parser.Command>> getCodeSegs() {
        return codeSegs;
    }



    public void saveTaskState(int taskID){
      // taskState.put(taskID, new Registers(Type.ALL));
    }

    public void restoreTaskState(int taskID){
       // taskState.get(taskID).restoreCPUState();
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

        private Word IC = null;
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
