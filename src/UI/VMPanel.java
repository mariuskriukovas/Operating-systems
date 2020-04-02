package UI;

import OS.Tools.ByteWord;
import OS.Tools.Word;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    VMPanel(){

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
}
