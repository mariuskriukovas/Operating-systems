package Processes;

import Components.Memory;
import Resources.Resource;
import Resources.ResourceDistributor;
import Resources.ResourceEnum;
import Tools.Constants;
import Components.SupervisorMemory;
import Tools.Word;

import java.util.ArrayList;

import static Processes.MainProc.State.TASK_CREATED;
import static Processes.ProcessEnum.Name.*;
import static Processes.ProcessEnum.VIRTUAL_MACHINE_PRIORITY;
import static Resources.ResourceEnum.Name.*;
import static Tools.Constants.ANSI_BLUE;
import static Tools.Constants.ANSI_RESET;

public class JobToSwap extends ProcessInterface{
    //JobToSwap – užduoties patalpinimas išorinėje atmintyje

    private final RealMachine realMachine;

    public JobToSwap(RealMachine father, ProcessPlaner processPlaner, ResourceDistributor resourceDistributor)
    {
        super(father, ProcessEnum.State.BLOCKED, ProcessEnum.JOB_TO_SWAP_PRIORITY, JOB_TO_SWAP,processPlaner, resourceDistributor);
        this.realMachine = father;
    }

    private static int TASKID = VIRTUAL_MACHINE_PRIORITY;
    private int IC = 0;

    @Override
    public void executeTask() {
        super.executeTask();

        switch (IC)
        {
            case 0:
                IC++;
                //Blokavimasis laukiant  “Užduoties vykdymo parametrai supervizorinėje atmintyje”  resurso
                resourceDistributor.ask(TASK_PARAMETERS_IN_SUPERVISOR_MEMORY,this);
                break;
            case 1:
                IC++;
                Resource task = resourceDistributor.get(TASK_PARAMETERS_IN_SUPERVISOR_MEMORY);
                SupervisorMemory supervisorMemory = (SupervisorMemory) resourceDistributor.get(SUPERVISOR_MEMORY);
                Memory external = (Memory) resourceDistributor.get(EXTERNAL_MEMORY);
                String fileName = (String)task.get(0);

                if(external.hasFreeSpace()) {
                    IC = 2;
                    //Išorinėje atmintyje įrašomi užduoties blokai.
                    //Sugeneruojamas unikalus uždoties ID.
                    //Atlaisvinamas “Užduotis būgne” resursas  su pranešimu " ID, Paruošta vykdyti"

                    int externalMemoryBegin = external.getFreeSpaceBeginAddress();
                    //load those commands to external memory
                    uploadTaskToExternalMemory(fileName, externalMemoryBegin, supervisorMemory);
                    resourceDistributor.disengage(ResourceEnum.Name.TASK_IN_DRUM,TASK_CREATED, fileName, TASKID++, externalMemoryBegin);
                }else {
                    IC = 0;
                    // Atlaisvinamas “"Užduotis įvykdyta” resursas su pranešimu " Truksta išorinės atminties"
                    resourceDistributor.disengage(ResourceEnum.Name.TASK_COMPLETED,  "Truksta atminties");
                }
                break;
            case 2:
                IC = 0;
                //Atlaisvinamas “"Užduotis įvykdyta” resursas su pranešimu " Užduotis užkrauta sėkmingai"
                resourceDistributor.disengage(ResourceEnum.Name.TASK_COMPLETED,  "Užduotis užkrauta sėkmingai");
                break;
        }

    }


    //    STACK_SEGMENT = 0;
    //    DATA_SEGMENT = 21760; 5500
    //    CODE_SEGMENT = 43520; AA00

    public void uploadTaskToExternalMemory(String task,  int externalMemoryBegin, SupervisorMemory supervisorMemory)
    {

        int dataSegBegin = Constants.DATA_SEGMENT + (externalMemoryBegin*256);
        int codeSegBegin = Constants.CODE_SEGMENT + (externalMemoryBegin*256);

        System.out.println(ANSI_BLUE + "externalMemoryBegin  ---------------> " + externalMemoryBegin + ANSI_RESET);
        System.out.println(ANSI_BLUE + "dataSegBegin  ---------------> " + dataSegBegin/256 + ANSI_RESET);
        System.out.println(ANSI_BLUE + "codeSegBegin  ---------------> " + codeSegBegin/256 + ANSI_RESET);


        ArrayList<Parser.Command> codeSegment = supervisorMemory.getCodeSegs().get(task);
        ArrayList<Parser.Command> dataSegment = supervisorMemory.getDataSegs().get(task);

        Memory externalMemory = realMachine.getExternalMemory();

        for(Parser.Command c : codeSegment)
        {
            int adr = c.getPosition() + codeSegBegin;
            try {
//                System.out.println(new Word(adr).getHEXFormat());
//                cpu.writeToExternalMemory(new Word(adr), new Word(c.getValue(), Word.WORD_TYPE.SYMBOLIC));
                externalMemory.setWord(new Word(c.getValue(), Word.WORD_TYPE.SYMBOLIC), adr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for(Parser.Command c : dataSegment)
        {
            int adr = c.getPosition() + dataSegBegin;
            try {
//                System.out.println(new Word(adr).getHEXFormat());
//                cpu.writeToExternalMemory(new Word(adr), new Word(c.getValue(), Word.WORD_TYPE.NUMERIC));
                externalMemory.setWord(new Word(c.getValue(), Word.WORD_TYPE.NUMERIC), adr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        supervisorMemory.getCodeSegs().remove(task);
        supervisorMemory.getDataSegs().remove(task);

    }

}
