package Processes;

import RealMachine.RealMachine;
import Resources.Resource;
import Resources.ResourceDistributor;
import Resources.ResourceEnum;

import static Processes.ProcessEnum.MAIN_PROC_PRIORITY;
import static Processes.ProcessEnum.Name.MAIN_PROC;
import static Tools.Constants.ANSI_BLACK;
import static Tools.Constants.ANSI_BLUE;

public class MainProc extends ProcessInterface
{

    public MainProc(RealMachine father, ProcessPlaner processPlaner, ResourceDistributor resourceDistributor) {
        super(father, ProcessEnum.State.BLOCKED, MAIN_PROC_PRIORITY, MAIN_PROC, processPlaner, resourceDistributor);
        new Resource(this, ResourceEnum.Name.TASK_IN_DRUM, ResourceEnum.Type.DYNAMIC);
        new Resource(this, ResourceEnum.Name.START_EXECUTION, ResourceEnum.Type.DYNAMIC);

    }

    private int IC = 0;


    @Override
    public void executeTask() {
        super.executeTask();
        switch (IC)
        {
            case 0:
                IC++;
                //Blokavimasis laukiant “Pradėti vykdymą” resurso
                resourceDistributor.ask(ResourceEnum.Name.START_EXECUTION,this);
                break;
            case 1:
                IC++;
                //Blokavimasis laukiant “Užduotis būgne” resurso
                resourceDistributor.ask(ResourceEnum.Name.TASK_IN_DRUM,this);
                break;
            case 2:
                IC=0;
                System.out.println(ANSI_BLUE + "TURIATEITIIKICIA --------------->"  + ANSI_BLACK);
                boolean isTaskPrepared = true;
                if(isTaskPrepared)
                {
                    //Kuriamas procesas JobGorvernor, suteikiant jam “Užduotis būgne” kaip pradinį resursą
                    resourceDistributor.disengage(ResourceEnum.Name.TASK_COMPLETED,  "Tam kartui uzteks");
                }else {
                    //Naikinamas procesas JobGorvernor, sukūręs gautąjį resursą
                }
                break;
        }

    }
}
