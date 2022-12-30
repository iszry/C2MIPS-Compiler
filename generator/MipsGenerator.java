package generator;

import components.TableElement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Stack;

public class MipsGenerator {
    /*
    $s0 -->> for stack top
    $s1 -->> for function return value delivery
    $s2 -->> function call stack pointer
    $s3 -->> function para stack
    $s4 -->> function stack top
     */
    private String middleCode;
    private String[] middleCodeLine = null;
    private String mips = "";

    private final String SPACE_ALLOC = "my_space_pre_allocated";

    private final String FUNCTION_PARA_STACK_SPACE_ALLOC = "my_para_stack_pre_allocated";

    private final int SPACE_SIZE = 0X200000;

    private final int FUNCTION_PARA_STACK_SPACE_SIZE = 0X20000;

    private int CUR_ALLOC_OFFSET = 0;

    private final int STACK_TOP = 0x7fffeffc;

    private final String stack_top = "STACK_TOP";
    private int SP_OFFSET = 0;
    private int vice_sp_offset = 0;

    private int TABLE_LEVEL = 0;

    private HashMap<Integer, HashMap<String, Integer>> SIGN_TABLE = new HashMap<>();
    private HashMap<String, Integer> PARA_TABLE = new HashMap<>();
    private HashMap<String, Integer> PARA_PTR_TABLE = new HashMap<>();
    private int PARA_OFFSET = 0;
    private int GLOBAL_ARRAY_SPACE = 0;
    private String CUR_FUNCTION_NAME = "";
    //string name and address
    private HashMap<String, Integer> GLOBAL_SIGN_TABLE = new HashMap<>();
    private boolean GLOBAL_SWITCH = true;
    private HashMap<Integer, Integer> STACK_OFFSET_TABLE = new HashMap<>();


    private boolean FUNCTION_DEF = true;
    //<function_label,<para_name>>
    private HashMap<String, Function> FUNCTION_LIST = new HashMap<>();

    //<name,addr_offset>
    private ArrayList<ParaElement> para_real = new ArrayList<>();

    private Stack<Function> function_call_stack = new Stack<>();
    private HashMap<String, ArrayList<String>> ptr2arrOffset = new HashMap<>();
    private HashMap<Integer, HashMap<String,ArrayTableElement>> array_ptr_table = new HashMap<>();

    public MipsGenerator(String middleCode) throws IOException {
        this.middleCode = middleCode;
        //System.out.println(middleCode);
        SIGN_TABLE.put(0, new HashMap<>());
        array_ptr_table.put(0, new HashMap<>());
        STACK_OFFSET_TABLE.put(0, STACK_TOP);
        middleCodeLine = middleCode.split("\n");
        start_convert();
        print_mips_to_file();
        //print_middle_code_to_file();
    }

    private void start_convert() {
        for (String line : middleCodeLine) {
            line_process(line);
        }
        add_line("label_00_end_main:");
    }


