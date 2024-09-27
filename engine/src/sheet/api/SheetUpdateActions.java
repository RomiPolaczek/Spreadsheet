package sheet.api;

import sheet.impl.SheetImpl;
import sheet.layout.api.Layout;

import java.util.List;

public interface SheetUpdateActions {
    void setCell(int row, int column, String value);
    Sheet updateCellValueAndCalculate(int row, int column, String value);
    void setLayout(Layout layout);
    void setName(String name);
    void IncreaseVersion();
    Sheet copySheet();
    void setEmptyCell(int row, int column);
    void addRange(String name, String rangeStr);
    void removeRange(String name);
    //void copyRow(int selectedRow, int startColumn, int endColumn, int startRow, int endRow, Sheet originalSheet);
    Sheet filterColumnBasedOnSelection(String rangeStr, List<String> checkBoxesValues, String selectedColumn);
    Sheet sortColumnBasedOnSelection(String rangeStr, List<String> selectedColumns);
}
