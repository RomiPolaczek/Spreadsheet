package sheet.impl;

import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.cell.impl.CellImpl;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SheetImpl implements Sheet {

    private Map<Coordinate, Cell> activeCells;

    public SheetImpl() {
        this.activeCells = new HashMap<>();
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public Cell getCell(int row, int column) {
        return activeCells.get(CoordinateFactory.createCoordinate(row, column));
    }

    @Override
    public void setCell(int row, int column, String value) {
        Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);
        Cell cell = activeCells.get(coordinate);

        if(cell == null) {
            EffectiveValue effectiveValue = new EffectiveValueImpl(CellType.setCellType(value), value); // example implementation
            int version = this.getVersion(); // You may have a different way to manage versions
            List<Cell> dependsOn = new ArrayList<>();
            List<Cell> influencingOn = new ArrayList<>();

            cell = new CellImpl(row, column, value, effectiveValue, version, dependsOn, influencingOn);
            activeCells.put(coordinate, cell);
        }
        cell.setCellOriginalValue(value);
    }
}
