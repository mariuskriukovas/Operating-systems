package UI;

import OS.RM.CPU;
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
    private static final String NO_INPUT = "NO INPUT";
    private static final String NOT_MULTIPLE_OF_16 = "NOT MULTIPLE OF 16";
    private static final String WORD_LENGTH_MORE_THAN_6_OR_LESS_THAN_1 = "WORD LENGTH MORE THAN 6 OR LESS THAN 1";
    private static final String ALL_GOOD = "ALL GOOD";
    private JLabel labelRMC;
    private JLabel labelRMTI;
    private JLabel labelRMSegmentPointers;
    private JLabel labelRMDSB;
    private JLabel labelRMCS;
    private JTextArea textAreaInput;
    private JLabel labelRMSS;
    private JLabel labelRMPI;
    private JLabel labelRLMDSB;
    private JLabel labelRLMCSB;
    private JLabel labelRLMTI;
    private JLabel labelRLMC;
    private JLabel labelRLMPI;
    private JLabel labelRLMSSB;
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

    private CPU cpu;
    Integer visible;

    RMPanel(CPU cpu, Integer visible) {
        this.cpu = cpu;
        this.visible = visible;
        inputScroll.setViewportView(textAreaInput);
        ENTRYButton.setEnabled(false);
        ENTRYButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> lines = Arrays.asList(textAreaInput.getText().split("\\n"));
                if (validation(lines)) {
                    System.out.println(lines);
                    try {
                        cpu.writeDS(lines);
                        System.out.println("DADA");
                    } catch (Exception ex) {
                        System.out.println("LALA");
                        ex.printStackTrace();
                    }
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
        labelRLMSSB.setText(SS.getHEXFormat());
        labelRLMDSB.setText(DS.getHEXFormat());
        labelRLMCSB.setText(CS.getHEXFormat());
    }

    private void checkVisibility() {
        synchronized (visible) {
            try {
                visible.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPTRRegister(Word PTR) {
        setBlack();
        labelRLMPTR.setForeground(Color.RED);
        labelRLMPTR.setText(PTR.getHEXFormat());
        checkVisibility();
    }

    public void setSSBRegister(Word SS) {
        setBlack();
        labelRLMSSB.setForeground(Color.RED);
        labelRLMSSB.setText(SS.getHEXFormat());
        checkVisibility();
    }

    public void setDSBRegister(Word DS) {
        setBlack();
        labelRLMDSB.setForeground(Color.RED);
        labelRLMDSB.setText(DS.getHEXFormat());
        checkVisibility();
    }

    public void setCSBRegister(Word CS) {
        setBlack();
        labelRLMCSB.setForeground(Color.RED);
        labelRLMCSB.setText(CS.getHEXFormat());
        checkVisibility();
    }

    public void setSIRegister(SYSTEM_INTERRUPTION SI) {
        setBlack();
        labelRLMSI.setForeground(Color.RED);
        labelRLMSI.setText(SI.toString());
        checkVisibility();
    }

    public void setCRegister(Constants.CONDITIONAL_MODE C) {
        setBlack();
        labelRLMC.setForeground(Color.RED);
        labelRLMC.setText(C.toString());
        checkVisibility();
    }

    public void setTIRegister(int TI) {
        setBlack();
        labelRLMTI.setForeground(Color.RED);
        labelRLMTI.setText("" + TI);
        checkVisibility();
    }

    public void setPIRegister(Constants.PROGRAM_INTERRUPTION PI) {
        setBlack();
        labelRLMPI.setForeground(Color.RED);
        labelRLMPI.setText(PI.toString());
        checkVisibility();
    }

    public void setMODERegister(Constants.SYSTEM_MODE MODE) {
        setBlack();
        labelRLMMode.setForeground(Color.RED);
        labelRLMMode.setText(MODE.toString());
        checkVisibility();
    }

    public boolean isReady() {
        return ready;
    }

    public void setBlack() {
        labelRLMDSB.setForeground(Color.BLACK);
        labelRLMCSB.setForeground(Color.BLACK);
        labelRLMTI.setForeground(Color.BLACK);
        labelRLMC.setForeground(Color.BLACK);
        labelRLMPI.setForeground(Color.BLACK);
        labelRLMSSB.setForeground(Color.BLACK);
        labelRLMPTR.setForeground(Color.BLACK);

        labelRLMSI.setForeground(Color.BLACK);
        labelRLMMode.setForeground(Color.BLACK);
    }

    public void setReady(boolean ready) {
        this.ready = ready;
        ENTRYButton.setEnabled(true);
    }
}
