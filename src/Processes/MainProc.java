package Processes;

import Resources.Resource;
import Resources.ResourceDistributor;
import Resources.ResourceEnum;

import static Processes.ProcessEnum.MAIN_PROC_PRIORITY;
import static Processes.ProcessEnum.Name.MAIN_PROC;
import static Resources.ResourceEnum.Name.EXTERNAL_MEMORY_DISENGAGED;
import static Resources.ResourceEnum.Name.START_EXECUTION;
import static Resources.ResourceEnum.Name.TASK_IN_DRUM;
import static Resources.ResourceEnum.Name.WAIT_UNTIL_DESTRUCTION;
import static Resources.ResourceEnum.Type.DYNAMIC;
import static Tools.Constants.ANSI_BLACK;
import static Tools.Constants.ANSI_RED;

public class MainProc extends ProcessInterface {

    private int TaskCounter = 0;

    public MainProc(RealMachine father, ProcessPlaner planner, ResourceDistributor distributor) {
        super(father, ProcessEnum.State.BLOCKED, MAIN_PROC_PRIORITY, MAIN_PROC, planner, distributor);
        new Resource(this, TASK_IN_DRUM, DYNAMIC);
        new Resource(this, START_EXECUTION, DYNAMIC);
        new Resource(this, EXTERNAL_MEMORY_DISENGAGED, DYNAMIC);
        new Resource(this, WAIT_UNTIL_DESTRUCTION, DYNAMIC);
    }

    @Override
    public void executeTask() {
        super.executeTask();
        switch (IC) {
            case 0:
                IC++;
                resourceDistributor.ask(START_EXECUTION, this);
                break;
            case 1:
                IC++;
                resourceDistributor.ask(TASK_IN_DRUM, this);
                break;
            case 2:
                IC = 1;
                TaskCounter++;
                Resource task = resourceDistributor.get(TASK_IN_DRUM);
                State taskState = (State) task.get(0);
                System.out.println(ANSI_RED + "TURI_ATEITI_IKI_CIA --------------->" + taskState + ANSI_BLACK);
                switch (taskState) {
                    case TASK_CREATED:
                        new JobGorvernor(this, processPlaner, resourceDistributor, task);
                        break;
                    case TASK_DELETE:
                        int taskID = (int) task.get(1);
                        JobGorvernor jobGorvernor = null;
                        for (int i = 0; i < createdProcesses.size(); i++) {
                            jobGorvernor = (JobGorvernor) createdProcesses.get(i);
                            if (jobGorvernor.getTaskID() == taskID) {
                                jobGorvernor.destroy();
                                break;
                            }
                        }
                        createdProcesses.remove(jobGorvernor);
                        if (createdProcesses.size() == 0) {
                            IC = 3;
                        }
                        System.out.println(ANSI_RED + "Naikinamas procesas JobGorvernor, sukūręs gautąjį resursą" + taskID + ANSI_BLACK);
                        break;
                }
                break;
            case 3:
                IC = 0;
                resourceDistributor.disengage(ResourceEnum.Name.TASK_COMPLETED, "Darbas BAIGTAS !!!");
                break;
        }
    }

    enum State {
        TASK_CREATED,
        TASK_PREPARED,
        TASK_DELETE,
    }
}
