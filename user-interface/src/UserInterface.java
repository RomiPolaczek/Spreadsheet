import dto.DTOsheet;
import impl.EngineImpl;
import jakarta.xml.bind.JAXBException;
import sheet.cell.api.Cell;

import java.util.Scanner;

public class UserInterface {
    private EngineImpl engine;

    public UserInterface(){
        engine = new EngineImpl();
    }

    public void LoadFile(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the full path to the file: ");

        String filePath = scanner.nextLine();

        try {
            engine.LoadFile(filePath);
        }
        catch (JAXBException e) {
            e.printStackTrace();
        }
        catch (Exception e){
           System.out.println(e.getMessage());
        }
    }

    public void DisplaySheet() {
        DTOsheet dtoSheet = engine.createDTOSheetForDisplay(); //DISPLAY
        System.out.println("Sheet Name: " + dtoSheet.getName());
        System.out.println("Version: " + dtoSheet.getVersion());
        System.out.println();

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
            } else
                System.out.print("   ");


            for (int col = 0; col < numCols; col++) {
                Cell cell = engine.getSheet().getCell(line, col); //להחליף לגיליון DTO

                if (cell != null && cell.getEffectiveValue() != null && row % lineHeight == 0) {
                    System.out.print("|" + String.format("%-" + columnWidth + "s", cell.getEffectiveValue().getValue().toString()));
                } else
                    System.out.print("|" + String.format("%-" + columnWidth + "s", ""));
            }
            System.out.println();

            //scanner.close();///////
        }
    }
}
