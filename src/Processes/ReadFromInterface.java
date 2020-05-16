package Processes;

import Components.CPU;
import RealMachine.RealMachine;
import Resources.Resource;
import Resources.ResourceDistributor;
import Resources.ResourceEnum;
import Tools.SupervisorMemory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import static Processes.ProcessEnum.Name.READ_FROM_INTERFACE;
import static Processes.ProcessEnum.READ_FROM_INTERFACE_PRIORITY;
import static Resources.ResourceEnum.Name.TASK_COMPLETED;
import static Tools.Constants.*;

public class ReadFromInterface extends ProcessInterface{

    private final RealMachine realMachine;
    private final CPU cpu;
    private final JTextArea inputScreen;
    private final JTextArea outputScreen;
    private final JButton button;

    enum Situation{
        CREATEVM,
        RUNALL,
        TICKMODEON,
        TICKMODEOFF,
        TEST,
        OSEND,
    }

    public ReadFromInterface(RealMachine father, ProcessPlaner processPlaner, ResourceDistributor resourceDistributor)
    {
        super(father, ProcessEnum.State.PREPARED, READ_FROM_INTERFACE_PRIORITY,READ_FROM_INTERFACE ,processPlaner, resourceDistributor);
        new Resource(this, ResourceEnum.Name.USER_INPUT, ResourceEnum.Type.DYNAMIC);
        new Resource(this, ResourceEnum.Name.TASK_IN_SUPERVISOR_MEMORY, ResourceEnum.Type.DYNAMIC);
        new Resource(this, TASK_COMPLETED, ResourceEnum.Type.DYNAMIC);


        new Resource(this, ResourceEnum.Name.UPLOAD_VIRTUAL_MACHINE, ResourceEnum.Type.DYNAMIC);

        // old
        this.realMachine = father;
        this.cpu = realMachine.getCpu();
        inputScreen = cpu.getRMScreen().getConsole();
        outputScreen = cpu.getRMScreen().getScreen();
        button = cpu.getRMScreen().getENTRYButton();
        button.addActionListener(UserInput);
        //
    }


//    new Thread(() -> testInteractions()).start();
//            new Thread(() -> interactions()).start();

    static int teest = 0;

    //klaidos trinti kas karta atleidziant pransima jo turini

