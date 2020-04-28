package RealMachine;

import Components.CPU;
import Components.Memory;
import Components.UI.OSFrame;
import Processes.*;
import VirtualMachine.VirtualMachine;

import java.util.ArrayDeque;
import java.util.Deque;

public class RealMachine {

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
    private final MainProc mainProc;

    private final Deque<String> waitingTasks;
    private final Deque<VirtualMachine> activeTasks;

    public RealMachine(){
        internalMemory = new Memory(16, 4);
        externalMemory = new Memory(65536, 256);
        screen = new OSFrame(this);
        cpu = new CPU(this);

        waitingTasks = new ArrayDeque<>(100);
        activeTasks = new ArrayDeque<VirtualMachine>(4);

        parser = new Parser();
        jobToSwap = new JobToSwap(this);
        loader = new Loader(this);
        swapping = new Swapping(this);
        printLine = new PrintLine(this);
        jobGorvernor = new JobGorvernor(this);
        mainProc = new MainProc(this);
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
