package components;

import java.util.Objects;

public class Block implements Component{
    public Block(){
        procedure();
        strPrint();
    }

    @Override
    public void strPrint() {
        if(Parser.TREE_PRINT_SWITCH){
            System.out.println("<Block>");
        }
    }

    //Block -> '{' { BlockItem } '}'
    @Override
    public void procedure() {
        Parser.next();
        SymbolTable.LEVEL_IN(true);
        while(BlockItem.has()){
            new BlockItem();
        }
        SymbolTable.LEVEL_OUT(true);
        Parser.next();
    }

    public static boolean has(){
        return Objects.equals(Parser.getCurName(), "{");
    }
}
