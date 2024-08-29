package expression.impl;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.impl.EffectiveValueImpl;

public class SubExpression implements Expression {
    private Expression source;
    private Expression startIndex;
    private Expression endIndex;

    public SubExpression(Expression source, Expression startIndex, Expression endIndex) {
        this.source = source;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public EffectiveValue eval(SheetReadActions sheet) throws Exception {
        String sourceString;
        double start;
        double end;

        EffectiveValue sourceValue = source.eval(sheet);
        EffectiveValue startIndexValue = startIndex.eval(sheet);
        EffectiveValue endIndexValue = endIndex.eval(sheet);

        try
        {
            sourceString = sourceValue.extractValueWithExpectation(String.class);
            start = startIndexValue.extractValueWithExpectation(Double.class);
            end = endIndexValue.extractValueWithExpectation(Double.class);
        }
        catch(Exception e){
            throw new IllegalArgumentException("Invalid argument types for SUB function. Expected the arguments to be STRING - NUMERIC - NUMERIC but got " +
                    sourceValue.getCellType() + " - " + startIndexValue.getCellType() + " - " + endIndexValue.getCellType());
        }

        // Check the validity of the indices
        if (sourceString == null || start < 0 || end < start || end >= sourceString.length()) {
            return new EffectiveValueImpl(CellType.STRING, "!UNDEFINED!");
        }

        // Perform the substring operation
        String result = sourceString.substring((int) start, (int) end + 1);

        return new EffectiveValueImpl(CellType.STRING, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.STRING;
    }
}
