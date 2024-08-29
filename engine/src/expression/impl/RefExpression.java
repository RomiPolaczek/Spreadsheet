package expression.impl;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.cell.impl.CellImpl;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.impl.CoordinateFactory;
import sheet.coordinate.impl.CoordinateImpl;

import java.io.Serializable;

public class RefExpression implements Expression, Serializable {

    private Coordinate coordinate;

    public RefExpression(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) throws Exception {
        CoordinateFactory.isValidCoordinate(coordinate, sheet);
        CellImpl newCell = (CellImpl) sheet.getCell(CoordinateFactory.createCoordinate(coordinate.getRow(), coordinate.getColumn()));
        if (newCell == null) {
            String cellStr = CoordinateImpl.convertNumberToAlphabetString(coordinate.getColumn()) + coordinate.getRow();
            throw new Exception("The cell " + cellStr + " is empty or not found.");
        }

        return newCell.getEffectiveValue();
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.UNKNOWN;
    }
}

