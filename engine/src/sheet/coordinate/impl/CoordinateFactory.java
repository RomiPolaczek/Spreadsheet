package sheet.coordinate.impl;

import sheet.api.SheetReadActions;
import sheet.coordinate.api.Coordinate;
import sheet.layout.impl.LayoutImpl;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class CoordinateFactory implements Serializable {

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
            // Separate the letters from the numbers
            StringBuilder columnPart = new StringBuilder();
            StringBuilder rowPart = new StringBuilder();

            for (char c : trim.toCharArray()) {
                if (Character.isLetter(c)) {
                    columnPart.append(c);
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
        catch (NumberFormatException e) {      ////FIX EXCEPTIONNNNNN
            return null;
        }
    }

    public static void isValidCoordinate(Coordinate coordinate, SheetReadActions sheet) throws Exception {
        LayoutImpl.CheckValidation(coordinate.getRow(), sheet.getLayout().getRows(), sheet.getLayout().getRowsLowerLimit(), "Row");
        LayoutImpl.CheckValidation(coordinate.getColumn(), sheet.getLayout().getColumns(), sheet.getLayout().getColsLowerLimit(), "Column");
    }
}
