package components;

import java.util.Objects;

public class ConstInitVal implements Component {
    private TableElement leftValue;
    public ConstInitVal(TableElement leftValue) {
        this.leftValue=leftValue;
        procedure();
        strPrint();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<ConstInitVal>");
        }
    }

    // ConstInitVal -> ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
    @Override
    public void procedure() {
        if(ConstExp.has()){
            leftValue.add_initList(new ConstExp().getValue());
        }else{
            Parser.next();
            if(ConstInitVal.has()){
                new ConstInitVal(leftValue);
                while(Objects.equals(Parser.getCurName(), ",")){
                    Parser.next();
                    new ConstInitVal(leftValue);
                }
            }
            Parser.next();
        }
    }


    public static boolean has() {
        return ConstExp.has() || Objects.equals(Parser.getCurName(), "{");
    }
}
