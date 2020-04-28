package Components.UI;

import Components.CPU;
import Tools.Constants;
import Tools.Constants.SYSTEM_INTERRUPTION;
import Tools.Word;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static Components.UI.OSFrame.TickMode;

public class RMPanel {
    private JLabel labelRMTI;
    private JLabel labelRMSegmentPointers;
    private JTextArea textAreaInput;
    private JLabel labelRLMDSB;
    private JLabel labelRLMCSB;
    private JLabel TI;
    private JLabel C;
    private JLabel labelRLMPI;
    private JLabel labelRLMSSB;
    private JLabel labelRLMPTR;
    private JLabel labelPTR;
    private JLabel labelRMSI;
    private JLabel SI;
    private JLabel MODE;
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
    private JLabel labelVMSS;
    private JLabel labelVRMSS;
    private JLabel labelVMDS;
    private JLabel labelVRMDS;
    private JLabel labelVMCS;
    private JLabel labelVRMCS;
    private boolean ready = false;
    private final JTabbedPane tabbedPanel;

    private CPU cpu;
    Integer visible;

    RMPanel(Integer visible, JTabbedPane tabbedPanel) {
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
        this.SI.setForeground(Color.RED);
        this.SI.setText(SI.toString());
//        checkVisibility();
    }

    public void setCRegister(Constants.CONDITIONAL_MODE C) {
        setBlack();
        this.C.setForeground(Color.RED);
        this.C.setText(C.toString());
        checkVisibility();
    }

    public void setTIRegister(int TI) {
        setBlack();
        this.TI.setForeground(Color.RED);
        this.TI.setText("" + TI);
        checkVisibility();
    }

    public void setSSRegister(Word SS) {
        setBlack();
        labelVRMSS.setForeground(Color.RED);
        labelVRMSS.setText(SS.getHEXFormat());
        checkVisibility();
    }

    public void setDSRegister(Word DS) {
        setBlack();
        labelVRMDS.setForeground(Color.RED);
        labelVRMDS.setText(DS.getHEXFormat());
        checkVisibility();
    }

    public void setCSRegister(Word CS) {
        setBlack();
        labelVRMCS.setForeground(Color.RED);
        labelVRMCS.setText(CS.getHEXFormat());
        checkVisibility();
    }


    public void setActiveProcess(String name) {
        process.setForeground(Color.BLUE);
        process.setText(name);
//        checkVisibility();
    }

    public void setMODERegister(Constants.SYSTEM_MODE MODE) {
        setBlack();
        this.MODE.setForeground(Color.RED);
        this.MODE.setText(MODE.toString());
        checkVisibility();
    }

    public boolean isReady() {
        return ready;
    }

    public void setBlack() {
//        labelRLMDSB.setForeground(Color.BLACK);
//        labelRLMCSB.setForeground(Color.BLACK);
//        labelRLMSSB.setForeground(Color.BLACK);

        labelVRMDS.setForeground(Color.BLACK);
        labelVRMCS.setForeground(Color.BLACK);
        labelVRMSS.setForeground(Color.BLACK);

        TI.setForeground(Color.BLACK);
//        C.setForeground(Color.BLACK);
//        labelRLMPI.setForeground(Color.BLACK);
        labelRLMPTR.setForeground(Color.BLACK);

        SI.setForeground(Color.BLACK);
        MODE.setForeground(Color.BLACK);

        tabbedPanel.setSelectedIndex(0);
    }

    public void setReady(boolean ready) {
        this.ready = ready;
        ENTRYButton.setEnabled(true);
    }
}
