package sheet;

import app.AppController;
import dto.DTOcell;
import dto.DTOsheet;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SheetController {

    @FXML private GridPane dynamicGridPane;
    private AppController mainController;
    private Map<String, Label> cellLabels;
    private Map<String, String> cellStyles;
    private Map<String, Pos> columnAlignments;


    @FXML
    public void initialize() {
        cellStyles = new HashMap<>();
        columnAlignments = new HashMap<>();
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public Label getCellLabel(String cellID) {
        return cellLabels.get(cellID);
    }

    public Map<String, String> getCellStyles(){
        return cellStyles;
    }

    public Map<String, Pos> getColumnAlignments() {
        return columnAlignments;
    }

    public void setColumnAlignment(String column, Pos alignment) {
        columnAlignments.put(column, alignment);
    }

    public void setSheet(DTOsheet dtoSheet, boolean applyCustomStyles) {
        cellLabels = new HashMap<>();
        int rows = dtoSheet.getLayout().getRows();
        int cols = dtoSheet.getLayout().getColumns();
        int columnWidth = dtoSheet.getLayout().getColumnsWidthUnits();
        int rowsHeight = dtoSheet.getLayout().getRowsHeightUnits();

        dynamicGridPane.setGridLinesVisible(false); // Disable temporarily
        dynamicGridPane.getStyleClass().add("grid-pane");

        // Clear existing constraints and children
        dynamicGridPane.getRowConstraints().clear();
        dynamicGridPane.getColumnConstraints().clear();
        dynamicGridPane.getChildren().clear();

        // Add new row constraints
        for (int i = 0; i <= rows; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight(rowsHeight);  // Set the preferred height of the rows
            dynamicGridPane.getRowConstraints().add(row);
        }

        // Add new column constraints
        for (int j = 0; j <= cols; j++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPrefWidth(columnWidth);  // Set the preferred width of the columns
            dynamicGridPane.getColumnConstraints().add(col);
        }

        // Add column headers (A, B, C, ...)
        for (int col = 0; col <= cols; col++) {
            Label columnHeader;

            if (col == 0) {
                columnHeader = new Label();
            } else {
                String column = Character.toString((char) ('A' + col - 1));
                columnHeader = new Label(column);
                mainController.addClickEventForSelectedColumn(columnHeader);
                columnHeader.getStyleClass().add("column-cell");
            }

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
                String cellName = Character.toString((char) ('A' + col - 1)) + row;  // e.g., "A1", "B2", etc.
                Label cellLabel = new Label(cellValue);
                mainController.addClickEventForSelectedCell(cellLabel, cellName, cellData);
                cellLabels.put(cellName, cellLabel);

                // Apply saved column alignment if it exists
                String columnName = Character.toString((char) ('A' + col - 1)); // e.g., "A", "B", "C"
                if (applyCustomStyles && columnAlignments.containsKey(columnName)) {
                    cellLabel.setAlignment(columnAlignments.get(columnName));
                } else {
                    cellLabel.setAlignment(Pos.CENTER); // Default alignment
                }

                // Apply custom styles only if the flag is true
                if (applyCustomStyles && cellStyles.containsKey(cellName)) {
                    cellLabel.setStyle(cellStyles.get(cellName));
                }

                cellLabel.getStyleClass().add("single-cell");
                dynamicGridPane.add(cellLabel, col, row);
            }
        }
        mainController.populateVersionSelector();
    }


    public void displaySheetVersionInPopup(DTOsheet dtoSheet) {
        // Create a new Stage (pop-up window)
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows
        popupStage.setTitle("Sheet Version - " + dtoSheet.getVersion());

        // Create a new GridPane for this version
        GridPane versionGrid = new GridPane();

        // Create a new SheetController instance for the pop-up
        SheetController newSheetController = new SheetController();
        newSheetController.setMainController(mainController);  // Pass the main controller
        newSheetController.dynamicGridPane = versionGrid; // Set the new GridPane

        // Use the existing setSheet() method to populate the grid with the version data
        newSheetController.setSheet(dtoSheet, false);

        // Create a VBox to hold the GridPane
        VBox vbox = new VBox(versionGrid);
        vbox.setPadding(new javafx.geometry.Insets(20));

        // Set the scene for the pop-up
        Scene scene = new Scene(vbox);
        scene.getStylesheets().add(getClass().getResource("sheet.css").toExternalForm()); // Ensure the path is correct
        popupStage.setScene(scene);

        // Show the pop-up window
        popupStage.showAndWait();
    }

    public List<Label> getAllCellLabelsInColumn(String column) {
        List<Label> labelsInColumn = new ArrayList<>();

        for (Map.Entry<String, Label> entry : cellLabels.entrySet()) {
            String cellId = entry.getKey();
            Label label = entry.getValue();

            // Check if the cell's column matches the specified column
            if (cellId.startsWith(column)) {
                labelsInColumn.add(label);
            }
        }

        return labelsInColumn;
    }


//    public void displaySheetVersionInPopup(DTOsheet dtoSheet) {
//        // Create a new pop-up stage
//        Stage popupStage = new Stage();
//        popupStage.initModality(Modality.APPLICATION_MODAL);
//        popupStage.setTitle("Sheet Version: " + dtoSheet.getVersion());
//
//        // Create a VBox to hold the GridPane and display the sheet
//        VBox vbox = new VBox(10);
//        vbox.setPadding(new javafx.geometry.Insets(20));
//
//        // Create a GridPane to display the sheet
//        GridPane sheetGridPane = new GridPane();
//        sheetGridPane.getStyleClass().add("gridpane");
//
//        int rows = dtoSheet.getLayout().getRows();
//        int cols = dtoSheet.getLayout().getColumns();
//
//        // Add column headers (A, B, C, ...)
//        for (int col = 1; col <= cols; col++) {
//            Label columnHeader = new Label(Character.toString((char) ('A' + col - 1)));
//            sheetGridPane.add(columnHeader, col, 0);  // Column headers in the first row
//        }
//
//        // Add row headers (1, 2, 3, ...)
//        for (int row = 1; row <= rows; row++) {
//            Label rowHeader = new Label(Integer.toString(row));
//            sheetGridPane.add(rowHeader, 0, row);  // Row headers in the first column
//        }
//
//        // Fill the grid with cell values from the DTO sheet
//        for (int row = 1; row <= rows; row++) {
//            for (int col = 1; col <= cols; col++) {
//                DTOcell cellData = dtoSheet.getCell(row ,col); // Assuming row/col are zero-indexed
//                String cellValue = (cellData != null && cellData.getEffectiveValue() != null) ? cellData.getEffectiveValue() : "";
//                Label cellLabel = new Label(cellValue != null ? cellValue : "");
//                cellLabel.getStyleClass().add("single-cell");
//                sheetGridPane.add(cellLabel, col, row);
//            }
//        }
//
//        vbox.getChildren().add(sheetGridPane);
//
//        // Set the scene and display the pop-up window
//        Scene scene = new Scene(vbox, 600, 400); // Adjust size as needed
//        popupStage.setScene(scene);
//        popupStage.showAndWait();
//    }
}
