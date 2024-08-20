import impl.EngineImpl;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;
import sheet.coordinate.CoordinateImpl;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.cell.impl.CellImpl;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;
import sheet.coordinate.CoordinateImpl;
import sheet.layout.api.Layout;
import sheet.layout.impl.LayoutImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
//        Sheet sheet = new SheetImpl();
//        sheet.setCell(0, 0, "Hello, World!");
//
//        Cell cell = sheet.getCell(0, 0);
//        Object value = cell.getEffectiveValue().getValue();
//        System.out.println(value);
//
//        String s = cell.getEffectiveValue().extractValueWithExpectation(String.class);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the full path to the file: ");

        // Get the file path from the user
        String filePath = scanner.nextLine();

        EngineImpl engine = new EngineImpl();

        engine.LoadFile(filePath);

        System.out.println("Sheet Name: " + engine.getSheet().getName());
        System.out.println("Version: " + engine.getSheet().getVersion());
        System.out.println();


        int numRows = engine.getSheet().getLayout().getRows();
        int numCols = engine.getSheet().getLayout().getColumns();
        int columnWidth = engine.getSheet().getLayout().getColumnsWidthUnits();
        int lineHeight = engine.getSheet().getLayout().getRowsHeightUnits();

        // Print column headers
        System.out.print("    "); // Padding for row numbers
        for (int col = 0; col < numCols; col++) {
            char colName = (char) ('A' + col);
            System.out.print(String.format("%-" + columnWidth + "s", colName) + "|");
        }
        System.out.println();

        // Print rows with cells
        for (int row = 0; row < numRows; row++) {
            // Print row number with two digits
            System.out.print(String.format("%02d", row + 1) + " ");

            for (int col = 0; col < numCols; col++) {
                Cell cell = engine.getSheet().getCell(row, col);

                String value = (cell != null && cell.getEffectiveValue() != null)
                        ? cell.getEffectiveValue().getValue().toString()
                        : "";

                // Print cell value with padding and separator
                System.out.print(String.format("%-" + columnWidth + "s", value) + "|");
            }
            System.out.println();
        }


        scanner.close();///////
    }
}
