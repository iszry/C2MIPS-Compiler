package components;

import java.util.ArrayList;
import java.util.Objects;

public class FormatString implements Component {
    private String formatString;
    private int data_cnt = 0;
    private ArrayList<TableElement> paras = new ArrayList<>();

    public FormatString() {
        if (Parser.ERROR_CHECK) {
            if (!ascii_check(Objects.requireNonNull(Parser.getCurName()))) {
                System.out.println(Parser.getCurLineId() + " a");
            }
        }
        this.formatString =Parser.getCurName();
        procedure();
        strPrint();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            //System.out.println("<FormatString>");
        }
    }

    @Override
    public void procedure() {
        Parser.next();
    }

    public static boolean has() {
        return true;
    }

    public boolean ascii_check(String str) {
        int quote_cnt = 0;
        boolean isBackSlash = false;
        boolean isPercent = false;
        for (char c : str.toCharArray()) {
            if (c == '\"') {
                quote_cnt++;
                continue;
            }
            if (isBackSlash) {
                if (c != 'n') {
                    return false;
                } else {
                    isBackSlash = false;
                }
            }
            if (c == '\\') {
                isBackSlash = true;
            }
            if (isPercent) {
                if (c != 'd') {
                    return false;
                } else {
                    isPercent = false;
                    data_cnt++;
                }
            }
            if (c == '%') {
                isPercent = true;
            }
            if (!((int) c <= 126 && (int) c >= 40 || (int) c == 32 || (int) c == 33 || (int) c == 37)) {
                return false;
            }
        }
        return quote_cnt == 2 && !isBackSlash && !isPercent;
    }

    public int get_data_cnt() {
        return data_cnt;
    }

    public void add_para(TableElement para) {
        paras.add(para);
    }

    public ArrayList<TableElement> getParas() {
        return paras;
    }

    public void output_middle_code() {
        int cnt = 0;
        String[] str_list = formatString.replace("%d","\"%d\"").split("%d");
        MiddleCode.output_str(str_list[cnt]);
        for (cnt++; cnt < str_list.length; cnt++) {
            MiddleCode.output_num(paras.get(cnt - 1).getName());
            MiddleCode.output_str(str_list[cnt]);
        }
    }
}
