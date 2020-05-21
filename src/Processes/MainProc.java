package Processes;

import Resources.Resource;
import Resources.ResourceDistributor;
import Resources.ResourceEnum;

import static Processes.ProcessEnum.MAIN_PROC_PRIORITY;
import static Processes.ProcessEnum.Name.MAIN_PROC;
import static Tools.Constants.*;

public class MainProc extends ProcessInterface
{

    public MainProc(RealMachine father, ProcessPlaner processPlaner, ResourceDistributor resourceDistributor) {
        super(father, ProcessEnum.State.BLOCKED, MAIN_PROC_PRIORITY, MAIN_PROC, processPlaner, resourceDistributor);
        new Resource(this, ResourceEnum.Name.TASK_IN_DRUM, ResourceEnum.Type.DYNAMIC);
        new Resource(this, ResourceEnum.Name.START_EXECUTION, ResourceEnum.Type.DYNAMIC);
        new Resource(this, ResourceEnum.Name.EXTERNAL_MEMORY_DISENGAGED, ResourceEnum.Type.DYNAMIC);
        new Resource(this, ResourceEnum.Name.WAIT_UNTIL_DESTRUCTION, ResourceEnum.Type.DYNAMIC);
    }

    enum State{
        TASK_CREATED,
        TASK_PREPARED,
        TASK_DELETE,
    }

    private int IC = 0;
    private int TaskCounter= 0;


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
                IC=1;
                TaskCounter++;
                Resource task = resourceDistributor.get(ResourceEnum.Name.TASK_IN_DRUM);
                State taskState = (State)task.get(0);
                System.out.println(ANSI_RED + "TURIATEITIIKICIA --------------->" + taskState + ANSI_BLACK);
                switch (taskState)
                {
                    case TASK_CREATED:
                        //Kuriamas procesas JobGorvernor, suteikiant jam “Užduotis būgne” kaip pradinį resursą
                        new JobGorvernor(this, processPlaner, resourceDistributor, task);
                        break;
                    case TASK_DELETE:
                        int taskID = (int)task.get(1);
                        JobGorvernor jobGorvernor = null;
                        for (int i= 0; i< createdProcesses.size(); i++){
                            jobGorvernor = (JobGorvernor)createdProcesses.get(i);
                            if(jobGorvernor.getTaskID()== taskID)
                            {
                                jobGorvernor.destroy();
                                break;
                            }
                        }
                        createdProcesses.remove(jobGorvernor);
                        if(createdProcesses.size()==0){
                            IC = 3;
                        }
                        System.out.println(ANSI_RED + "Naikinamas procesas JobGorvernor, sukūręs gautąjį resursą" + taskID+ ANSI_BLACK);
                        break;
                }
                break;
            case 3:
                IC = 0;
                resourceDistributor.disengage(ResourceEnum.Name.TASK_COMPLETED,  "Darbas BAIGTAS !!!");
                break;
        }
    }
}
