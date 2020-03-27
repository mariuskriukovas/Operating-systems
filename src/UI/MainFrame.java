package UI;

import OS.RM.Parser;
import OS.RM.RealMachine;
import OS.VM.VirtualMachine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.util.ArrayList;
import java.util.Arrays;

public class MainFrame extends JFrame {


    private JTextField textFieldRMPI;
    private JTextField textFieldRMIC;
    private JList listStackSegment;
    private JTextField textFieldRMTI;
    private JList listCodeSegment;
    private JTextField textFieldRMC;
    private JTextField textFieldRMSI;
    private JFormattedTextField textFieldRMSP;
    private JTextField textFieldRMSS;
    private JTextField textFieldRMDS;
    private JTextField textFieldRMCS;
    private JComboBox comboBoxVirtualMachineSelector;
    private JTextField textFieldVRIC;
    private JTextField textFieldVRSP;
    private JTextField textFieldVRSS;
    private JTextField textFieldVRDS;
    private JTextField textFieldVRCS;
    private JTextArea textAreaInput;
    private JButton ENTRYButton;
    private JLabel labelStackSegment;
    private JLabel labelDataSegment;
    private JLabel labelCodeSegment;
    private JLabel labelRMPI;
    private JLabel labelRMSI;
    private JLabel labelRMSP;
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
    private JLabel labelIc;
    private JPanel OperatingSystemFrame;
    private JScrollPane scrollRMCS;
    private JScrollPane scrollRMSS;
    private JScrollPane scrollRMDS;
    private JList listDataSegment;
    private JLabel labelVRIC;
    private JLabel labelVRC;
    private JLabel labelVRRL;
    private JLabel labelVirMRH;

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


        VirtualMachine virtualMachine = realMachine.getVirtualMachines().get(0);
        labelVRIC.setText(virtualMachine.getCpu().getIC().getASCIIFormat());
        labelVirMRH.setText(virtualMachine.getCpu().getRH().getASCIIFormat());
        labelVRRL.setText(virtualMachine.getCpu().getRL().getASCIIFormat());
        labelCodeSegment.setText(virtualMachine.getCpu().getCSValue(virtualMachine.getCpu().getIC()).getASCIIFormat());
        //        textFieldVRIC.setActionCommand("Penis");
//        listStackSegment.setListData(realMachine.);
//        scrollRMSS.setViewportView(listStackSegment);

//        listExteriorMemory.setListData();


    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
