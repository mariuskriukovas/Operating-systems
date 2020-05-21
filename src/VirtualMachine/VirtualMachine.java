package VirtualMachine;

import Components.CPU;
import Components.UI.VMPanel;
import Processes.JobGorvernor;
import Processes.ProcessEnum;
import Processes.ProcessInterface;
import Processes.ProcessPlaner;
import Processes.RealMachine;
import Resources.ResourceDistributor;
import Resources.ResourceEnum;
import Tools.Constants;
import Tools.Exceptions;
import Components.SupervisorMemory;
import Tools.Word;

import java.util.ArrayList;

import static Processes.ProcessEnum.Name.VIRTUAL_MACHINE;
import static Processes.ProcessEnum.VIRTUAL_MACHINE_PRIORITY;
import static Tools.Constants.*;
import static Tools.Constants.SYSTEM_INTERRUPTION.PRINTLINE_READING_DONE;

public class VirtualMachine extends ProcessInterface {

    public static int VirtualMachinePriority = VIRTUAL_MACHINE_PRIORITY;
    private static int Timer = 10;

    private  RealMachine realMachine;
    private  Interpretator interpretator;
    private final String name;
    private final CPU cpu;
    private boolean launchedFirstTime = true;
    private final int taskID;
    private final SupervisorMemory supervisorMemory;
    private final ArrayList<BufferElements> inputBuffer;
    private final ArrayList<BufferElements> outputBuffer;

    public class BufferElements{
        Word address;
        Word value;
        BufferElements(Word address, Word value){
            this.address = address;
            this.value = value;
        }

        public Word getAddress() {
            return address;
        }

        public Word getValue() {
            return value;
        }
    }

    public BufferElements bufferElementsFactory(Word address, Word value)
    {
        return new  BufferElements(address, value);
    }


    public VirtualMachine(JobGorvernor father, ProcessPlaner processPlaner, ResourceDistributor resourceDistributor,
                          String name, int ID)
    {
        super(father, ProcessEnum.State.ACTIVE, VirtualMachinePriority, VIRTUAL_MACHINE,processPlaner, resourceDistributor);
        VirtualMachinePriority++;

        this.name = name;
        this.taskID = ID;
        this.cpu = father.getCpu();
        inputBuffer = new ArrayList<>();
        outputBuffer = new ArrayList<>();

        interpretator = new Interpretator(cpu);
        supervisorMemory = father.getRealMachine().getSupervisorMemory();
    }

    public ArrayList<BufferElements> getInputBuffer() {
        return inputBuffer;
    }

    public ArrayList<BufferElements> getOutputBuffer() {
        return outputBuffer;
    }

    public CPU getCpu() {
        return cpu;
    }

    private void prepareRegisters(){
        try {
            System.out.println("---------------> prepareRegisters");
            cpu.setC(Constants.CONDITIONAL_MODE.NONE);
            cpu.setRL(new Word(0));
            cpu.setRH(new Word(0));
            cpu.setIC(new Word(0));
            cpu.setSP(new Word(0));
            cpu.setTI(Timer);
            cpu.setMODE(Constants.SYSTEM_MODE.USER_MODE);
            cpu.setPI(Constants.PROGRAM_INTERRUPTION.NONE);
            launchedFirstTime = false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void testTimer(){
        cpu.decTI();
        if(cpu.getTI()==0){
            cpu.setTI(Timer);
            cpu.setSI(Constants.SYSTEM_INTERRUPTION.TIMER);
        }
    }

    public void doYourMagic(){ }

    public Interpretator getInterpretator() {
        return interpretator;
    }

    private boolean writeFromBuffer(){
        if(inputBuffer.size()>0)
        {
            while (inputBuffer.size()!=0)
            {
                BufferElements e = inputBuffer.get(0);
                try {
                    cpu.setDS(e.getAddress(),e.getValue());
                } catch (Exceptions.WrongAddressException ex) {
                    ex.printStackTrace();
                }
                if(!cpu.getSI().equals(Constants.SYSTEM_INTERRUPTION.NONE))
                {
                    return true;
                }
                inputBuffer.remove(0);
            }
        }
        return false;
    }

    private int wordsReaded = 0;
    private boolean readToBuffer(){
        if(outputBuffer.size()>0)
        {
            while (outputBuffer.size()!=wordsReaded)
            {
                BufferElements e = outputBuffer.get(0);
                try {
                    wordsReaded++;
                    e.value = cpu.getDS(e.getAddress());
                } catch (Exceptions.WrongAddressException ex) {
                    ex.printStackTrace();
                }
                System.out.println(ANSI_PURPLE + "READ BUFFER -----> " + e.address + "<---->" + e.value +ANSI_BLACK);
                if(!cpu.getSI().equals(Constants.SYSTEM_INTERRUPTION.NONE))
                {
                    return true;
                }
            }
            wordsReaded = 0;
            cpu.setSI(PRINTLINE_READING_DONE);
            return true;
        }
        return false;
    }

    @Override
    public void executeTask() {
        super.executeTask();
        if(launchedFirstTime)prepareRegisters();
        cpu.setMODE(SYSTEM_MODE.USER_MODE);
        cpu.refresh();
        try {
            while (true)
            {
                if(writeFromBuffer())break;
                if(readToBuffer())break;

                Word address = cpu.getIC().copy();
                Word value =  cpu.getCS(address);
                if(value!=null)
                {
                    String command = value.getASCIIFormat();
                    System.out.println(ANSI_RED + "---------------------------------------------  > "+command + ANSI_BLACK);
                    interpretator.execute(command);
                    System.out.println(ANSI_RED + " RL: " + cpu.getRL() + "  RH:  "+ cpu.getRH() + " C: " + cpu.getC() + ANSI_BLACK);
                    if(!cpu.getSI().equals(Constants.SYSTEM_INTERRUPTION.NONE))break;
                    testTimer();
                    cpu.increaseIC();
                }
            }
        } catch (Exceptions.ProgramInteruptionException e) {
            e.printStackTrace();
        }
        resourceDistributor.disengage(ResourceEnum.Name.PROCESS_INTERRUPT, this);
    }

    public String getName() {
        return name;
    }

    public int getTaskID() {
        return taskID;
    }
}