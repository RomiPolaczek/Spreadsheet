package expression.impl;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.coordinate.api.Coordinate;
import sheet.impl.EffectiveValueImpl;
import sheet.range.Range;

public class AverageExpression implements Expression {
    private Expression rangeExp;
    Range range;

    public AverageExpression(Expression range) {
        this.rangeExp = range;
    }

     @Override
     public EffectiveValue eval(SheetReadActions sheet) throws Exception {
        EffectiveValue rangeValue = rangeExp.eval(sheet);
        double sum = 0;
        double result = 0;

        try {
            String rangeName = rangeValue.extractValueWithExpectation(String.class);
            range = sheet.getStringToRange().get(rangeName);
            for (Coordinate coordinate : range.getCells())
            {
                sum += sheet.getCell(coordinate).getEffectiveValue().extractValueWithExpectation(Double.class);
            }
            result = sum / range.getCells().size();
        }
        catch (Exception e)
        {
            return new EffectiveValueImpl(CellType.ERROR, "NaN");
        }

        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }
}
