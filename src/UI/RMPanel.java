package UI;

import OS.RM.RealMachine;
import OS.Tools.ByteWord;
import OS.Tools.Word;
import OS.VM.VirtualMachine;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;

import static java.lang.Integer.parseInt;

public class RMPanel
{
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

    RMPanel(){
    }

    JPanel getRMPanel(){return RMPanel;}


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

    public void setCSRegister(Word CS){
        labelRLMCS.setText(CS.getHEXFormat());
    }

    public void setSIRegister(ByteWord SI){
        labelRLMSI.setText(SI.toString());
    }

    public void setCRegister(ByteWord C){
        labelRLMC.setText(C.toString());
    }

    public void setTIRegister(ByteWord TI){
        labelRLMTI.setText(TI.toString());
    }
    public void setPIRegister(ByteWord PI){
        labelRLMPI.setText(PI.toString());
    }
    public void setMODERegister(ByteWord MODE){
        labelRLMMode.setText(MODE.toString());
    }

}
