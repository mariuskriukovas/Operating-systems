package Resources;

import Components.CPU;
import Processes.ProcessInterface;
import Processes.ProcessPlaner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static Tools.Constants.*;

public class ResourceDistributor {

    private final HashMap<ResourceEnum.Name, Resource> resourcesList;
    private final HashMap<ResourceEnum.Name, ArrayList<ProcessInterface>> waitingList;
    private final ArrayList<ResourceEnum.Name> unusedList;
    private final ProcessPlaner planer;

    public ResourceDistributor(ProcessPlaner planer){
        this.planer = planer;
        resourcesList = new HashMap<ResourceEnum.Name, Resource>(10);
        waitingList = new HashMap<ResourceEnum.Name, ArrayList<ProcessInterface>>(10);
        unusedList = new ArrayList<ResourceEnum.Name>(10);
    }

    public void addResource(Resource resource){
        resourcesList.put(resource.getName(), resource);
        ArrayList<ProcessInterface> waitingProcess = new ArrayList<>(10);
        waitingList.put(resource.getName(),waitingProcess);
    }

    //Prašyti resurso. Šį primityvą kartu su primityvu “atlaisvinti resursą” procesai
    //naudoja labai dažnai. Procesas, iškvietęs šį primityvą, yra užblokuojamas ir įtraukiamas į to
    //resurso laukiančių procesų sąrašą.

    // Sekantis šio primityvo žingsnis yra kviesti resurso
    //paskirstytoją.

    public void ask(ResourceEnum.Name resource, ProcessInterface process){
        System.out.println(ANSI_RED +  "ASK : " + process.getName()
                + ANSI_BLACK + " FOR "+ ANSI_PURPLE+ resource  +ANSI_BLACK
                + ANSI_BLACK + " PRIORITY "+ ANSI_PURPLE+ process.getPriority()  +ANSI_BLACK);

        //static resources are always available for simplicity
        if(resourcesList.get(resource).getType() == ResourceEnum.Type.STATIC)
        {
            return;
        }

        boolean isFree = unusedList.stream().anyMatch(x->x==resource);
        if(isFree) {
            unusedList.remove(resource);
            process.setPrepared(true);
        }else {
            process.setPrepared(false);
            waitingList.get(resource).add(process);
        }
        //process.getProcessPlaner().plan();
    }




    public Resource get(ResourceEnum.Name resource)
    {
        resourcesList.get(resource).setAvailability(false);
        return resourcesList.get(resource);
    }


//    Atlaisvinti resursą. Šį primityvą kviečia procesas, kuris nori atlaisvinti jam
//    nereikalingą resursą arba tiesiog perduoti pranešimą ar informaciją kitam procesui. Resurso
//    elementas, primityvui perduotas kaip funkcijos parametras, yra pridedamas prie resurso elementų
//    sąrašo. Šio primityvo pabaigoje yra kviečiamas resursų paskirstytojas.

    public void disengage(ResourceEnum.Name resource, Object... elements){
        System.out.println(ANSI_GREEN + "DISENGAGE : "+ ANSI_BLACK+ resource);
        resourcesList.get(resource).getElements().push(new ArrayList<>(Arrays.asList(elements)));
        resourcesList.get(resource).setAvailability(true);
        distribute(resource);
        //planer.plan();
    }


//    Resurso
//    paskirstytojas peržvelgia visus laukiančius šio resurso procesų sąrašą, ir, sutikęs galimybę
//    aptarnauti procesą, perduoda jam reikalingus resurso elementus ir pažymi jį pasiruošusiu.

    private void distribute(ResourceEnum.Name resource){
        System.out.println("DISTRIBUTE : "+  resource);
        //tikrinamas resurso laukianciu procesu sarasas
        if(waitingList.get(resource).size()==0)
        {
            //niekam tuo metu nereikalingas resursas
            unusedList.add(resource);
        }
        waitingList.get(resource).stream().forEach(x->{
            x.setPrepared(true);
        });
    }

}