    private void line_process(String line) {
        String[] elements = line.split("#");
        if (Objects.equals(elements[0], "END_CUR_FUNCTION")) {
            add_line("# return value");
            add_line("addiu $s2 $s2 -16");
            add_line("sw $s1 12($s2)");
            add_line("lw $t0 8($s2)");
            add_line("lw $sp -12($s2)");
            add_line("lw $s4 -16($s2)");
            add_line("addiu $s3 $s3 " + FUNCTION_LIST.get(CUR_FUNCTION_NAME).get_para_name_list().size() * (-4));
            add_line("jr $t0");
            add_line("nop");
            add_line("");
            destroy_cur_table(FUNCTION_LIST.get(CUR_FUNCTION_NAME).get_table_level());
            FUNCTION_DEF = false;
            PARA_TABLE.clear();
            PARA_PTR_TABLE.clear();
            CUR_FUNCTION_NAME = "";
        } else if (Objects.equals(elements[0], "END_INITIAL_FUNCTION_DEF")) {
            FUNCTION_DEF = false;
            SP_OFFSET = vice_sp_offset;
        } else if (Objects.equals(elements[0], "END_INITIAL_VAR_DEF")) {
            add_line("jal label_0_main_function");
            add_line("nop");
            add_line("j label_00_end_main");
            add_line("nop");
            add_line("");
            GLOBAL_SWITCH = false;
            vice_sp_offset = SP_OFFSET;
        } else if (Objects.equals(elements[0], "def")) {
            //block
            add_line(elements[1]);
            if (elements[1].equals(".text")) {
                add_line("addu $s0 $0 $sp");
                add_line("la $s2 " + SPACE_ALLOC);
                add_line("la $s3 " + FUNCTION_PARA_STACK_SPACE_ALLOC);
                PARA_OFFSET = 0;
            }
        } else if (Objects.equals(elements[0], "%")) {
            if (Objects.equals(elements[1], "const_str")) {
                add_line(elements[2] + ": .asciiz " + elements[3]);
            } else if (Objects.equals(elements[1], "const_data_space")) {
                add_line(SPACE_ALLOC + ": .space " + SPACE_SIZE);
                add_line(FUNCTION_PARA_STACK_SPACE_ALLOC + ": .space " + FUNCTION_PARA_STACK_SPACE_SIZE);
            }
            add_line("");
        } else if (Objects.equals(elements[0], "LABEL")) {
            add_line(elements[1] + ":");
            add_line("");
        } else if (Objects.equals(elements[0], "OUTPUT")) {
            if (Objects.equals(elements[1], "str")) {
                add_line("la $a0 " + elements[2]);
                add_line("li $v0 4");
                add_line("syscall");
            } else if (Objects.equals(elements[1], "int")) {
                add_line("lw $a0 " + name2address(elements[2]));
                add_line("li $v0 1");
                add_line("syscall");
            }
            add_line("");
        } else if (Objects.equals(elements[0], "DECL")) {
            if (Objects.equals(elements[1], "int")) {
                //int
                add_line("#decl int " + elements[2]);
                insert_to_sign_table(elements[2], SP_OFFSET);
                add_line("addiu $sp $sp -4");
                SP_OFFSET -= 4;
            } else if (Objects.equals(elements[1], "array")) {
                //array
                add_line("#decl array " + elements[2]);
                int array_space = Integer.parseInt(elements[3]) * 4;
                insert_to_array_table(elements[2], SP_OFFSET,array_space);
                if (GLOBAL_SWITCH) {
                    GLOBAL_ARRAY_SPACE += array_space;
                }
                add_line("addiu $sp $sp -" + array_space);
                SP_OFFSET -= array_space;
            }
            add_line("");
        } else if (Objects.equals(elements[0], "BEGIN")) {
            create_next_table();
        } else if (Objects.equals(elements[0], "END")) {
            destroy_cur_table(TABLE_LEVEL - 1);
        } else if (Objects.equals(elements[0], "LOAD")) {
            add_line("# LOAD");
            add_line("lw $t0 " + name2address(elements[3]));
            String[] sb = name2address(elements[1]).replace("(", ",").replace(")", "").split(",");
            int int_offset = Integer.parseInt(sb[0]) + Integer.parseInt(elements[2]);
            String str_offset = String.valueOf(int_offset);
            add_line("sw $t0 " + str_offset + "(" + sb[1] + ")");
            add_line("");
        } else if (Objects.equals(elements[0], "READ")) {
            add_line("li $v0 5");
            add_line("syscall");
            add_line("sw $v0 " + name2address(elements[1]));
            add_line("");
        } else if (Objects.equals(elements[0], "LI")) {
            add_line("li $t0 " + elements[2]);
            add_line("sw $t0 " + name2address(elements[1]));
            add_line("");
        } else if (Objects.equals(elements[0], "STORE")) {
            add_line("# store "+elements[1]+" "+elements[2]);
            add_line("lw $t0 " + name2address(elements[2]));
            add_line("sw $t0 " + name2address(elements[1]));
            add_line("");
        } else if (Objects.equals(elements[0], "MOVE")) {
            add_line("# MOVE "+elements[2]+" TO " +elements[1]);
            if (Objects.equals(elements[3], "ptr")) {
                String[] sb = name2address(elements[2]).replace("(", ",").replace(")", "").split(",");
                add_line("addiu $t0 " + sb[1] + " " + sb[0]);
                add_line("sw $t0 " + name2address(elements[1]));
                add_line("");
            } else {
                add_line("lw $t0 " + name2address(elements[2]));
                add_line("sw $t0 " + name2address(elements[1]));
                add_line("");
            }
        } else if (Objects.equals(elements[0], "ADD")) {
            add_line("lw $t0 " + name2address(elements[2]));
            add_line("lw $t1 " + name2address(elements[3]));
            add_line("addu $t2 $t0 $t1");
            add_line("sw $t2 " + name2address(elements[1]));
            add_line("");
        } else if (Objects.equals(elements[0], "AND")) {
            add_line("lw $t0 " + name2address(elements[2]));
            add_line("sne $t0 $t0 $0");
            add_line("lw $t1 " + name2address(elements[3]));
            add_line("sne $t1 $t1 $0");
            add_line("and $t2 $t0 $t1");
            add_line("sw $t2 " + name2address(elements[1]));
            add_line("");
        } else if (Objects.equals(elements[0], "OR")) {
            add_line("lw $t0 " + name2address(elements[2]));
            add_line("sne $t0 $t0 $0");
            add_line("lw $t1 " + name2address(elements[3]));
            add_line("sne $t1 $t1 $0");
            add_line("or $t2 $t0 $t1");
            add_line("sw $t2 " + name2address(elements[1]));
            add_line("");
        } else if (Objects.equals(elements[0], "SUB")) {
            add_line("lw $t0 " + name2address(elements[2]));
            add_line("lw $t1 " + name2address(elements[3]));
            add_line("subu $t2 $t0 $t1");
            add_line("sw $t2 " + name2address(elements[1]));
            add_line("");
        } else if (Objects.equals(elements[0], "MULT")) {
            add_line("lw $t0 " + name2address(elements[2]));
            add_line("lw $t1 " + name2address(elements[3]));
            add_line("mul $t2 $t0 $t1");
            add_line("sw $t2 " + name2address(elements[1]));
            add_line("");
        } else if (Objects.equals(elements[0], "DIV")) {
            add_line("lw $t0 " + name2address(elements[2]));
            add_line("lw $t1 " + name2address(elements[3]));
            add_line("div $t0 $t1");
            add_line("mflo $t2");
            add_line("sw $t2 " + name2address(elements[1]));
            add_line("");
        } else if (Objects.equals(elements[0], "MOD")) {
            add_line("lw $t0 " + name2address(elements[2]));
            add_line("lw $t1 " + name2address(elements[3]));
            add_line("div $t0 $t1");
            add_line("mfhi $t2");
            add_line("sw $t2 " + name2address(elements[1]));
            add_line("");
        } else if (Objects.equals(elements[0], "FUNC_DEFINE")) {
            FUNCTION_LIST.put(elements[2], new Function(elements[2]));
            FUNCTION_LIST.get(elements[2]).set_table_level(TABLE_LEVEL);
            if (!Objects.equals(elements[2], "label_0_main_function")) {
                FUNCTION_DEF = true;
                SP_OFFSET = 0;
            }
            create_next_table();
            CUR_FUNCTION_NAME = elements[2];
            PARA_OFFSET = 0;
            add_line("sw $ra 8($s2)");
            add_line("sw $sp 4($s2)");
            add_line("sw $sp 0($s2)");
            add_line("addu $s4 $0 $sp");
            add_line("addiu $s2 $s2 16");
        } else if (Objects.equals(elements[0], "PARA_DEFINE")) {
            if (Objects.equals(elements[2], "int")) {
                FUNCTION_LIST.get(elements[3]).add_real_para(elements[1]);
                PARA_OFFSET -= 4;
                insert_to_para_table(elements[1], PARA_OFFSET);
                add_line("");
            } else if (Objects.equals(elements[2], "array")) {
                FUNCTION_LIST.get(elements[3]).add_real_para(elements[1]);
                PARA_OFFSET -= 4;
                insert_to_para_ptr(elements[1], PARA_OFFSET);
                add_line("");
            }
        } else if (Objects.equals(elements[0], "JUMP_TO_FUNCTION")) {
            int para_offset_s3_cnt=0;
            for (ParaElement paraElement : para_real) {
                add_line("#PARA_REAL "+paraElement.getName());
                if (paraElement.isVar()) {
                    paraElement.setAddress(name2address(paraElement.getName()));
                    add_line("lw $t0 " + paraElement.getAddress());
                } else if (paraElement.isPtr()) {
                    add_line("# ptr para address in $t0 store in $t3");
                    paraElement.setAddress(name2address(paraElement.getName()));
                    add_line("addiu $t0" + paraElement.getPtrAddress());
                }
                add_line("sw $t0 "+para_offset_s3_cnt+"($s3)");
                add_line("");
                para_offset_s3_cnt+=4;
            }
            add_line("addiu $s3 $s3 "+para_offset_s3_cnt);
            para_real.clear();
            add_line("sw $sp -12($s2)");
            add_line("jal " + elements[1]);
            add_line("nop");
            add_line("");
        } else if (Objects.equals(elements[0], "PARA_REAL")) {
            ParaElement curPara = new ParaElement(elements[1], elements[2]);
            para_real.add(curPara);
        } else if (Objects.equals(elements[0], "FUNCTION_RETURN")) {
            add_line("# function return in $s1");

            add_line("lw $s1 12($s2)");
            add_line("sw $s1 " + name2address(elements[1]));
            add_line("");
        } else if (Objects.equals(elements[0], "RETURN")) {
            add_line("# return value");
            add_line("lw $s1 " + name2address(elements[1]));
            add_line("addiu $s2 $s2 -16");
            add_line("sw $s1 12($s2)");
            add_line("lw $t0 8($s2)");
            add_line("lw $sp -12($s2)");
            add_line("lw $s4 -16($s2)");
            add_line("addiu $s3 $s3 " + FUNCTION_LIST.get(CUR_FUNCTION_NAME).get_para_name_list().size() * (-4));
            add_line("jr $t0");
            add_line("nop");
            add_line("");
        } else if (Objects.equals(elements[0], "EQ")) {
            add_line("lw $t1 " + name2address(elements[2]));
            add_line("lw $t2 " + name2address(elements[3]));
            add_line("seq $t0 $t1 $t2");
            add_line("sw $t0 " + name2address(elements[1]));
            add_line("");
        } else if (Objects.equals(elements[0], "BEQ")) {
            add_line("lw $t0 " + name2address(elements[1]));
            add_line("lw $t1 " + name2address(elements[2]));
            add_line("beq $t0 $t1 " + elements[3]);
            add_line("");
        } else if (Objects.equals(elements[0], "BNE")) {
            add_line("lw $t0 " + name2address(elements[1]));
            add_line("lw $t1 " + name2address(elements[2]));
            add_line("bne $t0 $t1 " + elements[3]);
            add_line("");
        } else if (Objects.equals(elements[0], "JUMP")) {
            add_line("j " + elements[1]);
            add_line("");
        } else if (Objects.equals(elements[0], "NEQ")) {
            add_line("lw $t1 " + name2address(elements[2]));
            add_line("lw $t2 " + name2address(elements[3]));
            add_line("sne $t0 $t1 $t2");
            add_line("sw $t0 " + name2address(elements[1]));
            add_line("");
        } else if (Objects.equals(elements[0], "LEQ")) {
            add_line("# LEQ");
            add_line("lw $t1 " + name2address(elements[2]));
            add_line("lw $t2 " + name2address(elements[3]));
            add_line("sle $t0 $t1 $t2");
            add_line("sw $t0 " + name2address(elements[1]));
            add_line("");
        } else if (Objects.equals(elements[0], "GEQ")) {
            add_line("lw $t1 " + name2address(elements[2]));
            add_line("lw $t2 " + name2address(elements[3]));
            add_line("sle $t0 $t2 $t1");
            add_line("sw $t0 " + name2address(elements[1]));
            add_line("");
        } else if (Objects.equals(elements[0], "LESS")) {
            add_line("lw $t1 " + name2address(elements[2]));
            add_line("lw $t2 " + name2address(elements[3]));
            add_line("sgt $t0 $t2 $t1");
            add_line("sw $t0 " + name2address(elements[1]));
            add_line("");
        } else if (Objects.equals(elements[0], "BIG")) {
            add_line("lw $t1 " + name2address(elements[2]));
            add_line("lw $t2 " + name2address(elements[3]));
            add_line("sgt $t0 $t1 $t2");
            add_line("sw $t0 " + name2address(elements[1]));
            add_line("");
        } else if (Objects.equals(elements[0], "MULT_I")) {
            add_line("lw $t0 " + name2address(elements[2]));
            add_line("li $t1 " + elements[3]);
            add_line("mul $t2 $t0 $t1");
            add_line("sw $t2 " + name2address(elements[1]));
            add_line("");
        } else if (Objects.equals(elements[0], "NOT")) {
            add_line("lw $t0 " + name2address(elements[2]));
            add_line("seq $t1 $t0 0");
            add_line("sw $t1 " + name2address(elements[1]));
            add_line("");
        } else if (Objects.equals(elements[0], "ADDI")) {
            add_line("lw $t0 " + name2address(elements[2]));
            add_line("addiu $t1 $t0 " + elements[3]);
            add_line("sw $t1 " + name2address(elements[1]));
            add_line("");
        } else if (Objects.equals(elements[0], "MULTI")) {
            add_line("lw $t0 " + name2address(elements[2]));
            add_line("mul $t1 $t0 " + elements[3]);
            add_line("sw $t1 " + name2address(elements[1]));
            add_line("");
        } else if (Objects.equals(elements[0], "USE_POINTER")) {
            add_line("#USE_POINTER "+elements[1]+" "+elements[2]+" "+elements[3]);
            ptr2arrOffset.put(elements[1], new ArrayList<>());
            ptr2arrOffset.get(elements[1]).add(elements[2]);
            ptr2arrOffset.get(elements[1]).add(elements[3]);
        }
    }

