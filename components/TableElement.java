package components;

import java.util.*;

public class TableElement {
    // function name -> "#FUNC-" + name
    // variety name -> "#VAR-" + name
    private String name;
    private ArrayList<TableElement> initList = new ArrayList<>();

    public void rename(String name) {
        this.name = name;
    }

    public void add_initList(TableElement e) {
        initList.add(e);
    }

    public ArrayList<TableElement> getInitList() {
        return initList;
    }

    private TableElement quote_element = null;

    public TableElement getQuote_name() {
        return quote_element;
    }

    public void setQuote_name(TableElement quote_element) {
        this.quote_element = quote_element;
        this.isConst = quote_element.isConst();
        if (quote_element.isArray()) {
            this.varDim = quote_element.array_get_dimension();
        }
    }

    public void init_symbolTable() {

    }

    //type
    private String type = null;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    //value
    private boolean isValue = false;
    private int numberValue = 0;
    private String strValue = null;

    public void setNumberValue(int num) {
        numberValue = num;
    }

    //variety
    private boolean isConst = false;
    private boolean isVar = false;

    //function
    private boolean isFunc = false;
    private HashMap<String, String> paraName2Type = new HashMap<>();
    private ArrayList<TableElement> func_paras = new ArrayList<>();

    public void func_add_para(TableElement para) {
        func_paras.add(para);
    }

    private String funcReturnValue = "void";
    private int func_start_pos, func_end_pos;

    public void setFuncReturnValue(String funcReturnValue) {
        this.funcReturnValue = funcReturnValue;
        if (Objects.equals(funcReturnValue, "void"))
            this.varDim = -1;
    }

    private String func_label = "";

    public void setFuncLabel(String label) {
        func_label = label;
    }

    public String getFunc_label() {
        return func_label;
    }

    public ArrayList<TableElement> getFunc_paras() {
        return func_paras;
    }

    public void func_R_para_reset() {
        for(int i=0;i<func_check_para_count();i++){
            initList.remove(initList.size()-1);
        }
    }

    public boolean func_check_para_type() {
        for (int i = 0; i < func_paras.size(); i++) {
            if (func_paras.get(i).array_get_dimension() != initList.get(i).getVarDim()) {
                return false;
            }
        }
        return true;
    }

    public int func_check_para_count() {
        return func_paras.size();
    }

    public void saveFuncPointer(int st, int end) {
        func_start_pos = st;
        func_end_pos = end;
    }

    public String getFuncReturnValue() {
        return funcReturnValue;
    }

    public int getFunc_start_pos() {
        return func_start_pos;
    }

    public int getFunc_end_pos() {
        return func_end_pos;
    }

    //array
    private boolean isArray = false;
    private ArrayList<TableElement> dimension = new ArrayList<>();
    private int arraySpace = 1;
    private int dimCnt = 0;

    private int varDim = 0;

    public void setVarDim(int varDim) {
        this.varDim = varDim;
    }

    private int quoteDim=0;
    public void setQuoteDim(int quoteDim){
        this.quoteDim=quoteDim;
    }

    public void array_add_dimension(TableElement dim) {
        if (dim.isValue)
            arraySpace *= dim.getNumberValue();
        else arraySpace = 0;
        dimension.add(dim);
        dimCnt++;
    }

    public int array_get_space(){
        return arraySpace;
    }

    public ArrayList<TableElement> getDimension() {
        return dimension;
    }


    int[] dim_info;
    public void cal_dim_info(){
        dim_info=new int[dimension.size()];
        int weight=1;
        for(int i=dim_info.length-1;i>=0;i--){
            dim_info[i] = weight;
            weight*=dimension.get(i).getNumberValue();
        }
    }
    public int[] getDimInfo(){
        return dim_info;
    }

    public int getVarDim() {
        return varDim;
    }

    public int array_get_dimension() {
        return dimCnt;
    }

    private ArrayList<Integer> constArray=new ArrayList<>();

    public int findConstArray(int pos){
        return constArray.get(pos);
    }

    public void addConstArrayNumber(int num){
        constArray.add(num);
    }

    public TableElement getConstValue(){
        if(isArray){

        }
        return this;
    }

    private TableElement arrayOffset=null;
    public void setArrayOffset(TableElement offset){
        this.arrayOffset=offset;
    }

    //Expr
    private boolean isExpr = false;
    private Stack<TableElement> expr = new Stack<>();

    private Stack<String> exprOp = new Stack<>();

    public void expr_add_element(TableElement exprElement, String op) {
        HashMap<String, Integer> sign_weight = new HashMap<>();
        sign_weight.put("+", 1);
        sign_weight.put("-", 1);
        sign_weight.put("*", 2);
        sign_weight.put("/", 2);
        sign_weight.put("%", 2);
        if (exprElement != null) {
            expr.push(exprElement);
        } else if (op != null) {
            if (!exprOp.empty()) {
                while (sign_weight.get(exprOp.peek()) >= sign_weight.get(op)) {
                    String curOp = exprOp.pop();
                    TableElement op1 = expr.pop();
                    TableElement op2 = expr.pop();
                    if(op2.getQuote_name()!=null||op2.isConst()){
                        TableElement tmp=new TableElement(SymbolTable.getTmpName());
                        MiddleCode.three_op_cal(tmp.getName(), op2.getName(), op1.getName(), curOp);
                        expr_cal_num(tmp,op2,op1,curOp);
                        expr.push(tmp);
                    }else{
                        MiddleCode.three_op_cal(op2.getName(), op2.getName(), op1.getName(), curOp);
                        expr_cal_num(op2,op2,op1,curOp);
                        expr.push(op2);
                    }
                    if (exprOp.empty()) {
                        break;
                    }
                }
            }
            exprOp.push(op);
        }
    }

