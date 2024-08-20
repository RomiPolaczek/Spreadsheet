package expression.impl;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
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

    public EffectiveValue eval() {
        EffectiveValue sourceValue = source.eval();
        EffectiveValue startIndexValue = startIndex.eval();
        EffectiveValue endIndexValue = endIndex.eval();
        // do some checking... error handling...

        String sourceString = sourceValue.extractValueWithExpectation(String.class);
        double start = startIndexValue.extractValueWithExpectation(Double.class);
        double end = endIndexValue.extractValueWithExpectation(Double.class);

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
