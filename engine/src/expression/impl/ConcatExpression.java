package expression.impl;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.cell.api.Cell;
import sheet.impl.EffectiveValueImpl;

public class ConcatExpression implements Expression {

    private Expression left;
    private Expression right;

    public ConcatExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet, Cell cell) throws Exception {
        EffectiveValue leftValue = left.eval(sheet, cell);
        EffectiveValue rightValue = right.eval(sheet, cell);
        // do some checking... error handling...
        String result = leftValue.extractValueWithExpectation(String.class) + rightValue.extractValueWithExpectation(String.class);

        return new EffectiveValueImpl(CellType.STRING, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.STRING;
    }
}
