package sheet.range;

import sheet.coordinate.api.Coordinate;
import sheet.coordinate.impl.CoordinateFactory;
import sheet.layout.api.Layout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Range implements Serializable {
    private List<Coordinate> cells;
    private String name;
    private Layout layout;
    private Coordinate topLeftCoordinate;
    private Coordinate bottomRightCoordinate;

    public Range(String name, Layout layout) {
        this.cells = new ArrayList<Coordinate>();
        this.name = name;
        this.layout = layout;
    }
    public List<Coordinate> getCells() { return cells; }

    public String getName() { return name; }

    public Coordinate getTopLeftCoordinate() { return topLeftCoordinate; }

    public Coordinate getBottomRightCoordinate() { return bottomRightCoordinate; }

    public void parseRange(String range) {
        // Split the input range into top-left and bottom-right cells
        String[] parts = range.split("\\.\\.");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid range format. Expected format: <top-left-cell>..<bottom-right-cell>");
        }

        String topLeft = parts[0].trim();
        String bottomRight = parts[1].trim();

        if(bottomRight.startsWith(".")) {
            throw new IllegalArgumentException("Invalid range format. Expected format: <top-left-cell>..<bottom-right-cell>");
        }

        // Convert top-left and bottom-right cell references into column letters and row numbers
        topLeftCoordinate = CoordinateFactory.from(topLeft);
        bottomRightCoordinate = CoordinateFactory.from(bottomRight);

        CoordinateFactory.isValidCoordinate(topLeftCoordinate, layout);
        CoordinateFactory.isValidCoordinate(bottomRightCoordinate, layout);

        if (topLeftCoordinate.getColumn() > bottomRightCoordinate.getColumn() /*|| topLeftCoordinate.getRow() > bottomRightCoordinate.getRow()*/) {
            throw new IllegalArgumentException("Invalid range. Expected <top-left-cell>..<bottom-right-cell>");
        }

        int topLeftRow = topLeftCoordinate.getRow();
        int topLeftColumn = topLeftCoordinate.getColumn();
        int bottomRightRow = bottomRightCoordinate.getRow();
        int bottomRightColumn = bottomRightCoordinate.getColumn();

        // Iterate over the range of columns and rows
        for (int col = topLeftColumn; col <= bottomRightColumn; col++) {
            if(topLeftRow == bottomRightRow) {
                cells.add(CoordinateFactory.createCoordinate(topLeftRow, col));
            }
            else {
                for (int row = topLeftRow; row <= bottomRightRow; row++) {
                    cells.add(CoordinateFactory.createCoordinate(row, col));
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(cells.getFirst().toString());
        stringBuilder.append("..");
        stringBuilder.append(cells.getLast().toString());
        return stringBuilder.toString();
    }
}
