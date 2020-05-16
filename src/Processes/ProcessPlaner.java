package Processes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static Processes.ProcessEnum.Name.READ_FROM_INTERFACE;

public class ProcessPlaner {

    public static Integer looop = 1;

    private final ArrayList<ProcessInterface> processList;

    public ProcessPlaner(){
        processList = new ArrayList<ProcessInterface>(10);
    }

    public ProcessInterface findActive(){
        return processList.stream().filter(x->x.isActive())
                .findFirst()
                .get();
    }

    public List<ProcessInterface> findPrepared(){
        List<ProcessInterface> preparedList = processList.stream()
                .filter(x -> x.isPrepared())
                .collect(Collectors.toList());
        return preparedList.stream()
                .sorted(Comparator.comparing(ProcessInterface::getPriority))
                .collect(Collectors.toList());
    }

    private Integer nextStep = 0;


    public void runOperatingSystem(){
        for(int i = 0; i<10; i++)
        {
            plan();
        }
    }




    public void plan(){
        System.out.println("PLAN : ");
        ProcessInterface active = findActive();
        System.out.println("ACTIVE : " + active.getName());

        List<ProcessInterface> prepared = findPrepared();
        System.out.println("ar yra pasiruosiu " + !prepared.isEmpty());
        if (prepared.size() > 0) {
            ProcessInterface firstPrepared = prepared.get(0);
            active.setActive(false);
            firstPrepared.setActive(true);
            System.out.println("FIRST PREPARED : " + firstPrepared.getName());
            firstPrepared.activate();
        } else {
            synchronized (looop) {
                try {
                    looop.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.err.println("isejo is Ypatinga situacija ");
            ProcessInterface readFromInterface = processList.stream().filter(x->x.getName()
                    .equals(READ_FROM_INTERFACE.name()))
                    .findFirst()
                    .get();
            System.out.println(readFromInterface.getName());
            readFromInterface.activate();
        }
    }

    private List<ProcessInterface> getStateList(ProcessEnum.State state){
        List<ProcessInterface> preparedList = processList.stream()
                .filter(x -> x.getState() == state)
                .collect(Collectors.toList());
        return preparedList.stream()
                .sorted(Comparator.comparing(ProcessInterface::getPriority))
                .collect(Collectors.toList());
    }

    public ArrayList<ProcessInterface> getProcessList() {
        return processList;
    }
}
