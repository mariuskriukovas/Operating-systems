package OS.VM;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Parser {

    private HashMap<String, String> commands = new HashMap<String, String>(100);
    private String dictionaryLocation = "commands.txt";

    Parser(String fileLocation) {
        try {
            File file = new File(dictionaryLocation);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine())
            {
                String command = scanner.nextLine();
                int index = command.indexOf("->");
                commands.put(command.substring(0,index),command.substring(index+2));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        parse(fileLocation);
    }

    private void parse(String fileLocation)
    {
        try {
            File parsedCode = new File("code_seg.txt");
            BufferedWriter codeWriter = new BufferedWriter(new FileWriter(parsedCode.getAbsoluteFile()));

            File parsedData = new File("data_seg.txt");
            BufferedWriter dataWriter = new BufferedWriter(new FileWriter(parsedData.getAbsoluteFile()));

            File file = new File(fileLocation);
            Scanner scanner = new Scanner(file);
            ArrayList<String> fileContent = new ArrayList<String>(100);
            while (scanner.hasNextLine()) {
               fileContent.add(scanner.next());
            }
            int codeSegmentIndex = fileContent.indexOf("CODESEG");
            int dataSegmentIndex = fileContent.indexOf("DATASEG");

            for (int i = dataSegmentIndex+1; i<codeSegmentIndex; i++) {
                dataWriter.write(fileContent.get(i).replaceAll("\\s+",""));
            }
            for (int i = codeSegmentIndex+1; i<fileContent.size(); i++) {
                String hexCode = commands.get(fileContent.get(i));
                if(hexCode == null) codeWriter.write(fileContent.get(i).replaceAll("\\s+",""));
                else codeWriter.write(hexCode);
            }

            dataWriter.close();
            codeWriter.close();

        }catch (Exception e){

            e.printStackTrace();
        }
    }

    public HashMap<String, String> getCommands() {
        return commands;
    }
}
