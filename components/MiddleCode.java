package components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Stack;

public class MiddleCode {
    private static String code = "";

    private static HashMap<String, String> const_string = new HashMap<>();

    private static final ArrayList<MiddleCodeElement> elements = new ArrayList<>();
    private static int label_cnt = 0;

    public static Stack<String> label_stack_continue = new Stack<>();
    public static Stack<String> label_stack_break = new Stack<>();

    public MiddleCode() {

    }

    public static void store_in(String dest, String source) {
        add_line("STORE#" + dest + "#" + source + "# ");
    }

    public static void instr_ptr_move(String dest, String sourse) {
        add_line("MOVE#" + dest + "#" + sourse + "#ptr");
    }
    public static void instr_move(TableElement dest, TableElement sourse) {
        if(sourse.isConst()){
            dest.setNumberValue(sourse.getNumberValue());
            dest.setConst(true);
            dest.setValue(true);
        }
        add_line("MOVE#" + dest.getName() + "#" + sourse.getName() + "# ");


    }

    public static void instr_move_i(String dest, int offset, String sourse) {
        add_line("MOVE#" + dest + "#" + offset + "#" + sourse);
    }

    public static void instr_load_i(String dest, int offset, String sourse) {
        add_line("LOAD#" + dest + "#" + offset + "#" + sourse);
    }

    public static void end_var_def(){
        add_line("END_INITIAL_VAR_DEF### ");
    }

    public static void end_cur_func(){
        add_line("END_CUR_FUNCTION### ");
    }

    public static void end_func_def(){
        add_line("END_INITIAL_FUNCTION_DEF### ");
    }


    public static void initialize_identity(TableElement e) {
        if(e.isArray()){
            add_line("DECL#array#" + e.getName() + "#" + e.array_get_space());
            e.cal_dim_info();
        }else{
            add_line("DECL#" + e.getType() + "#" + e.getName() + "#" + e.array_get_space());
        }
        if(e.isConst()&&!e.isArray()){
            e.setNumberValue(e.getInitList().get(0).getNumberValue());
        }else if(e.isConst()&&e.isArray()){
            for(TableElement constNum:e.getInitList()){
                e.addConstArrayNumber(constNum.getNumberValue());
            }
        }
        for (int i = 0; i < e.getInitList().size(); i++) {
            instr_load_i(e.getName(), -i*4, e.getInitList().get(i).getName());
        }

    }

    public static void load_number_li(String dest, int number) {
        add_line("LI#" + dest + "#" + number + "# ");
    }

    public static void three_op_cal(String dest, String op1, String op2, String opName) {
        String line = dest + "#" + op1 + "#" + op2;
        if (Objects.equals(opName, "+")) {
            add_line("ADD#" + line);
        } else if (Objects.equals(opName, "-")) {
            add_line("SUB#" + line);
        } else if (Objects.equals(opName, "*")) {
            add_line("MULT#" + line);
        } else if (Objects.equals(opName, "/")) {
            add_line("DIV#" + line);
        } else if (Objects.equals(opName, "%")) {
            add_line("MOD#" + line);
        } else if (Objects.equals(opName, "||")) {
            add_line("OR#" + line);
        } else if (Objects.equals(opName, "&&")) {
            add_line("AND#" + line);
        } else if (Objects.equals(opName, "==")) {
            add_line("EQ#" + line);
        } else if (Objects.equals(opName, "!=")) {
            add_line("NEQ#" + line);
        } else if (Objects.equals(opName, "<=")) {
            add_line("LEQ#" + line);
        } else if (Objects.equals(opName, ">=")) {
            add_line("GEQ#" + line);
        } else if (Objects.equals(opName, "<")) {
            add_line("LESS#" + line);
        } else if (Objects.equals(opName, ">")) {
            add_line("BIG#" + line);
        }else if(Objects.equals(opName, "*i")){
            add_line("MULTI#"+line);
        }
    }

    public static String getCode() {
        code = "";
        StringBuilder sb = new StringBuilder(code);
        sb.append("def#.data#\n\n");
        sb.append("%#const_data_space\n");
        sb.append("%#const_str#file_address#\"input.txt\"\n");
        for (String str_label : const_string.keySet()) {
            sb.append("%#const_str#").append(str_label).append("#").append(const_string.get(str_label)).append("\n");
        }
        sb.append("\ndef#.text#\n\n");
        for (MiddleCodeElement s : elements) {
            sb.append(s.toString()).append("\n");
        }
        code = sb.toString();
        return code;
    }

    public static void add_line(String line) {
        String[] str = line.split("#");
        elements.add(new MiddleCodeElement(str[0], str[1], str[2], str[3]));
    }

    public static void middle_code_print() {
        System.out.println(getCode());
    }

