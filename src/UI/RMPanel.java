package UI;

import OS.RM.CPU;
import OS.Tools.Constants;
import OS.Tools.Constants.SYSTEM_INTERRUPTION;
import OS.Tools.Word;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import static UI.OSFrame.TickMode;

public class RMPanel {
    private JLabel labelRMC;
    private JLabel labelRMTI;
    private JLabel labelRMSegmentPointers;
    private JLabel labelRMDSB;
    private JLabel labelRMCS;
    private JTextArea textAreaInput;
    private JLabel labelRMSS;
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
    private JTextArea screen;
    private JLabel Screen;
    private JButton TickButton;
    private JLabel process;
    private JButton InputKey;
    private boolean ready = false;
    private final JTabbedPane tabbedPanel;

    private CPU cpu;
    Integer visible;

    RMPanel(CPU cpu, Integer visible, JTabbedPane tabbedPanel) {
        this.cpu = cpu;
        this.visible = visible;
        this.tabbedPanel = tabbedPanel;
        inputScroll.setViewportView(textAreaInput);
        ENTRYButton.setEnabled(false);
        InputKey.setVisible(false);

        TickButton.addActionListener(TickAction);
        TickButton.addKeyListener(enterListener);
        screen.setEditable(false);
    }


    public JButton getENTRYButton() {
        return ENTRYButton;
    }

    KeyAdapter enterListener = new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent keyEvent) {
            if (keyEvent.getKeyChar() == '\n') {
                try {
                    synchronized (RMPanel.this.visible) {
                        RMPanel.this.visible.notify();
                    }
                    System.out.println("Tick");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private ActionListener TickAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                synchronized (RMPanel.this.visible) {
                    RMPanel.this.visible.notify();
                }
                System.out.println("Tick");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public JTextArea getConsole() {
        return textAreaInput;
    }

    public JTextArea getScreen() {
        return screen;
    }

    public JButton getInputKey() {
        return InputKey;
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
                if (TickMode) {
                    visible.wait();
                }
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
//        checkVisibility();
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
//        checkVisibility();
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

//    public void setPIRegister(Constants.PROGRAM_INTERRUPTION PI) {
//        setBlack();
//        labelRLMPI.setForeground(Color.RED);
//        labelRLMPI.setText(PI.toString());
//        checkVisibility();
//    }

    public void setActiveProcess(String name) {
        process.setForeground(Color.BLUE);
        process.setText(name);
//        checkVisibility();
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

        tabbedPanel.setSelectedIndex(0);
    }

    public void setReady(boolean ready) {
        this.ready = ready;
        ENTRYButton.setEnabled(true);
    }
}
