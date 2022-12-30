package components;

import java.util.Objects;

public class AddExp implements Component {
    private TableElement value;

    public AddExp(TableElement value) {
        this.value=value;
        procedure();
        strPrint();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<AddExp>");
        }
    }

    @Override
    public void procedure() {
        new MulExp(value);
        strPrint();
        if (Objects.equals(Parser.getCurName(), "+") ||
                Objects.equals(Parser.getCurName(), "-")) {
            String op = Parser.getCurName();
            Parser.next();
            value.expr_add_element(null,op);
            new AddExp(value);
        }
    }

    public TableElement getValue() {
        return value;
    }

    public static boolean has() {
        return MulExp.has();
    }
}
