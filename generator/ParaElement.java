package generator;

import java.util.Objects;

public class ParaElement {
    private String name;
    private String FuncName;
    private boolean isPtr=false;
    private boolean isVar=false;

    private String address="";
    public ParaElement(String name,String type){
        this.name=name;
        if(Objects.equals(type, "ptr")){
            isPtr=true;
        }else if(Objects.equals(type, "var")){
            isVar=true;
        }
    }

    public boolean isVar() {
        return isVar;
    }

    public boolean isPtr() {
        return isPtr;
    }

    public String getName() {
        return name;
    }

    public String getFuncName() {
        return FuncName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPtrAddress(){
        String[] sb=address.replace("(",",").replace(")","").split(",");
        return " "+sb[1]+" "+sb[0];
    }
}
