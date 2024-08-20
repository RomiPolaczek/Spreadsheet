package expression.impl;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.coordinate.CoordinateImpl;
import sheet.impl.EffectiveValueImpl;

public class RefExpression implements Expression {
    private Expression e;

    public RefExpression(Expression e) { this.e = e; }

    @Override
    public EffectiveValue eval() {
        EffectiveValue eval = e.eval();
        String result = eval.extractValueWithExpectation(String.class);
        int[] coordinates = getCoordinates(result);
       // Cell cell =

        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }


    public static int[] getCoordinates(String cellId) {
        // Validate the cell ID first
        if (!isValidCellId(cellId)) {
            throw new IllegalArgumentException("Invalid cell ID: " + cellId);
        }

        // Separate the letters from the numbers
        StringBuilder columnPart = new StringBuilder();
        StringBuilder rowPart = new StringBuilder();

        for (char c : cellId.toCharArray()) {
            if (Character.isLetter(c)) {
                columnPart.append(c);
            } else if (Character.isDigit(c)) {
                rowPart.append(c);
            }
        }

        // Convert the column letters to a number
        int columnNumber = CoordinateImpl.convertStringColumnToNumber(columnPart.toString());
        // Parse the row part
        int rowNumber = Integer.parseInt(rowPart.toString());


        // CHECK THAT THE CELL IS IN THE OF THE SHEET!!!!


        return new int[]{rowNumber, columnNumber};
    }

    private static boolean isValidCellId(String cellId) {
        // A valid cell ID should match the pattern: letters followed by digits
        return cellId.matches("^[A-Z]+[0-9]+$");
    }
}
