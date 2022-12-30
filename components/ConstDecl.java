package components;

import java.util.Objects;

public class ConstDecl implements Component {
    public ConstDecl() {
        procedure();
        strPrint();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<ConstDecl>");
        }
    }

    // ConstDecl -> 'const' BType ConstDef {',' ConstDef}';'
    @Override
    public void procedure() {
        Parser.next();
        String type=new BType().getType();
        new ConstDef(type);
        while(Objects.equals(Parser.getCurName(), ",")){
            Parser.next();
            new ConstDef(type);
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

    public static boolean has() {
        return Objects.equals(Parser.getCurName(), "const");
    }
}
