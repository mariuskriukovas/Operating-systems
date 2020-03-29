package UI;

import OS.RM.Parser;
import OS.RM.RealMachine;
import OS.VM.VirtualMachine;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

public class MainFrame extends JFrame {


    private JList listStackSegment;
    private JList listCodeSegment;
    private JComboBox comboBoxVirtualMachineSelector;
    private JTextField textFieldVRIC;
    private JTextArea textAreaInput;
    private JButton ENTRYButton;
    private JLabel labelStackSegment;
    private JLabel labelDataSegment;
    private JLabel labelCodeSegment;
    private JLabel labelRMPI;
    private JLabel labelRMSI;
    private JLabel labelRMSS;
    private JLabel labelRMCS;
    private JLabel labelRMDS;
    private JLabel labelRMTI;
    private JLabel labelRMC;
    private JLabel labelRMSegmentPointers;
    private JLabel labelRM;
    private JLabel labelVM;
    private JLabel labelVMC;
    private JLabel labelVMIC;
    private JLabel labelVMRX;
    private JLabel labelVMRH;
    private JLabel labelVMRL;
    private JLabel labelVMSP;
    private JLabel labelVMDS;
    private JLabel labelVMSS;
    private JLabel labelVMCS;
    private JLabel labelPTR;
    private JPanel OperatingSystemFrame;
    private JScrollPane scrollRMCS;
    private JScrollPane scrollRMSS;
    private JScrollPane scrollRMDS;
    private JList listDataSegment;
    private JLabel labelVRIC;
    private JLabel labelVRC;
    private JLabel labelVRRL;
    private JLabel labelVirMRH;
    private JLabel labelVRMSP;
    private JLabel labelVRMSS;
    private JLabel labelVRMCS;
    private JLabel labelVRMDS;
    private JLabel labelRLMPTR;
    private JLabel labelRLMPI;
    private JLabel labelRLMSI;
    private JLabel labelRLMSS;
    private JLabel labelRLMCS;
    private JLabel labelRLMDS;
    private JLabel labelRLMTI;
    private JLabel labelRLMC;
    private JLabel labelRMMode;
    private JLabel labelRLMMode;

    private RealMachine realMachine;
    private Parser parser;

    public MainFrame(RealMachine realMachine) throws Exception {
        setTitle("OPERATING_SYSTEMS");
        setSize(1500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(OperatingSystemFrame);

        this.parser = new Parser("prog.txt");
        this.realMachine = realMachine;

        ENTRYButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                String s[] = textAreaInput.getText().split("\\r?\\n");
                ArrayList<String> arrList = new ArrayList<>(Arrays.asList(s));
                System.out.println(arrList);
            }
        });

        listDataSegment.setListData(parser.getDataSegment().toArray());
        scrollRMDS.setViewportView(listDataSegment);

        listCodeSegment.setListData(parser.getCodeSegment().toArray());
        scrollRMCS.setViewportView(listCodeSegment);

        String virtualMachineIndex = (String)comboBoxVirtualMachineSelector.getSelectedItem();

        //Set virtual Machine
        VirtualMachine virtualMachine = realMachine.getVirtualMachines().get(Integer.parseInt(virtualMachineIndex)-1);
        labelVRIC.setText(virtualMachine.getCpu().getIC().getHEXFormat());
        labelVirMRH.setText(virtualMachine.getCpu().getRH().getHEXFormat());
        labelVRRL.setText(virtualMachine.getCpu().getRL().getHEXFormat());
        labelVRC.setText(virtualMachine.getCpu().getC().name());
        labelVRMSP.setText(virtualMachine.getCpu().getSP().getHEXFormat());
        labelVRMSS.setText(virtualMachine.getCpu().getSSValue().getHEXFormat());
        labelVRMCS.setText(virtualMachine.getCpu().getCSValue(virtualMachine.getCpu().getIC()).getHEXFormat());
        labelVRMDS.setText(virtualMachine.getCpu().getDSValue(virtualMachine.getCpu().getIC()).getHEXFormat());
        //SET REAL MACHINE
        labelRLMC.setText(realMachine.getRealCPU().getC().getValue().toString());
        labelRLMPI.setText(realMachine.getRealCPU().getPI().getValue().toString());
        labelRLMTI.setText(realMachine.getRealCPU().getTI().getValue().toString());
        labelRLMSI.setText(realMachine.getRealCPU().getSI().getValue().toString());
        labelRLMMode.setText(realMachine.getRealCPU().getMODE().getValue().toString());

        labelRLMPTR.setText(realMachine.getRealCPU().getPTRValue(0).getHEXFormat());
        labelRLMSS.setText(realMachine.getRealCPU().getSS().getHEXFormat());
        labelRLMDS.setText(realMachine.getRealCPU().getDS().getHEXFormat());
        labelRLMCS.setText(realMachine.getRealCPU().getCS().getHEXFormat());




    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
