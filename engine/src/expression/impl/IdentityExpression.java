package expression.impl;

import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.cell.api.Cell;
import sheet.impl.EffectiveValueImpl;
import expression.api.Expression;

public class IdentityExpression implements Expression {

    private final Object value;
    private final CellType type;

    public IdentityExpression(Object value, CellType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet, Cell cell) throws Exception {
        return new EffectiveValueImpl(type, value);
    }

    @Override
    public CellType getFunctionResultType() {
        return type;
    }
}
