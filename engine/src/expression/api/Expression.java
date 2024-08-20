package expression.api;

import sheet.api.CellType;
import sheet.api.EffectiveValue;

public interface Expression {
    EffectiveValue eval();
    CellType getFunctionResultType();
}
