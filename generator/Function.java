package generator;

import java.util.ArrayList;
import java.util.Stack;

public class Function {
    private String function_label_name;
    private ArrayList<String> para_name_list=new ArrayList<>();
    public Function(String function_label_name){
        this.function_label_name=function_label_name;
    }

    public void add_real_para(String para_name){
        para_name_list.add(para_name);
    }

    public void clear_para_real_list(){
        para_name_list.clear();
    }

    public ArrayList<String> get_para_name_list(){
        return para_name_list;
    }

    private Stack<Integer> para_tabel_use_level=new Stack<>();
    public int get_table_level(){
        return para_tabel_use_level.pop();
    }

    public void set_table_level(int level){
        para_tabel_use_level.add(level);
    }
}
