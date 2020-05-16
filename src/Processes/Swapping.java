package Processes;

import Components.CPU;
import Components.Memory;
import RealMachine.RealMachine;
import Resources.Resource;
import Resources.ResourceDistributor;
import Resources.ResourceEnum;
import Tools.Word;

import static Processes.ProcessEnum.Name.SWAPPING;
import static Tools.Constants.*;

public class Swapping extends ProcessInterface {


    private final CPU cpu;
    private final RealMachine realMachine;
    private final Memory internalMemory;
    private final Memory externalMemory;

    public Swapping(RealMachine father, ProcessPlaner processPlaner, ResourceDistributor resourceDistributor) {

        super(father, ProcessEnum.State.BLOCKED,  ProcessEnum.SWAPPING_PRIORITY, SWAPPING,processPlaner, resourceDistributor);

        new Resource(this, ResourceEnum.Name.SWAPPING, ResourceEnum.Type.DYNAMIC);

        this.realMachine = father;
        this.cpu = realMachine.getCpu();
        internalMemory = realMachine.getInternalMemory();
        externalMemory = realMachine.getExternalMemory();


    }


    @Override
    public void executeTask() {
        super.executeTask();

        resourceDistributor.ask(ResourceEnum.Name.SWAPPING,this);

    }

    public void setSS(int newSSBlock){
        try {
            //save old SS block values
            realMachine.getLoader().saveSSBlock();
            //save old SS register value back to memory table
            long addressInMemoryTable = realMachine.getLoader().findSegmentInMemoryTable('S');
            internalMemory.setWord(cpu.getSS(),addressInMemoryTable);
            //find and set new SS block
            int ptr = (int) cpu.getPTR().getNumber() * 256;
            long adr = newSSBlock +  ptr + (STACK_SEGMENT/256);
            cpu.setSS(internalMemory.getWord(adr));
            //save cahanges in memory table
            Word value =  cpu.getPTR().add(1);
            value.setByte('S', 0);
            internalMemory.setWord(value, adr);
            //upload new SS block to internalMemory
            realMachine.getLoader().uploadSSBlock();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setDS(int newDSBlock){
        try {
            //save old DS block values
            realMachine.getLoader().saveDSBlock();
            //save old DS register value back to memory table
            long addressInMemoryTable = realMachine.getLoader().findSegmentInMemoryTable('D');
            internalMemory.setWord(cpu.getDS(),addressInMemoryTable);
            //find and set new DS block
            int ptr = (int) cpu.getPTR().getNumber() * 256;
            long adr = newDSBlock +  ptr + (DATA_SEGMENT/256);
            cpu.setDS(internalMemory.getWord(adr));
            //save cahanges in memory table
            Word value =  cpu.getPTR().add(2);
            value.setByte('D', 0);
            internalMemory.setWord(value, adr);
            //upload new DS block to internalMemory
            realMachine.getLoader().uploadDSBlock();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setCS(int newCSBlock){
        try {
            //save old CS block values
            realMachine.getLoader().saveCSBlock();
            //save old CS register value back to memory table
            long addressInMemoryTable = realMachine.getLoader().findSegmentInMemoryTable('C');
            internalMemory.setWord(cpu.getCS(),addressInMemoryTable);
            //find and set new CS block
            int ptr = (int) cpu.getPTR().getNumber() * 256;
            long adr = newCSBlock +  ptr + (CODE_SEGMENT/256);
            cpu.setCS(internalMemory.getWord(adr));
            //save cahanges in memory table
            Word value =  cpu.getPTR().add(3);
            value.setByte('C', 0);
            internalMemory.setWord(value, adr);
            //upload new CS block to internalMemory
            realMachine.getLoader().uploadCSBlock();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}


//try {
//        System.out.println(" word  --- > " + word);
//        System.out.println(" block   --- > " + block);
//        System.out.println(" getSSB   --- > " + cpu.getSSB().getNumber());
//        System.out.println(" virtual   --- > " + cpu.getSS().add(word).getHEXFormat());
//        System.out.println(" get from internal   --- > " +  cpu.getFromInternalMemory(cpu.getSS().add(word)).getHEXFormat());
//        } catch (Exception e) {
//        e.printStackTrace();
//        }
