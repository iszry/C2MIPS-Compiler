package components;

import java.util.Objects;

public class FuncFParams implements Component {
    private TableElement leftValue;
    public FuncFParams(TableElement leftValue) {
        this.leftValue=leftValue;
        procedure();
        strPrint();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<FuncFParams>");
        }
    }

    // FuncFParams -> FuncFParam { ',' FuncFParam }
    @Override
    public void procedure() {
        leftValue.func_add_para(new FuncFParam().getValue());
        while (Objects.equals(Parser.getCurName(), ",")) {
            Parser.next();
            leftValue.func_add_para(new FuncFParam().getValue());
        }
    }

    public static boolean has() {
        return FuncFParam.has();
    }
}
