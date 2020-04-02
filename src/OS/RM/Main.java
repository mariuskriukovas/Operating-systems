package OS.RM;


import UI.OSFrame;

public class Main {

    public static void main(String[] args) throws Exception {
        OSFrame screen = new OSFrame();
        screen.setVisible(true);
        RealMachine realMachine = new RealMachine(screen);

    }
}
