package UI;


import OS.RM.CPU;

import javax.swing.*;

public class OSFrame extends JFrame {

    private JTabbedPane tabbedPane1;
    private VMPanel vmPanel;
    private RMPanel rmPanel;
    private InternalMemory internalMemory;
    private ExternalMemory externalMemory;

    public static boolean TickMode = false;

    public Integer visible = 1;

    int f() {
        System.out.println("iskviete");
        return 1;
    }

    public OSFrame(CPU cpu) throws Exception {
        setTitle("OPERATING_SYSTEMS");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        vmPanel = new VMPanel(cpu, visible,tabbedPane1);
        rmPanel = new RMPanel(cpu, visible,tabbedPane1);
        externalMemory = new ExternalMemory(cpu);
        internalMemory = new InternalMemory(cpu);

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
}
