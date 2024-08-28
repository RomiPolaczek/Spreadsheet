package sheet.layout.api;


public interface Layout {
    void setRowsHeightUnits(int rowsHeightUnits);
    void setColumnsWidthUnits(int columnsWidthUnits);
    void setRows(int rows);
    void setColumns(int columns);
    int getColumns();
    int getRows();
    int getRowsHeightUnits();
    int getColumnsWidthUnits();
    int getRowsLowerLimit();
    int getColsLowerLimit();
    void CheckValidation(int value, String str) throws Exception;
}
