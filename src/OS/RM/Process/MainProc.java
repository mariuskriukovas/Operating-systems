package OS.RM.Process;

import OS.RM.CPU;
import OS.Tools.Constants;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import static UI.OSFrame.TickMode;

public class MainProc {
    private final CPU cpu;
    private final JTextArea inputScreen;
    private final JTextArea outputScreen;
    private final JButton button;

    public MainProc(CPU cpu)
    {
        this.cpu = cpu;
        inputScreen = cpu.getRMScreen().getConsole();
        outputScreen = cpu.getRMScreen().getScreen();
        button = cpu.getRMScreen().getENTRYButton();
        button.addActionListener(InteractionMode);
    }

    private ActionListener InteractionMode = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
//            new Thread(() -> interactions()).start();
            new Thread(() -> testInteractions()).start();
        }
    };

    void testInteractions(){
        cpu.getJobGorvernor().createVirtualMachine( "prog.txt");
//        cpu.getJobGorvernor().createVirtualMachine( "prog2.txt");
//        cpu.getJobGorvernor().createVirtualMachine( "prog3.txt");
        cpu.getJobGorvernor().runAll();
//        cpu.getPrintLine().read();
    }

    //su tuo TICK negerai
    void interactions()
    {
        List<String> lines = Arrays.asList(outputScreen.getText().split("\\s+"));
        String command = lines.get(0);
        button.setEnabled(false);

        if(command.equalsIgnoreCase("CREATEVM")) {
            String filename = lines.get(1).replace("\"", "");
            outputScreen.append(command + " ----------------- > " + filename+'\n');
            System.out.println(filename);
            Constants.PROCESS_STATUS status = cpu.getJobGorvernor().createVirtualMachine(filename);
            outputScreen.append(command + " ----------------- > "+status+'\n');
        }else if(command.equalsIgnoreCase("RUN")) {
            String filename = lines.get(1).replace("\"", "");
            outputScreen.append(command + " ----------------- > " + filename+'\n');
//        Constants.PROCESS_STATUS status = cpu.getProcessForCreatingVM().run(filename);
//        screen.append(command + " ----------------- > "+status);
        } else if(command.equalsIgnoreCase("TICKMODE")) {
            String action = lines.get(1);
            if(action.equalsIgnoreCase("ON")) {
                outputScreen.append(command + " ----------------- > " + action+'\n');
                TickMode = true;
            }else if(action.equalsIgnoreCase("OFF")) {
                outputScreen.append(command + " ----------------- > " + action+'\n');
                TickMode = false;
            }
        }else {
            outputScreen.append("Sorry can not understand you :( ");
        }
        button.setEnabled(true);
    }

}
