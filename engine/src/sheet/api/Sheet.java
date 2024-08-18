package sheet.api;

import sheet.cell.api.Cell;
import sheet.layout.impl.LayoutImpl;

public interface Sheet {
    int getVersion();
    String getName();
    Cell getCell(int row, int column);
    void setCell(int row, int column, String value);
    void setLayout(LayoutImpl layout);
    void setName(String name);
}
