package RealMachine;

import Components.CPU;
import Components.Memory;
import Components.UI.OSFrame;
import Processes.*;
import Resources.Resource;
import Resources.ResourceDistributor;
import Resources.ResourceEnum;
import VirtualMachine.VirtualMachine;

import java.util.ArrayDeque;
import java.util.Deque;

import static Processes.ProcessEnum.Name.REAL_MACHINE;
import static Processes.ProcessEnum.REAL_MACHINE_PRIORITY;
import static Resources.ResourceEnum.Name.*;

public class RealMachine extends ProcessInterface {

    private final int MAXRUNNING = 4;

    private final Memory externalMemory;
    private final Memory internalMemory;
    private final OSFrame screen;
    private final CPU cpu;

    private final Parser parser;
    private final Loader loader;
    private final JobToSwap jobToSwap;
    private final Swapping swapping;
    private final PrintLine printLine;
    private final JobGorvernor jobGorvernor;
    private final ReadFromInterface readFromInterface;

    private final Deque<String> waitingTasks;
    private final Deque<VirtualMachine> activeTasks;



    public RealMachine(ProcessInterface father, ProcessPlaner processPlaner,  ResourceDistributor resourceDistributor) {
        super(father, ProcessEnum.State.ACTIVE, REAL_MACHINE_PRIORITY, REAL_MACHINE,processPlaner, resourceDistributor);

        //creating static resurces

        internalMemory = new Memory(this, INTERNAL_MEMORY, 16, 4);
        externalMemory = new Memory(this, EXTERNAL_MEMORY,65536, 256);


        screen = new OSFrame(this);
        cpu = new CPU(this);


        // creating dynamic resurces
        Resource r = new Resource(this, OS_END, ResourceEnum.Type.DYNAMIC);

        waitingTasks = new ArrayDeque<>(100);
        activeTasks = new ArrayDeque<VirtualMachine>(4);

        // creating process

        parser = new Parser(this, processPlaner, resourceDistributor);
        jobToSwap = new JobToSwap(this, processPlaner, resourceDistributor);
        loader = new Loader(this, processPlaner, resourceDistributor);
        swapping = new Swapping(this, processPlaner, resourceDistributor);
        printLine = new PrintLine(this, processPlaner, resourceDistributor);
        jobGorvernor = new JobGorvernor(this, processPlaner, resourceDistributor);

        readFromInterface = new ReadFromInterface(this, processPlaner, resourceDistributor);

        MainProc mainProc = new MainProc(this, processPlaner, resourceDistributor);
        Interrupt interrupt = new Interrupt(this, processPlaner, resourceDistributor);


        setActive(true);

        parser.setPrepared(false);
        jobToSwap.setPrepared(false);
        loader.setPrepared(false);
        swapping.setPrepared(false);
        printLine.setPrepared(false);
        jobGorvernor.setPrepared(false);
        readFromInterface.setPrepared(false);
        mainProc.setPrepared(false);
        interrupt.setPrepared(false);

        readFromInterface.setPrepared(true);
        mainProc.setPrepared(true);
        parser.setPrepared(true);
        jobToSwap.setPrepared(true);

        processPlaner.runOperatingSystem();
    }

    @Override
    public void executeTask() {
        super.executeTask();
        resourceDistributor.ask(OS_END,this);
//        stop();
    }

    public Memory getExternalMemory() {
        return externalMemory;
    }

    public Memory getInternalMemory() {
        return internalMemory;
    }

    public OSFrame getScreen() {
        return screen;
    }

    public CPU getCpu() {
        return cpu;
    }

    public Parser getParser() {
        return parser;
    }

    public PrintLine getPrintLine() {
        return printLine;
    }

    public Loader getLoader() {
        return loader;
    }

    public JobToSwap getJobToSwap() {
        return jobToSwap;
    }

    public Deque<String> getWaitingTasks() {
        return waitingTasks;
    }

    public Deque<VirtualMachine> getActiveTasks() {
        return activeTasks;
    }

    public Swapping getSwapping() {
        return swapping;
    }

    public JobGorvernor getJobGorvernor() {
        return jobGorvernor;
    }

    public int getMAXRUNNING() {
        return MAXRUNNING;
    }
}
