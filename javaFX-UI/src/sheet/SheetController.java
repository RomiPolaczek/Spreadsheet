package sheet;

import app.AppController;
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
    public void initialSheetAccordingToSize(){
        int rows = mainController.getEngine().getSheet().getLayout().getRows();
        int cols = mainController.getEngine().getSheet().getLayout().getColumns();
        resizeGrid(rows, cols);
    }



    private void resizeGrid(int rows, int cols) {
        // Clear existing constraints and children
        dynamicGridPane.getRowConstraints().clear();
        dynamicGridPane.getColumnConstraints().clear();
        dynamicGridPane.getChildren().clear();

        // Add new row constraints
        for (int i = 0; i < rows; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight(30);  // Set the preferred height of the rows
            dynamicGridPane.getRowConstraints().add(row);
        }

        // Add new column constraints
        for (int j = 0; j < cols; j++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPrefWidth(50);  // Set the preferred width of the columns
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

        // Fill the rest of the grid with empty cells or placeholders
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= cols; col++) {
                Label cellLabel = new Label("");  // Empty cell for now
                cellLabel.getStyleClass().add("single-cell");
                dynamicGridPane.add(cellLabel, col, row);
            }
        }
    }
}
