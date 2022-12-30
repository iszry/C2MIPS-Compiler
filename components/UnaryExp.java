package components;

import java.util.Objects;

public class UnaryExp implements Component {
    private TableElement value;

    public UnaryExp(TableElement value) {
        this.value = value;
        procedure();
        strPrint();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<UnaryExp>");
        }
    }

    // UnaryExp -> PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
    @Override
    public void procedure() {
        if (Ident.has() && Objects.equals(Parser.getNextName(), "(")) {
            int error_line_id = Parser.getCurLineId(), para_cnt = 0;
            TableElement primaryExp = new Ident().getValue();
            Parser.next();
            if (FuncRParams.has()) {
                para_cnt = new FuncRParams(primaryExp).getPara_cnt();
            }
            if (Parser.ERROR_CHECK && SymbolTable.SYMBOL_DEFINED) {
                if (primaryExp.func_check_para_count() != para_cnt) {
                    System.out.println(error_line_id + " d");
                } else if (!primaryExp.func_check_para_type()) {
                    System.out.println(error_line_id + " e");
                }
            }
            for(int pos=primaryExp.getInitList().size()-1,j=primaryExp.func_check_para_count()-1;j>=0;j--){
                TableElement para=primaryExp.getInitList().get(pos-j);
                MiddleCode.func_real_para(para.getName(), para.isPtr());
            }
            MiddleCode.jump_to_function(primaryExp.getFunc_label());
            new TableElement(SymbolTable.getTmpName());
            TableElement ret=new TableElement(SymbolTable.getTmpName());
            MiddleCode.return_ra(ret.getName());
            value.expr_add_element(ret,null);
            primaryExp.func_R_para_reset();
            check_right_little_bracket();
        } else if (UnaryOp.has()) {
            String op = new UnaryOp().getValue();
            TableElement exp = new UnaryExp(new TableElement(SymbolTable.getTmpName())).getValue();
            MiddleCode.single_op(exp, exp.expr2value(), op);
            value.expr_add_element(exp,null);
        } else if (PrimaryExp.has()) {
            new PrimaryExp(value);
        }
    }

    public static boolean has() {
        return PrimaryExp.has() || Ident.has() || UnaryOp.has();
    }

    public TableElement getValue() {
        return value;
    }

    public void check_right_little_bracket() {
        if (Parser.ERROR_CHECK) {
            int error_line_id = Parser.getPosLineId(-1);
            if (!Objects.equals(Parser.getCurName(), ")")) {
                System.out.println(error_line_id + " j");
            } else {
                Parser.next();
            }
        } else {
            Parser.next();
        }
    }
}
