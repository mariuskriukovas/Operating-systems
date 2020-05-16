package Processes;

import Components.Memory;
import Resources.Resource;
import Resources.ResourceDistributor;
import Resources.ResourceEnum;
import Tools.SupervisorMemory;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import static Processes.ProcessEnum.Name.PARSER;
import static Resources.ResourceEnum.Name.*;
import static Tools.Constants.*;
import static Tools.Constants.ANSI_RESET;

public class Parser extends ProcessInterface {

    private final int COMMAND_LENGTH = 6;

    private final ArrayList<Command> dataSegment;
    private final ArrayList<Command> codeSegment;

    public Parser(ProcessInterface father, ProcessPlaner processPlaner,  ResourceDistributor resourceDistributor){

        super(father, ProcessEnum.State.BLOCKED,  ProcessEnum.PARSER_PRIORITY, PARSER,processPlaner, resourceDistributor);


        new Resource(this, ResourceEnum.Name.TASK_PARAMETERS_IN_SUPERVISOR_MEMORY, ResourceEnum.Type.DYNAMIC);


        dataSegment = new ArrayList<Command>(100);
        codeSegment = new ArrayList<Command>(100);

    }

    public void parseFile(String fileLocation) {
        dataSegment.clear();
        codeSegment.clear();
        try {
            File file = new File(fileLocation);
            Scanner scanner = new Scanner(file);
            parse(scanner);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String checkCommand(String command) {
        if (command.length() < COMMAND_LENGTH) {
            for (int i = command.length(); i < COMMAND_LENGTH; i++) {
                command += "0";
            }
        }
        return command;
    }

    private void parse(Scanner scanner) {
        ArrayList<String> fileContent = new ArrayList<String>(100);
        while (scanner.hasNextLine()) {
            fileContent.add(scanner.nextLine());
        }
        int codeSegmentIndex = fileContent.indexOf("CODSEG");
        int dataSegmentIndex = fileContent.indexOf("DATSEG");

        for (int i = dataSegmentIndex + 1; i < codeSegmentIndex; i++) {
            String parsed = checkCommand(fileContent.get(i));
            dataSegment.add(new Command(i-1, parsed));
//            System.out.println(i-1+" : "+parsed);
        }

        int address = 0;
        for (int i = codeSegmentIndex + 1; i < fileContent.size(); i++) {
            String parsed = checkCommand(fileContent.get(i));
//            System.out.println(address+" : "+parsed);
            codeSegment.add(new Command(address, parsed));
            address++;
        }

    }

    public class Command
    {
        int position;
        String value;
        Command(int pos, String val)
        {
            position = pos;
            value = val;
        }

        public int getPosition() {
            return position;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return position + " : " + value;
        }
    }

    public ArrayList<Command> getCodeSegment() { return codeSegment; }
    public ArrayList<Command> getDataSegment() {
        return dataSegment;
    }

    private int IC = 0;

    @Override
    public void executeTask() {
        super.executeTask();

        switch (IC)
        {
            case 0:
                IC++;
                //Blokavimasis laukiant “Užduotis supervizorinėje atmintyje” resurso
                resourceDistributor.ask(TASK_IN_SUPERVISOR_MEMORY,this);
                break;
            case 1:
                IC++;
                //Nuskaitomas resurso pranesime nurodytas failas.
                SupervisorMemory supervisorMemory = (SupervisorMemory) resourceDistributor.get(SUPERVISOR_MEMORY);
                Memory externalMemory = (Memory) resourceDistributor.get(EXTERNAL_MEMORY);
                try {
                    System.out.println(ANSI_BLUE + " ---------------> " + supervisorMemory.getFileList().get(0) + ANSI_RESET);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Ar failas turi antrastę DATSEG ?
                boolean isDataSegGood = true;

                if(isDataSegGood) {
                    //taip
                    //Supervizorinėje atmintyje išsaugomas užduoties duomenų segentas.

                    boolean isCodeSegGood = true;
                    if(isCodeSegGood)
                    {
                        IC = 0;
                        //Supervizorinėje atmintyje išsaugomas užduoties kodo segentas.

                        //Atlaisvinamas “Užduoties vykdymo parametrai supervizorinėje atmintyje” resursas.
                        resourceDistributor.disengage(ResourceEnum.Name.TASK_PARAMETERS_IN_SUPERVISOR_MEMORY);
                    }
                    else {
                        IC = 0;
                        //Atlaisvinamas  "Užduotis įvykdyta"  resursas su pranešimu " Nekorektiškas užduoties failas"
                        resourceDistributor.disengage(ResourceEnum.Name.TASK_COMPLETED,  " Nekorektiškas užduoties failas");
                    }
                }else {
                    IC = 0;
                    //Atlaisvinamas  "Užduotis įvykdyta"  resursas su pranešimu " Nekorektiškas užduoties failas"
                    resourceDistributor.disengage(ResourceEnum.Name.TASK_COMPLETED,  " Nekorektiškas užduoties failas");
                }
                break;
        }
    }
}

// switch (IC)
//        {
//            case 0:
//                IC++;
//
//                break;
//            case 1:
//                IC++;
//
//
//                break;
//            case 2:
//
//                break;
//            case 3:
//
//
//                break;
//        }