package OS.RM;

import UI.MainFrame;

public class Main {

    public static void main(String[] args) throws Exception {
        RealMachine realMachine = new RealMachine();
        MainFrame userInterface = new MainFrame(realMachine);
        userInterface.setVisible(true);
    }
}
