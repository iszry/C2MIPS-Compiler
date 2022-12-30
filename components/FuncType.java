package components;

import java.util.Objects;

public class FuncType implements Component {
    private final String type;

    public FuncType() {
        this.type = Parser.getCurName();
        procedure();
        strPrint();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<FuncType>");
        }
    }

    // FuncType -> 'void' | 'int'
    @Override
    public void procedure() {
        Parser.next();
    }

    public String getType() {
        return type;
    }

    public static boolean has() {
        return Objects.equals(Parser.getCurName(), "void") || Objects.equals(Parser.getCurName(), "int");
    }
}
