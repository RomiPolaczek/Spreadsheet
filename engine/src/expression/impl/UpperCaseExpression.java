package expression.impl;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.impl.EffectiveValueImpl;

public class UpperCaseExpression implements Expression {

    private final String value;

    public UpperCaseExpression(String value) {
        this.value = value;
    }

    @Override
    public EffectiveValue eval() {
        return new EffectiveValueImpl(CellType.STRING, value.toUpperCase());
    }
}
