package sheet.range;

import sheet.cell.api.Cell;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.impl.CoordinateFactory;
import sheet.layout.api.Layout;

import java.util.ArrayList;
import java.util.List;

public class Range {
    private List<Coordinate> cells;
    String name;
    Layout layout;

    public Range(String name, Layout layout) {
        this.cells = new ArrayList<Coordinate>();
        this.name = name;
        this.layout = layout;
    }
    public List<Coordinate> getCells() { return cells; }

    public String getName() { return name; }

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
        Coordinate topLeftCoordinate = CoordinateFactory.from(topLeft);
        Coordinate bottomRightCoordinate = CoordinateFactory.from(bottomRight);

        CoordinateFactory.isValidCoordinate(topLeftCoordinate, layout);
        CoordinateFactory.isValidCoordinate(bottomRightCoordinate, layout);

        if (topLeftCoordinate.getColumn() > bottomRightCoordinate.getColumn() || topLeftCoordinate.getRow() > bottomRightCoordinate.getRow()) {
            throw new IllegalArgumentException("Invalid range. Expected <top-left-cell>..<bottom-right-cell>");
        }

        int topLeftRow = topLeftCoordinate.getRow();
        int topLeftColumn = topLeftCoordinate.getColumn();
        int bottomRightRow = bottomRightCoordinate.getRow();
        int bottomRightColumn = bottomRightCoordinate.getColumn();

        // Iterate over the range of columns and rows
        for (int col = topLeftColumn; col <= bottomRightColumn; col++) {
            int startRow = (col == topLeftColumn) ? topLeftRow : 1;
            int endRow = (col == bottomRightColumn) ? bottomRightRow : layout.getRows();

            for (int row = startRow; row <= endRow ; row++ ){
                cells.add(CoordinateFactory.createCoordinate(row,col));
            }
        }
    }
}
