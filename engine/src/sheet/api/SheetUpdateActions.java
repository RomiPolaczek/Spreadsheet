package sheet.api;

import sheet.impl.SheetImpl;
import sheet.layout.api.Layout;

public interface SheetUpdateActions {
    void setCell(int row, int column, String value);
    Sheet updateCellValueAndCalculate(int row, int column, String value);
    void setLayout(Layout layout);
    void setName(String name);
    void IncreaseVersion ();
    SheetImpl copySheet();
    void setEmptyCell(int row, int column);
    void addRange(String name, String rangeStr);
    void removeRange(String name);
}
