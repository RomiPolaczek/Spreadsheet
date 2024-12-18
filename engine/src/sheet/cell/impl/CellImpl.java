package sheet.cell.impl;

import expression.api.Expression;
import expression.parser.FunctionParser;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.cell.api.Cell;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.impl.CoordinateImpl;
import sheet.api.SheetReadActions;
import sheet.impl.EffectiveValueImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CellImpl implements Cell, Serializable {

    private final Coordinate coordinate;
    private String originalValue;
    private EffectiveValue effectiveValue;
    private int version;
    private List<Coordinate> dependsOn;
    private List<Coordinate> influencingOn;
    private SheetReadActions sheet;
    private String userName;


    public CellImpl(int row, int column, String originalValue, int version, SheetReadActions sheet, String userName)  {
        this.sheet = sheet;
        this.coordinate = new CoordinateImpl(row, column);
        this.originalValue = originalValue;
        this.version = version;
        this.dependsOn = new ArrayList<>();
        this.influencingOn = new ArrayList<>();
        this.userName = userName;
    }

    @Override
    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public String getOriginalValue() {
        return originalValue;
    }

    @Override
    public void setCellOriginalValue(String value) {
        this.originalValue = value;
    }

    @Override
    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }

    @Override
    public boolean calculateEffectiveValue(){
        // build the expression object out of the original value...
        // it can be {PLUS, 4, 5} OR {CONCAT, {ref, A4}, world}
        if(Objects.equals(originalValue, "")){
            effectiveValue = new EffectiveValueImpl(CellType.EMPTY, "");
            return false;
        }

        try
        {
            Expression expression = FunctionParser.parseExpression(originalValue);
            EffectiveValue newEffectiveValue = expression.eval(sheet);
            newEffectiveValue = newEffectiveValue.upperCaseIfBoolean();

            if (newEffectiveValue.equals(effectiveValue)) {
                return false;
            } else {
                effectiveValue = newEffectiveValue;
                return true;
            }
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void updateVersion(int version){
        this.version = version;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public String getUserName() { return userName; }

    @Override
    public List<Coordinate> getDependsOn() {
        return dependsOn;
    }

    @Override
    public List<Coordinate> getInfluencingOn() {
        return influencingOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellImpl cell = (CellImpl) o;
        return version == cell.version && Objects.equals(coordinate, cell.coordinate) && Objects.equals(originalValue, cell.originalValue) && Objects.equals(effectiveValue, cell.effectiveValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinate, originalValue, effectiveValue, version);
    }

}
