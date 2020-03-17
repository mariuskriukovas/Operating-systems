package OS.VM;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ChannelDevice  {

    private String hexCode = null;
    private int index = 0;

    ChannelDevice() {

    }

    public void readFile(String filename){
        try {
            Scanner scanner = null;
            scanner = new Scanner(new File(filename));
            hexCode = scanner.nextLine();
            index = 0;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String readNextByte(){
        this.index +=2;
        if(index<=hexCode.length()){
            return hexCode.substring(index-2,index);
        }else {
            return null;
        }
    }
    public String readNextWord(){
        this.index +=8;
        if(index<=hexCode.length()){
            return hexCode.substring(index-8,index);
        }else {
            return null;
        }
    }

    public String getCurrentByte(){
       return  hexCode.substring(index-2,index);
    }
    public boolean hasNext() {
        return index<hexCode.length();
    }

}
