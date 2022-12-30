package components;

import java.util.Objects;

public class EqExp implements Component {
    private TableElement value;
    public EqExp(TableElement value){
        this.value=value;
        procedure();
    }
    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<EqExp>");
        }
    }

    @Override
    public void procedure() {
        new RelExp(value);
        strPrint();
        if (Objects.equals(Parser.getCurName(), "==") ||
                Objects.equals(Parser.getCurName(), "!=")) {
            String op = Parser.getCurName();
            Parser.next();
            value.cond_add_element(null,op);
            new EqExp(value);
        }
    }

    public TableElement getValue() {
        value.cond2bool();
        return value;
    }

    public static boolean has() {
        return RelExp.has();
    }
}
