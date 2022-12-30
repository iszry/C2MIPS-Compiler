package components;

import java.util.Objects;

public class CompUnit implements Component {
    public CompUnit() {
        procedure();
        strPrint();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<CompUnit>");
        }
    }

    // CompUnit->{Decl}{FuncDef}MainFuncDef
    @Override
    public void procedure() {
        boolean DeclFinished = false; //for error processing
        //Decl and FuncDef
        while ((Decl.has() || FuncDef.has()) && !Objects.equals(Parser.getNextName(), "main")) {
            if (Objects.equals(Parser.getNext2Name(), "(")) {
                if(!DeclFinished){
                    MiddleCode.end_var_def();
                }
                new FuncDef();
                DeclFinished = true;
            } else {
                new Decl();
            }
        }
        if(!DeclFinished){
            MiddleCode.end_var_def();
        }
        MiddleCode.end_func_def();
        //MainFuncDef
        new MainFuncDef();
    }

    public static boolean has() {
        return Decl.has() || FuncDef.has() || MainFuncDef.has();
    }
}
