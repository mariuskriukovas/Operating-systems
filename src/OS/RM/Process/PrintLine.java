package OS.RM.Process;

import OS.RM.CPU;
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

    public PrintLine(CPU cpu){
        this.cpu = cpu;
        inputScreen = cpu.getRMScreen().getConsole();
        outputScreen = cpu.getRMScreen().getScreen();
        button = cpu.getRMScreen().getInputKey();
        button.addActionListener(InsertAction);
    }

    //address --> RL
    public void print() {
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
    }


    public void printRegisters() {
        String command  = "PRINT ";
        outputScreen.append("RL ---> " +  cpu.getRL().toString() +'\n');
        outputScreen.append("RH ---> " +  cpu.getRH().toString() +'\n');
        outputScreen.append("C --->  " + cpu.getC().toString() +'\n');
    }


    //address --> RL
    public void read() {
        button.setVisible(true);
    }

    private ActionListener InsertAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            List<String> lines = Arrays.asList(inputScreen.getText().split("\\n"));
            if (validation(lines)) {
                System.out.println(lines);
                try {
                    long address = cpu.getRL().getNumber();
                    writeToDataSegment(lines, address);
                    button.setVisible(false);
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
        System.out.println("lines2 " + address);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.length() != 6) {
                while (line.length() != 6) {
                    line = line + " ";
                }
            }
            System.out.println("line " + line);
            try {
                System.out.println("before interupt1");
                cpu.setRL(new Word(address + i));
                System.out.println("before interupt2 " + line);
                cpu.setRH(new Word(line, Word.WORD_TYPE.SYMBOLIC));
                System.out.println("before interupt");
                cpu.getSwapping().SETDS();
                System.out.println("after interupt");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Finish writing");
    }


}
