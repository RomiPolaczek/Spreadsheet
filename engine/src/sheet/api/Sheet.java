package sheet.api;

import sheet.cell.api.Cell;
import sheet.coordinate.Coordinate;
import sheet.layout.api.Layout;
import sheet.layout.impl.LayoutImpl;

import java.util.Map;

public interface Sheet {
    int getVersion();
    String getName();
    Cell getCell(int row, int column);
    Layout getLayout();
    Map<Coordinate, Cell> getActiveCells();
    void setCell(int row, int column, String value);
    void setLayout(LayoutImpl layout);
    void setName(String name);
}
