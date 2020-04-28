package Processes;

import Components.CPU;
import Components.UI.OSFrame;
import RealMachine.RealMachine;
import Tools.Constants;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

public class MainProc {
    private final RealMachine realMachine;
    private final CPU cpu;
    private final JTextArea inputScreen;
    private final JTextArea outputScreen;
    private final JButton button;

    public MainProc(RealMachine realMachine)
    {
        this.realMachine = realMachine;
        this.cpu = realMachine.getCpu();
        inputScreen = cpu.getRMScreen().getConsole();
        outputScreen = cpu.getRMScreen().getScreen();
        button = cpu.getRMScreen().getENTRYButton();
        button.addActionListener(InteractionMode);
    }

    private ActionListener InteractionMode = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
//            new Thread(() -> testInteractions()).start();
            new Thread(() -> interactions()).start();
        }
    };

    void interactions()
    {
        List<String> lines = Arrays.asList(inputScreen.getText().split("\\s+"));
        String command = lines.get(0);
        button.setEnabled(false);

        if(command.equalsIgnoreCase("CREATEVM")) {
            String filename = lines.get(1).replace("\"", "");
            outputScreen.append(command + " ----------------- > " + filename+'\n');
            System.out.println(filename);
            Constants.PROCESS_STATUS status = realMachine.getJobGorvernor().createVirtualMachine(filename);
            outputScreen.append(command + " ----------------- > "+status+'\n');
        }else if(command.equalsIgnoreCase("RUNALL")) {
            Constants.PROCESS_STATUS status = realMachine.getJobGorvernor().runAll();
            outputScreen.append(command + " ----------------- > "+status);
        } else if(command.equalsIgnoreCase("TICKMODE")) {
            String action = lines.get(1);
            if(action.equalsIgnoreCase("ON")) {
                outputScreen.append(command + " ----------------- > " + action+'\n');
                OSFrame.TickMode = true;
            }else if(action.equalsIgnoreCase("OFF")) {
                outputScreen.append(command + " ----------------- > " + action+'\n');
                OSFrame.TickMode = false;
            }
        }else {
            outputScreen.append("Sorry can not understand you :( "+'\n');
        }
        button.setEnabled(true);
    }



////            new Thread(() -> testInteractions()).start();
    void testInteractions(){
////        CREATEVM "prog3.txt"
        for (int i = 0;i<1; i++) realMachine.getJobGorvernor().createVirtualMachine( "prog3.txt");
        realMachine.getJobGorvernor().runAll();
////        TickMode = true;
//////        RUNALL
//        cpu.getJobGorvernor().runAll();
////        cpu.getPrintLine().read();
    }

}
