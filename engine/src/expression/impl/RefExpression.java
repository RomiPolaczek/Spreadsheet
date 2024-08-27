package expression.impl;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.cell.api.Cell;
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
    public EffectiveValue eval(SheetReadActions sheet, Cell cell) throws Exception {
        // error handling if the cell is empty or not found
        CoordinateFactory.isValidCoordinate(coordinate, sheet);
        CellImpl newCell = (CellImpl) sheet.getCell(CoordinateFactory.createCoordinate(coordinate.getRow(), coordinate.getColumn()));
        if(newCell == null)
        {
            String cellStr = CoordinateImpl.convertNumberToAlphabetString(coordinate.getColumn()) + coordinate.getRow();
            throw new Exception("The cell " + cellStr + " is empty or not found.");
        }
//        if(!cell.getDependsOn().contains(newCell))
//            cell.getDependsOn().add(newCell);
//        if(!newCell.getInfluencingOn().contains(cell))
//            newCell.getInfluencingOn().add(cell);

//        boolean isInDependsOn = false;
//        for (Cell dependentCell : cell.getDependsOn()) {
//            if (dependentCell.getCoordinate().equals(newCell.getCoordinate())) {
//                isInDependsOn = true;
//                break;
//            }
//        }
//        if (!isInDependsOn) {
//            cell.getDependsOn().add(newCell);
//        }
//
//        // Check if 'cell' is already in 'newCell.getInfluencingOn()' by comparing coordinates
//        boolean isInInfluencingOn = false;
//        for (Cell influencingCell : newCell.getInfluencingOn()) {
//            if (influencingCell.getCoordinate().equals(cell.getCoordinate())) {
//                isInInfluencingOn = true;
//                break;
//            }
//        }
//        if (!isInInfluencingOn) {
//            newCell.getInfluencingOn().add(cell);
//        }

        return newCell.getEffectiveValue();
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.UNKNOWN;
    }

}
