package UI;

import OS.RM.CPU;
import OS.Tools.Constants.CONDITIONAL_MODE;
import OS.Tools.Word;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static UI.OSFrame.TickMode;

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
    private JLabel labelVMRX;
    private JLabel labelVMRH;
    private JLabel labelVMRL;
    private JLabel labelVMDS;
    private JLabel labelVMCS;
    private JLabel labelVMSS;
    private JLabel labelVMSP;
    private JLabel labelVM;
    private JLabel labelVRRL;
    private JLabel labelVirMRH;

    private JLabel labelVRMDS;
    private JLabel labelVRMCS;
    private JLabel labelVRMSS;
    private JLabel labelVRMSP;

    private JLabel labelVMIC;
    private JLabel labelVMC;
    private JLabel labelVRC;
    private JLabel labelVRIC;
    private final JTabbedPane tabbedPanel;


    private JPanel VMPanel;
    private JButton Tick;
    private JButton Refresh;

    private boolean ready = false;

    Integer visible;

    private CPU cpu;

    VMPanel(CPU cpu, Integer visible,JTabbedPane tabbedPanel) {

        Refresh.setEnabled(false);
        Tick.setEnabled(false);
        this.visible = visible;
        this.cpu = cpu;
        this.tabbedPanel=tabbedPanel;

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
                    setDataSegment(cpu.getInternalMemory().getBlock(cpu.getDS().getBlockFromAddress()));
                    setCodeSegment(cpu.getInternalMemory().getBlock(cpu.getCS().getBlockFromAddress()));
                    setStackSegment(cpu.getInternalMemory().getBlock(cpu.getSS().getBlockFromAddress()));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }


    JPanel getVMPanel() {
        return this.VMPanel;
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

    public void setStackPointer(Word SP) {
        setBlack();
        labelVRMSP.setForeground(Color.RED);
        labelVRMSP.setText(SP.getHEXFormat());
        checkVisibility();
    }

    public void setInstructionCounter(Word IC) {
        labelVRIC.setText(IC.getHEXFormat());
    }

//    public void setDataRegisters(Word RL, Word RH) {
//        setBlack();
//        labelVRRL.setForeground(Color.RED);
//        labelVirMRH.setForeground(Color.RED);
//        labelVRRL.setText(RL.getHEXFormat());
//        labelVirMRH.setText(RH.getHEXFormat());
//        checkVisibility();
//    }

    // mazdaug pagal sita modeli visus
    public void setRLRegister(Word RL) {
        setBlack();
        labelVRRL.setForeground(Color.RED);
        labelVRRL.setText(RL.getHEXFormat());
        checkVisibility();
    }

    public void setRHRegister(Word RH) {
        setBlack();
        labelVirMRH.setForeground(Color.RED);
        labelVirMRH.setText(RH.getHEXFormat());
        checkVisibility();
    }

    public void setCRegister(CONDITIONAL_MODE C) {
        setBlack();
        labelVRC.setForeground(Color.RED);
        labelVRC.setText(C.toString());
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
        List<String> str = Arrays.stream(arr).map(x -> x.getASCIIFormat()).collect(Collectors.toList());
        listCodeSegment.setListData(str.toArray());
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
        labelVRRL.setForeground(Color.BLACK);
        labelVirMRH.setForeground(Color.BLACK);

        labelVRMDS.setForeground(Color.BLACK);
        labelVRMCS.setForeground(Color.BLACK);
        labelVRMSS.setForeground(Color.BLACK);
        labelVRMSP.setForeground(Color.BLACK);
        labelVRC.setForeground(Color.BLACK);
        labelVRIC.setForeground(Color.BLACK);

        tabbedPanel.setSelectedIndex(1);
    }

    private void checkVisibility() {
        synchronized (visible) {
            try {
                if (TickMode)
                {
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