    public TableElement expr2value() {
        while (!exprOp.empty()) {
            String curOp = exprOp.pop();
            TableElement op1 = expr.pop();
            TableElement op2 = expr.pop();
            if(op2.getQuote_name()!=null||op2.isConst()){
                TableElement tmp=new TableElement(SymbolTable.getTmpName());
                MiddleCode.three_op_cal(tmp.getName(), op2.getName(), op1.getName(), curOp);
                expr_cal_num(tmp,op2,op1,curOp);
                expr.push(tmp);
            }else{
                MiddleCode.three_op_cal(op2.getName(), op2.getName(), op1.getName(), curOp);
                expr_cal_num(op2,op2,op1,curOp);
                expr.push(op2);
            }
        }
        TableElement last_element=expr.pop();
        if(last_element.isArray()){
            return last_element;
        }else{
            MiddleCode.instr_move(this, last_element);
        }
        if(last_element.isValue()){
            this.setValue(true);
            this.setNumberValue(last_element.getNumberValue());
        }
        return this;
    }

    public void expr_cal_num(TableElement result,TableElement left,TableElement right,String op){
        if(left.isValue()&& right.isValue()){
            result.setValue(true);
            if(Objects.equals(op, "+")){
                result.setNumberValue(left.getNumberValue()+ right.getNumberValue());
            }else if(Objects.equals(op, "-")){
                result.setNumberValue(left.getNumberValue()- right.getNumberValue());
            }else if(Objects.equals(op, "*")){
                result.setNumberValue(left.getNumberValue()* right.getNumberValue());
            }else if(Objects.equals(op, "/")){
                result.setNumberValue(left.getNumberValue()/ right.getNumberValue());
            }else if(Objects.equals(op, "%")){
                result.setNumberValue(left.getNumberValue()% right.getNumberValue());
            }
        }
    }

    private Stack<TableElement> cond = new Stack<>();

    private Stack<String> condOp = new Stack<>();

    public void cond_add_element(TableElement condElement, String op) {
        HashMap<String, Integer> sign_weight = new HashMap<>();
        sign_weight.put("||", 1);
        sign_weight.put("&&", 2);
        sign_weight.put("==", 3);
        sign_weight.put("!=", 3);
        sign_weight.put(">=", 4);
        sign_weight.put("<=", 4);
        sign_weight.put(">", 4);
        sign_weight.put("<", 4);
        if (condElement != null) {
            cond.push(condElement);
        } else if (op != null) {
            if (!condOp.empty()) {
                while (sign_weight.get(condOp.peek()) >= sign_weight.get(op)) {
                    String curOp = condOp.pop();
                    TableElement op1 = cond.pop();
                    TableElement op2 = cond.pop();
                    if(op2.getQuote_name()!=null||op2.isConst()){
                        TableElement tmp=new TableElement(SymbolTable.getTmpName());
                        MiddleCode.three_op_cal(tmp.getName(), op2.getName(), op1.getName(), curOp);
                        cond.push(tmp);
                    }else{
                        MiddleCode.three_op_cal(op2.getName(), op2.getName(), op1.getName(), curOp);
                        cond.push(op2);
                    }
                    if (condOp.empty()) {
                        break;
                    }
                }
            }
            condOp.push(op);
        }
    }

    public TableElement cond2bool() {
        while (!condOp.empty()) {
            String curOp = condOp.pop();
            TableElement op1 = cond.pop();
            TableElement op2 = cond.pop();
            if(op2.getQuote_name()!=null||op2.isConst()){
                TableElement tmp=new TableElement(SymbolTable.getTmpName());
                MiddleCode.three_op_cal(tmp.getName(), op2.getName(), op1.getName(), curOp);
                cond.push(tmp);
            }else{
                MiddleCode.three_op_cal(op2.getName(), op2.getName(), op1.getName(), curOp);
                cond.push(op2);
            }
        }
        TableElement last_element=cond.peek();
        MiddleCode.instr_move(this, last_element);
        return this;
    }


    //constructor

    public TableElement(String name) {
        this.name = name;
    }

    public TableElement(String name, int sp_code) {
        this.name = name;
        if (sp_code == 0) {
            //init a constant with value zero
            isValue = true;
            numberValue = 0;
        } else if (sp_code == 1) {
            // error: no identity
        }
    }

    public int getNumberValue() {
        return numberValue;
    }

    public String getName() {
        if (isFunc) {
            return "#FUNC_" + name;
        }
        return name;
    }

    public void setConst(boolean isConst) {
        this.isConst = isConst;
    }

    public boolean isConst() {
        return isConst;
    }

    public void setFunc(boolean isFunc) {
        this.isFunc = isFunc;
    }

    public boolean isFunc() {
        return isFunc;
    }

    public void setArray(boolean isArray) {
        this.isArray = isArray;
    }

    public boolean isArray() {
        return isArray;
    }

    public boolean isPtr(){
        if(quote_element==null&&dimension.size()>0){
            return true;
        }
        if(isArray&&quote_element!=null&&quote_element.getDimension().size()>quoteDim)
            return true;
        else return false;
    }

    public void setValue(boolean isValue) {
        this.isValue = isValue;
    }

    public boolean isValue() {
        return isValue;
    }

    public boolean isVar() {
        return isVar;
    }

    public void setVar(boolean isVar) {
        this.isVar = isVar;
    }

    public boolean isExpr() {
        return isExpr;
    }

    public void setExpr(boolean isExpr) {
        this.isExpr = isExpr;
    }
}
