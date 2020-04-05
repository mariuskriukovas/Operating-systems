package UI;


import javax.swing.*;
import java.util.concurrent.Callable;

public class OSFrame extends JFrame
{

    private JTabbedPane tabbedPane1;
    private VMPanel vmPanel;
    private RMPanel rmPanel;

    int f(){
        System.out.println("iskviete");
        return 1;
    }

    public OSFrame() throws Exception {
        setTitle("OPERATING_SYSTEMS");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        vmPanel = new VMPanel();
        rmPanel = new RMPanel();

        tabbedPane1.addTab("VM", vmPanel.getVMPanel());
        tabbedPane1.addTab("RM", rmPanel.getRMPanel());
        this.add(tabbedPane1);

    }

    public void setReady (boolean isReady) {
        vmPanel.setReady(isReady);
        rmPanel.setReady(isReady);
    }

    public VMPanel getScreenForVirtualMachine()
    {
        return vmPanel;
    }

    public RMPanel getScreenForRealMachine()
    {
        return rmPanel;
    }


}
