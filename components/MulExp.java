package components;

import java.util.Objects;

public class MulExp implements Component {

    private TableElement value;

    public MulExp(TableElement value) {
        this.value=value;
        procedure();
        strPrint();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<MulExp>");
        }
    }

    // MulExp -> UnaryExp |  UnaryExp ('*' | '/' | '%' ) MulExp
    @Override
    public void procedure() {
        new UnaryExp(value);
        strPrint();
        if (Objects.equals(Parser.getCurName(), "*") ||
                Objects.equals(Parser.getCurName(), "/") ||
                Objects.equals(Parser.getCurName(), "%")) {
            String op = Parser.getCurName();
            Parser.next();
            value.expr_add_element(null,op);
            new MulExp(value);
        }
    }

    public static boolean has() {
        return UnaryExp.has();
    }

    public TableElement getValue(){
        return value;
    }
}
