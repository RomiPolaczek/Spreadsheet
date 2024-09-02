package expression.impl;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.impl.EffectiveValueImpl;

public class ConcatExpression implements Expression {

    private Expression left;
    private Expression right;

    public ConcatExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) throws Exception {
        EffectiveValue leftValue = left.eval(sheet);
        EffectiveValue rightValue = right.eval(sheet);

        String result;
        try{
            result = leftValue.extractValueWithExpectation(String.class) + rightValue.extractValueWithExpectation(String.class);
        }
        catch(Exception e){
            return new EffectiveValueImpl(CellType.ERROR, "!UNDEFINED!");
        }

        return new EffectiveValueImpl(CellType.STRING, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.STRING;
    }
}
