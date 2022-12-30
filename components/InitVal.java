package components;

import java.util.Objects;

public class InitVal implements Component {
    private TableElement leftValue;
    public InitVal(TableElement leftValue) {
        this.leftValue=leftValue;
        procedure();
        strPrint();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<InitVal>");
        }
    }

    //InitVal -> Exp | '{' [ InitVal { ',' InitVal } ] '}'
    @Override
    public void procedure() {
        if (Exp.has()) {
            leftValue.add_initList(new Exp().getValue());
        } else if (Objects.equals(Parser.getCurName(), "{")) {
            Parser.next();
            if (InitVal.has()) {
                new InitVal(leftValue);
                while (Objects.equals(Parser.getCurName(), ",")) {
                    Parser.next();
                    new InitVal(leftValue);
                }
            }
            Parser.next();
        }
    }

    public static boolean has() {
        return Exp.has() || Objects.equals(Parser.getCurName(), "{");
    }
}
