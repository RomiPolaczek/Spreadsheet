package sheet.coordinate.impl;

import sheet.api.SheetReadActions;
import sheet.coordinate.api.Coordinate;
import java.util.HashMap;
import java.util.Map;


public class CoordinateFactory {

    private static Map<String, Coordinate> cachedCoordinates = new HashMap<>();

    public static Coordinate createCoordinate(int row, int column) {

        String key = row + ":" + column;
        if (cachedCoordinates.containsKey(key)) {
            return cachedCoordinates.get(key);
        }

        CoordinateImpl coordinate = new CoordinateImpl(row, column);
        cachedCoordinates.put(key, coordinate);

        return coordinate;
    }

    public static Coordinate from(String trim) {
        try {
            if (!trim.matches("^[A-Za-z]+\\d+$")) {
                throw new IllegalArgumentException("Invalid coordinate provided. Please provide a valid cell identity (e.g., A4, C3).");
            }

            // Separate the letters from the numbers
            StringBuilder columnPart = new StringBuilder();
            StringBuilder rowPart = new StringBuilder();

            for (char c : trim.toCharArray()) {
                if (Character.isLetter(c)) {
                    char upperCaseLetter = Character.toUpperCase(c);
                    columnPart.append(upperCaseLetter);
                } else if (Character.isDigit(c)) {
                    rowPart.append(c);
                }
            }

            // Convert the column letters to a number
            int columnNumber =  CoordinateImpl.convertStringColumnToNumber(columnPart.toString());
            // Parse the row part
            int rowNumber = Integer.parseInt(rowPart.toString());

            return createCoordinate(rowNumber, columnNumber);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid coordinate provided, please provide a valid cell identity (e.g., A4 or B7).");
        }
    }

    public static void isValidCoordinate(Coordinate coordinate, SheetReadActions sheet) {
        int row = coordinate.getRow();
        int column = coordinate.getColumn();
        int rowLowerLimit = sheet.getLayout().getRowsLowerLimit();
        int rowUpperLimit = sheet.getLayout().getRows();
        int columnUpperLimit = sheet.getLayout().getColumns();
        int columnLowerLimit = sheet.getLayout().getColsLowerLimit();
        String columnStr = CoordinateImpl.convertNumberToAlphabetString(column);
        String columnUpperLimitStr = CoordinateImpl.convertNumberToAlphabetString(columnUpperLimit);
        String columnLowerLimitStr = CoordinateImpl.convertNumberToAlphabetString(columnLowerLimit);
        String exception;

        if((row > rowUpperLimit || row < rowLowerLimit)  && (column > columnUpperLimit || column < columnLowerLimit)) {
            exception =
                    "Expected rows to be between " + rowLowerLimit + "-" + rowUpperLimit + " but got " + row +
                    "\nExpected columns to be between " + columnLowerLimitStr + "-" + columnUpperLimitStr + " but got " + columnStr;
            throw new IllegalArgumentException(exception);
        }
        else if(row > rowUpperLimit || row < rowLowerLimit) {
            exception = "Expected rows to be between " + rowLowerLimit + "-" + rowUpperLimit + " but got " + row;
            throw new IllegalArgumentException(exception);
        }
        else if(column > columnUpperLimit || column < columnLowerLimit) {
            exception = "Expected columns to be between " + columnLowerLimitStr + "-" + columnUpperLimitStr + " but got " + columnStr;
            throw new IllegalArgumentException(exception);
        }
    }
}
