package Resources;

import Processes.ProcessInterface;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public  class Resource {
    private final ProcessInterface fatherProcess;
    private final ResourceEnum.Name name;
    private final ResourceEnum.Type type;
    private final Deque<ArrayList<Object>> elements;

    private boolean isFree = false;

    private final ArrayList<Object> elementList;
    private final ArrayList<ProcessInterface> waitingList;


//    Kurti resursą. Resursus kuria tik procesas. Resurso kūrimo metu perduodami
//    kaip parametrai: nuoroda į proceso kūrėją, resurso išorinis vardas.
//    Resursas kūrimo metu yra:
//    pridedamas prie bendro resursų sąrašo, pridedamas prie tėvo suskurtų resursų sąrašo, jam
//    priskiriamas unikalus vidinis vardas, sukuriamas resurso elementų sąrašas ir sukuriamas
//    laukiančių procesų sąrašas.

    public Resource(ProcessInterface father, ResourceEnum.Name name, ResourceEnum.Type type){
            this.fatherProcess = father;
            this.name = name;
            this.type = type;

        System.out.println("CREATE RES : "+  name);

        fatherProcess.getResourceDistributor().addResource(this);
            fatherProcess.addResource(this);
            elements = new ArrayDeque<>(10);
            elementList = new ArrayList<>(100);
            waitingList = new ArrayList<>(100);
    }


//    Naikinti resursą. Resurso deskriptorius išmetamas iš jo tėvo sukurtų resursų
//    sąrašo, naikinamas jo elementų sąrašas, atblokuojami procesai, laukiantys šio resurso, išmetamas
//    iš bendro resursų sąrašo, ir, galiausiai naikinamas pats deskriptorius.

    public void destroy(){
        fatherProcess.getCreatedResources().remove(this);
        elementList.clear();
        System.err.println("toBeImplemented");
        //deletemyself
    }

    public Object get(int i)
    {
        return elements.getFirst().get(i);
    }

    public void setAvailability(boolean free) {
        isFree = free;
    }

    public boolean isAvailable() {
        return isFree;
    }

    public ResourceEnum.Type getType() {
        return type;
    }

    public ResourceEnum.Name getName() {
        return name;
    }

    public ArrayList<Object> getElementList() {
        return elements.getFirst();
    }

    public Deque<ArrayList<Object>> getElements() {
        return elements;
    }

}
