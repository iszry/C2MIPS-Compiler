package components;

public class Cond implements Component{
    private TableElement value;
    private String out_label;
    private String in_label;
    public Cond(String in_label,String out_label){
        value=new TableElement(SymbolTable.getTmpName());
        this.in_label=in_label;
        this.out_label=out_label;
        procedure();
        strPrint();
        value.cond2bool();
    }

    @Override
    public void strPrint() {
        if(Parser.TREE_PRINT_SWITCH){
            System.out.println("<Cond>");
        }
    }

    @Override
    public void procedure() {
        new LOrExp(value,in_label,out_label);
    }

    public TableElement getValue() {
        return value;
    }

    public static boolean has(){
        return LOrExp.has();
    }
}
