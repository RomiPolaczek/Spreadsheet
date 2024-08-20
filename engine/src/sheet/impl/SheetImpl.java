package sheet.impl;

import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.cell.impl.CellImpl;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;
import sheet.coordinate.CoordinateImpl;
import sheet.layout.api.Layout;
import sheet.layout.impl.LayoutImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SheetImpl implements Sheet {

    private Map<Coordinate, Cell> activeCells;
    private LayoutImpl layout;
    private String name;
    private int version = 1;

    public SheetImpl(){
        this.activeCells = new HashMap<>();
    }

    @Override
    public void setLayout(LayoutImpl layout) {
        this.layout = layout;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public Layout getLayout() {
        return layout;
    }

    @Override
    public Cell getCell(int row, int column) {
        return activeCells.get(CoordinateFactory.createCoordinate(row, column));
    }

    @Override
    public void setCell(int row, int column, String value) {
        if(row > layout.getRows() - 1)
            throw new IndexOutOfBoundsException("Row " + row+1 + " out of bounds");
        if(column > layout.getColumns() - 1)
            throw new IndexOutOfBoundsException("Column " + CoordinateImpl.convertNumberToAlphabetString(column) + " out of bounds");

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
