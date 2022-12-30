package components;

import java.util.Objects;

public class FuncRParams implements Component {
    private TableElement func;
    private int para_cnt = 0;

    public FuncRParams(TableElement func) {
        this.func = func;
        procedure();
        strPrint();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<FuncRParams>");
        }
    }

    // FuncRParams -> Exp { ',' Exp }
    @Override
    public void procedure() {
        func.add_initList(new Exp().getValue());
        para_cnt++;
        while (Objects.equals(Parser.getCurName(), ",")) {
            Parser.next();
            func.add_initList(new Exp().getValue());
            para_cnt++;
        }
    }

    public int getPara_cnt() {
        return para_cnt;
    }

    public static boolean has() {
        return Exp.has();
    }
}
