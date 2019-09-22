import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileParser {
    String path;
    String inputExt = "asm";
    String outputExt = "hack";

    public Boolean validateFileType(String path) {
        String[] fileNameArr = path.split("\\.");
        String ext = fileNameArr[fileNameArr.length - 1];
        return ext.equals(this.inputExt);
    }

    public String getHackFilePath(String path) {
        String[] fileNameArr = path.split("\\.");
        fileNameArr[fileNameArr.length - 1] = this.outputExt;
        return String.join(".", fileNameArr);
    }

    public BufferedReader getBufferedReader(String path) throws Exception {
        File file = new File(path);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        return br;
    }

    public BufferedWriter getBufferedWriter(String path) throws Exception {
        File file = new File(path);
        FileWriter fr = new FileWriter(file);
        BufferedWriter br = new BufferedWriter(fr);
        return br;
    }

    public void translateFile(BufferedReader br, BufferedWriter bw) throws Exception {
        Formatter formatter = new Formatter();
        Translator translator = new Translator();
        String st;
        ArrayList<String> contents = new ArrayList<String>();
        
        while((st = br.readLine()) != null) {
            contents.add(st);
        }
        
        List<String> formattedContents = formatter.format(contents);
        List<String> binaryContents = translator.translate(formattedContents);
        binaryContents.forEach(line -> {
            try {
                bw.write(line + "\n");
            } catch(IOException e) {
                e.printStackTrace();
            }
        });
        
        br.close();
        bw.close();
    }

    public FileParser(String args[]) throws Exception{
        for (String path: args) {
            Boolean isValidFileType = this.validateFileType(path);
            
            if(isValidFileType) {
                String outputFilePath = this.getHackFilePath(path);
                BufferedReader br = this.getBufferedReader(path);
                BufferedWriter bw = this.getBufferedWriter(outputFilePath);
                this.translateFile(br, bw);
            }  
        }
    }
}