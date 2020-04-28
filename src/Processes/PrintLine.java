package Processes;

import Components.CPU;
import RealMachine.RealMachine;
import Tools.Constants;
import Tools.Exceptions;
import Tools.Word;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class PrintLine {

    public static final int MULTIPLE = 1;
    private final RealMachine realMachine;
    private final CPU cpu;
    private final JTextArea inputScreen;
    private final JTextArea outputScreen;
    private final JButton button;
    private final Boolean waitingForInput = true;

    public PrintLine(RealMachine realMachine){
        this.realMachine = realMachine;
        this.cpu = realMachine.getCpu();
        inputScreen = cpu.getRMScreen().getConsole();
        outputScreen = cpu.getRMScreen().getScreen();
        button = cpu.getRMScreen().getInputKey();
        button.addActionListener(InsertAction);
    }


    public void print(Word address) {
        String command  = "PRINT";
        try {
            for (int i = 0; i<16; i++){
                String value =  cpu.getDS(new Word(address.getNumber()+i)).toString();
                outputScreen.append(command + " ----------------- > " + value +'\n');
            }
        } catch (Exceptions.WrongAddressException e) {
            e.printStackTrace();
            Constants.PROGRAM_INTERRUPTION interruption =  e.getReason();
            cpu.setPI(interruption);
        }
    }


    public void printRegisters() {
        outputScreen.append("RL ---> " +  cpu.getRL().toString() +'\n');
        outputScreen.append("RH ---> " +  cpu.getRH().toString() +'\n');
        outputScreen.append("C --->  " + cpu.getC().toString() +'\n');
    }


    public void printHalt(String name) {
        outputScreen.append("HALT ---> " +  name +'\n');
    }

    public void read(Word address) {
        realMachine.getScreen().getTabbs().setSelectedIndex(0);
        outputScreen.append("READ ---> "+MULTIPLE+ " SYMBOLS " + '\n');
        button.setVisible(true);
        synchronized (waitingForInput){
            try {
                waitingForInput.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int destinationAddress = (int)address.getNumber();
        writeToDataSegment(inputLines, destinationAddress);
    }

    private List<String> inputLines;

    private ActionListener InsertAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            List<String> lines = Arrays.asList(inputScreen.getText().split("\\n"));
            if (validation(lines)) {
                System.out.println(lines);
                try {
                    inputLines = lines;
                    button.setVisible(false);
                    synchronized (waitingForInput){
                        waitingForInput.notifyAll();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    };

    private static final String NO_INPUT = "NO INPUT";
    private static final String NOT_MULTIPLE_OF_16 = "NOT MULTIPLE OF 16";
    private static final String WORD_LENGTH_MORE_THAN_6_OR_LESS_THAN_1 = "WORD LENGTH MORE THAN 6 OR LESS THAN 1";
    private static final String ALL_GOOD = "ALL GOOD";

    private boolean validation(List<String> input) {
        String command  = "READ";
        if (input.size() == 0) { // no input
            System.out.println(NO_INPUT);
            outputScreen.append(command + " ----------------- > " + NO_INPUT +'\n');
            return false;
        } else if (input.size() % MULTIPLE != 0) { // not multiple of 16
            System.out.println(NOT_MULTIPLE_OF_16);
            outputScreen.append(command + " ----------------- > " + NOT_MULTIPLE_OF_16 +'\n');
            return false;
        } else if (checkWordLength(input)) { // word length more than 6
            System.out.println(WORD_LENGTH_MORE_THAN_6_OR_LESS_THAN_1);
            outputScreen.append(command + " ----------------- > " + WORD_LENGTH_MORE_THAN_6_OR_LESS_THAN_1 +'\n');
            return false;
        } else {
            System.out.println(ALL_GOOD);
            outputScreen.append(command + " ----------------- > " + ALL_GOOD +'\n');
            return true;
        }
    }

    private boolean checkWordLength(List<String> input) {
        return input.stream()
                .filter(a -> a.length() > 6 || a.isEmpty())
                .collect(toList())
                .size() != 0;
    }

    private void writeToDataSegment(List<String> lines, long address) {

        try {
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.length() != 6) {
                    while (line.length() != 6) {
                        line = line + " ";
                    }
                }
                System.out.println("line " + line);
                int value = Integer.parseInt(line.replaceAll("\\s+",""),16);
                cpu.setDS(new Word(address), new Word(value));
            }
        } catch (Exceptions.WrongAddressException e) {
            e.printStackTrace();
            Constants.PROGRAM_INTERRUPTION interruption =  e.getReason();
            cpu.setPI(interruption);
        }
        System.out.println("Finish writing");
    }
}
