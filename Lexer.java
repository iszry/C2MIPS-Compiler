import components.Parser;
import components.Token;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public class Lexer {
    private final String text;
    private final ArrayList<Token> tokens;

    private final HashMap<String, String> reservedWords;

    private int line_id = 1, pos = 0, text_len;

    public Lexer(String text) throws FileNotFoundException {
        this.tokens = new ArrayList<>();
        this.text = text;
        this.text_len = text.length();
        this.reservedWords = new HashMap<>();
        initialize();
        tokenSplit();
    }

    public HashMap<String, String> getReservedWords() {
        return reservedWords;
    }

    private void initialize() {
        reservedWords.put("main", "MAINTK");
        reservedWords.put("const", "CONSTTK");
        reservedWords.put("int", "INTTK");
        reservedWords.put("break", "BREAKTK");
        reservedWords.put("continue", "CONTINUETK");
        reservedWords.put("if", "IFTK");
        reservedWords.put("else", "ELSETK");
        reservedWords.put("while", "WHILETK");
        reservedWords.put("getint", "GETINTTK");
        reservedWords.put("printf", "PRINTFTK");
        reservedWords.put("return", "RETURNTK");
        reservedWords.put("void", "VOIDTK");
        reservedWords.put("!", "NOT");
        reservedWords.put("&&", "AND");
        reservedWords.put("||", "OR");
        reservedWords.put("+", "PLUS");
        reservedWords.put("-", "MINU");
        reservedWords.put("*", "MULT");
        reservedWords.put("/", "DIV");
        reservedWords.put("%", "MOD");
        reservedWords.put("<", "LSS");
        reservedWords.put("<=", "LEQ");
        reservedWords.put(">", "GRE");
        reservedWords.put(">=", "GEQ");
        reservedWords.put("==", "EQL");
        reservedWords.put("!=", "NEQ");
        reservedWords.put("=", "ASSIGN");
        reservedWords.put(";", "SEMICN");
        reservedWords.put(",", "COMMA");
        reservedWords.put("(", "LPARENT");
        reservedWords.put(")", "RPARENT");
        reservedWords.put("[", "LBRACK");
        reservedWords.put("]", "RBRACK");
        reservedWords.put("{", "LBRACE");
        reservedWords.put("}", "RBRACE");
    }

    private void tokenSplit() {
        StringBuilder curTk = new StringBuilder("");
        while (pos < text_len) {
            while (isBlank(text.charAt(pos))) {
                pos++;
                if (pos >= text_len) {
                    break;
                }
            }
            if (pos >= text_len) {
                break;
            }
            if (Character.isLetter(text.charAt(pos)) || text.charAt(pos) == '_') {
                //Identity
                while (Character.isLetterOrDigit(text.charAt(pos)) || text.charAt(pos) == '_') {
                    curTk.append(text.charAt(pos));
                    pos++;
                    if (pos >= text_len) {
                        break;
                    }
                }
                tokenProcess(curTk);
            } else if (Character.isDigit(text.charAt(pos))) {
                while (Character.isDigit(text.charAt(pos))) {
                    curTk.append(text.charAt(pos));
                    pos++;
                    if (pos >= text_len) {
                        break;
                    }
                }
                tokenProcess(curTk);
            } else if (isSingleChar(text.charAt(pos))) {
                curTk.append(text.charAt(pos));
                pos++;
                tokenProcess(curTk);
            } else if (text.charAt(pos) == '&' || text.charAt(pos) == '|') {
                curTk.append(text.charAt(pos));
                curTk.append(text.charAt(pos));
                pos += 2;
                tokenProcess(curTk);
            } else if (text.charAt(pos) == '!') {
                curTk.append(text.charAt(pos));
                pos++;
                if (text.charAt(pos) == '=') {
                    curTk.append(text.charAt(pos));
                    pos++;
                }
                tokenProcess(curTk);
            } else if (text.charAt(pos) == '=') {
                curTk.append(text.charAt(pos));
                pos++;
                if (text.charAt(pos) == '=') {
                    curTk.append(text.charAt(pos));
                    pos++;
                }
                tokenProcess(curTk);
            } else if (text.charAt(pos) == '<') {
                curTk.append(text.charAt(pos));
                pos++;
                if (text.charAt(pos) == '=') {
                    curTk.append(text.charAt(pos));
                    pos++;
                }
                tokenProcess(curTk);
            } else if (text.charAt(pos) == '>') {
                curTk.append(text.charAt(pos));
                pos++;
                if (text.charAt(pos) == '=') {
                    curTk.append(text.charAt(pos));
                    pos++;
                }
                tokenProcess(curTk);
            } else if (text.charAt(pos) == '/') {
                pos++;
                if (text.charAt(pos) == '*') {
                    //multi
                    pos++;
                    while (pos + 1 < text_len && !(text.charAt(pos) == '*' && text.charAt(pos + 1) == '/')) {
                        if (text.charAt(pos) == '\n') {
                            line_id++;
                        }
                        pos++;
                    }
                    if (text.charAt(pos) == '*' && text.charAt(pos + 1) == '/') {
                        pos += 2;
                    }
                } else if (text.charAt(pos) == '/') {
                    //single
                    pos++;
                    if (pos >= text_len) {
                        continue;
                    }
                    while (text.charAt(pos) != '\n') {
                        pos++;
                        if (pos >= text_len) {
                            break;
                        }
                    }
                    if (text.charAt(pos) == '\n') {
                        line_id++;
                        pos++;
                    }
                } else {
                    curTk.append('/');
                    tokenProcess(curTk);
                }
            } else if (text.charAt(pos) == '\"') {
                curTk.append(text.charAt(pos));
                pos++;
                while (text.charAt(pos) != '\"') {
                    curTk.append(text.charAt(pos));
                    pos++;
                }
                curTk.append(text.charAt(pos));
                pos++;
                tokenProcess(curTk);
            } else {
                System.out.println("----> Illegal Character in line" + line_id + "!->" + text.charAt(pos) + "<----\n");
            }
        }
    }

    private boolean isBlank(char c) {
        if (c == '\n') {
            line_id++;
        }
        return c == '\n' || c == '\r' || c == '\t' || c == ' ';
    }

    private boolean isSingleChar(char c) {
        boolean isBracket = (c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}');
        boolean isChar = (c == '+' || c == '-' || c == ';' || c == ',' || c == '*' || c == '%');
        return isBracket || isChar;
    }

    private void tokenProcess(StringBuilder tk) {
        String tkName = tk.toString();
        if (reservedWords.containsKey(tkName)) {
            tokens.add(new Token(tkName, reservedWords.get(tkName), line_id));
        } else {
            if (Character.isLetter(tkName.charAt(0)) || tkName.charAt(0) == '_') {
                tokens.add(new Token(tkName, "IDENFR", line_id));
            } else if (Character.isDigit(tkName.charAt(0))) {
                tokens.add(new Token(tkName, "INTCON", line_id));
            } else if (tkName.charAt(0) == '\"') {
                tokens.add(new Token(tkName, "STRCON", line_id));
            } else {
                System.out.println("----> Wrong type token:" + tkName + " in line " + line_id + " <----\n");
            }
        }
        tk.delete(0, tk.length());
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }
}
