package expression.impl;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.impl.EffectiveValueImpl;

import java.util.InputMismatchException;

public class IfExpression implements Expression {

    private Expression condition;
    private Expression thenPart;
    private Expression elsePart;

    public IfExpression(Expression condition, Expression thenPart, Expression elsePart) {
        this.condition = condition;
        this.thenPart = thenPart;
        this.elsePart = elsePart;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) throws Exception {
        EffectiveValue conditionValue = condition.eval(sheet);

        Boolean conditionResult;

        CellType thenType = thenPart.getFunctionResultType();
        CellType elseType = elsePart.getFunctionResultType();

        try {
            if(!thenType.equals(elseType))
                throw new InputMismatchException();
            conditionResult = conditionValue.extractValueWithExpectation(Boolean.class);
        } catch (Exception e) {
            return new EffectiveValueImpl(CellType.ERROR, "UNKNOWN");
        }

        if (conditionResult) {
            return thenPart.eval(sheet);
        } else {
            return elsePart.eval(sheet);
        }
    }

    //לוודא שככה זה באמת צריך להיות או שהבדיקה למעלה מספיקה ואז מספיק להחזיר רק את הthen למשל
    @Override
    public CellType getFunctionResultType() {
        // The result type of IF depends on the types of the then and else expressions
        CellType thenType = thenPart.getFunctionResultType();
        CellType elseType = elsePart.getFunctionResultType();

        if (thenType == elseType) {
            return thenType;
        }

        // Return ERROR if then and else types don't match
        return CellType.ERROR;
    }
}