    private void print_mips_to_file() throws IOException {
        File file = new File("mips.txt");
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(mips);
        bw.close();
    }

    private void print_middle_code_to_file() throws IOException {
        File file = new File("middle_code.txt");
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(middleCode);
        bw.close();
    }

    private int line_cnt = 1;

    private void add_line(String line_info) {
        mips += (line_info + "\n");
        line_cnt++;
    }

    private String grf(String name) {
        return "$t0";
    }

    private void insert_to_sign_table(String name, int sp_offset) {
        if (GLOBAL_SWITCH) {
            GLOBAL_SIGN_TABLE.put(name, sp_offset);
        } else {
            SIGN_TABLE.get(TABLE_LEVEL).put(name, sp_offset);
        }
    }

    private void insert_to_array_table(String name, int sp_offset, int size) {
        array_ptr_table.get(TABLE_LEVEL).put(name,new ArrayTableElement(name,sp_offset,size));
    }

    private void insert_to_para_ptr(String name, int para_offset) {
        PARA_PTR_TABLE.put(name, para_offset);
    }

    private void insert_to_para_table(String name, int para_offset) {
        PARA_TABLE.put(name, para_offset);
    }

    private void create_next_table() {
        TABLE_LEVEL++;
        STACK_OFFSET_TABLE.put(TABLE_LEVEL, STACK_OFFSET_TABLE.get(TABLE_LEVEL - 1) + SP_OFFSET);
        SIGN_TABLE.put(TABLE_LEVEL, new HashMap<>());
        array_ptr_table.put(TABLE_LEVEL, new HashMap<>());
    }

