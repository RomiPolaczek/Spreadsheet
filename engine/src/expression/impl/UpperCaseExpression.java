package expression.impl;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.cell.api.Cell;
import sheet.impl.EffectiveValueImpl;

public class UpperCaseExpression implements Expression {

    private final Expression e;

    public UpperCaseExpression(Expression value) {
        this.e = value;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) throws Exception {
        EffectiveValue eval = e.eval(sheet);
        String upperCaseResult = eval.extractValueWithExpectation(String.class).toUpperCase();
        return new EffectiveValueImpl(CellType.STRING, upperCaseResult);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.STRING;
    }
}
