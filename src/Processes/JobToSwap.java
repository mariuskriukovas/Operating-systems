package Processes;

import Components.Memory;
import RealMachine.RealMachine;
import Resources.ResourceDistributor;
import Resources.ResourceEnum;
import Tools.Constants;
import Tools.Word;

import java.util.ArrayList;

import static Processes.ProcessEnum.Name.JOB_TO_SWAP;
import static Resources.ResourceEnum.Name.TASK_PARAMETERS_IN_SUPERVISOR_MEMORY;

public class JobToSwap extends ProcessInterface{
    //JobToSwap – užduoties patalpinimas išorinėje atmintyje

    private final RealMachine realMachine;

    public JobToSwap(RealMachine father, ProcessPlaner processPlaner, ResourceDistributor resourceDistributor)
    {
        super(father, ProcessEnum.State.BLOCKED, ProcessEnum.JOB_TO_SWAP_PRIORITY, JOB_TO_SWAP,processPlaner, resourceDistributor);
        this.realMachine = father;

    }

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
                boolean doInternalMemoryHasSpace = true;

                if(doInternalMemoryHasSpace) {
                    IC = 2;
                    //Išorinėje atmintyje įrašomi užduoties blokai.
                    //Sugeneruojamas unikalus uždoties ID.
                    //Atlaisvinamas “Užduotis būgne” resursas  su pranešimu " ID, Paruošta vykdyti"
                    resourceDistributor.disengage(ResourceEnum.Name.TASK_IN_DRUM,  "1234567890");
                }else {
                    IC = 0;
                    // Atlaisvinamas “"Užduotis įvykdyta” resursas su pranešimu " Truksta išorinės atminties"
                    resourceDistributor.disengage(ResourceEnum.Name.TASK_COMPLETED,  "Truksta isorines atminties");
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

    public void uploadTaskToExternalMemory(String task,  int externalMemoryBegin)
    {
        realMachine.getParser().parseFile(task);
        int dataSegBegin = Constants.DATA_SEGMENT + (externalMemoryBegin*256);
        int codeSegBegin = Constants.CODE_SEGMENT + (externalMemoryBegin*256);

        //System.out.println("dataSegBegin-----> "+dataSegBegin/256);
        //System.out.println("codeSegBegin-----> "+codeSegBegin/256);

        ArrayList<Parser.Command> codeSegment = realMachine.getParser().getCodeSegment();
        ArrayList<Parser.Command> dataSegment = realMachine.getParser().getDataSegment();

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
    }

}
