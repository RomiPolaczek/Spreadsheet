package expression.api;

import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.cell.api.Cell;

public interface Expression {
    EffectiveValue eval(SheetReadActions sheet, Cell cell) throws Exception;
    CellType getFunctionResultType();
}
