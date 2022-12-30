package components;

public class Exp implements Component {
    private TableElement value;

    public Exp() {
        this.value = new TableElement(SymbolTable.getTmpName());
        procedure();
        strPrint();
        this.value=value.expr2value();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<Exp>");
        }
    }

    @Override
    public void procedure() {
        new AddExp(value);
    }

    public TableElement getValue() {
        return value;
    }

    public static boolean has() {
        return AddExp.has();
    }
}
