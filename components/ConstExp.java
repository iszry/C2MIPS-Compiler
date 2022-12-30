package components;

public class ConstExp implements Component {
    private TableElement value;

    public ConstExp() {
        this.value = new TableElement(SymbolTable.getTmpName());
        procedure();
        strPrint();
        this.value=value.expr2value();
        this.value=value.getConstValue();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<ConstExp>");
        }
    }

    // ConstExp -> AddExp
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
