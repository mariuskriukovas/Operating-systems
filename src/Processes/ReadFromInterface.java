package Processes;

import Components.SupervisorMemory;
import Components.UI.OSFrame;
import Resources.Resource;
import Resources.ResourceDistributor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import static Processes.ProcessEnum.Name.READ_FROM_INTERFACE;
import static Processes.ProcessEnum.READ_FROM_INTERFACE_PRIORITY;
import static Processes.ProcessEnum.State.PREPARED;
import static Processes.ProcessPlaner.looop;
import static Processes.ReadFromInterface.Situation.CREATEVM;
import static Processes.ReadFromInterface.Situation.OSEND;
import static Processes.ReadFromInterface.Situation.RUNALL;
import static Resources.ResourceEnum.Name.OS_END;
import static Resources.ResourceEnum.Name.START_EXECUTION;
import static Resources.ResourceEnum.Name.SUPERVISOR_MEMORY;
import static Resources.ResourceEnum.Name.TASK_COMPLETED;
import static Resources.ResourceEnum.Name.TASK_IN_SUPERVISOR_MEMORY;
import static Resources.ResourceEnum.Name.UPLOAD_VIRTUAL_MACHINE;
import static Resources.ResourceEnum.Name.USER_INPUT;
import static Resources.ResourceEnum.Type.DYNAMIC;
import static Tools.Constants.ANSI_BLACK;
import static Tools.Constants.ANSI_RED;

public class ReadFromInterface extends ProcessInterface {

    static int teest = 0;
    private final RealMachine realMachine;
    private final JTextArea inputScreen;
    private final JTextArea outputScreen;
    private final JButton button;

    String message;

    private ActionListener UserInput = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            readUserInput();
        }
    };
    private Resource userInput;

    public ReadFromInterface(
            RealMachine father,
            ProcessPlaner planner,
            ResourceDistributor distributor) {

        super(father, PREPARED, READ_FROM_INTERFACE_PRIORITY, READ_FROM_INTERFACE, planner, distributor);
        new Resource(this, USER_INPUT, DYNAMIC);
        new Resource(this, TASK_IN_SUPERVISOR_MEMORY, DYNAMIC);
        new Resource(this, TASK_COMPLETED, DYNAMIC);
        new Resource(this, UPLOAD_VIRTUAL_MACHINE, DYNAMIC);

        this.realMachine = father;
        this.inputScreen = realMachine.getScreen().getScreenForRealMachine().getConsole();
        this.outputScreen = realMachine.getScreen().getScreenForRealMachine().getScreen();
        this.button = realMachine.getScreen().getScreenForRealMachine().getENTRYButton();

        this.button.addActionListener(UserInput);
    }

    private void readUserInput() {
        List<String> lines = Arrays.asList(inputScreen.getText().split("\\s+"));
        String command = lines.get(0);
        if (command.equalsIgnoreCase("CREATEVM")) {
            String filename = lines.get(1).replace("\"", "");
            outputScreen.append(command + " ----------------- > " + filename + '\n');
            resourceDistributor.disengage(USER_INPUT, CREATEVM, inputScreen.getText());
            synchronized (looop) {
                looop.notifyAll();
            }

        } else if (command.equalsIgnoreCase("RUNALL")) {
            outputScreen.append(command + " ----------------- > " + '\n');
            outputScreen.setForeground(Color.BLACK);
            resourceDistributor.disengage(USER_INPUT, RUNALL);
            synchronized (looop) {
                looop.notifyAll();
            }

        } else if (command.equalsIgnoreCase("TICKMODE")) {
            String action = lines.get(1);
            if (action.equalsIgnoreCase("ON")) {
                outputScreen.append(command + " ----------------- > " + action + '\n');
                OSFrame.TickMode = true;
            } else if (action.equalsIgnoreCase("OFF")) {
                outputScreen.append(command + " ----------------- > " + action + '\n');
                OSFrame.TickMode = false;
            }
        } else if (command.equalsIgnoreCase("OSEND")) {
            outputScreen.append(command + " ----------------- > " + command + '\n');
            resourceDistributor.disengage(USER_INPUT, OSEND);
            synchronized (looop) {
                looop.notifyAll();
            }
        } else {
            outputScreen.append("Sorry can not understand you :( " + '\n');
        }
        button.setEnabled(true);
    }

    @Override
    public void executeTask() {
        super.executeTask();

        switch (IC) {
            case 0:
                IC++;
                System.out.println(ANSI_RED + " --------------->1" + ANSI_BLACK);
                resourceDistributor.ask(USER_INPUT, this);
                break;
            case 1:
                IC++;
                userInput = resourceDistributor.get(USER_INPUT);
                Situation situation = (Situation) userInput.getElementList().get(0);
                switch (situation) {
                    case RUNALL:
                        resourceDistributor.disengage(START_EXECUTION);
                        break;
                    case CREATEVM:
                        SupervisorMemory memory = (SupervisorMemory) resourceDistributor.get(SUPERVISOR_MEMORY);
                        String[] words = inputScreen.getText().split("\\s+");
                        memory.getFileList().push(words[1]);
                        resourceDistributor.disengage(TASK_IN_SUPERVISOR_MEMORY);
                        break;
                    case OSEND:
                        resourceDistributor.disengage(OS_END);
                        break;
                }
                break;
            case 2:
                IC++;
                resourceDistributor.ask(TASK_COMPLETED, this);
                break;
            case 3:
                IC = 0;
                message = (String) resourceDistributor.get(TASK_COMPLETED).getElementList().toString();
                outputScreen.setForeground(Color.BLUE);
                outputScreen.append(message + '\n');
                System.out.println(ANSI_RED + "TURI_ATEITI_IKI_CIA --------------->" + message + teest + ANSI_BLACK);
                teest++;
                break;
        }
    }

    enum Situation {
        CREATEVM,
        RUNALL,
        OSEND,
    }
}