    private ActionListener UserInput = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(teest==0)
            {
                resourceDistributor.specialDarkMagic(ResourceEnum.Name.USER_INPUT, Situation.CREATEVM);
            }else {
                resourceDistributor.specialDarkMagic(ResourceEnum.Name.USER_INPUT, Situation.RUNALL);
            }
            synchronized (ProcessPlaner.looop)
            {
                ProcessPlaner.looop.notifyAll();
            }

        }
    };


    private void readUserInput()
    {
        List<String> lines = Arrays.asList(inputScreen.getText().split("\\s+"));
        String command = lines.get(0);
//        button.setEnabled(false);

        if(command.equalsIgnoreCase("CREATEVM")) {
            String filename = lines.get(1).replace("\"", "");
            outputScreen.append(command + " ----------------- > " + filename+'\n');
            resourceDistributor.disengage(ResourceEnum.Name.USER_INPUT, Situation.CREATEVM, inputScreen.getText());
        }else if(command.equalsIgnoreCase("RUNALL")) {
            outputScreen.append(command + " ----------------- > ");
            resourceDistributor.disengage(ResourceEnum.Name.USER_INPUT, Situation.RUNALL);
        } else if(command.equalsIgnoreCase("TICKMODE")) {
            String action = lines.get(1);
            if(action.equalsIgnoreCase("ON")) {
                outputScreen.append(command + " ----------------- > " + action+'\n');
                resourceDistributor.disengage(ResourceEnum.Name.USER_INPUT, Situation.TICKMODEON);
            }else if(action.equalsIgnoreCase("OFF")) {
                outputScreen.append(command + " ----------------- > " + action+'\n');
                resourceDistributor.disengage(ResourceEnum.Name.USER_INPUT, Situation.TICKMODEOFF);
            }
        } else if(command.equalsIgnoreCase("TEST")) {
                outputScreen.append(command + " ----------------- > " + command+'\n');
                resourceDistributor.disengage(ResourceEnum.Name.USER_INPUT, Situation.TEST);
        } else if(command.equalsIgnoreCase("OSEND")) {
                outputScreen.append(command + " ----------------- > " + command+'\n');
                resourceDistributor.disengage(ResourceEnum.Name.USER_INPUT, Situation.OSEND);
        } else {
            outputScreen.append("Sorry can not understand you :( "+'\n');
        }
        button.setEnabled(true);
    }

    int IC = 0;
    private Resource userInput;
    String message;

    @Override
    public void executeTask() {
        super.executeTask();

        switch (IC)
        {
            case 0:
                IC++;
                //Blokavimasis laukiant “Iš vartotojo sąsajos” resurso (1)
                System.out.println(ANSI_RED + " --------------->1"+ ANSI_BLACK);
                resourceDistributor.ask(ResourceEnum.Name.USER_INPUT, this);
                break;
            case 1:
                IC++;
                userInput = resourceDistributor.get(ResourceEnum.Name.USER_INPUT);
                //Uzduoties nuskaitymas
                Situation situation = (Situation)userInput.getElementList().get(0);
                //Ar tai uzduoties sukurimas
                switch (situation)
                {
                    case RUNALL:
                        //Atlaisvinamas resursas “Pradėti vykdymą”. Skirtas procesui MainProc
                        System.out.println(ANSI_BLUE + " --------------->2"+ ANSI_BLACK);
                        resourceDistributor.disengage(ResourceEnum.Name.START_EXECUTION);

                        break;
                    case CREATEVM:
                        //Blokavimasis laukiant supervizorinės atminties resurso
                        System.out.println(ANSI_RED + " --------------->2"+ ANSI_BLACK);
                        //Blokavimasis laukiant supervizorinės atminties resurso
                        SupervisorMemory supervisorMemory = (SupervisorMemory)resourceDistributor.get(ResourceEnum.Name.SUPERVISOR_MEMORY);
                        //Failo pavadinimo nuskaitymas
                        supervisorMemory.getFileList().add("prog3.txt");
                        //Atlaisvinamas resursas “Užduotis supervizorinėje atmintyje”. Skirtas procesui Parser
                        resourceDistributor.disengage(ResourceEnum.Name.TASK_IN_SUPERVISOR_MEMORY);
                        break;
                    case TICKMODEON:
                        break;
                    case TICKMODEOFF:
                        break;
                    case OSEND:
                        break;
                }
                break;
            case 2:
                IC++;
                //Blokavimasis laukiant  "Užduotis įvykdyta" resurso
                resourceDistributor.ask(ResourceEnum.Name.TASK_COMPLETED,this);
                break;
            case 3:
                IC=0;
                message = (String)resourceDistributor.get(ResourceEnum.Name.TASK_COMPLETED).getElementList().toString();
                System.out.println(ANSI_RED + "TURIATEITIIKICIA --------------->" + message + teest + ANSI_BLACK);
                teest++;
                break;
        }


//        if(command.equalsIgnoreCase("CREATEVM")) {
//            String filename = lines.get(1).replace("\"", "");
//            outputScreen.append(command + " ----------------- > " + filename+'\n');
//            System.out.println(filename);
//            Constants.PROCESS_STATUS status = realMachine.getJobGorvernor().createVirtualMachine(filename);
//            outputScreen.append(command + " ----------------- > "+status+'\n');
//        }else if(command.equalsIgnoreCase("RUNALL")) {
//            Constants.PROCESS_STATUS status = realMachine.getJobGorvernor().runAll();
//            outputScreen.append(command + " ----------------- > "+status);
//        } else if(command.equalsIgnoreCase("TICKMODE")) {
//            String action = lines.get(1);
//            if(action.equalsIgnoreCase("ON")) {
//                outputScreen.append(command + " ----------------- > " + action+'\n');
//                OSFrame.TickMode = true;
//            }else if(action.equalsIgnoreCase("OFF")) {
//                outputScreen.append(command + " ----------------- > " + action+'\n');
//                OSFrame.TickMode = false;
//            }


        System.out.println(ANSI_PURPLE + "iseejo is rekursijos"+ANSI_BLACK);
    }
}



//    ////            new Thread(() -> testInteractions()).start();
//    void testInteractions(){
//////        CREATEVM "prog3.txt"
//        for (int i = 0;i<100; i++) realMachine.getJobGorvernor().createVirtualMachine( "prog3.txt");
//        realMachine.getJobGorvernor().runAll();
//////        TickMode = true;
////////        RUNALL
////        cpu.getJobGorvernor().runAll();
//////        cpu.getPrintLine().read();
//    }