package components;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Parser {

    public static int tk_pos = 0;
    public static boolean ERROR_CHECK = false;
    public static boolean TREE_PRINT_SWITCH = false;

    public static boolean IN_LOOP = false;
    public static boolean IN_FUNCTION = false;
    public static String FUNC_TYPE = "void";

    public static boolean HAS_RETURN = false;
    public static ArrayList<Token> tokens = null;
    public static ArrayList<String> IdentityName = new ArrayList<>();

    public Parser(ArrayList<Token> tokens, HashMap<String, String> reservedWords) throws FileNotFoundException {

        /**/
        PrintStream ps = new PrintStream(new FileOutputStream("error.txt"));
        System.setOut(ps);
        new SymbolTable();
        new MiddleCode();

        Parser.tokens = tokens;
        fillIdentity();
        new CompUnit();
        //MiddleCode.middle_code_print();
    }

    public String getMiddleCode(){
        return MiddleCode.getCode();
    }

    private void fillIdentity() {
        for (Token tk : tokens) {
            if (Objects.equals(tk.getKind(), "IDENFR")) {
                IdentityName.add(tk.getName());
            }
        }
    }

    public static int getPosLineId(int pos) {
        if (tk_pos + pos < 0 || tk_pos + pos >= tokens.size()) {
            return 0;
        }
        return tokens.get(tk_pos + pos).getLine_id();
    }

    public static Token getCurToken() {
        return tk_pos < tokens.size() ? tokens.get(tk_pos) : null;
    }

    public static Token getNextToken() {
        return tk_pos + 1 < tokens.size() ? tokens.get(tk_pos + 1) : null;
    }

    public static Token getNext2Token() {
        return tk_pos + 2 < tokens.size() ? tokens.get(tk_pos + 2) : null;
    }

    public static String getCurName() {
        return getCurToken() != null ? getCurToken().getName() : null;
    }

    public static String getNextName() {
        return getNextToken() != null ? getNextToken().getName() : null;
    }

    public static String getNext2Name() {
        return getNext2Token() != null ? getNext2Token().getName() : null;
    }

    public static void next() {
        if (TREE_PRINT_SWITCH)
            System.out.println(Objects.requireNonNull(getCurToken()).getKind() + " " + getCurName());
        tk_pos += 1;
    }

    public static int getCurLineId() {
        return Objects.requireNonNull(getCurToken()).getLine_id();
    }
}
