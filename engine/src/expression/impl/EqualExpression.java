package expression.impl;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.impl.EffectiveValueImpl;

public class EqualExpression implements Expression {

    private Expression left;
    private Expression right;

    public EqualExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) throws Exception {
        EffectiveValue leftValue = left.eval(sheet);
        EffectiveValue rightValue = right.eval(sheet);

        Boolean result = false;

        if (leftValue.getValue() != null && rightValue.getValue() != null) {
            if (!leftValue.getCellType().equals(rightValue.getCellType())){
                result = false; // Different types, so not equal
            }
            else {
                result = leftValue.getValue().equals(rightValue.getValue());
            }
        }

        return new EffectiveValueImpl(CellType.BOOLEAN, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.BOOLEAN;
    }
}
