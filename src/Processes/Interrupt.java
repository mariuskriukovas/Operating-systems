package Processes;

import RealMachine.RealMachine;
import Resources.Resource;
import Resources.ResourceDistributor;
import Resources.ResourceEnum;

import static Processes.ProcessEnum.INTERRUPT_PRIORITY;
import static Processes.ProcessEnum.Name.INTERRUPT;
import static Resources.ResourceEnum.Name.PROCESS_INTERRUPT;

public class Interrupt extends ProcessInterface {


    public Interrupt(RealMachine father, ProcessPlaner processPlaner, ResourceDistributor resourceDistributor) {
        super(father, ProcessEnum.State.BLOCKED, INTERRUPT_PRIORITY, INTERRUPT, processPlaner, resourceDistributor);


        new Resource(this, PROCESS_INTERRUPT, ResourceEnum.Type.DYNAMIC);

    }

    @Override
    public void executeTask() {
        super.executeTask();

        resourceDistributor.ask(PROCESS_INTERRUPT,this);
    }


    //            new Resource(this,ResourceEnum.Name.SWAPPING, ResourceEnum.Type.DYNAMIC);



    public void interrupt(){

    }

    private void address(){

    }


    private void input(){

    }


    private void output(){

    }

}
