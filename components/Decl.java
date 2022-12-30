package components;

public class Decl implements Component{
    public Decl(){
        procedure();
        strPrint();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            //System.out.println("<Decl>");
        }
    }

    // Decl -> ConstDecl | VarDecl
    @Override
    public void procedure() {
        if(ConstDecl.has()){
            new ConstDecl();
        }else if(VarDecl.has()){
            new VarDecl();
        }
    }

    public static boolean has() {
        return ConstDecl.has()||VarDecl.has();
    }
}
