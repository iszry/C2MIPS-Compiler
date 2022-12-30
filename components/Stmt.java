package components;

import java.util.Objects;

public class Stmt implements Component {
    public Stmt() {
        procedure();
        strPrint();
    }

    @Override
    public void strPrint() {
        if (Parser.TREE_PRINT_SWITCH) {
            System.out.println("<Stmt>");
        }
    }

    @Override
    public void procedure() {
        if (LVal.has() && Objects.equals(LVal.follow(), "=")) {
            int error_line_id = Parser.getCurLineId();
            TableElement left = new LVal().getValue();
            if (Parser.ERROR_CHECK && left.isConst()) {
                System.out.println(error_line_id + " h");
            }
            //That's it! Don't change it!
            if (Parser.ERROR_CHECK && !Objects.equals(Parser.getCurName(), "=")) {
                System.out.println(error_line_id + " i");
                new Stmt();
            }else{
                Parser.next();
                if (Exp.has()) {
                    MiddleCode.store_in(left.getName(), new Exp().getValue().getName());
                } else {
                    MiddleCode.get_int(left.getName());
                    Parser.next();
                    Parser.next();
                    check_right_little_bracket();
                }
                check_semicolon();
            }
        } else if (Exp.has()) {
            new Exp();
            check_semicolon();
        } else if (Objects.equals(Parser.getCurName(), "if")) {
            Parser.next();
            Parser.next();
            String label_in_if=MiddleCode.get_new_label();
            String label_end_if = MiddleCode.get_new_label();
            String label_out_if_else = MiddleCode.get_new_label();
            MiddleCode.branch_equal(new Cond(label_in_if,label_end_if).getValue().getName(), MiddleCode.setZero(), label_end_if);
            check_right_little_bracket();
            MiddleCode.insert_name_label(label_in_if);
            SymbolTable.LEVEL_IN(true);
            new Stmt();
            SymbolTable.LEVEL_OUT(true);
            if (Objects.equals(Parser.getCurName(), "else")) {
                MiddleCode.jump_to(label_out_if_else);
                MiddleCode.insert_name_label(label_end_if);
                Parser.next();
                SymbolTable.LEVEL_IN(true);
                new Stmt();
                SymbolTable.LEVEL_OUT(true);
                MiddleCode.insert_name_label(label_out_if_else);
            } else {
                MiddleCode.insert_name_label(label_end_if);
            }
        } else if (Objects.equals(Parser.getCurName(), "while")) {
            Parser.next();
            Parser.next();
            MiddleCode.insert_blank_line();
            MiddleCode.insert_blank_line();
            String label_in_while = MiddleCode.get_new_label();
            String label_in_block = MiddleCode.get_new_label();
            MiddleCode.label_stack_continue.push(label_in_while);
            MiddleCode.insert_name_label(label_in_while);
            String label_out_while = MiddleCode.get_new_label();
            MiddleCode.label_stack_break.push(label_out_while);
            MiddleCode.branch_equal(new Cond(label_in_block,label_out_while).getValue().getName(), MiddleCode.setZero(), label_out_while);
            check_right_little_bracket();
            MiddleCode.insert_name_label(label_in_block);
            Parser.IN_LOOP = true;
            SymbolTable.LEVEL_IN(true);
            new Stmt();
            SymbolTable.LEVEL_OUT(true);
            Parser.IN_LOOP = false;
            MiddleCode.jump_to(label_in_while);
            MiddleCode.insert_name_label(label_out_while);
            MiddleCode.insert_blank_line();
            MiddleCode.insert_blank_line();
            MiddleCode.label_stack_continue.pop();
            MiddleCode.label_stack_break.pop();
        } else if (Objects.equals(Parser.getCurName(), "break")) {
            if (Parser.ERROR_CHECK && !Parser.IN_LOOP) {
                System.out.println(Parser.getCurLineId() + " m");
            }
            Parser.next();
            MiddleCode.jump_to(MiddleCode.label_stack_break.peek());
            check_semicolon();
        } else if (Objects.equals(Parser.getCurName(), "continue")) {
            if (Parser.ERROR_CHECK && !Parser.IN_LOOP) {
                System.out.println(Parser.getCurLineId() + " m");
            }
            Parser.next();
            MiddleCode.jump_to(MiddleCode.label_stack_continue.peek());
            check_semicolon();
        } else if (Objects.equals(Parser.getCurName(), "return")) {
            int error_line_id = Parser.getCurLineId();
            if (Parser.IN_FUNCTION) {
                Parser.HAS_RETURN = true;
            }
            Parser.next();
            if (Exp.has()) {
                if (Parser.ERROR_CHECK && Parser.IN_FUNCTION && Objects.equals(Parser.FUNC_TYPE, "void")) {
                    System.out.println(error_line_id + " f");
                }
                MiddleCode.func_return(new Exp().getValue().getName());
            }else{
                MiddleCode.func_return(new TableElement(SymbolTable.getTmpName()).getName());
            }
            check_semicolon();
        } else if (Objects.equals(Parser.getCurName(), "printf")) {
            int error_line_id = Parser.getCurLineId(), exp_cnt = 0, data_in_str = 0;
            Parser.next();
            Parser.next();
            FormatString formatStr = new FormatString();
            data_in_str = formatStr.get_data_cnt();
            while (Objects.equals(Parser.getCurName(), ",")) {
                Parser.next();
                formatStr.add_para(new Exp().getValue());
            }
            if (Parser.ERROR_CHECK && formatStr.getParas().size() != data_in_str) {
                System.out.println(error_line_id + " l");
            }
            formatStr.output_middle_code();
            check_right_little_bracket();
            check_semicolon();
        } else if (Block.has()) {
            new Block();
        }else{
            if(Parser.ERROR_CHECK){
                int error_line_id = Parser.getPosLineId(-1);
                if(!Objects.equals(Parser.getCurName(), ";")){
                    System.out.println(error_line_id + " i");
                }else{
                    Parser.next();
                }
            }else{
                Parser.next();
            }
        }
    }

    public static boolean has() {
        String str = Parser.getCurName();
        boolean strEqual = Objects.equals(str, ";") ||
                Objects.equals(str, "if") ||
                Objects.equals(str, "while") ||
                Objects.equals(str, "break") ||
                Objects.equals(str, "continue") ||
                Objects.equals(str, "return") ||
                Objects.equals(str, "printf");
        return LVal.has() || Exp.has() || Block.has() || strEqual;
    }

    public void check_semicolon() {
        if (Parser.ERROR_CHECK) {
            int error_line_id = Parser.getPosLineId(-1);
            if (!Objects.equals(Parser.getCurName(), ";")) {
                System.out.println(error_line_id + " i");
            } else {
                Parser.next();
            }
        } else {
            Parser.next();
        }
    }

    public void check_right_little_bracket() {
        if (Parser.ERROR_CHECK) {
            int error_line_id = Parser.getPosLineId(-1);
            if (!Objects.equals(Parser.getCurName(), ")")) {
                System.out.println(error_line_id + " j");
            } else {
                Parser.next();
            }
        } else {
            Parser.next();
        }
    }
}
