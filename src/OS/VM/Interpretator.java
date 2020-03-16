package OS.VM;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Interpretator {

    private Scanner scanner = null;
    private ArrayList<String> dataSegment = new ArrayList<String>(100);
    private ArrayList<String> codeSegment = new ArrayList<String>(100);
    private ArrayList<String> code = new ArrayList<String>(100);

    Interpretator(String fileLocation)
    {
        try {
            File file =
                    new File(fileLocation);
            scanner = new Scanner(file);
    }catch (Exception e){
         e.printStackTrace();
        }
    }

    public void read()
    {
        while (scanner.hasNextLine())
        {
            code.add(scanner.nextLine());
        }
    }

    public void interpreter() throws Exception {
        if(!code.get(0).equals(Constants.FILE_SEG.DATSEG.name())) {throw new Exception("NO DATSEG");}
        int indexOfCodeSeg = code.indexOf(Constants.FILE_SEG.CODSEG.name());
        if(indexOfCodeSeg == -1){throw new Exception("NO CODSEG");}
        dataSegment.addAll(code.subList(1,indexOfCodeSeg));
        codeSegment.addAll(code.subList(indexOfCodeSeg+1,code.size()));
    }
    public ArrayList<String> getCodeSegment() { return codeSegment; }
    public ArrayList<String> getDataSegment() { return dataSegment; }
}
