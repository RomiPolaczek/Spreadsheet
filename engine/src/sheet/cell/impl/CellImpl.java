package sheet.cell.impl;

import expression.api.Expression;
import expression.impl.UpperCaseExpression;
import expression.parser.FunctionParser;
import sheet.api.EffectiveValue;
import sheet.cell.api.Cell;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateImpl;

import java.util.List;

public class CellImpl implements Cell {

    private final Coordinate coordinate;
    private String originalValue;
    private EffectiveValue effectiveValue;
    private int version;
    private final List<Cell> dependsOn;
    private final List<Cell> influencingOn;

    public CellImpl(int row, int column, String originalValue, EffectiveValue effectiveValue, int version, List<Cell> dependsOn, List<Cell> influencingOn) {
        this.coordinate = new CoordinateImpl(row, column);
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.version = version;
        this.dependsOn = dependsOn;
        this.influencingOn = influencingOn;
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
    public void calculateEffectiveValue() {///////
        // build the expression object out of the original value...
        // it can be {PLUS, 4, 5} OR {CONCAT, hello, world}
        Expression expression = FunctionParser.parseExpression(originalValue);

        // second question: what is the return type of eval() ?
        effectiveValue = expression.eval();
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
