package Processes;

import Components.CPU;
import Components.Memory;
import Resources.Resource;
import Resources.ResourceDistributor;
import Resources.ResourceEnum;
import Tools.Constants;
import Tools.Word;

import static Processes.ProcessEnum.Name.LOADER;
import static Resources.ResourceEnum.Name.FROM_LOADER;
import static Resources.ResourceEnum.Name.LOADING_PACKAGE;
import static Tools.Constants.*;

public class Loader extends ProcessInterface {

    private final RealMachine realMachine;
    private final Memory internalMemory;
    private final Memory externalMemory;

    enum State{
        LOAD_FROM_INTERNAL_TO_EXTERNAL,
        LOAD_FROM_EXTERNAL_TO_INTERNAL,
        LOAD_VIRTUAL_MACHINE_MEMORY,
        SAVE_VIRTUAL_MACHINE_MEMORY,
    }

    public Loader(RealMachine father, ProcessPlaner processPlaner, ResourceDistributor resourceDistributor)
    {
        super(father, ProcessEnum.State.BLOCKED, ProcessEnum.LOADER_PRIORITY, LOADER,processPlaner, resourceDistributor);
        this.realMachine = father;
       internalMemory = realMachine.getInternalMemory();
       externalMemory = realMachine.getExternalMemory();

        new Resource(this, LOADING_PACKAGE, ResourceEnum.Type.DYNAMIC);
        new Resource(this, FROM_LOADER, ResourceEnum.Type.DYNAMIC);
    }

    private int IC = 0;


//    //    int fromBlock -->RL
//    //    int toBlock --> RH
//    public void loadToInternalMemory()
//    {
//        int fromBlock  = (int) cpu.getRL().getNumber();
//        int toBlock  = (int) cpu.getRH().getNumber();
//
//        System.out.println(fromBlock);
//
//        int internalBegin = toBlock*256;
//        int externalBegin = fromBlock*256;
//
//        for(int i = 0;i<Constants.BLOCK_LENGTH; i++)
//        {
//            try {
//                Word word = cpu.getFromExternalMemory(new Word(i+externalBegin));
//                cpu.writeToInternalMemory(new Word(i+internalBegin), word);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    //int fromBlock -->RL
//    //int toBlock --> RH
//    public void loadToExternalMemory()
//    {
//        int fromBlock  = (int) cpu.getRL().getNumber();
//        int toBlock  = (int) cpu.getRH().getNumber();
//
//        int internalBegin = fromBlock*Constants.BLOCK_LENGTH;
//        int externalBegin = toBlock*Constants.BLOCK_LENGTH;
//
//        for(int i = 0;i<Constants.BLOCK_LENGTH; i++)
//        {
//            try {
//                Word word = cpu.getFromInternalMemory(new Word(i+internalBegin));
//                cpu.writeToExternalMemory(new Word(i+externalBegin), word);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void saveSegmentRegisters(){
//        Memory internalMemory = realMachine.getInternalMemory();
//
//        long ptr = cpu.getPTR().getNumber()*256;
//
//        for(int i = 0; i<256; i++){
//            try {
//                int segment = internalMemory.getWord(ptr+i).getByte(0);
//                if(segment == 'S'){
//                    System.out.println("S");
//                }else if(segment == 'D'){
//                    System.out.println("D");
//                }else if(segment == 'C'){
//                    System.out.println("C");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

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


    //  PTR ----- > internalBEGIN
    //  SS ----- > internalMemory
    //  DS ----- > internalMemory
    //  CS ----- > internalMemory
    public void loadVirtualMachineMemory(CPU cpu) {

        long ptr = cpu.getPTR().getNumber()*256;
        int ss_internal = cpu.getSS().getBlockFromAddress();
        int ss_external = cpu.getSS().getWordFromAddress();
        int ds_internal = cpu.getDS().getBlockFromAddress();
        int ds_external = cpu.getDS().getWordFromAddress();
        int cs_internal = cpu.getCS().getBlockFromAddress();
        int cs_external = cpu.getCS().getWordFromAddress();

        uploadBlock(ptr, ss_internal, ss_external, 'S');
        uploadBlock(ptr, ds_internal, ds_external, 'D');
        uploadBlock(ptr, cs_internal, cs_external, 'C');

    }


    public void saveBlock(int internal, int external) {
        try {
            Word[] block = internalMemory.getBlock(internal);
            externalMemory.setBlock(external,block);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveVirtualMachineMemory(CPU cpu) {
        int internal = cpu.getSS().getBlockFromAddress();
        int external = cpu.getExternalMemoryBegin() + cpu.getSS().getWordFromAddress();
        saveBlock(internal, external);
        internal = cpu.getDS().getBlockFromAddress();
        external = cpu.getExternalMemoryBegin() + cpu.getDS().getWordFromAddress();
        saveBlock(internal, external);
        internal = cpu.getCS().getBlockFromAddress();
        external = cpu.getExternalMemoryBegin() + cpu.getCS().getWordFromAddress();
        saveBlock(internal, external);
    }


    @Override
    public void executeTask() {
        super.executeTask();

        switch (IC) {
            case 0:
                IC++;
                resourceDistributor.ask(LOADING_PACKAGE,this);
                break;
            case 1:
                IC=0;
                //Nustatinėjami registrai ir vykdomas blokų perkėlimas
                State state = (State) resourceDistributor.get(LOADING_PACKAGE).get(0);
                CPU cpu = (CPU) resourceDistributor.get(LOADING_PACKAGE).get(1);
                System.out.println(ANSI_GREEN+"STATE -----------------> "+ state+ ANSI_BLACK);

                switch (state)
                {
                    case LOAD_VIRTUAL_MACHINE_MEMORY:
                        loadVirtualMachineMemory(cpu);
                        break;
                    case SAVE_VIRTUAL_MACHINE_MEMORY:
                        saveVirtualMachineMemory(cpu);
                        break;
                    case LOAD_FROM_EXTERNAL_TO_INTERNAL:

                        break;
                    case LOAD_FROM_INTERNAL_TO_EXTERNAL:

                        break;
                }
                resourceDistributor.disengage(FROM_LOADER);
                break;
        }
    }

}
