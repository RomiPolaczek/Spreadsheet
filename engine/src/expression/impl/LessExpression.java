package expression.impl;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.impl.EffectiveValueImpl;

public class LessExpression implements Expression {

    private Expression left;
    private Expression right;

    public LessExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) throws Exception {
        EffectiveValue leftValue = left.eval(sheet);
        EffectiveValue rightValue = right.eval(sheet);

        Boolean result;

        try
        {
            result = leftValue.extractValueWithExpectation(Double.class) <= rightValue.extractValueWithExpectation(Double.class);
        }
        catch (Exception e)
        {
            return new EffectiveValueImpl(CellType.ERROR, "UNKNOWN");
        }

        return new EffectiveValueImpl(CellType.BOOLEAN, result);

    }

    @Override
    public CellType getFunctionResultType() { return CellType.BOOLEAN; }
}
