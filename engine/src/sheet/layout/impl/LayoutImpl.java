package sheet.layout.impl;

import sheet.layout.api.Layout;

import java.io.Serializable;
import java.util.Objects;

public class LayoutImpl implements Layout, Serializable {
    private int rowsHeightUnits;
    private int columnsWidthUnits;
    private int rows;
    private int columns;

    private final int ROWS_LOWER_LIMIT = 1 ;
    private final int COLUMNS_LOWER_LIMIT = 1 ;
    private final int ROWS_UPPER_LIMIT = 50 ;
    private final int COLUMNS_UPPER_LIMIT = 20 ;


    @Override
    public void setRowsHeightUnits (int rowsHeightUnits)  {
        CheckValidation(rowsHeightUnits, "Rows Height Units");
        this.rowsHeightUnits = rowsHeightUnits;
    }

    @Override
    public void setColumnsWidthUnits (int columnsWidthUnits) {
        CheckValidation(columnsWidthUnits, "Columns Height Units");
        this.columnsWidthUnits = columnsWidthUnits;
    }

    @Override
    public void setRows (int rows) {
        CheckValidation(rows, ROWS_UPPER_LIMIT, ROWS_LOWER_LIMIT, "Rows");
        this.rows = rows;
    }

    @Override
    public void setColumns (int columns) {
        CheckValidation(columns, COLUMNS_UPPER_LIMIT, COLUMNS_LOWER_LIMIT, "Columns");
        this.columns = columns;
    }

    @Override
    public int getRows () {
        return rows;
    }

    @Override
    public int getColumns () {
        return columns;
    }

    @Override
    public int getRowsHeightUnits (){ return rowsHeightUnits; }

    @Override
    public int getColumnsWidthUnits (){ return columnsWidthUnits; }

    @Override
    public int getRowsLowerLimit () { return ROWS_LOWER_LIMIT; }

    @Override
    public int getColsLowerLimit () { return COLUMNS_LOWER_LIMIT; }

    public static void CheckValidation (int value, int upperLimit, int lowerLimit, String str) {
        if(value > upperLimit || value < lowerLimit) {
            throw new IllegalArgumentException(str + " must be between " + lowerLimit + " and " + upperLimit);
        }
    }

    @Override
    public void CheckValidation (int value, String str) {
        if(value <= 0)
            throw new IllegalArgumentException(str + " must be greater than zero");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LayoutImpl layout = (LayoutImpl) o;
        return rowsHeightUnits == layout.rowsHeightUnits && columnsWidthUnits == layout.columnsWidthUnits && rows == layout.rows && columns == layout.columns && ROWS_LOWER_LIMIT == layout.ROWS_LOWER_LIMIT && COLUMNS_LOWER_LIMIT == layout.COLUMNS_LOWER_LIMIT && ROWS_UPPER_LIMIT == layout.ROWS_UPPER_LIMIT && COLUMNS_UPPER_LIMIT == layout.COLUMNS_UPPER_LIMIT;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowsHeightUnits, columnsWidthUnits, rows, columns, ROWS_LOWER_LIMIT, COLUMNS_LOWER_LIMIT, ROWS_UPPER_LIMIT, COLUMNS_UPPER_LIMIT);
    }
}
