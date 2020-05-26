package Resources;

import Processes.ProcessInterface;
import Processes.ProcessPlaner;
import Resources.ResourceEnum.Name;

import java.util.ArrayList;
import java.util.HashMap;

import static Resources.ResourceEnum.Type.STATIC;
import static Tools.Constants.ANSI_BLACK;
import static Tools.Constants.ANSI_GREEN;
import static Tools.Constants.ANSI_PURPLE;
import static Tools.Constants.ANSI_RED;
import static java.util.Arrays.asList;

public class ResourceDistributor {

    private final HashMap<Name, Resource> resourcesList;
    private final HashMap<Name, ArrayList<ProcessInterface>> waitingList;
    private final ArrayList<Name> unusedList;
    private final ProcessPlaner planer;

    public ResourceDistributor(ProcessPlaner planer) {
        this.planer = planer;
        resourcesList = new HashMap<Name, Resource>(10);
        waitingList = new HashMap<Name, ArrayList<ProcessInterface>>(10);
        unusedList = new ArrayList<Name>(10);
    }

    public void addResource(Resource resource) {
        resourcesList.put(resource.getName(), resource);
        ArrayList<ProcessInterface> waitingProcess = new ArrayList<>(10);
        waitingList.put(resource.getName(), waitingProcess);
    }

    public void ask(Name resource, ProcessInterface process) {
        System.out.println(ANSI_RED + "ASK : " + process.getName()
                + ANSI_BLACK + " FOR " + ANSI_PURPLE + resource + ANSI_BLACK
                + ANSI_BLACK + " PRIORITY " + ANSI_PURPLE + process.getPriority() + ANSI_BLACK);

        if (resourcesList.get(resource).getType() == STATIC) {
            return;
        }

        boolean isFree = unusedList.stream().anyMatch(x -> x == resource);
        if (isFree) {
            unusedList.remove(resource);
            process.setPrepared(true);
        } else {
            process.setPrepared(false);
            waitingList.get(resource).add(process);
        }
    }


    public Resource get(Name resource) {
        resourcesList.get(resource).setAvailability(false);
        return resourcesList.get(resource);
    }

    public void disengage(Name resource, Object... elements) {
        System.out.println(ANSI_GREEN + "DISENGAGE : " + ANSI_BLACK + resource);
        resourcesList.get(resource).getElements().push(new ArrayList<>(asList(elements)));
        resourcesList.get(resource).setAvailability(true);
        distribute(resource);
    }

    private void distribute(Name resource) {
        System.out.println("DISTRIBUTE : " + resource);
        if (waitingList.get(resource).size() == 0) {
            unusedList.add(resource);
        }
        waitingList.get(resource).stream().forEach(x -> {
            x.setPrepared(true);
        });
        waitingList.get(resource).clear();
    }
}
