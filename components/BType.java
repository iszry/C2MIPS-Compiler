package components;

import java.util.Objects;

public class BType implements Component{

    public BType(){
        procedure();
        strPrint();
    }
    @Override
    public void strPrint() {
        if(Parser.TREE_PRINT_SWITCH){
            //System.out.println("<BType>");
        }
    }

    // BType -> 'int'
    @Override
    public void procedure() {
        if(Objects.equals(Parser.getCurName(), "int")){
            Parser.next();
        }
    }

    public String getType(){
        return "int";
    }

    public static boolean has(){
        return Objects.equals(Parser.getCurName(), "int");
    }
}
