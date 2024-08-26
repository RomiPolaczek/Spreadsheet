package sheet.coordinate.impl;

import sheet.coordinate.api.Coordinate;

import java.io.Serializable;

public class CoordinateImpl implements Coordinate, Serializable {
    private final int row;
    private final int column;

    public CoordinateImpl(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column;
    }

    public static String convertNumberToAlphabetString(int number) {
        StringBuilder result = new StringBuilder();

        while (number > 0) {
            number--;
            char letter = (char) ('A' + (number % 26));
            result.insert(0, letter);
            number /= 26;
        }

        return result.toString();
    }

    public static int convertStringColumnToNumber(String column) {
        int result = 0;
        int length = column.length();

        for (int i = 0; i < length; i++) {
            int position = column.charAt(i) - 'A' + 1;
            result = result * 26 + position;
        }
        return result;
    }
}
