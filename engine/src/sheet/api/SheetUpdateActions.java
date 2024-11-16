package sheet.api;

import sheet.impl.SheetImpl;
import sheet.layout.api.Layout;

import java.util.List;
import java.util.Map;

public interface SheetUpdateActions {
    void setCell(int row, int column, String value);
    Sheet updateCellValueAndCalculate(int row, int column, String value, String username);
    void setLayout(Layout layout);
    void setName(String name);
    void IncreaseVersion();
    Sheet copySheet();
    void setEmptyCell(int row, int column, String username);
    void addRange(String name, String rangeStr);
    void removeRange(String name);
    Sheet filterColumnBasedOnSelection(String rangeStr, Map<String, List<String>> columnToValues, Map<String, String> newCoordToOldCoord);
    Sheet sortColumnBasedOnSelection(String rangeStr, List<String> selectedColumns, Map<String, String> newCoordToOldCoord);
}
