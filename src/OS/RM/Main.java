package OS.RM;


import UI.OSFrame;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        OSFrame screen = new OSFrame();
        RealMachine realMachine = new RealMachine(screen);
    }
}
