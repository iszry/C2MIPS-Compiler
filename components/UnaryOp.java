package components;

import java.util.Objects;

public class UnaryOp implements Component {
    private final String op;
    public UnaryOp() {
        op=Parser.getCurName();
        procedure();
        strPrint();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<UnaryOp>");
        }
    }

    @Override
    public void procedure() {
        Parser.next();
    }

    public String getValue() {
        return op;
    }

    public static boolean has() {
        return Objects.equals(Parser.getCurName(), "+") ||
                Objects.equals(Parser.getCurName(), "-") ||
                Objects.equals(Parser.getCurName(), "!");
    }
}
