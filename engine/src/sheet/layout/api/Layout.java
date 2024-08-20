package sheet.layout.api;

import jakarta.xml.bind.ValidationException;

public interface Layout {
    void setRowsHeightUnits(int rowsHeightUnits);
    void setColumnsWidthUnits(int columnsWidthUnits);
    void setRows(int rows);
    void setColumns(int columns);
    int getColumns();
    int getRows();
    int getRowsHeightUnits();
    int getColumnsWidthUnits();
    void CheckValidation(int value, int upperLimit, int lowerLimit, String str) throws ValidationException;
    public void CheckValidation(int value, String str) throws ValidationException;
}
