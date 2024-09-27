package sheet.api;

import sheet.cell.api.Cell;
import sheet.coordinate.api.Coordinate;
import sheet.layout.api.Layout;
import sheet.range.Range;

import java.util.List;
import java.util.Map;

public interface SheetReadActions {
    int getVersion();
    Cell getCell(int row, int column);
    String getName();
    Layout getLayout();
    Map<Coordinate, Cell> getActiveCells();
    Cell getCell(Coordinate coordinate);
    int getNumberCellsThatHaveChanged();
    Map<String, Range> getStringToRange();
    List<Double> getNumericalValuesFromRange(String range) throws IllegalArgumentException;
    List<String> createListOfValuesForFilter(String column, String range);
    List<String> getColumnsWithinRange(String range);
}
