package sheet.api;

import sheet.layout.api.Layout;

public interface SheetUpdateActions {
    Sheet updateCellValueAndCalculate(int row, int column, String value);
  //  void setCell(int row, int column, String value);
    void setLayout(Layout layout);
    void setName(String name);
}
