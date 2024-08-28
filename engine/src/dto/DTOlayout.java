package dto;

import java.io.Serializable;

public class DTOlayout implements Serializable {
    private int rowsHeightUnits;
    private int columnsWidthUnits;
    private int rows;
    private int columns;

    public  DTOlayout(int rowsHeightUnits, int columnsWidthUnits, int rows, int columns) {
        this.rowsHeightUnits = rowsHeightUnits;
        this.columnsWidthUnits = columnsWidthUnits;
        this.rows = rows;
        this.columns = columns;
    }

    public int getRowsHeightUnits() {
        return rowsHeightUnits;
    }

    public int getColumnsWidthUnits() {
        return columnsWidthUnits;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

}
