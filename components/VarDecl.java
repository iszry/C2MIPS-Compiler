package components;

import java.util.Objects;

public class VarDecl implements Component{
    public VarDecl(){
        procedure();
        strPrint();
    }
    @Override
    public void strPrint() {
        if(Parser.TREE_PRINT_SWITCH){
            System.out.println("<VarDecl>");
        }
    }

    // VarDecl -> BType VarDef { ',' VarDef } ';'
    @Override
    public void procedure() {
        String type=new BType().getType();
        new VarDef(type);
        while(Objects.equals(Parser.getCurName(), ",")){
            Parser.next();
            new VarDef(type);
        }
        check_semicolon();
    }

    public void check_semicolon() {
        if (Parser.ERROR_CHECK) {
            int error_line_id = Parser.getPosLineId(-1);
            if (!Objects.equals(Parser.getCurName(), ";")) {
                System.out.println(error_line_id + " i");
            }else{
                Parser.next();
            }
        }else{
            Parser.next();
        }
    }

    public static boolean has(){
        return BType.has();
    }
}
