import components.Parser;
import components.Token;
import generator.MipsGenerator;

import java.io.*;
import java.util.ArrayList;

public class Compiler {
    public static void main(String[] args) throws IOException {
        String textUrl = "testfile.txt";
        File file = new File(textUrl);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line;
        StringBuilder text = new StringBuilder("");
        while (true) {
            try {
                if (null == (line = bufferedReader.readLine())) {
                    break;
                } else {
                    text.append(line);
                    text.append("\n");
                }
            } catch (IOException e) {
                System.out.println("----> Can't open the file! <----\n");
                e.printStackTrace();
            }
        }
        Lexer lexer = new Lexer(text.toString());
        Parser parser=new Parser(lexer.getTokens(),lexer.getReservedWords());
        MipsGenerator mipsGenerator=new MipsGenerator(parser.getMiddleCode());
        //testLexerOutput(lexer);
        //testLexerFunction(lexer.getTokens());
    }

    private static void testLexerOutput(Lexer lexer) {
        String outUrl = "output.txt";
        File file = new File(outUrl);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(outUrl, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PrintWriter printWriter = new PrintWriter(fileWriter);
        for (Token tk : lexer.getTokens()) {
            printWriter.println(tk.toString());
            printWriter.flush();
        }
        printWriter.close();
    }
    
    private static void testLexerFunction(ArrayList<Token> tokens) {
        int line_cnt = 1;
        for (Token tk : tokens) {
            while (line_cnt < tk.getLine_id()) {
                System.out.print("\n");
                line_cnt++;
            }
            System.out.print(tk.getName()+" ");
        }
    }
}