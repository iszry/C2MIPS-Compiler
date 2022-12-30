package generator;

public class ArrayTableElement {
    private String name;
    private int sp_offset;
    private int size;
    public ArrayTableElement(String name, int sp_offset, int size){
        this.name=name;
        this.sp_offset=sp_offset;
        this.size=size;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public int getSp_offset() {
        return sp_offset;
    }
}
