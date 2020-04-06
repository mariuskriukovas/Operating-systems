package UI;

import OS.Tools.ByteWord;
import OS.Tools.Word;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

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

    private JPanel VMPanel;
    private JButton INCSIButton;
    private JButton NODEBUGButton;

    private boolean ready = false;

    VMPanel(){

        NODEBUGButton.setEnabled(false);
        INCSIButton.setEnabled(false);

        NODEBUGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    System.out.println("NO_DEBUG");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setIncButtonFunction(Callable function)
    {
        INCSIButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    function.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    JPanel getVMPanel(){
        return this.VMPanel;
    }

    public void setSSRegister(Word SS) {
        labelVRMSS.setText(SS.getHEXFormat());
    }

    public void setDSRegister(Word DS) {
        labelVRMDS.setText(DS.getHEXFormat());
    }

    public void setCSRegister(Word CS){
        labelVRMCS.setText(CS.getHEXFormat());
    }

    public void setStackPointer(Word SP)  {
        labelVRMSP.setText(SP.getHEXFormat());
    }

    public void setInstructionCounter(Word IC) {
        labelVRIC.setText(IC.getHEXFormat());
    }

    public void setDataRegisters(Word RL, Word RH)  {
        labelVRRL.setText(RL.getHEXFormat());
        labelVirMRH.setText(RH.getHEXFormat());
    }

    public void setCRegister(ByteWord C){
        labelVRC.setText(C.toString());
    }

    public void setSIRegister(ByteWord SI){

    }

    public void setStackSegment(Word[] arr) throws Exception {
        listStackSegment.setListData(arr);
        scrollRMSS.setViewportView(listStackSegment);
    }

    public void setDataSegment(Word[]  arr) throws Exception {
        listDataSegment.setListData(arr);
        scrollRMDS.setViewportView(listDataSegment);
    }

    public void setCodeSegment(Word[] arr) throws Exception {
        List<String> str = Arrays.stream(arr).map(x->x.getASCIIFormat()).collect(Collectors.toList());
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


    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
        INCSIButton.setEnabled(true);
        NODEBUGButton.setEnabled(true);
    }
}
