package dto;

import sheet.api.EffectiveValue;
import sheet.cell.api.Cell;
import sheet.coordinate.impl.CoordinateFactory;
import sheet.coordinate.impl.CoordinateImpl;

import java.util.ArrayList;
import java.util.List;

public class DTOcell {
    private int row;
    private int col;
    private String effectiveValue;
    private String originalValue;
    private int version;
    private List<String> dependsOn;
    private List<String> influencingOn;


    public DTOcell(int row, int col, String effectiveValue, String originalValue, int version, List<Cell> dependsOn, List<Cell> influencingOn) {
        this.row = row;
        this.col = col;
        this.effectiveValue = effectiveValue;
        this.originalValue = originalValue;
        this.version = version;
        this.dependsOn = convertListCellsToString(dependsOn);
        this.influencingOn = convertListCellsToString(influencingOn);
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

    public String getOriginalValue() {return originalValue;}

    public int getVersion() {return version;}

    public List<String> getDependsOn() {return dependsOn;}

    public List<String> getInfluencingOn() {return influencingOn;}

    private List<String> convertListCellsToString(List<Cell> cells) {
        List<String> cellsList = new ArrayList<>();

        for (Cell cell : cells) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(CoordinateImpl.convertNumberToAlphabetString(cell.getCoordinate().getColumn()));
            stringBuilder.append(cell.getCoordinate().getRow());

            cellsList.add(stringBuilder.toString());
        }
        return cellsList;
    }
}
