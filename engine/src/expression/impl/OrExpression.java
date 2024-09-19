package expression.impl;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.impl.EffectiveValueImpl;

public class OrExpression implements Expression {

    private Expression left;
    private Expression right;

    public OrExpression(Expression left, Expression right) {
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
            result = leftValue.extractValueWithExpectation(Boolean.class) || rightValue.extractValueWithExpectation(Boolean.class);
        }
        catch (Exception e)
        {
            return new EffectiveValueImpl(CellType.ERROR, "UNKNOWN");
        }

        String resultStr = result.toString().toUpperCase();
        return new EffectiveValueImpl(CellType.BOOLEAN, resultStr);    }

    @Override
    public CellType getFunctionResultType() { return CellType.BOOLEAN; }
}