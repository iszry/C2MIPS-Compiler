package components;

import java.util.Objects;

public class LAndExp implements Component {
    private TableElement value;
    private String out_label;
    private String in_label;
    public LAndExp(TableElement value, String in_label,String out_label) {
        this.value=value;
        this.in_label=in_label;
        this.out_label=out_label;
        procedure();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<LAndExp>");
        }
    }

    @Override
    public void procedure() {
        MiddleCode.branch_equal(new EqExp(value).getValue().getName(), MiddleCode.setZero(), out_label);
        SymbolTable.LEVEL_IN(true);
        strPrint();
        if (Objects.equals(Parser.getCurName(), "&&")) {
            String op = Parser.getCurName();
            Parser.next();
            value.cond_add_element(null,op);
            new LAndExp(value,in_label ,out_label);
        }
        SymbolTable.LEVEL_OUT(true);
    }

    public TableElement getValue() {
        return value;
    }

    public static boolean has() {
        return EqExp.has();
    }
}
