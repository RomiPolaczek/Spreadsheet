package dto;

import sheet.api.EffectiveValue;

public class DTOcell {
    private int row;
    private int col;
    private String effectiveValue;

    public  DTOcell(int row, int col, String effectiveValue) {
        this.row = row;
        this.col = col;
        this.effectiveValue = effectiveValue;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return col;
    }

    public String getEffectiveValue() {
        return effectiveValue;
    }
}
