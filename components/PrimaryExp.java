package components;

import java.util.Objects;

public class PrimaryExp implements Component {
    private TableElement value;

    public PrimaryExp(TableElement value) {
        this.value = value;
        procedure();
        strPrint();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<PrimaryExp>");
        }
    }

    @Override
    public void procedure() {
        if (Objects.equals(Parser.getCurName(), "(")) {
            Parser.next();
            value.expr_add_element(new Exp().getValue(), null);
            Parser.next();
        } else if (LVal.has()) {

            TableElement ptr=new LVal().getValue();
            if(ptr.isArray()){
                value.expr_add_element(ptr, null);
            }else{
                TableElement tmp_lval=new TableElement(SymbolTable.getTmpName());
                MiddleCode.instr_move(tmp_lval,ptr);
                value.expr_add_element(tmp_lval, null);
            }
        } else if (Num.has()) {
            TableElement num = new TableElement(SymbolTable.getTmpName());
            int numberValue=new Num().getValue();
            MiddleCode.load_number_li(num.getName(), numberValue);
            num.setValue(true);
            num.setNumberValue(numberValue);
            value.expr_add_element(num, null);
        }
    }

    public TableElement getValue() {
        return value;
    }

    public static boolean has() {
        return Objects.equals(Parser.getCurName(), "(") || LVal.has() || Num.has();
    }
}
