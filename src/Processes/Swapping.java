package Processes;

import Components.CPU;
import Components.Memory;
import Resources.Resource;
import Resources.ResourceDistributor;
import Resources.ResourceEnum;
import Components.SupervisorMemory;
import Tools.Word;
import VirtualMachine.VirtualMachine;

import static Processes.ProcessEnum.Name.SWAPPING;
import static Tools.Constants.*;

public class Swapping extends ProcessInterface {

    private final RealMachine realMachine;
    private final Memory internalMemory;
    private final Memory externalMemory;
    private final SupervisorMemory supervisorMemory;

    public Swapping(RealMachine father, ProcessPlaner processPlaner, ResourceDistributor resourceDistributor) {

        super(father, ProcessEnum.State.BLOCKED,  ProcessEnum.SWAPPING_PRIORITY, SWAPPING,processPlaner, resourceDistributor);

        new Resource(this, ResourceEnum.Name.FROM_SWAPING, ResourceEnum.Type.DYNAMIC);
        new Resource(this, ResourceEnum.Name.SWAPPING, ResourceEnum.Type.DYNAMIC);

        this.realMachine = father;
        internalMemory = realMachine.getInternalMemory();
        externalMemory = realMachine.getExternalMemory();
        supervisorMemory = realMachine.getSupervisorMemory();
    }

    private int IC = 0;
    @Override
    public void executeTask() {
        super.executeTask();
        VirtualMachine vm = null;
        switch (IC) {
            case 0:
                IC++;
                resourceDistributor.ask(ResourceEnum.Name.SWAPPING,this);
                break;
            case 1:
                IC++;
                Resource resource = resourceDistributor.get(ResourceEnum.Name.SWAPPING);
                vm = (VirtualMachine)  resource.get(0);
                CPU cpu = vm.getCpu();
                String state = (String) resource.get(1);
                int block = (int) resource.get(2);
                switch (state)
                {
                    case "CS":
                        swapCS(block, cpu);
                        break;
                    case "DS":
                        swapDS(block, cpu);
                        break;
                    case "SS":
                        swapSS(block, cpu);
                        break;
                }
            case 2:
                IC = 0;
                resourceDistributor.disengage(ResourceEnum.Name.FROM_SWAPING);
                break;
        }
    }


    public void saveBlock(int internal, int external) {
        try {
            Word[] block = internalMemory.getBlock(internal);
            externalMemory.setBlock(external,block);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void swapSS(int newBlock, CPU cpu)
    {
        int internal = cpu.getSS().getBlockFromAddress();
        int external = cpu.getExternalMemoryBegin() + cpu.getSS().getWordFromAddress();
        saveBlock(internal, external);

        long addr = findSegmentInMemoryTable('S', cpu);
        try {
            internalMemory.setWord(new Word(external),addr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long ptr = cpu.getPTR().getNumber()*256;
        external = (STACK_SEGMENT/256) + newBlock;
        uploadBlock(ptr, internal, external, 'S');

        int newSSvalue = internal*256 + external;
        cpu.setSS(new Word(newSSvalue));
    }

    public void uploadBlock(long ptr, int internal, int external, char segment){
        try {

            long external_in_memory_table = internalMemory.getWord(ptr + external).getNumber();
            Word[] ds = externalMemory.getBlock((int) external_in_memory_table);
            internalMemory.setBlock(internal,ds);


            Word value =  new Word(internal);
            value.setByte(segment, 0);
            internalMemory.setWord(value, ptr + external);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void swapDS(int newBlock, CPU cpu)
    {
        int internal = cpu.getDS().getBlockFromAddress();
        int external = cpu.getExternalMemoryBegin() + cpu.getDS().getWordFromAddress();
        saveBlock(internal, external);

        long addr = findSegmentInMemoryTable('D', cpu);
        try {
            internalMemory.setWord(new Word(external),addr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long ptr = cpu.getPTR().getNumber()*256;
        external = (DATA_SEGMENT/256) + newBlock;
        uploadBlock(ptr, internal, external, 'D');


        int newDSvalue = internal*256 + external;
        cpu.setDS(new Word(newDSvalue));

    }


    private void swapCS(int newBlock, CPU cpu)
    {
        int internal = cpu.getCS().getBlockFromAddress();
        int external = cpu.getExternalMemoryBegin() + cpu.getCS().getWordFromAddress();
        saveBlock(internal, external);

        long addr = findSegmentInMemoryTable('C', cpu);
        try {
            internalMemory.setWord(new Word(external),addr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long ptr = cpu.getPTR().getNumber()*256;
        external = (CODE_SEGMENT/256) + newBlock;
        uploadBlock(ptr, internal, external, 'C');

        int newCSvalue = internal*256 + external;
        cpu.setCS(new Word(newCSvalue));

    }


    public long findSegmentInMemoryTable(int segment, CPU cpu){
        Memory internalMemory = realMachine.getInternalMemory();
        long ptr = cpu.getPTR().getNumber()*256;

        for(int i = 0; i<256; i++){
            try {
                long virtualAddress = ptr+i;
                int firstByte = internalMemory.getWord(virtualAddress).getByte(0);
                if(firstByte == segment){
                    return virtualAddress;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return  -1;
    }
}

