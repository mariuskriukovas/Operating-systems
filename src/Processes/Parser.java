package Processes;

import Components.SupervisorMemory;
import Resources.Resource;
import Resources.ResourceDistributor;
import Resources.ResourceEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import static Processes.ProcessEnum.Name.PARSER;
import static Processes.ProcessEnum.PARSER_PRIORITY;
import static Processes.ProcessEnum.State.BLOCKED;
import static Resources.ResourceEnum.Name.SUPERVISOR_MEMORY;
import static Resources.ResourceEnum.Name.TASK_COMPLETED;
import static Resources.ResourceEnum.Name.TASK_IN_SUPERVISOR_MEMORY;
import static Resources.ResourceEnum.Name.TASK_PARAMETERS_IN_SUPERVISOR_MEMORY;
import static Resources.ResourceEnum.Type.*;

public class Parser extends ProcessInterface {

    private final int COMMAND_LENGTH = 6;

    private final ArrayList<Command> dataSegment;
    private final ArrayList<Command> codeSegment;

    public Parser(ProcessInterface father, ProcessPlaner planner, ResourceDistributor distributor) {

        super(father, BLOCKED, PARSER_PRIORITY, PARSER, planner, distributor);

        new Resource(this, TASK_PARAMETERS_IN_SUPERVISOR_MEMORY, DYNAMIC);

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
            dataSegment.add(new Command(i - 1, parsed));
        }
        int address = 0;
        for (int i = codeSegmentIndex + 1; i < fileContent.size(); i++) {
            String parsed = checkCommand(fileContent.get(i));
            codeSegment.add(new Command(address, parsed));
            address++;
        }
    }

    public ArrayList<Command> getCodeSegment() {
        return codeSegment;
    }

    public ArrayList<Command> getDataSegment() {
        return dataSegment;
    }

    @Override
    public void executeTask() {
        super.executeTask();

        switch (IC) {
            case 0:
                IC++;
                resourceDistributor.ask(TASK_IN_SUPERVISOR_MEMORY, this);
                break;
            case 1:
                IC++;
                SupervisorMemory supervisorMemory = (SupervisorMemory) resourceDistributor.get(SUPERVISOR_MEMORY);
                String fileName = supervisorMemory.getFileList().getFirst();
                parseFile(fileName);
                if (dataSegment.size() > 0) {
                    supervisorMemory.getDataSegs().put(fileName, dataSegment);
                    if (codeSegment.size() > 0) {
                        IC = 0;
                        supervisorMemory.getCodeSegs().put(fileName, codeSegment);
                        resourceDistributor.disengage(TASK_PARAMETERS_IN_SUPERVISOR_MEMORY, fileName);
                    } else {
                        IC = 0;
                        resourceDistributor.disengage(TASK_COMPLETED, " Nekorektiškas užduoties failas");
                    }
                } else {
                    IC = 0;
                    resourceDistributor.disengage(TASK_COMPLETED, " Nekorektiškas užduoties failas");
                }
                break;
        }
    }

    public class Command {
        int position;
        String value;

        Command(int pos, String val) {
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
}