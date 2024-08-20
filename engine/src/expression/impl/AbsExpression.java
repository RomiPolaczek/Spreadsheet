package expression.impl;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.impl.EffectiveValueImpl;

public class AbsExpression implements Expression {
    private Expression e;

    public AbsExpression(Expression e) { this.e = e; }

    @Override
    public EffectiveValue eval() {
        EffectiveValue eval = e.eval();
        double result = eval.extractValueWithExpectation(Double.class);

        if(result < 0)
            result = result * (-1);

        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }

}
