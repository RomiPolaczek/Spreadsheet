package expression.parser;


public enum Operation {
    PLUS(2),
    MINUS(2),
    TIMES(2),
    DIVIDE(2),
    MOD(2),
    POW(2),
    ABS(1),
    CONCAT(2),
    SUB(3),
    REF(1),
    PERCENT(2),
    AVERAGE(1),
    SUM(1),
    IF(3),
    EQUAL(2),
    NOT(1),
    BIGGER(2),
    LESS(2),
    OR(2),
    AND(2);

    private final int numArgs;

    Operation(int numArgs) {
        this.numArgs = numArgs;
    }

    public int getNumArgs() {
        return numArgs;
    }
}