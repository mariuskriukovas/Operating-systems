package Processes;

import Processes.ProcessEnum.State;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static Processes.ProcessEnum.Name.READ_FROM_INTERFACE;
import static java.util.Comparator.comparing;

public class ProcessPlaner {

    public static Integer looop = 1;

    private final ArrayList<ProcessInterface> processList;
    private Integer nextStep = 0;

    public ProcessPlaner() {
        processList = new ArrayList<ProcessInterface>(10);
    }

    public ProcessInterface findActive() {
        return processList.stream().filter(ProcessInterface::isActive)
                .findFirst()
                .get();
    }

    public List<ProcessInterface> findPrepared() {
        List<ProcessInterface> preparedList = processList.stream()
                .filter(ProcessInterface::isPrepared)
                .collect(Collectors.toList());
        return preparedList.stream()
                .sorted(comparing(ProcessInterface::getPriority))
                .collect(Collectors.toList());
    }

    public void runOperatingSystem() {
        for (int i = 0; i < 100000; i++) {
            //System.out.println(ANSI_RED + "Iteracija ---------------> " + i + ANSI_BLACK);
            plan();
        }
    }

    public void plan() {
        System.out.println("PLAN : ");
        ProcessInterface active = findActive();
        System.out.println("ACTIVE : " + active.getName());

        List<ProcessInterface> prepared = findPrepared();
        //System.out.println("ar yra pasiruosiu " + ! prepared.isEmpty());
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
            ProcessInterface readFromInterface = processList.stream().filter(x -> x.getName()
                    .equals(READ_FROM_INTERFACE.name()))
                    .findFirst()
                    .get();
            System.out.println(readFromInterface.getName());
            readFromInterface.activate();
        }
    }

    private List<ProcessInterface> getStateList(State state) {
        List<ProcessInterface> preparedList = processList.stream()
                .filter(x -> x.getState() == state)
                .collect(Collectors.toList());
        return preparedList.stream()
                .sorted(comparing(ProcessInterface::getPriority))
                .collect(Collectors.toList());
    }

    public ArrayList<ProcessInterface> getProcessList() {
        return processList;
    }
}
