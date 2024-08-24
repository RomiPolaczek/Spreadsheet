package expression.api;

import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.coordinate.api.Coordinate;

public interface Expression {
    EffectiveValue eval(SheetReadActions sheet) throws Exception;
    CellType getFunctionResultType();
}
