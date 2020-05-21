package Components.UI;


import Processes.RealMachine;

import javax.swing.*;

public class OSFrame extends JFrame {

    private JTabbedPane tabbedPane1;
    private VMPanel vmPanel;
    private RMPanel rmPanel;
    private InternalMemory internalMemory;
    private ExternalMemory externalMemory;

    public static boolean TickMode = false;

    public Integer visible = 1;


    public OSFrame(RealMachine realMachine) {
        setTitle("OPERATING_SYSTEMS");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        vmPanel = new VMPanel(visible,tabbedPane1, realMachine);
        rmPanel = new RMPanel(visible,tabbedPane1);
        externalMemory = new ExternalMemory(realMachine.getExternalMemory());
        internalMemory = new InternalMemory(realMachine.getInternalMemory());

        tabbedPane1.addTab("RM", rmPanel.getRMPanel());
        tabbedPane1.addTab("VM", vmPanel.getVMPanel());
        tabbedPane1.addTab("External memory", externalMemory.getExternalMemory());
        tabbedPane1.addTab("Internal memory", internalMemory.getInternalMemory());
        this.add(tabbedPane1);
    }


    public void setReady(boolean isReady) {
        vmPanel.setReady(isReady);
        rmPanel.setReady(isReady);
        this.setVisible(true);
    }

    public VMPanel getScreenForVirtualMachine() {
        return vmPanel;
    }

    public RMPanel getScreenForRealMachine() {
        return rmPanel;
    }

    public JTabbedPane getTabbs() {
        return tabbedPane1;
    }
}
