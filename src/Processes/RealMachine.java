package Processes;

import Components.Memory;
import Components.SupervisorMemory;
import Components.UI.OSFrame;
import Resources.Resource;
import Resources.ResourceDistributor;
import Resources.ResourceEnum;
import Resources.ResourceEnum.Type;

import static Processes.ProcessEnum.Name.REAL_MACHINE;
import static Processes.ProcessEnum.REAL_MACHINE_PRIORITY;
import static Resources.ResourceEnum.Name.EXTERNAL_MEMORY;
import static Resources.ResourceEnum.Name.INTERNAL_MEMORY;
import static Resources.ResourceEnum.Name.OS_END;

public class RealMachine extends ProcessInterface {

    private final Memory externalMemory;
    private final Memory internalMemory;
    private final OSFrame screen;
    private final SupervisorMemory supervisorMemory;

    private final Parser parser;
    private final Loader loader;
    private final JobToSwap jobToSwap;
    private final Swapping swapping;
    private final PrintLine printLine;
    private final ReadFromInterface readFromInterface;
    private final MainProc mainProc;
    private final Interrupt interrupt;


    public RealMachine(ProcessPlaner processPlaner, ResourceDistributor distributor) {
        super(null, ProcessEnum.State.ACTIVE, REAL_MACHINE_PRIORITY, REAL_MACHINE, processPlaner, distributor);
        internalMemory = new Memory(this, INTERNAL_MEMORY, 16, 1);
        externalMemory = new Memory(this, EXTERNAL_MEMORY, 2560, 256);

        screen = new OSFrame(this);
        supervisorMemory = new SupervisorMemory(this);

        new Resource(this, OS_END, Type.DYNAMIC);

        parser = new Parser(this, processPlaner, distributor);
        jobToSwap = new JobToSwap(this, processPlaner, distributor);
        loader = new Loader(this, processPlaner, distributor);
        swapping = new Swapping(this, processPlaner, distributor);
        printLine = new PrintLine(this, processPlaner, distributor);
        readFromInterface = new ReadFromInterface(this, processPlaner, distributor);
        mainProc = new MainProc(this, processPlaner, distributor);
        interrupt = new Interrupt(this, processPlaner, distributor);

        setActive(true);
        setPrepared(true);

        readFromInterface.setPrepared(true);
        mainProc.setPrepared(true);
        parser.setPrepared(true);
        jobToSwap.setPrepared(true);
        loader.setPrepared(true);
        interrupt.setPrepared(true);
        printLine.setPrepared(true);
        swapping.setPrepared(true);

        screen.setVisible(true);
        screen.setReady(true);

        processPlaner.runOperatingSystem();
    }

    @Override
    public void executeTask() {
        super.executeTask();
        switch(IC) {
            case 0:
                IC++;
                resourceDistributor.ask(OS_END, this);
                break;
            case 1:
                screen.getScreenForRealMachine().getScreen().append("EXITING: " + '\n');
                try {
                    for (ProcessInterface proc : createdProcesses) {
                        screen.getScreenForRealMachine().getScreen().append("DESTROYING: " + proc.getName() + '\n');
                        Thread.sleep(300);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);
                break;
        }
    }

    public Memory getExternalMemory() {
        return externalMemory;
    }

    public Memory getInternalMemory() {
        return internalMemory;
    }

    public SupervisorMemory getSupervisorMemory() {
        return supervisorMemory;
    }

    public OSFrame getScreen() {
        return screen;
    }

    public Loader getLoader() {
        return loader;
    }
}
