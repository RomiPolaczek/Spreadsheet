package expression.impl;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.impl.CoordinateFactory;


import java.io.Serializable;

public class RefExpression implements Expression, Serializable {

    private Coordinate coordinate;

    public RefExpression(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) throws Exception {
        CoordinateFactory.isValidCoordinate(coordinate, sheet.getLayout());
        return sheet.getCell(CoordinateFactory.createCoordinate(coordinate.getRow(), coordinate.getColumn())).getEffectiveValue();
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.UNKNOWN;
    }
}

