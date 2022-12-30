package components;

import java.util.Objects;

public class FuncDef implements Component {
    public FuncDef() {
        procedure();
        strPrint();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<FuncDef>");
        }
    }

    // FuncDef ->FuncType Ident '(' [FuncFParams] ')' Block
    @Override
    public void procedure() {
        String type = new FuncType().getType();
        int func_st_pt = Parser.tk_pos;
        TableElement leftValue = new Ident(1).getValue();
        leftValue.setFunc(true);
        leftValue.setFuncReturnValue(type);
        Parser.next();
        if (FuncFParams.has()) {
            new FuncFParams(leftValue);
        }
        MiddleCode.define_function(leftValue);
        check_right_little_bracket();
        Parser.IN_FUNCTION = true;
        Parser.FUNC_TYPE = type;
        Parser.HAS_RETURN = false;
        new Block();
        Parser.IN_FUNCTION = false;
        if (Parser.ERROR_CHECK && !Objects.equals(type, "void") && !Parser.HAS_RETURN) {
            System.out.println(Parser.getPosLineId(-1) + " g");
        }
        int func_end_pt = Parser.tk_pos;
        leftValue.saveFuncPointer(func_st_pt, func_end_pt);
        MiddleCode.end_cur_func();
    }

    public void check_right_little_bracket(){
        if (Parser.ERROR_CHECK) {
            int error_line_id = Parser.getPosLineId(-1);
            if (!Objects.equals(Parser.getCurName(), ")")) {
                System.out.println(error_line_id + " j");
            }else{
                Parser.next();
            }
        }else{
            Parser.next();
        }
    }

    public static boolean has() {
        return FuncType.has();
    }
}
