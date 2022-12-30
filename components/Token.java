package components;

public class Token {
    private final String name;
    private final String kind;
    private final int line_id;

    public Token(String name, String kind, int line_id) {
        this.name = name;
        this.kind=kind;
        this.line_id=line_id;
    }

    public String getName(){
        return name;
    }

    public String getKind() {
        return kind;
    }

    public int getLine_id() {
        return line_id;
    }

    @Override
    public String toString(){
        return kind+" "+name;
    }
}
