package Processes;

import Components.CPU;
import Resources.Resource;
import Resources.ResourceDistributor;
import Resources.ResourceEnum;
import Components.SupervisorMemory;
import Tools.Word;
import VirtualMachine.VirtualMachine;

import static Processes.Interrupt.State.*;
import static Processes.ProcessEnum.INTERRUPT_PRIORITY;
import static Processes.ProcessEnum.Name.INTERRUPT;
import static Resources.ResourceEnum.Name.FROM_INTERUPT;
import static Resources.ResourceEnum.Name.PROCESS_INTERRUPT;
import static Tools.Constants.SYSTEM_INTERRUPTION.NONE;

public class Interrupt extends ProcessInterface {

    public Interrupt(RealMachine father, ProcessPlaner processPlaner, ResourceDistributor resourceDistributor) {
        super(father, ProcessEnum.State.BLOCKED, INTERRUPT_PRIORITY, INTERRUPT, processPlaner, resourceDistributor);
        new Resource(this, PROCESS_INTERRUPT, ResourceEnum.Type.DYNAMIC);
        new Resource(this, FROM_INTERUPT, ResourceEnum.Type.DYNAMIC);
    }

    int IC = 0;

    enum State{
        HALT,
        TIMER,
        PRINTLINE,
        SWAPING,
        PRINTLINE_NEEDS_BUFFER,
    }


    @Override
    public void executeTask() {
        super.executeTask();

        switch (IC) {
            case 0:
                IC++;
                resourceDistributor.ask(PROCESS_INTERRUPT,this);
                break;
            case 1:
                IC = 0;
                VirtualMachine vm = (VirtualMachine)resourceDistributor.get(PROCESS_INTERRUPT).get(0);
                CPU cpu = vm.getCpu();
                switch (cpu.getSI())
                {
                    case HALT:
                        resourceDistributor.disengage(FROM_INTERUPT, HALT);
                        break;
                    case TIMER:
                        cpu.increaseIC();
                        cpu.setSI(NONE);
                        resourceDistributor.disengage(FROM_INTERUPT, TIMER);
                        break;
                    case PRINTLINE_GET:
                        String address = vm.getInterpretator().getVirtualAddress();
                        cpu.increaseIC();
                        cpu.setSI(NONE);
                        resourceDistributor.disengage(FROM_INTERUPT,PRINTLINE, "INPUT", address);
                        break;
                    case PRINTLINE_PUT:
                        address = vm.getInterpretator().getVirtualAddress();
                        for (int i = 0; i<16; i++)
                        {
                            int addr = Integer.parseInt(address, 16);
                            vm.getOutputBuffer().add(vm.bufferElementsFactory(
                                    new Word(addr+i),
                                    null
                            ));
                        }
                        cpu.increaseIC();
                        cpu.setSI(NONE);
                        resourceDistributor.disengage(FROM_INTERUPT, PRINTLINE_NEEDS_BUFFER);
                        break;
                    case PRINTLINE_READING_DONE:
                        cpu.setSI(NONE);
                        resourceDistributor.disengage(FROM_INTERUPT, PRINTLINE,"OUTPUT", "WORDS");
                        break;
                    case PRINTLINE_PUT_R:
                        cpu.increaseIC();
                        cpu.setSI(NONE);
                        resourceDistributor.disengage(FROM_INTERUPT,PRINTLINE, "OUTPUT", "REGISTERS");
                        break;
                    case SWAPING_CS:
                        int newBlock = cpu.getRL().getBlockFromAddress();
                        cpu.setSI(NONE);
                        resourceDistributor.disengage(FROM_INTERUPT, SWAPING, "CS",newBlock);
                        break;
                    case SWAPING_DS:
                        newBlock = cpu.getRL().getBlockFromAddress();
                        cpu.setSI(NONE);
                        resourceDistributor.disengage(FROM_INTERUPT, SWAPING, "DS",newBlock);
                        break;
                    case SWAPING_SS:
                        newBlock = cpu.getRL().getBlockFromAddress();
                        cpu.setSI(NONE);
                        resourceDistributor.disengage(FROM_INTERUPT, SWAPING,"SS",newBlock);
                        break;
                }
                break;
        }
    }


}