    private void destroy_cur_table(int to_level) {
        //clear stack
        /*
        TO DO: calculate the size of stack space used in current level
         */
        for (int i = TABLE_LEVEL; i > to_level; i--) {
                SP_OFFSET += 4 * SIGN_TABLE.get(i).size();
                for(ArrayTableElement e:array_ptr_table.get(i).values()) {
                    SP_OFFSET += e.getSize();
                }
                SIGN_TABLE.remove(i);
                STACK_OFFSET_TABLE.remove(i);
                array_ptr_table.remove(i);
        }
        TABLE_LEVEL = to_level;
    }

    private String name2address(String name, int offset) {
        int ret_address = offset;
        if (FUNCTION_DEF) {
            if (PARA_TABLE.containsKey(name)) {
                return (PARA_TABLE.get(name) + offset) + "($s3)";
            }
            for (int i = TABLE_LEVEL; i >= 0; i--) {
                if (SIGN_TABLE.get(i).containsKey(name)) {
                    return (SIGN_TABLE.get(i).get(name) + offset) + "($s4)";
                }
            }
        } else {
            for (int i = TABLE_LEVEL; i >= 0; i--) {
                if (SIGN_TABLE.get(i).containsKey(name)) {
                    ret_address += SIGN_TABLE.get(i).get(name);
                    break;
                }
            }

        }
        if (GLOBAL_SIGN_TABLE.containsKey(name)) {
            ret_address += GLOBAL_SIGN_TABLE.get(name);
        }
        return ret_address + "($s0)";
    }


