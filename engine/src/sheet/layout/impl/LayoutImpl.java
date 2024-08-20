package sheet.layout.impl;

import jakarta.xml.bind.ValidationException;
import sheet.layout.api.Layout;

public class LayoutImpl implements Layout {
    private int rowsHeightUnits;
    private int columnsWidthUnits;
    private int rows;
    private int columns;

    private final int ROWS_LOWER_LIMIT = 1 ;
    private final int COLUMNS_LOWER_LIMIT = 1 ;
    private final int ROWS_UPPER_LIMIT = 50 ;
    private final int COLUMNS_UPPER_LIMIT = 20 ;


    @Override
    public void setRowsHeightUnits(int rowsHeightUnits) {
        try
        {
            CheckValidation(rowsHeightUnits, "Rows Height Units");
            this.rowsHeightUnits = rowsHeightUnits;
        }
        catch (ValidationException e)
        {
            //לבדוק אם אנחנו רוצות לתפוס פה או לזרוק הלאה
        }
    }
    @Override
    public void setColumnsWidthUnits(int columnsWidthUnits) {
        try
        {
            CheckValidation(columnsWidthUnits, "Columns Height Units");
            this.columnsWidthUnits = columnsWidthUnits;
        }
        catch (ValidationException e)
        {
            //לבדוק אם אנחנו רוצות לתפוס פה או לזרוק הלאה
        }
    }

    @Override
    public void setRows(int rows) {
        try
        {
            CheckValidation(rows, ROWS_UPPER_LIMIT, ROWS_LOWER_LIMIT, "Rows");
            this.rows = rows;
        }
        catch (ValidationException e)
        {
            //לבדוק אם אנחנו רוצות לתפוס פה או לזרוק הלאה
        }
    }

    @Override
    public void setColumns(int columns) {
        try
        {
            CheckValidation(columns, COLUMNS_UPPER_LIMIT, COLUMNS_LOWER_LIMIT, "Columns");
            this.columns = columns;
        }
        catch (ValidationException e)
        {
            //לבדוק אם אנחנו רוצות לתפוס פה או לזרוק הלאה
        }
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public int getColumns() {
        return columns;
    }

    @Override
    public int getRowsHeightUnits(){ return rowsHeightUnits; }

    @Override
    public int getColumnsWidthUnits(){ return columnsWidthUnits; }

    @Override
    public void CheckValidation(int value, int upperLimit, int lowerLimit, String str) throws ValidationException {
        if(value > upperLimit || value < lowerLimit) {
            throw new ValidationException(str + " must be between " + upperLimit + " and " + lowerLimit);
        }
    }

    @Override
    public void CheckValidation(int value, String str) throws ValidationException {
        if(value <= 0)
            throw new ValidationException(str + " must be greater than zero");
    }

}