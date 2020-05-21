package Processes;

import Components.Memory;
import Components.UI.OSFrame;
import Resources.Resource;
import Resources.ResourceDistributor;
import Resources.ResourceEnum;
import Components.SupervisorMemory;

import static Processes.ProcessEnum.Name.REAL_MACHINE;
import static Processes.ProcessEnum.REAL_MACHINE_PRIORITY;
import static Resources.ResourceEnum.Name.*;

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



    public RealMachine(ProcessPlaner processPlaner,  ResourceDistributor resourceDistributor) {
        super(null, ProcessEnum.State.ACTIVE, REAL_MACHINE_PRIORITY, REAL_MACHINE,processPlaner, resourceDistributor);
        internalMemory = new Memory(this, INTERNAL_MEMORY, 16, 1);
        //externalMemory = new Memory(this, EXTERNAL_MEMORY,65536, 256);
        externalMemory = new Memory(this, EXTERNAL_MEMORY,2560, 256);

        screen = new OSFrame(this);
        //cpu = new CPU(this);
        supervisorMemory = new SupervisorMemory(this);


        new Resource(this, OS_END, ResourceEnum.Type.DYNAMIC);

        // creating process

        parser = new Parser(this, processPlaner, resourceDistributor);
        jobToSwap = new JobToSwap(this, processPlaner, resourceDistributor);
        loader = new Loader(this, processPlaner, resourceDistributor);
        swapping = new Swapping(this, processPlaner, resourceDistributor);
        printLine = new PrintLine(this, processPlaner, resourceDistributor);
        readFromInterface = new ReadFromInterface(this, processPlaner, resourceDistributor);
        mainProc = new MainProc(this, processPlaner, resourceDistributor);
        interrupt = new Interrupt(this, processPlaner, resourceDistributor);


        setActive(true);

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
        resourceDistributor.ask(OS_END,this);
//        stop();
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
