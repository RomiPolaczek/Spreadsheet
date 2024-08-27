package sheet.api;

import sheet.cell.api.Cell;
import sheet.coordinate.api.Coordinate;
import sheet.layout.api.Layout;

import java.util.Map;

public interface SheetReadActions {
    int getVersion();
    Cell getCell(int row, int column);
    String getName();
    Layout getLayout();
    Map<Coordinate, Cell> getActiveCells();
    Cell getCell(Coordinate coordinate);
}
