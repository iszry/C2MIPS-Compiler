package components;

import java.util.Objects;

public class RelExp implements Component {
    private TableElement value;

    public RelExp(TableElement value) {
        this.value = value;
        procedure();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<RelExp>");
        }
    }

    @Override
    public void procedure() {
        value.cond_add_element(new AddExp(new TableElement(SymbolTable.getTmpName())).getValue().expr2value(), null);
        strPrint();
        if (Objects.equals(Parser.getCurName(), "<=") ||
                Objects.equals(Parser.getCurName(), ">=") ||
                Objects.equals(Parser.getCurName(), ">") ||
                Objects.equals(Parser.getCurName(), "<")) {
            String op = Parser.getCurName();
            Parser.next();
            value.cond_add_element(null, op);
            new RelExp(value);
        }
    }

    public TableElement getValue() {
        return value;
    }

    public static boolean has() {
        return AddExp.has();
    }
}
