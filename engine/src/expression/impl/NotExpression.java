package expression.impl;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.impl.EffectiveValueImpl;

public class NotExpression implements Expression {
    private Expression expression;

    public NotExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) throws Exception {
        EffectiveValue value = expression.eval(sheet);

        Boolean result;

        try{
            result = !(value.extractValueWithExpectation(Boolean.class));
        }
        catch(Exception e){
            return new EffectiveValueImpl(CellType.ERROR, "UNKNOWN");
        }

        return new EffectiveValueImpl(CellType.BOOLEAN, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.BOOLEAN;
    }
}