    public static String insert_anonymity_label() {
        label_cnt++;
        String label_name = "label_" + label_cnt;
        add_line("LABEL#" + label_name + "## ");
        return label_name;
    }

    public static String get_new_label() {
        label_cnt++;
        return "label_" + label_cnt;
    }

    public static String get_new_label_name(String name) {
        label_cnt++;
        return "label_" + label_cnt + "_" + name;
    }

    public static String insert_name_label(String name) {
        add_line("LABEL#" + name + "#BLOCK# ");
        return name;
    }

    public static String insert_func_label(String name) {
        add_line("LABEL#" + name + "#FUNCTION# ");
        return name;
    }

    public static void insert_blank_line(){
        add_line(" # # # ");
    }


    public static void single_op(TableElement dest, TableElement name, String op) {
        if(Objects.equals(op, "-")){
            if(name.getNumberValue()==-2147483648){
                instr_move(dest,name);
                dest.setNumberValue(-2147483648);
            }else{
                add_line("MULT_I#"+dest.getName()+"#"+name.getName()+"#-1");
                dest.setNumberValue(dest.getNumberValue()*(-1));
            }
        }else if(Objects.equals(op,"!")){
            add_line("NOT#"+dest.getName()+"#"+name.getName()+"# ");
        }else if(Objects.equals(op, "+")){
            add_line("ADDI#"+dest.getName()+"#"+name.getName()+"#0");
        }
    }

    public static void in_block() {
        add_line("BEGIN### ");
    }

    public static void out_block() {
        add_line("END### ");
    }

    public static void define_function(TableElement e) {
        String func_name = get_new_label_name(e.getName().replace("#", ""));
        insert_func_label(func_name);
        e.setFuncLabel(func_name);
        add_line("FUNC_DEFINE#" + e.getFuncReturnValue() + "#" + func_name + "# ");
        decl_tmp_val(SymbolTable.getTmpName());
        for(int i=e.getFunc_paras().size()-1;i>=0;i--){
            if(e.getFunc_paras().get(i).isArray()){
                add_line("PARA_DEFINE#" + e.getFunc_paras().get(i).getName() + "#array#" + e.getFunc_label());
            }else{
                add_line("PARA_DEFINE#" + e.getFunc_paras().get(i).getName() + "#int#" + e.getFunc_label());
            }
        }
    }

    public static void func_return(String return_value) {
        if (return_value != null)
            add_line("RETURN#" + return_value + "## ");
    }

    public static void jump_to_function(String func_name) {
        add_line("JUMP_TO_FUNCTION#" + func_name + "## ");
    }

    public static void return_ra(String return_address) {
        add_line("FUNCTION_RETURN#" + return_address + "## ");
    }

    public static void func_real_para(String name,boolean isPtr) {
        if(isPtr)
            add_line("PARA_REAL#" + name + "#ptr# ");
        else{
            add_line("PARA_REAL#" + name + "#var# ");
        }
    }

    public static void cal_array_dim(TableElement offset, TableElement value,TableElement array, int dim) {
        TableElement tmp_op=new TableElement(SymbolTable.getTmpName());
        three_op_cal(tmp_op.getName(), value.getName(), String.valueOf(array.getDimInfo()[dim]),"*i");
        three_op_cal(offset.getName(), offset.getName(), tmp_op.getName(), "+");
        int curOffset=offset.getNumberValue();
        curOffset+=array.getDimInfo()[dim]*value.getNumberValue();
        offset.setNumberValue(curOffset);
        if(!value.isConst()&&!value.isValue()){
            offset.setConst(false);
        }
    }

    public static void get_int(String name) {
        add_line("READ#" + name + "## ");
    }

    public static void branch_not_equal(String op1, String op2, String label) {
        add_line("BNE#" + op1 + "#" + op2 + "#" + label);
    }

    public static void branch_equal(String op1, String op2, String label) {
        add_line("BEQ#" + op1 + "#" + op2 + "#" + label);
    }

    public static void jump_to(String label) {
        add_line("JUMP#" + label + "## ");
    }

    public static void output_str(String str) {
        String const_str_label = get_new_label();
        add_line("OUTPUT#str#" + const_str_label + "# ");
        const_string.put(const_str_label, str);
    }

    public static void output_num(String str) {
        add_line("OUTPUT#int#" + str + "# ");
    }

    public static void decl_tmp_val(String name) {
        add_line("DECL#int#" + name + "#0");
    }

    public static String setZero() {
        TableElement zero = new TableElement(SymbolTable.getTmpName());
        load_number_li(zero.getName(), 0);
        return zero.getName();
    }

    public static void decl_array_pointer(String name, String quote,String offset){
        add_line("USE_POINTER#"+name+"#"+quote+"#"+offset);
    }
}
