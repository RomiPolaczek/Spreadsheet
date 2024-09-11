package sheet;

import app.AppController;
import dto.DTOcell;
import dto.DTOsheet;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;

public class SheetController {

    @FXML private GridPane dynamicGridPane;
    private AppController mainController;


    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

//    public void initialize() {
//        // Method to dynamically resize the GridPane using predefined values
//        resizeGrid(5, 6);
//    }

    public void setSheet() {
        DTOsheet dtoSheet = mainController.getEngine().createDTOSheetForDisplay(mainController.getEngine().getSheet());
        int rows = dtoSheet.getLayout().getRows();
        int cols = dtoSheet.getLayout().getColumns();
        int columnWidth = dtoSheet.getLayout().getColumnsWidthUnits();
        int rowsHeight = dtoSheet.getLayout().getRowsHeightUnits();


        // Clear existing constraints and children
        dynamicGridPane.getRowConstraints().clear();
        dynamicGridPane.getColumnConstraints().clear();
        dynamicGridPane.getChildren().clear();

        // Add new row constraints
        for (int i = 0; i < rows; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight(rowsHeight);  // Set the preferred height of the rows
            dynamicGridPane.getRowConstraints().add(row);
        }

        // Add new column constraints
        for (int j = 0; j < cols; j++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPrefWidth(columnWidth);  // Set the preferred width of the columns
            dynamicGridPane.getColumnConstraints().add(col);
        }

        // Add column headers (A, B, C, ...)
        for (int col = 1; col <= cols; col++) {
            Label columnHeader = new Label(Character.toString((char) ('A' + col - 1)));
            columnHeader.getStyleClass().add("header-cell");
            dynamicGridPane.add(columnHeader, col, 0);  // Column headers in the first row
        }

        // Add row headers (1, 2, 3, ...)
        for (int row = 1; row <= rows; row++) {
            Label rowHeader = new Label(Integer.toString(row));
            rowHeader.getStyleClass().add("header-cell");
            dynamicGridPane.add(rowHeader, 0, row);  // Row headers in the first column
        }

        // Fill the rest of the grid with cell values from the DTOsheet
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= cols; col++) {
                // Get the effective value for the current cell from the DTOsheet
                DTOcell cellData = dtoSheet.getCell(row ,col); // Assuming row/col are zero-indexed
                String cellValue = (cellData != null && cellData.getEffectiveValue() != null) ? cellData.getEffectiveValue() : "";

                // Create the Label for the cell and set the value
                Label cellLabel = new Label(cellValue);
                String cellName = Character.toString((char) ('A' + col - 1)) + row;  // e.g., "A1", "B2", etc.
                mainController.getHeaderComponentController().addClickEventForCell(cellLabel ,cellName, cellData.getOriginalValue());
                cellLabel.getStyleClass().add("single-cell");
                dynamicGridPane.add(cellLabel, col, row);
            }
        }
    }


}
