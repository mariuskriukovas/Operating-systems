package OS.RM;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Parser {

    private final int COMMAND_LENGTH = 6;
    private final ArrayList<String> dataSegment = new ArrayList<String>(100);
    private final ArrayList<String> codeSegment = new ArrayList<String>(100);


    public Parser(String fileLocation) {
        try {
            File file = new File(fileLocation);
            Scanner scanner = new Scanner(file);
            parse(scanner);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String checkCommand(String command) {
        if (command.length() < COMMAND_LENGTH) {
            for (int i = command.length(); i < COMMAND_LENGTH; i++) {
                command += "0";
            }
        }
        return command;
    }

    private void parse(Scanner scanner) {
        ArrayList<String> fileContent = new ArrayList<String>(100);
        while (scanner.hasNextLine()) {
            fileContent.add(scanner.nextLine());
        }
        int codeSegmentIndex = fileContent.indexOf("CODSEG");
        int dataSegmentIndex = fileContent.indexOf("DATSEG");

        for (int i = dataSegmentIndex + 1; i < codeSegmentIndex; i++) {
            String parsed = checkCommand(fileContent.get(i));
            dataSegment.add(parsed);
//            System.out.println(i-1+" : "+parsed);
        }

        int address = 0;
        for (int i = codeSegmentIndex + 1; i < fileContent.size(); i++) {
            String parsed = checkCommand(fileContent.get(i));
            codeSegment.add(parsed);
//            System.out.println(address+" : "+parsed);
            address++;
        }
//        System.out.println();
//        System.out.println("--------------------------------------------");
//        System.out.println();
    }

    public ArrayList<String> getCodeSegment() {
        return codeSegment;
    }

    public ArrayList<String> getDataSegment() {
        return dataSegment;
    }
}
