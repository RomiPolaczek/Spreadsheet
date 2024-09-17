package expression.impl;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.impl.EffectiveValueImpl;

public class PercentExpression implements Expression {

    private Expression part;
    private Expression whole;

    public PercentExpression(Expression part, Expression whole) {
        this.part = part;
        this.whole = whole;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) throws Exception {
        double result;
        EffectiveValue partValue = part.eval(sheet);
        EffectiveValue wholeValue = whole.eval(sheet);

        try {
            result = (partValue.extractValueWithExpectation(Double.class) * wholeValue.extractValueWithExpectation(Double.class)) / 100;
        }
        catch (Exception e) {
            return new EffectiveValueImpl(CellType.ERROR, "NaN");
        }

        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }
}
