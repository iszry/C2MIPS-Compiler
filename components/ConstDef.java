package components;

import java.util.Objects;

public class ConstDef implements Component {

    private final String type;

    public ConstDef(String type) {
        this.type = type;
        procedure();
        strPrint();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<ConstDef>");
        }
    }

    // ConstDef -> Ident{'['ConstExp']'}'='ConstInitVal
    @Override
    public void procedure() {
        String identity = new Ident(0).getName();
        TableElement leftValue = new TableElement(identity);
        leftValue.setType(type);
        leftValue.setConst(true);
        leftValue.setValue(true);
        while (Objects.equals(Parser.getCurName(), "[")) {
            leftValue.setArray(true);
            leftValue.setQuote_name(leftValue);
            Parser.next();
            leftValue.array_add_dimension(new ConstExp().getValue());
            check_right_middle_bracket();
        }
        Parser.next();
        new ConstInitVal(leftValue);
        SymbolTable.SymbolGet(identity, 2, leftValue);
        MiddleCode.initialize_identity(leftValue);
    }

    public void check_right_middle_bracket() {
        if (Parser.ERROR_CHECK) {
            int error_line_id = Parser.getPosLineId(-1);
            if (!Objects.equals(Parser.getCurName(), "]")) {
                System.out.println(error_line_id + " k");
            } else {
                Parser.next();
            }
        }else{
            Parser.next();
        }
    }

    public static boolean has() {
        return Ident.has();
    }
}
