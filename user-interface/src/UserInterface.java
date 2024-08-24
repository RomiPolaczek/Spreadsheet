import api.Engine;
import dto.DTOcell;
import dto.DTOsheet;
import impl.EngineImpl;
import sheet.api.SheetReadActions;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.impl.CoordinateFactory;

import java.util.List;
import java.util.Scanner;

public class UserInterface {
    private Engine engine;

    public UserInterface(){
        engine = new EngineImpl();
    }

    public void LoadFile(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the full path to the file: ");

        String filePath = scanner.nextLine();

        try{
            engine.LoadFile(filePath);
        }
        catch (Exception e){
           System.out.println(e.getMessage());
        }
    }

    public void DisplaySheet() {
        DTOsheet dtoSheet = engine.createDTOSheetForDisplay(engine.getSheet());

        System.out.println("Sheet Name: " + dtoSheet.getName());
        System.out.println("Version: " + dtoSheet.getVersion());
        System.out.println();

        printSheet(dtoSheet);
    }

    private void printSheet(DTOsheet dtoSheet) {
        int numRows = dtoSheet.getLayout().getRows();
        int numCols = dtoSheet.getLayout().getColumns();
        int columnWidth = dtoSheet.getLayout().getColumnsWidthUnits();
        int lineHeight = dtoSheet.getLayout().getRowsHeightUnits();

        // Print column headers
        System.out.print("   "); // Padding for row numbers
        for (int col = 0; col < numCols; col++) {
            char colName = (char) ('A' + col);
            System.out.print("|" + String.format("%-" + columnWidth + "s", colName));
        }
        System.out.println();

        // Print rows with cells
        int line = 0;
        for (int row = 0; row < numRows * lineHeight; row++) {
            // Print row number with two digits
            if (row % lineHeight == 0) {
                line++;
                System.out.print(String.format("%02d", line) + " ");
            }
            else
                System.out.print("   ");

            for (int col = 1; col <= numCols; col++) {
                DTOcell cell = dtoSheet.getCell(line, col);

                if (cell != null && cell.getEffectiveValue() != null && row % lineHeight == 0) {
                    System.out.print("|" + String.format("%-" + columnWidth + "s", cell.getEffectiveValue()));
                }
                else
                    System.out.print("|" + String.format("%-" + columnWidth + "s", ""));
            }
            System.out.println();
        }
    }

    public void DisplayCell(){
        String input = getCellFromUser();

        DTOsheet dtoSheet = engine.createDTOSheetForDisplay(engine.getSheet());
        Coordinate coordinate = CoordinateFactory.from(input);

        try {
            CoordinateFactory.isValidCoordinate(coordinate, engine.getSheet());

            DTOcell dtoCell = dtoSheet.getCell(coordinate);
            if(dtoCell == null){
                System.out.println("Cell identity: " + input);
                throw new Exception("The cell " + input + " is empty"); //לא צריך לזרוק אקספשן לדעתי, זה רק הדפסה
            }

            printCellFirstParts(input, dtoCell);
            System.out.println("The last version that commited changes: " + dtoCell.getVersion());

            List<String> dependsOn = dtoCell.getDependsOn();
            if(dependsOn.isEmpty())
                System.out.println("There are no cells depending on cell " + input);
            else {
                System.out.println("The list of cells that depends on cell " + input + " : ");
                for (String dependsOnName : dependsOn) {
                    System.out.print(dependsOnName + " ");
                }
            }

            List<String> influencingOn = dtoCell.getInfluencingOn();
            if(influencingOn.isEmpty())
                System.out.println("There are no cells influencing on cell " + input);
            else{
                System.out.println("The list of cells that influencing on cell " + input + " : ");
                for (String influencingOnName : influencingOn) {
                    System.out.print(influencingOnName + " ");
                }
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private String getCellFromUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter the cell identity (e.g., A4 or B7): ");
        String input = scanner.nextLine().toUpperCase();
        return input;
    }

    private void printCellFirstParts(String input, DTOcell dtoCell) {
        System.out.println("Cell identity: " + input);
        System.out.println("Original value: " + dtoCell.getOriginalValue());
        System.out.println("Effective value: " + dtoCell.getEffectiveValue());
    }

    public void EditCell(){
        String inputCell = getCellFromUser();
        DTOsheet dtoSheet = engine.createDTOSheetForDisplay(engine.getSheet());
        Coordinate coordinate = CoordinateFactory.from(inputCell);

        try {
            CoordinateFactory.isValidCoordinate(coordinate, engine.getSheet()); //האם לשנות לדיטיאו

            DTOcell dtoCell = dtoSheet.getCell(coordinate);
            if(dtoCell == null){
                System.out.println("Cell identity: " + inputCell);
                throw new Exception("The cell " + inputCell + " is empty"); //לא צריך לזרוק אקספשן לדעתי, זה רק הדפסה
            }
            printCellFirstParts(inputCell, dtoCell);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter the new value of the cell: ");
        String inputValue = scanner.nextLine().toUpperCase();

        engine.getSheet().updateCellValueAndCalculate(coordinate.getRow(), coordinate.getColumn(), inputValue);

        DisplaySheet();
        engine.AddSheetVersionToMap(engine.getSheet());
    }

    public void DisplayVersions () {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter a version number: ");

        try {
            int version = Integer.parseInt(scanner.nextLine());
            DTOsheet dtoSheet = engine.getSheetVersion(version);
            DisplaySheetVersion(dtoSheet);
        } catch (Exception e) {
            System.out.print("Version must be an integer");
        }
    }

        public void DisplaySheetVersion(DTOsheet dtoSheet) {
            System.out.println("Sheet Name: " + dtoSheet.getName());
            System.out.println("Version: " + dtoSheet.getVersion());
            System.out.println();

            printSheet(dtoSheet);
    }



}
