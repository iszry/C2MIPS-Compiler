package components;

import java.util.Objects;

public class LVal implements Component {
    private TableElement value;

    public LVal() {
        procedure();
        strPrint();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<LVal>");
        }
    }

    // LVal -> Ident { '[' Exp ']' }
    @Override
    public void procedure() {
        value =new Ident().getValue();
        TableElement offset = null;
        int dim_cnt = 0;
        while (Objects.equals(Parser.getCurName(), "[")) {
            if (offset==null) {
                offset = new TableElement(SymbolTable.getTmpName());
                offset.setConst(true);
                MiddleCode.load_number_li(offset.getName(), 0);
            }
            Parser.next();
            TableElement dim_value = new Exp().getValue();
            MiddleCode.cal_array_dim(offset, dim_value, value, dim_cnt++);
            value.setVarDim(value.getVarDim() - 1);
            check_right_middle_bracket();
        }
        if (value.isArray()&&offset!=null){
            if(value.isConst()&&offset.isConst()){
                int numValue= value.findConstArray(offset.getNumberValue());
                TableElement num=new TableElement(SymbolTable.getTmpName());
                num.setNumberValue(numValue);
                num.setValue(true);
                num.setConst(true);
                MiddleCode.load_number_li(num.getName(),numValue);
                value=num;
            }else{
                TableElement ptr=new TableElement(SymbolTable.createArrayPointer());
                ptr.setArray(true);
                ptr.setQuoteDim(dim_cnt);
                ptr.setQuote_name(value);
                SymbolTable.findPointerPos(ptr.getName(),value.getName(),offset.getName());
                ptr.setArrayOffset(offset);
                value=ptr;
            }
        }else if(!value.isArray()){

        }
    }

    public static boolean has() {
        return Ident.has();
    }

    public TableElement getValue() {
        return value;
    }

    public static String follow() {
        int cnt = 0, pos = Parser.tk_pos + 1;
        while (cnt > 0 || Objects.equals(Parser.tokens.get(pos).getName(), "[")) {
            if (pos >= Parser.tokens.size()) {
                pos = Parser.tk_pos - 1;
                break;
            }
            if (Objects.equals(Parser.tokens.get(pos).getName(), "=") || Objects.equals(Parser.tokens.get(pos).getName(), ";")) {
                break;
            }
            if (Objects.equals(Parser.tokens.get(pos).getName(), "[")) {
                cnt += 1;
            } else if (Objects.equals(Parser.tokens.get(pos).getName(), "]")) {
                cnt -= 1;
            }
            pos += 1;
        }
        return Parser.tokens.get(pos).getName();
    }

    public void check_right_middle_bracket() {
        if (Parser.ERROR_CHECK) {
            int error_line_id = Parser.getPosLineId(-1);
            if (!Objects.equals(Parser.getCurName(), "]")) {
                System.out.println(error_line_id + " k");
            } else {
                Parser.next();
            }
        } else {
            Parser.next();
        }
    }
}
