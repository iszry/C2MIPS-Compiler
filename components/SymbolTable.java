package components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SymbolTable {
    public static ArrayList<HashMap<String, TableElement>> symbolTable = new ArrayList<>();
    public static int CUR_LEVEL = 0;

    public static boolean SYMBOL_DEFINED = false;
    public static int TMP_CNT = 0;

    public static int ARRAY_POINTER_CNT = 0;

    public SymbolTable() {
        symbolTable.add(new HashMap<>());
    }

    public static int LEVEL_IN(boolean setBlock) {
        if(symbolTable.size()>=CUR_LEVEL)
            symbolTable.add(new HashMap<>());
        if (setBlock)
            MiddleCode.in_block();
        return (CUR_LEVEL += 1);
    }

    public static int LEVEL_OUT(boolean setBlock) {
        symbolTable.remove(CUR_LEVEL);
        if (setBlock)
            MiddleCode.out_block();
        return (CUR_LEVEL -= 1);
    }

    public static boolean SymbolFind(String name) {
        return symbolTable.get(CUR_LEVEL).containsKey(name);
    }

    /* sp_code
     * 1: Insert new element using name
     * 2: rename the value as name and insert it
     * Others: search the element using name
     */
    public static TableElement SymbolGet(String name, int sp_code, TableElement value) {
        SYMBOL_DEFINED = true;
        if (sp_code == 1) {
            InsertSymbol(new TableElement(name));
        } else if (sp_code == 2) {
            //rename and insert
            value.rename(name);
            InsertSymbol(value);
        }else if(sp_code==3){
            //func_para
            symbolTable.add(new HashMap<>());
            TableElement newElement=new TableElement(name);
            symbolTable.get(CUR_LEVEL+1).put(name, newElement);
            return newElement;
        }
        for (int level = CUR_LEVEL; level >= 0; level--) {
            if (symbolTable.get(level).containsKey(name)) {
                return symbolTable.get(level).get(name);
            }
        }
        if (Parser.ERROR_CHECK) {
            System.out.println(Parser.getCurLineId() + " c");
        }
        SYMBOL_DEFINED = false;
        return new TableElement(getTmpName(), 1);
    }

    public static void InsertSymbol(TableElement tableElement) {
        if (Parser.ERROR_CHECK && symbolTable.get(CUR_LEVEL).containsKey(tableElement.getName())) {
            //duplicated identity
            System.out.println(Parser.getCurLineId() + " b");
        }
        symbolTable.get(CUR_LEVEL).put(tableElement.getName(), tableElement);
    }

    public static String getTmpName() {
        TMP_CNT += 1;
        MiddleCode.decl_tmp_val("$tmp_val_" + TMP_CNT);
        return "$tmp_val_" + TMP_CNT;
    }

    public static String createArrayPointer() {
        ARRAY_POINTER_CNT += 1;
        return "$tmp_arr_" + ARRAY_POINTER_CNT;
    }

    public static void findPointerPos(String dest, String quote, String offset){
        MiddleCode.decl_array_pointer(dest, quote,offset);
    }
}
