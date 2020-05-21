package Components.UI;

import Components.CPU;
import Processes.RealMachine;
import Tools.Constants;
import Tools.Constants.CONDITIONAL_MODE;
import Tools.Word;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static Components.UI.OSFrame.TickMode;

public class VMPanel {
    private JLabel labelDataSegment;
    private JLabel labelCodeSegment;
    private JLabel labelStackSegment;
    private JScrollPane scrollRMDS;
    private JList listDataSegment;
    private JScrollPane scrollRMCS;
    private JList listCodeSegment;
    private JScrollPane scrollRMSS;
    private JList listStackSegment;
    private JLabel labelVM;
    private JLabel labelVRRL;
    private JLabel labelVirMRH;

    private JLabel labelVRC;
    private JLabel labelVRIC;
    private final JTabbedPane tabbedPanel;

    private JPanel VMPanel;
    private JButton Tick;
    private JButton Refresh;
    private JLabel labelVMSP;

    private JLabel RL;
    private JLabel C;
    private JLabel RH;
    private JLabel SP;
    private JLabel IC;
    private JLabel PI;
    private CPU cpu;

    private boolean ready = false;

    Integer visible;

    private final RealMachine realMachine;

    VMPanel(Integer visible, JTabbedPane tabbedPanel, RealMachine realMachine)
    {
        Refresh.setEnabled(false);
        Tick.setEnabled(false);
        this.visible = visible;
        this.tabbedPanel = tabbedPanel;
        this.realMachine = realMachine;

        Tick.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    synchronized (VMPanel.this.visible) {
                        VMPanel.this.visible.notify();
                    }
                    System.out.println("Tick");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("Refresh");
                try {

                    setStackSegment(realMachine.getInternalMemory().getBlock(cpu.getSS().getBlockFromAddress()));
                    setDataSegment(realMachine.getInternalMemory().getBlock(cpu.getDS().getBlockFromAddress()));
                    setCodeSegment(realMachine.getInternalMemory().getBlock(cpu.getCS().getBlockFromAddress()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Tick.addKeyListener(enterListener);
    }

    public void setCpu(CPU cpu) {
        this.cpu = cpu;
    }

    private KeyAdapter enterListener = new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent keyEvent) {
            if (keyEvent.getKeyChar() == '\n') {
                try {
                    synchronized (VMPanel.this.visible) {
                        VMPanel.this.visible.notify();
                    }
                    System.out.println("Tick");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    JPanel getVMPanel() {
        return this.VMPanel;
    }


    public void setStackPointer(Word SP) {
        setBlack();
        this.SP.setForeground(Color.RED);
        this.SP.setText(SP.getHEXFormat());
        checkVisibility();
    }

    public void setInstructionCounter(Word IC) {
        setBlack();
        this.IC.setForeground(Color.RED);
        this.IC.setText(IC.getHEXFormat());
        checkVisibility();
    }

    public void setRLRegister(Word RL) {
        setBlack();
        this.RL.setForeground(Color.RED);
        this.RL.setText(RL.getHEXFormat());
        checkVisibility();
    }

    public void setRHRegister(Word RH) {
        setBlack();
        this.RH.setForeground(Color.RED);
        this.RH.setText(RH.getHEXFormat());
        checkVisibility();
    }

    public void setCRegister(CONDITIONAL_MODE C) {
        setBlack();
        this.C.setForeground(Color.RED);
        this.C.setText(C.toString());
        checkVisibility();
    }

    public void setPIRegister(Constants.PROGRAM_INTERRUPTION PI) {
        setBlack();
        this.PI.setForeground(Color.RED);
        this.PI.setText(PI.toString());
        checkVisibility();
    }

    private void setStackSegment(Word[] arr) throws Exception {
        listStackSegment.setListData(arr);
        scrollRMSS.setViewportView(listStackSegment);
    }

    private void setDataSegment(Word[] arr) throws Exception {
        listDataSegment.setListData(arr);
        scrollRMDS.setViewportView(listDataSegment);
    }

    private void setCodeSegment(Word[] arr) throws Exception {
//        List<String> str = Arrays.stream(arr).map(x -> x.getASCIIFormat()).collect(Collectors.toList());
        listCodeSegment.setListData(arr);
        scrollRMCS.setViewportView(listCodeSegment);
    }

    private void createUIComponents() {
        listStackSegment = new JList();
        listCodeSegment = new JList();
        listDataSegment = new JList();
        listCodeSegment.setFixedCellWidth(166);
        listDataSegment.setFixedCellWidth(166);
        listStackSegment.setFixedCellWidth(167);
    }

    public void setBlack() {
        this.RL.setForeground(Color.BLACK);
        this.RH.setForeground(Color.BLACK);

        this.C.setForeground(Color.BLACK);
        this.IC.setForeground(Color.BLACK);
        this.SP.setForeground(Color.BLACK);
        this.PI.setForeground(Color.BLACK);

        tabbedPanel.setSelectedIndex(1);
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

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
        Tick.setEnabled(true);
        Refresh.setEnabled(true);
    }
}
