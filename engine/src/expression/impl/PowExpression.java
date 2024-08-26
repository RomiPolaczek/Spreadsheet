package expression.impl;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.cell.api.Cell;
import sheet.impl.EffectiveValueImpl;

public class PowExpression implements Expression {

    private Expression left;
    private Expression right;

    public PowExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet, Cell cell) throws Exception {
        EffectiveValue leftValue = left.eval(sheet, cell);
        EffectiveValue rightValue = right.eval(sheet, cell);
        // do some checking... error handling...
        //double result = (Double) leftValue.getValue() + (Double) rightValue.getValue();
        double result = Math.pow(leftValue.extractValueWithExpectation(Double.class), rightValue.extractValueWithExpectation(Double.class));
        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }
}
