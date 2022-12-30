package components;

import java.util.Objects;

public class FuncFParam implements Component {
    private TableElement leftValue;
    public FuncFParam() {
        procedure();
        strPrint();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<FuncFParam>");
        }
    }

    // FuncFParam -> BType Ident [ '[' ']' { '[' ConstExp ']' }]
    @Override
    public void procedure() {
        String type= new BType().getType();
        leftValue=new Ident(3).getValue();
        leftValue.setType(type);
        leftValue.setVar(true);
        if (Objects.equals(Parser.getCurName(), "[")) {
            leftValue.setVar(false);
            leftValue.setArray(true);
            leftValue.array_add_dimension(new TableElement(SymbolTable.getTmpName(),0));
            Parser.next();
            check_right_middle_bracket();
            if (Objects.equals(Parser.getCurName(), "[")) {
                Parser.next();
                leftValue.array_add_dimension(new ConstExp().getValue());
                check_right_middle_bracket();
            }
        }
        if(leftValue.isArray()){
            leftValue.cal_dim_info();
        }
    }

    public void check_right_middle_bracket(){
        if (Parser.ERROR_CHECK) {
            int error_line_id = Parser.getPosLineId(-1);
            if (!Objects.equals(Parser.getCurName(), "]")) {
                System.out.println(error_line_id + " k");
            }else{
                Parser.next();
            }
        }else{
            Parser.next();
        }
    }

    public TableElement getValue() {
        return leftValue;
    }

    public static boolean has() {
        return BType.has();
    }
}
