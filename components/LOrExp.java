package components;

import java.util.Objects;

public class LOrExp implements Component {
    private TableElement value;
    private String out_label;
    private String in_label;
    public LOrExp(TableElement value,String in_label,String out_label) {
        this.value=value;
        this.out_label=out_label;
        this.in_label=in_label;
        procedure();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<LOrExp>");
        }
    }

    @Override
    public void procedure() {
        String out_or_cond_label=MiddleCode.get_new_label();
        MiddleCode.branch_not_equal(new LAndExp(value,in_label,out_or_cond_label).getValue().getName(), MiddleCode.setZero(), in_label);
        MiddleCode.insert_name_label(out_or_cond_label);
        SymbolTable.LEVEL_IN(true);
        strPrint();
        if (Objects.equals(Parser.getCurName(), "||")) {
            String op = Parser.getCurName();
            Parser.next();
            value.cond_add_element(null,op);
            new LOrExp(value,in_label,out_label);
        }
        SymbolTable.LEVEL_OUT(true);
    }

    public TableElement getValue() {
        return value;
    }

    public static boolean has() {
        return LAndExp.has();
    }
}
