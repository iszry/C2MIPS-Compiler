package components;

import java.util.Objects;

public class MainFuncDef implements Component {
    public MainFuncDef() {
        procedure();
        strPrint();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<MainFuncDef>");
        }
    }

    // MainFuncDef -> 'int' 'main' '(' ')' Block
    @Override
    public void procedure() {
        MiddleCode.insert_func_label("label_0_main_function");
        MiddleCode.add_line("FUNC_DEFINE#void#label_0_main_function# ");
        Parser.next();
        Parser.next();
        Parser.next();
        Parser.next();
        Parser.IN_FUNCTION = true;
        Parser.HAS_RETURN = false;
        Parser.FUNC_TYPE = "int";
        new Block();
        Parser.IN_FUNCTION = false;
        if (Parser.ERROR_CHECK && !Parser.HAS_RETURN) {
            System.out.println(Parser.getPosLineId(-1) + " g");
        }
    }

    public static boolean has() {
        return Objects.equals(Parser.getCurName(), "int");
    }
}
