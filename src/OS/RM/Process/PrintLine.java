package OS.RM.Process;

import OS.RM.CPU;
import OS.Tools.Constants;
import OS.Tools.Word;
import UI.RMPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class PrintLine {

    private final CPU cpu;
    private final JTextArea inputScreen;
    private final JTextArea outputScreen;
    private final JButton button;
    private final Boolean waitingForInput = true;

    public PrintLine(CPU cpu){
        this.cpu = cpu;
        inputScreen = cpu.getRMScreen().getConsole();
        outputScreen = cpu.getRMScreen().getScreen();
        button = cpu.getRMScreen().getInputKey();
        button.addActionListener(InsertAction);
    }

    //address --> RL
    public void print() {
        cpu.showProcess(Constants.PROCESS.Print);
        String command  = "PRINT";
        long address = cpu.getRL().getNumber();
        for (int i = 0; i<16; i++){
            //    Word address, ---> RL
            //    RL ---> value
            try {
                cpu.setRL(new Word(address+i));
                cpu.getSwapping().GETDS();
                String value = cpu.getRL().getHEXFormat();
                outputScreen.append(command + " ----------------- > " + value +'\n');
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cpu.showPreviousProcess();
    }


    public void printRegisters() {
        cpu.showProcess(Constants.PROCESS.Print);
        String command  = "PRINT ";
        outputScreen.append("RL ---> " +  cpu.getRL().toString() +'\n');
        outputScreen.append("RH ---> " +  cpu.getRH().toString() +'\n');
        outputScreen.append("C --->  " + cpu.getC().toString() +'\n');
        cpu.showPreviousProcess();
    }


    //address --> RL
    public void read() {
        cpu.showProcess(Constants.PROCESS.Print);
        button.setVisible(true);
        synchronized (waitingForInput){
            try {
                waitingForInput.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            writeToDataSegment(inputLines, destinationAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cpu.showPreviousProcess();
    }

    private List<String> inputLines;
    private int destinationAddress;

    private ActionListener InsertAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            List<String> lines = Arrays.asList(inputScreen.getText().split("\\n"));
            if (validation(lines)) {
                System.out.println(lines);
                try {
                    long address = cpu.getRL().getNumber();
                    inputLines = lines;
                    destinationAddress = (int) address;
                    button.setVisible(false);
                    synchronized (waitingForInput){
                        waitingForInput.notifyAll();
                    }
                    System.out.println("DADA");
                } catch (Exception ex) {
                    System.out.println("LALA");
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
        } else if (input.size() % 16 != 0) { // not multiple of 16
            System.out.println(NOT_MULTIPLE_OF_16);
            outputScreen.append(command + " ----------------- > " + NOT_MULTIPLE_OF_16 +'\n');
            return false;
        } else if (checkWordLength(input)) { // word length more than 6
            System.out.println(WORD_LENGTH_MORE_THAN_6_OR_LESS_THAN_1);
            outputScreen.append(command + " ----------------- > " + WORD_LENGTH_MORE_THAN_6_OR_LESS_THAN_1 +'\n');
            return false;
        } else {
            System.out.println(ALL_GOOD); //ALL GOOD BITCH
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

    private void writeToDataSegment(List<String> lines, long address) throws Exception {

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.length() != 6) {
                while (line.length() != 6) {
                    line = line + " ";
                }
            }
            System.out.println("line " + line);
            try {
                Word w = new Word(line, Word.WORD_TYPE.SYMBOLIC);
                System.out.println(w.getHEXFormat());
                System.out.println(address);
                cpu.setRL(new Word(address + i));
                cpu.setRH(new Word(line, Word.WORD_TYPE.SYMBOLIC));
                cpu.getSwapping().SETDS();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Finish writing");
    }
}
