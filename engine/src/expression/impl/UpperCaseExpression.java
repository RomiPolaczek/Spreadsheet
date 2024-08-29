package expression.impl;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.impl.EffectiveValueImpl;

public class UpperCaseExpression implements Expression {

    private final Expression e;

    public UpperCaseExpression(Expression value) {
        this.e = value;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) throws Exception {
        String upperCaseResult;
        EffectiveValue eval = e.eval(sheet);

        try{
            upperCaseResult = eval.extractValueWithExpectation(String.class).toUpperCase();
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Invalid argument types for UPPER_CASE function. Expected STRING, but got " + eval.getCellType());
        }

        return new EffectiveValueImpl(CellType.STRING, upperCaseResult);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.STRING;
    }
}
