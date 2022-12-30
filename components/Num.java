package components;

import java.util.Objects;
import java.util.regex.Pattern;

public class Num implements Component {
    private final int number;
    public Num() {
        if(Objects.equals(Parser.getCurName(), "2147483648")){
            number=-2147483648;
        }else{
            number=Integer.parseInt(Objects.requireNonNull(Parser.getCurName())) ;
        }
        procedure();
        strPrint();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<Number>");
        }
    }

    @Override
    public void procedure() {
        Parser.next();
    }

    public int getValue() {
        return number;
    }

    public static boolean has() {
        return Objects.requireNonNull(Parser.getCurName()).matches("^[0-9]*$");
    }
}
