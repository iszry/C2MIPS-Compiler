package components;

public class Ident implements Component {
    private String name;
    private TableElement value;

    public Ident() {
        value = SymbolTable.SymbolGet(Parser.getCurName(), 0, null);
        this.name = value.getName();
        procedure();
        strPrint();
    }

    public Ident(int get_or_insert) {
        if (get_or_insert == 0) {
            this.name = Parser.getCurName();
            procedure();
            strPrint();
        } else {
            name = Parser.getCurName();
            value = SymbolTable.SymbolGet(this.name, get_or_insert, null);
            procedure();
            strPrint();
        }
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            //System.out.println("<Ident>");
        }
    }


    @Override
    public void procedure() {
        Parser.next();
    }

    public String getName() {
        return name;
    }

    public TableElement getValue() {
        return value;
    }

    public static boolean has() {
        return Parser.IdentityName.contains(Parser.getCurName());
    }
}
