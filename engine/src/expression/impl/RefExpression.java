package expression.impl;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.impl.CoordinateFactory;

public class RefExpression implements Expression {

    private final Coordinate coordinate;

    public RefExpression(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) throws Exception {
        // error handling if the cell is empty or not found
        CoordinateFactory.isValidCoordinate(coordinate, sheet);
        if(sheet.getCell(coordinate.getRow(), coordinate.getColumn()) == null)
            throw new Exception("The cell is empty or not found.");

        return sheet.getCell(coordinate.getRow(), coordinate.getColumn()).getEffectiveValue();
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.UNKNOWN;
    }
}
