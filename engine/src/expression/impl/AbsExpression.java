package expression.impl;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.impl.EffectiveValueImpl;

public class AbsExpression implements Expression {
    private Expression e;

    public AbsExpression(Expression e) { this.e = e; }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) throws Exception {
        double result;
        EffectiveValue eval = e.eval(sheet);

        try {
            result = eval.extractValueWithExpectation(Double.class);
        }
        catch (Exception e){
            return new EffectiveValueImpl(CellType.ERROR, "NaN");
        }

        if(result < 0)
            result = result * (-1);

        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }
}
