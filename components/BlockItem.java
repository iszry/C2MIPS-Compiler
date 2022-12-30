package components;

import java.util.Objects;

public class BlockItem implements Component {
    public BlockItem() {
        procedure();
        strPrint();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            //System.out.println("<BlockItem>");
        }
    }

    @Override
    public void procedure() {
        if (Decl.has()) {
            new Decl();
        } else if (Stmt.has()) {
            new Stmt();
        }
    }

    public static boolean has() {
        return Decl.has() || Stmt.has();
    }
}
