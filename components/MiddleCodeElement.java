package components;

public class MiddleCodeElement {
    private final String kind, result, op1, op2;

    public MiddleCodeElement(String kind, String result, String op1, String op2) {
        this.kind = kind;
        this.result = result;
        this.op1 = op1;
        this.op2 = op2;
    }

    public String getKind() {
        return kind;
    }

    public String getOp1() {
        return op1;
    }

    public String getOp2() {
        return op2;
    }

    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        return kind + "#" + result + "#" + op1 + "#" + op2;
    }
}
