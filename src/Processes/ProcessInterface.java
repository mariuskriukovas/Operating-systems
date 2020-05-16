package Processes;

import Resources.Resource;
import Resources.ResourceDistributor;

import java.util.ArrayList;

public abstract class ProcessInterface
{
//    Taigi, galime išskirti jau žinomas procesų būsenas:
    protected boolean active = false;
    protected boolean prepared = false;
    protected boolean stopped = false;


    protected final ProcessInterface father;
    protected ProcessEnum.State state;
    protected ProcessEnum.Name name;
    protected final int priority;

    protected final ResourceDistributor resourceDistributor;
    protected final ProcessPlaner processPlaner;

    protected final ArrayList <Resource> createdResources;
    protected final ArrayList <ProcessInterface> createdProcesses;

//    Procesų primityvai
//    Procesų primityvų paskirtis – pateikti vienodą ir paprastą vartotojo sąsają darbui su
//    procesais. Darbui su procesais skirti 4 primityvai:
//        1. Kurti procesą. Šiam primityvui perduodama nuoroda į jo tėvą, jo pradinė būsena,
//    prioritetas, perduodamų elementų sąrašas ir išorinis vardas. Pačio primityvo viduje vyksta
//    proceso kuriamasis darbas. Jis yra registruojamas bendrame procesų sąraše, tėvo-sūnų sąraše,
//    skaičiuojamas vidinis identifikacijos numeris, sukuriamas jo vaikų procesų sąrašas (tuščias),
//    sukurtų resursų sąrašas ir t.t.

    public ProcessInterface(ProcessInterface father, ProcessEnum.State state, int priority,
                            ProcessEnum.Name name, ProcessPlaner processPlaner, ResourceDistributor resourceDistributor){
        this.father = father;
        this.state = state;
        this.name = name;
        this.priority = priority;

        System.out.println("CREATE PROC : "+ this.getName());


        this.processPlaner = processPlaner;
        this.resourceDistributor = resourceDistributor;
        createdResources = new ArrayList<Resource>(10);
        createdProcesses = new ArrayList<ProcessInterface>(10);

        processPlaner.getProcessList().add(this);
        if(this.father != null){
            this.father.addProcess(this);
        }
    }

//2. Naikinti procesą. Pradedama naikinti proceso sukurtus resursus ir vaikus. Vėliau
//    išmetamas iš tėvo sukurtų procesų sąrašo. Toliau išmetamas iš bendro procesų sąrašo ir, jei
//    reikia, iš pasiruošusių procesų sąrašo. Galiausiai naikinami visi jam perduoti resursai ir proceso
//    deskriptorius yra sunaikinamas.

    public void destroy(){
        System.out.println("DESTROY : "+ this.getName());
        createdResources.forEach(Resource::destroy);
        createdProcesses.forEach(ProcessInterface::destroy);

        if(this.father != null){
            this.father.createdProcesses.remove(this);
        }
        processPlaner.getProcessList().remove(this);
        //jei reikia, iš pasiruošusių procesų sąrašo
        System.err.println("Not implemented");
        //Galiausiai naikinami visi jam perduoti resursai ir proceso deskriptorius yra sunaikinamas.
    }


//3. Stabdyti procesą. Keičiama proceso būsena iš blokuotos į blokuotą sustabdytą
//    arba iš pasiruošusios į pasiruošusią sustabdytą. Einamasis procesas stabdomas tampa
//    pasiruošusiu sustabdytu.

    public void stop(){
        System.out.println("STOP : "+ this.getName());

        stopped = true;
        if(active){
            active = false;
            prepared = true;
        }

        System.out.println("PO STOP : "+ this);
        processPlaner.plan();
    }

//        4. Aktyvuoti procesą. Keičiama proceso būsena iš blokuotos sustabdytos į
//    blokuotą, ar pasiruošusios sustabdytos į pasiruošusią.
//        Pastaba: Procesai labai aktyviai naudojasi resurso primityvais “prašyti resurso” ir
//“atlaisvini resursą”. Nereikia jų painioti su procesų primityvais.
//    Kiekvieno primityvo programos gale yra kviečiamas planuotojas.

    public void activate(){
        System.out.println("ACTIVATE : "+ this.getName());
        stopped = false;
        executeTask();
//        processPlaner.plan();
    }

    public void executeTask(){
        System.out.println("EXECUTE : "+ this.getName());
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

    public void setState(ProcessEnum.State state) {
        this.state = state;
    }

    public ProcessEnum.State getState() {
        return state;
    }

    public int getPriority() {
        return priority;
    }

    public void addProcess(ProcessInterface process){
        createdProcesses.add(process);
    }

    public void addResource(Resource resource){
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
        return  "--------------------------------------------" +  "\n"+
                name + " " + "\n"+
                "Active : " + active+ "\n"+
                "Preapared : " + prepared+"\n"+
                "Stopped : " + stopped+"\n"+
                 " Priority " + priority +
        "--------------------------------------------" ;
    }
}
