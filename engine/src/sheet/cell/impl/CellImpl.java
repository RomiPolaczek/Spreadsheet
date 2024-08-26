package sheet.cell.impl;

import expression.api.Expression;
import expression.parser.FunctionParser;
import sheet.api.EffectiveValue;
import sheet.cell.api.Cell;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.impl.CoordinateImpl;
import sheet.api.SheetReadActions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CellImpl implements Cell, Serializable {

    private final Coordinate coordinate;
    private String originalValue;
    private EffectiveValue effectiveValue;
    private int version;
    private List<Cell> dependsOn;
    private List<Cell> influencingOn;
    private final SheetReadActions sheet;


    public CellImpl(int row, int column, String originalValue, int version, SheetReadActions sheet)  {
        this.sheet = sheet;
        this.coordinate = new CoordinateImpl(row, column);
        this.originalValue = originalValue;
        this.version = version;
        this.dependsOn = new ArrayList<>();
        this.influencingOn = new ArrayList<>();
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

        try //CHECK THIS AVI
        {
            Expression expression = FunctionParser.parseExpression(originalValue);

            EffectiveValue newEffectiveValue = expression.eval(sheet, this);

            if (newEffectiveValue.equals(effectiveValue)) {
                return false;
            } else {
                effectiveValue = newEffectiveValue;
                return true;
            }
        }
        catch (Exception e){
            return false;
        }
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public List<Cell> getDependsOn() {
        return dependsOn;
    }

    @Override
    public List<Cell> getInfluencingOn() {
        return influencingOn;
    }
}
