package Processes;

import Processes.ProcessEnum.Name;
import Processes.ProcessEnum.State;
import Resources.Resource;
import Resources.ResourceDistributor;

import java.util.ArrayList;

public abstract class ProcessInterface {
    protected final ProcessInterface father;
    protected final ResourceDistributor resourceDistributor;
    protected final ProcessPlaner processPlaner;
    protected final ArrayList<Resource> createdResources;
    protected final ArrayList<ProcessInterface> createdProcesses;

    protected boolean active = false;
    protected boolean prepared = false;
    protected boolean stopped = false;
    protected State state;
    protected Name name;
    protected int priority;

    public ProcessInterface(ProcessInterface father, State state, int priority,
                            Name name, ProcessPlaner planner, ResourceDistributor distributor) {
        this.father = father;
        this.state = state;
        this.name = name;
        this.priority = priority;

        System.out.println("CREATE PROC : " + this.getName());


        this.processPlaner = planner;
        this.resourceDistributor = distributor;
        createdResources = new ArrayList<Resource>(10);
        createdProcesses = new ArrayList<ProcessInterface>(10);

        planner.getProcessList().add(this);
        if (this.father != null) {
            this.father.addProcess(this);
        }
    }

    public void destroy() {
        System.out.println("DESTROY : " + this.getName());
        createdResources.forEach(Resource::destroy);
        createdProcesses.forEach(ProcessInterface::destroy);

        if (this.father != null) {
            this.father.createdProcesses.remove(this);
        }
        processPlaner.getProcessList().remove(this);
        System.err.println("Not implemented");
    }

    public void stop() {
        System.out.println("STOP : " + this.getName());
        stopped = true;
        if (active) {
            active = false;
            prepared = true;
        }

        System.out.println("PO STOP : " + this);
        processPlaner.plan();
    }

    public void activate() {
        System.out.println("ACTIVATE : " + this.getName());
        stopped = false;
        executeTask();
    }

    public void executeTask() {
        System.out.println("EXECUTE : " + this.getName());
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isPrepared() {
        return prepared;
    }

    public void setPrepared(boolean prepared) {
        this.prepared = prepared;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void addProcess(ProcessInterface process) {
        createdProcesses.add(process);
    }

    public void addResource(Resource resource) {
        createdResources.add(resource);
    }

    public ResourceDistributor getResourceDistributor() {
        return resourceDistributor;
    }

    public ArrayList<Resource> getCreatedResources() {
        return createdResources;
    }

    public ProcessPlaner getProcessPlaner() {
        return processPlaner;
    }

    public String getName() {
        return name.name();
    }

    @Override
    public String toString() {
        return "--------------------------------------------" + "\n" +
                name + " " + "\n" +
                "Active : " + active + "\n" +
                "Preapared : " + prepared + "\n" +
                "Stopped : " + stopped + "\n" +
                " Priority " + priority +
                "--------------------------------------------";
    }
}
