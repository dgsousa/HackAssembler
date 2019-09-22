import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class Translator {
    int symbolCounter = 0;
    int varAddress = 16;
    HashMap<String, String> symbolTable = new HashMap<String, String>() {{
        put("SP", "0");
        put("LCL", "1");
        put("ARG", "2");
        put("THIS", "3");
        put("THAT", "4");
        put("R0", "0");
        put("R1", "1");
        put("R2", "2");
        put("R3", "3");
        put("R4", "4");
        put("R5", "5");
        put("R6", "6");
        put("R7", "7");
        put("R8", "8");
        put("R9", "9");
        put("R10", "10");
        put("R11", "11");
        put("R12", "12");
        put("R13", "13");
        put("R14", "14");
        put("R15", "15");
        put("SCREEN", "16384");
        put("KBD", "24576");
    }};

    HashMap<String, Integer> varTable = new HashMap<String, Integer>();

    HashMap<String, String> compTable = new HashMap<String, String>() {{
        put("0", "0101010");
        put("1", "0111111");
        put("-1", "0111010");
        put("D", "0001100");
        put("A", "0110000");
        put("!D", "0001101");
        put("!A", "0110001");
        put("-D", "0001111");
        put("-A", "0110011");
        put("D+1", "0011111");
        put("A+1", "0110111");
        put("D-1", "0001110");
        put("A-1", "0110010");
        put("D+A", "0000010");
        put("D-A", "0010011");
        put("A-D", "0000111");
        put("D&A", "0000000");
        put("D|A", "0010101");
        put("M", "1110000");
        put("!M", "1110001");
        put("-M", "1110011");
        put("M+1", "1110111");
        put("M-1", "1110010");
        put("D+M", "1000010");
        put("D-M", "1010011");
        put("M-D", "1000111");
        put("D&M", "1000000");
        put("D|M", "1010101");
    }};

    HashMap<String, String> destTable = new HashMap<String, String>() {{
        put("M", "001");
        put("D", "010");
        put("MD", "011");
        put("A", "100");
        put("AM", "101");
        put("AD", "110");
        put("AMD", "111");
    }};

    HashMap<String, String> jumpTable = new HashMap<String, String>() {{
        put("JGT", "001");
        put("JEQ", "010");
        put("JGE", "011");
        put("JLT", "100");
        put("JNE", "101");
        put("JLE", "110");
        put("JMP", "111");
    }};

    public boolean isNotSymbol(String line) {
        Boolean isSymbol = line.charAt(0) == '(' && line.charAt(line.length() - 1)  == ')';
        if(isSymbol) {
            String symbol = line.substring(1, line.length() - 1);
            symbolTable.put(symbol, String.valueOf(symbolCounter));
        } else {
            symbolCounter++;
        }
        return !isSymbol;
    };

    public List<String> removeSymbols(List<String> contents) {
        List<String> formattedContents = contents
            .stream()
            .filter(line -> isNotSymbol(line))
            .collect(Collectors.toList());
        return formattedContents;
    };

    public String parseSymbolOrVariable(String line) {
        String varString = line.substring(1);
        if(isNumeric(varString)) {
            return convertToBinaryString(Double.parseDouble(varString));
        } else if(symbolTable.containsKey(varString)) {
            return convertToBinaryString(Double.valueOf(symbolTable.get(varString)));
        } else if(varTable.containsKey(varString)) {
            return convertToBinaryString(Double.valueOf(varTable.get(varString)));
        } else {
            String address = convertToBinaryString(Double.valueOf(varAddress));
            varTable.put(varString, varAddress);
            varAddress++;
            return address;
        }
    }

    public Boolean isNumeric(String val) {
        try {
            Integer.parseInt(val);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    

    public String convertToBinaryString(Double num) {
        Double remainder = num;
        Double currentDigit = Math.pow(2, 15);
        String binaryString = "";
        for(int i = 0; i < 16; i++) {
            currentDigit = Math.pow(2, (15 - i));
            if(remainder >= currentDigit) {
                binaryString = binaryString + "1";
                remainder = remainder - currentDigit;
            } else {
                binaryString = binaryString + "0";
            }
        }
        return binaryString;
    }

    public List<String> translateToBinary(List<String> contents) {
        List<String> binaryContents = contents
            .stream()
            .map(line -> line.charAt(0) == '@' ? parseSymbolOrVariable(line) : parseCommand(line))
            .collect(Collectors.toList());
        return binaryContents;
    }

    public List<String> translate(List<String> contents) {
        List<String> contentsWithSymbolsRemoved = removeSymbols(contents);
        List<String> binaryContents = translateToBinary(contentsWithSymbolsRemoved); 
        return binaryContents;
    }

    public String parseCommand(String cmd) {
        String dest = "";
        String jump = "";
        String comp = "";
        if(cmd.contains("=")) {
            String[] cmdArr = cmd.split("=");
            dest = cmdArr[0];
            comp = cmdArr[1];
        } else if(cmd.contains(";")) {
            String[] cmdArr = cmd.split(";");
            comp = cmdArr[0];
            jump = cmdArr[1];
        }
        return String.join("",
            "111",
            compTable.get(comp),
            destTable.getOrDefault(dest, "000"),
            jumpTable.getOrDefault(jump, "000")
        );
    };
}