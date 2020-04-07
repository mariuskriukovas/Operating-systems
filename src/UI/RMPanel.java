package UI;

import OS.Tools.Constants;
import OS.Tools.Constants.SYSTEM_INTERRUPTION;
import OS.Tools.Word;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class RMPanel {
    public static final String NO_INPUT = "NO INPUT";
    public static final String NOT_MULTIPLE_OF_16 = "NOT MULTIPLE OF 16";
    public static final String WORD_LENGTH_MORE_THAN_6_OR_LESS_THAN_1 = "WORD LENGTH MORE THAN 6 OR LESS THAN 1";
    public static final String ALL_GOOD = "ALL GOOD";
    private JLabel labelRMC;
    private JLabel labelRMTI;
    private JLabel labelRMSegmentPointers;
    private JLabel labelRMDS;
    private JLabel labelRMCS;
    private JTextArea textAreaInput;
    private JLabel labelRMSS;
    private JLabel labelRMPI;
    private JLabel labelRLMDS;
    private JLabel labelRLMCS;
    private JLabel labelRLMTI;
    private JLabel labelRLMC;
    private JLabel labelRLMPI;
    private JLabel labelRLMSS;
    private JLabel labelRLMPTR;
    private JLabel labelPTR;
    private JLabel labelRMSI;
    private JLabel labelRLMSI;
    private JLabel labelRLMMode;
    private JLabel labelRMMode;
    private JButton ENTRYButton;
    private JLabel labelRM;
    private JPanel RMPanel;
    private JScrollPane inputScroll;
    private JLabel commentWindow;
    private boolean ready = false;

    RMPanel() {

        inputScroll.setViewportView(textAreaInput);
        ENTRYButton.setEnabled(false);
        ENTRYButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> lines = Arrays.asList(textAreaInput.getText().split("\\n"));
                if (validation(lines)) {
                    System.out.println(lines); // DO SOMETHING
                }
            }
        });

    }

    private boolean validation(List<String> input) {
        if (input.size() == 0) { // no input
            System.out.println(NO_INPUT);
            commentWindow.setText(NO_INPUT);
            return false;
        } else if (input.size() % 16 != 0) { // not multiple of 16
            System.out.println(NOT_MULTIPLE_OF_16);
            commentWindow.setText(NOT_MULTIPLE_OF_16);
            return false;
        } else if (checkWordLength(input)) { // word length more than 6
            System.out.println(WORD_LENGTH_MORE_THAN_6_OR_LESS_THAN_1);
            commentWindow.setText(WORD_LENGTH_MORE_THAN_6_OR_LESS_THAN_1);
            return false;
        } else {
            System.out.println(ALL_GOOD); //ALL GOOD BITCH
            commentWindow.setText(ALL_GOOD);
            return true;
        }
    }

    private boolean checkWordLength(List<String> input) {
        return input.stream()
                .filter(a -> a.length() > 6 || a.isEmpty())
                .collect(toList())
                .size() != 0;
    }

    JPanel getRMPanel() {
        return RMPanel;
    }

    public void setSegmentRegisters(Word PTR, Word SS, Word DS, Word CS) throws Exception {
        labelRLMPTR.setText(PTR.getHEXFormat());
        labelRLMSS.setText(SS.getHEXFormat());
        labelRLMDS.setText(DS.getHEXFormat());
        labelRLMCS.setText(CS.getHEXFormat());
    }

    public void setPTRRegister(Word PTR) {
        labelRLMPTR.setText(PTR.getHEXFormat());
    }

    public void setSSRegister(Word SS) {
        labelRLMSS.setText(SS.getHEXFormat());
    }

    public void setDSRegister(Word DS) {
        labelRLMDS.setText(DS.getHEXFormat());
    }

    public void setCSRegister(Word CS) {
        labelRLMCS.setText(CS.getHEXFormat());
    }

    public void setSSBRegister(Word r) {
       System.out.println("Image Not implemented");
    }
    public void setDSBRegister(Word r) {
        System.out.println("Image Not implemented");
    }
    public void setCSBRegister(Word r) {
        System.out.println("Image Not implemented");
    }

    public void setSIRegister(SYSTEM_INTERRUPTION SI) {
        labelRLMSI.setText(SI.toString());
    }

    public void setCRegister(Constants.CONDITIONAL_MODE C) {
        labelRLMC.setText(C.toString());
    }

    public void setTIRegister(int TI) {
        labelRLMTI.setText(""+TI);
    }

    public void setPIRegister(Constants.PROGRAM_INTERRUPTION PI) {
        labelRLMPI.setText(PI.toString());
    }

    public void setMODERegister(Constants.SYSTEM_MODE MODE) {
        labelRLMMode.setText(MODE.toString());
    }

    public boolean isReady() {
        return ready;
    }

    public void setBlack()
    {
        labelRLMDS.setForeground(Color.BLACK);
        labelRLMCS.setForeground(Color.BLACK);
        labelRLMTI.setForeground(Color.BLACK);
        labelRLMC.setForeground(Color.BLACK);
        labelRLMPI.setForeground(Color.BLACK);
        labelRLMSS.setForeground(Color.BLACK);
        labelRLMPTR.setForeground(Color.BLACK);

        labelRLMSI.setForeground(Color.BLACK);
        labelRLMMode.setForeground(Color.BLACK);
    }



    public void setReady(boolean ready) {
        this.ready = ready;
        ENTRYButton.setEnabled(true);
    }
}