    private String name2address(String name) {
        int ret_address = 0;
        if (FUNCTION_DEF) {
            if (PARA_TABLE.containsKey(name)) {
                return PARA_TABLE.get(name) + "($s3)";
            } else if (PARA_PTR_TABLE.containsKey(name)) {
                add_line("# use ptr para in function");
                add_line("addiu $t3 $s3 " + PARA_PTR_TABLE.get(name));
                add_line("lw $t3 0($t3)");
                add_line("");
                return "0($t3)";
            }
            for (int i = TABLE_LEVEL; i > 0; i--) {
                if (array_ptr_table.get(i).containsKey(name)) {
                    ret_address = array_ptr_table.get(i).get(name).getSp_offset();
                    return ret_address + "($s4)";
                }
                if (SIGN_TABLE.get(i).containsKey(name)) {
                    ret_address = SIGN_TABLE.get(i).get(name);
                    return ret_address + "($s4)";
                }
            }
        } else {
            for (int i = TABLE_LEVEL; i >= 0; i--) {
                if (SIGN_TABLE.get(i).containsKey(name)) {
                    ret_address = SIGN_TABLE.get(i).get(name);
                    return ret_address + "($s0)";
                }
            }
            for (int i = TABLE_LEVEL; i > 0; i--) {
                if (array_ptr_table.get(i).containsKey(name)) {
                    ret_address = array_ptr_table.get(i).get(name).getSp_offset();
                    return ret_address + "($s0)";
                }
            }
        }
        if (GLOBAL_SIGN_TABLE.containsKey(name)) {
            ret_address = GLOBAL_SIGN_TABLE.get(name);
        } else if (ptr2arrOffset.containsKey(name)) {
            String[] sb = name2address(ptr2arrOffset.get(name).get(0)).replace("(", ",").replace(")", "").split(",");
            add_line("addiu $t3 " + sb[1] + " " + sb[0]);
            add_line("lw $t4 " + name2address(ptr2arrOffset.get(name).get(1)));
            add_line("sll $t4 $t4 2");
            add_line("subu $t3 $t3 $t4");
            // $t0 is the array element's position
            return "0($t3)";
        }else if (array_ptr_table.get(0).containsKey(name)) {
            ret_address = array_ptr_table.get(0).get(name).getSp_offset();
            return ret_address + "($s0)";
        }
        return ret_address + "($s0)";
    }

}
