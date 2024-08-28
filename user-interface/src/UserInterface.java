import api.Engine;
import dto.DTOcell;
import dto.DTOsheet;
import impl.EngineImpl;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.impl.CoordinateFactory;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.*;

public class UserInterface {
    private Engine engine;
    private Scanner scanner;

    public UserInterface() {
        engine = new EngineImpl();
        scanner = new Scanner(System.in);
    }

    public void DisplayMenu() {
        String optionsToChoose = """
                
                Please choose one of the following options:
                1) Load new XML file
                2) Display current sheet
                3) Display a certain cell's data
                4) Edit a cell
                5) Display versions of the sheet
                6) Save system state
                7) Load system state
                8) Exit
                """;
        int chosenOption;
        do {
            System.out.print(optionsToChoose);

            chosenOption = getChoiceAndCheckValidation(8, 1);

            if (chosenOption == 1)
                LoadFile();
            else if (engine.getFile()!=null || chosenOption == 8) {
                switch (chosenOption) {
                    case 2:
                        DisplaySheet();
                        break;
                    case 3:
                        DisplayCell();
                        break;
                    case 4:
                        EditCell();
                        break;
                    case 5:
                        DisplayVersions();
                        break;
                    case 6:
                        saveSystemState();
                        break;
                    case 7:
                        loadSystemState();
                        break;
                    case 8:
                        System.out.println("Exiting...");
                        break;
                }
            }
            else {
                System.out.println("No file loaded yet. Please load a file first.");
            }
        }
        while(chosenOption != 8);
    }

    public void LoadFile(){
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
                throw new Exception("The cell " + input + " is empty");
            }

            printCellFirstParts(input, dtoCell);
            System.out.println("The last version that commited changes: " + dtoCell.getVersion());

            List<String> dependsOn = dtoCell.getDependsOn();
            if(dependsOn.isEmpty())
                System.out.println("There are no cells that cell " + input + " depends on");
            else {
                System.out.print("The list of cells that cell " + input + " depends on: ");
                for (String dependsOnName : dependsOn) {
                    System.out.print(dependsOnName + " ");
                }
                System.out.println();
            }

            List<String> influencingOn = dtoCell.getInfluencingOn();
            if(influencingOn.isEmpty())
                System.out.println("There are no cells that cell " + input + " influencing on");
            else{
                System.out.print("The list of cells that cell " + input + " influence on: ");
                for (String influencingOnName : influencingOn) {
                    System.out.print(influencingOnName + " ");
                }
                System.out.println();
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private String getCellFromUser() {
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
                System.out.println("The cell " + inputCell + " is empty"); //לא צריך לזרוק אקספשן לדעתי, זה רק הדפסה
            }
            else{
                printCellFirstParts(inputCell, dtoCell);
            }

            System.out.print("Please enter the new value of the cell: ");
            String inputValue = scanner.nextLine();
         //   if(inputValue.isBlank())

            engine.EditCell(coordinate, inputValue);
            DisplaySheet();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void DisplayVersions () {
        printVersionChangesTable();

        System.out.print("Please enter a version number: ");

        try {
            String version = scanner.nextLine();
            DTOsheet dtoSheet = engine.GetVersionForDisplay(version);
            DisplaySheetVersion(dtoSheet);
        }
        catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
        catch (Exception e) {
            System.out.print("Version must be an integer");
        }
    }

    public void printVersionChangesTable() {
        int numberOfVersions = engine.getNumberOfVersions();
        System.out.println("Version | Changes");
        System.out.println("-----------------");

        // Iterate through the map and print each entry
        for (int i=1; i<=numberOfVersions; i++) {
            int changes = engine.getChangesAccordingToVersionNumber(i);
            System.out.printf("%-7d | %-7d%n", i, changes);
        }
}

    public void DisplaySheetVersion(DTOsheet dtoSheet) {
        System.out.println("Sheet Name: " + dtoSheet.getName());
        System.out.println("Version: " + dtoSheet.getVersion());
        System.out.println();

        printSheet(dtoSheet);
    }

    private int getChoiceAndCheckValidation (int upperLimit, int lowerLimit) {
        String choice = "";
        while (true) {
            choice = scanner.nextLine().trim();
            try {
                int choiceNum = Integer.parseInt(choice);
                if (choiceNum >= lowerLimit && choiceNum <= upperLimit) {
                    return choiceNum;
                } else {
                    System.out.print("Invalid choice. Please enter a number between " + lowerLimit + " and " + upperLimit + ": ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number between " + lowerLimit + " and " + upperLimit + ": ");
            }
        }
    }

     private void saveSystemState() {
        System.out.print("Enter the full path and filename to save the system state (without extension): ");
        String filePath = scanner.nextLine();
        try {
            engine.saveSystemState(filePath);
            System.out.println("System state saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving system state: " + e.getMessage());
        }
    }

    private void loadSystemState() {
        System.out.print("Enter the full path and filename to load the system state (without extension): ");
        String filePath = scanner.nextLine();
        try {
            engine = EngineImpl.loadSystemState(filePath);
            System.out.println("System state loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading system state: " + e.getMessage());
        }
    }

}
