package spreadsheet.client.component.mainSheet.sheet;

import spreadsheet.client.component.mainSheet.MainSheetController;
import dto.DTOcell;
import dto.DTOsheet;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.impl.CoordinateFactory;
import sheet.range.Range;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SheetController {

    @FXML
    private GridPane dynamicGridPane;
    private MainSheetController mainController;
    private Map<String, Label> cellLabels;
    private Map<String, String> cellStyles;
    private Map<String, Pos> columnAlignments;
    private Map<Integer, Integer> columnsWidth;
    private Map<Integer, Integer> rowsHeight;

    @FXML
    public void initialize() {
        cellStyles = new HashMap<>();
        columnAlignments = new HashMap<>();
        columnsWidth = new HashMap<>();
        rowsHeight = new HashMap<>();
    }

    public void initializeSheetController() {
        columnsWidth = new HashMap<>();
        rowsHeight = new HashMap<>();
    }

    public void setMainController(MainSheetController mainController) {
        this.mainController = mainController;
    }

    public void setDynamicGridPane(GridPane dynamicGridPane) {
        this.dynamicGridPane = dynamicGridPane;
    }

    public Label getCellLabel(String cellID) {
        return cellLabels.get(cellID);
    }

    public Map<String, String> getCellStyles() {
        return cellStyles;
    }

    public void setColumnAlignment(String column, Pos alignment) {
        columnAlignments.put(column, alignment);
    }

    public int getColumnWidth(String column) {
        int columnIndex = column.charAt(0) - 'A' + 1;
        return columnsWidth.get(columnIndex);
    }

    public void setColumnWidth(String column, Integer newWidth) {
        int columnIndex = column.charAt(0) - 'A' + 1;
        columnsWidth.put(columnIndex, newWidth);
    }

    public int getRowHeight(String row) {
        int rowIndex = Integer.parseInt(row);
        return rowsHeight.get(rowIndex);
    }

    public void setRowHeight(String row, Integer newHeight) {
        int rowIndex = Integer.parseInt(row);
        rowsHeight.put(rowIndex, newHeight);
    }

    public void setSheet(DTOsheet dtoSheet, boolean applyCustomStyles) {
        cellLabels = new HashMap<>();
        int rows = dtoSheet.getLayout().getRows();
        int cols = dtoSheet.getLayout().getColumns();
        int columnWidthOriginal = dtoSheet.getLayout().getColumnsWidthUnits();
        int rowsHeightOriginal = dtoSheet.getLayout().getRowsHeightUnits();

        dynamicGridPane.setGridLinesVisible(false); // Disable temporarily
        dynamicGridPane.getStyleClass().add("grid-pane");

        // Clear existing constraints and children
        dynamicGridPane.getRowConstraints().clear();
        dynamicGridPane.getColumnConstraints().clear();
        dynamicGridPane.getChildren().clear();

        // Add new row constraints
        for (int i = 0; i <= rows; i++) {
            RowConstraints row = new RowConstraints();
            if (!applyCustomStyles) {
                rowsHeight.put(i, rowsHeightOriginal);
            }
            row.setPrefHeight(rowsHeight.get(i));
            dynamicGridPane.getRowConstraints().add(row);
        }

        // Add new column constraints
        for (int j = 0; j <= cols; j++) {
            ColumnConstraints col = new ColumnConstraints();
            if (!applyCustomStyles) {
                columnsWidth.put(j, columnWidthOriginal);
            }
            col.setPrefWidth(columnsWidth.get(j));
            dynamicGridPane.getColumnConstraints().add(col);
        }

        // Add column headers (A, B, C, ...)
        for (int col = 0; col <= cols; col++) {
            Label columnHeader;

            if (col == 0) {
                columnHeader = new Label();
            } else {
                columnHeader = new Label(Character.toString((char) ('A' + col - 1)));
                mainController.addClickEventForSelectedColumn(columnHeader);
            }
            columnHeader.getStyleClass().add("header-cell");
            dynamicGridPane.add(columnHeader, col, 0);  // Column headers in the first row
        }

        // Add row headers (1, 2, 3, ...)
        for (int row = 1; row <= rows; row++) {
            Label rowHeader = new Label(Integer.toString(row));
            mainController.addClickEventForSelectedRow(rowHeader);
            rowHeader.getStyleClass().add("header-cell");
            dynamicGridPane.add(rowHeader, 0, row);  // Row headers in the first column
        }

        // Fill the rest of the grid with cell values from the DTOsheet
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= cols; col++) {
                // Get the effective value for the current cell from the DTOsheet
                DTOcell cellData = dtoSheet.getCell(row, col);
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
        newSheetController.setMainController(this.mainController);
        newSheetController.columnsWidth = new HashMap<>();
        newSheetController.rowsHeight = new HashMap<>();
        newSheetController.dynamicGridPane = versionGrid; // Set the new GridPane

        // Use the existing setSheet() method to populate the grid with the version data
        newSheetController.setSheet(dtoSheet, false);

        // Create a VBox to hold the GridPane
        VBox vbox = new VBox(versionGrid);
        vbox.setPadding(new javafx.geometry.Insets(20));

        // Set the scene for the pop-up
        Scene scene = new Scene(vbox);
        mainController.setTheme(scene);
        popupStage.setScene(scene);

        // Show the pop-up window
        popupStage.showAndWait();
    }

    public List<Label> getAllCellLabelsInColumn(String column) {
        List<Label> labelsInColumn = new ArrayList<>();

        // Calculate the starting row index
        int startingRow = 1; // Assuming rows start from 1

        // Iterate through the rows to gather labels for the specified column
        for (int row = startingRow; row <= rowsHeight.size(); row++) {
            String cellId = column + row; // Construct the cell ID (e.g., "A1", "A2", ...)
            Label label = cellLabels.get(cellId); // Retrieve the label from the map

            // If the label is found, add it to the list
            if (label != null) {
                labelsInColumn.add(label);
            }
        }

        return labelsInColumn;
    }

    public ColumnConstraints getColumnConstraintsByColumn(String column) {
        // Calculate the column index based on the letter (A -> 0, B -> 1, etc.)
        int columnIndex = column.charAt(0) - 'A' + 1;

        // Check if the column index is valid
        if (columnIndex >= 0 && columnIndex < dynamicGridPane.getColumnConstraints().size()) {
            return dynamicGridPane.getColumnConstraints().get(columnIndex);
        } else {
            return null;  // Return null if the column index is out of bounds
        }
    }

    public RowConstraints getRowConstraintsByRow(String row) {
        // Calculate the column index based on the letter (A -> 0, B -> 1, etc.)
        int rowIndex = Integer.parseInt(row);

        // Check if the column index is valid
        if (rowIndex >= 0 && rowIndex < dynamicGridPane.getRowConstraints().size()) {
            return dynamicGridPane.getRowConstraints().get(rowIndex);
        } else {
            return null;  // Return null if the column index is out of bounds
        }
    }

    public void highlightColumn(String column) { //all in once
        if (mainController.isAnimationSelectedProperty()) {
            List<Label> cellsInColumn = getAllCellLabelsInColumn(column);

            if (cellsInColumn.isEmpty()) {
                return; // No cells to highlight
            }

            // Set the total animation duration to 2 seconds
            Duration highlightDuration = Duration.seconds(1.5); // Total duration for highlighting
            Duration stepDuration = highlightDuration.divide(30); // Duration for each step (adjust for speed)

            // Create a Timeline to animate the highlighting
            Timeline timeline = new Timeline();

            // Create keyframes for gradually darkening and then bringing back to original
            for (int i = 0; i <= 10; i++) {
                final double factor = (double) i / 10; // Create a factor from 0 to 1
                KeyFrame keyFrame = new KeyFrame(
                        stepDuration.multiply(i), // Use multiply to get the total time for this step
                        event -> {
                            for (Label cellLabel : cellsInColumn) {
                                Color originalColor = (Color) cellLabel.getTextFill(); // Get the original text color
                                Color darkerColor = originalColor.darker().interpolate(Color.LIGHTGREY, factor); // Darken gradually
                                cellLabel.setStyle("-fx-background-color: " + toRgbString(darkerColor) + ";");
                            }
                        }
                );
                timeline.getKeyFrames().add(keyFrame);
            }

            // KeyFrame to bring back to the original color
            KeyFrame resetKeyFrame = new KeyFrame(
                    highlightDuration, // Total duration for bringing back
                    event -> {
                        for (Label cellLabel : cellsInColumn) {
                            cellLabel.setStyle(""); // Reset to original color
                        }
                    }
            );

            timeline.getKeyFrames().add(resetKeyFrame);

            // Play the animation
            timeline.play();
        }
    }

    // Convert Color to RGB string for CSS
    private String toRgbString(Color color) {
        return String.format("rgb(%d, %d, %d)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public void setSheetStyle(Scene scene, String theme) {
        String css = getClass().getResource("/spreadsheet/client/component/mainSheet/sheet/style/" + theme + "Sheet.css").toExternalForm();
        scene.getStylesheets().add(css);
    }


//    public void displayFilteredSortedSheetInPopup(DTOsheet dtoSheet, String title, String range) {
//        Stage popupStage = new Stage();
//        popupStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows
//        popupStage.setTitle(title);
//
//        // Create a VBox to hold the GridPane and display the sheet
//        VBox vbox = new VBox(10);
//        vbox.setPadding(new javafx.geometry.Insets(20));
//
//        // Create a GridPane to display the sheet
//        GridPane sheetGridPane = new GridPane();
//        sheetGridPane.getStyleClass().add("gridpane");
//
//        // Create a new SheetController instance for the pop-up
//        SheetController newSheetController = new SheetController();
//        newSheetController.setMainController(this.mainController);
//        newSheetController.columnsWidth = this.columnsWidth;
//        newSheetController.rowsHeight = this.rowsHeight;
//        newSheetController.columnAlignments = this.columnAlignments;
//        Map<String, String> newCellStyles = new HashMap<>();
//
//        for (Map.Entry<String, String> entry : cellStyles.entrySet()) {
//            String originalStr = entry.getValue();
//            String copiedStr = originalStr; // Create a new Label with the same properties
//            newCellStyles.put(entry.getKey(), copiedStr);
//        }
//        newSheetController.cellStyles = newCellStyles;
//
//        Range tempRange = new Range("temp", mainController.getEngine().getSheet().getLayout());
//        tempRange.parseRange(range);
//
//        int rows = dtoSheet.getLayout().getRows();
//        int cols = dtoSheet.getLayout().getColumns();
//        int columnWidthOriginal = dtoSheet.getLayout().getColumnsWidthUnits();
//        int rowsHeightOriginal = dtoSheet.getLayout().getRowsHeightUnits();
//
//        sheetGridPane.setGridLinesVisible(false); // Disable temporarily
//        sheetGridPane.getStyleClass().add("grid-pane");
//
//        // Clear existing constraints and children
//        sheetGridPane.getRowConstraints().clear();
//        sheetGridPane.getColumnConstraints().clear();
//        sheetGridPane.getChildren().clear();
//
//        // Add new row constraints
//        for (int i = 0; i <= rows; i++) {
//            RowConstraints row = new RowConstraints();
//            row.setPrefHeight(rowsHeight.get(i));
//            sheetGridPane.getRowConstraints().add(row);
//        }
//
//        // Add new column constraints
//        for (int j = 0; j <= cols; j++) {
//            ColumnConstraints col = new ColumnConstraints();
//            col.setPrefWidth(columnsWidth.get(j));
//            sheetGridPane.getColumnConstraints().add(col);
//        }
//
//        // Add column headers (A, B, C, ...)
//        for (int col = 0; col <= cols; col++) {
//            Label columnHeader;
//
//            if (col == 0) {
//                columnHeader = new Label();
//            } else {
//                columnHeader = new Label(Character.toString((char) ('A' + col - 1)));
//            }
//            columnHeader.getStyleClass().add("header-cell");
//            sheetGridPane.add(columnHeader, col, 0);  // Column headers in the first row
//        }
//
//        // Add row headers (1, 2, 3, ...)
//        for (int row = 1; row <= rows; row++) {
//            Label rowHeader = new Label(Integer.toString(row));
//            rowHeader.getStyleClass().add("header-cell");
//            sheetGridPane.add(rowHeader, 0, row);  // Row headers in the first column
//        }
//
//        // Fill the rest of the grid with cell values from the DTOsheet
//        for (int row = 1; row <= rows; row++) {
//            for (int col = 1; col <= cols; col++) {
//                // Get the effective value for the current cell from the DTOsheet
//                DTOcell cellData = dtoSheet.getCell(row, col);
//                String cellValue = (cellData != null && cellData.getEffectiveValue() != null) ? cellData.getEffectiveValue() : "";
//
//                // Create the Label for the cell and set the value
//                String cellName = Character.toString((char) ('A' + col - 1)) + row;  // e.g., "A1", "B2", etc.
//                Label cellLabel = new Label(cellValue);
//
//
//                // Apply saved column alignment if it exists
//                String columnName = Character.toString((char) ('A' + col - 1)); // e.g., "A", "B", "C"
//                if (columnAlignments.containsKey(columnName)) {
//                    cellLabel.setAlignment(columnAlignments.get(columnName));
//                } else {
//                    cellLabel.setAlignment(Pos.CENTER); // Default alignment
//                }
//
//                // Apply custom styles only if the flag is true
//                if (mainController.getNewCoordToOldCoord().containsKey(cellName)) {
//                    String oldCoord = mainController.getNewCoordToOldCoord().get(cellName);
//                    if(oldCoord != null)
//                    {
//                        cellLabel.setStyle(newSheetController.cellStyles.get(oldCoord));
//                        newSheetController.cellStyles.remove(oldCoord);
//                    }
//                }
//                else{
//                    if(!isCellInRange(tempRange, cellName))
//                        cellLabel.setStyle(newSheetController.cellStyles.get(cellName));
//                }
//
//                cellLabel.getStyleClass().add("single-cell");
//                sheetGridPane.add(cellLabel, col, row);
//            }
//        }
//
//        newSheetController.dynamicGridPane = sheetGridPane; // Set the new GridPane
//
//        vbox.getChildren().add(sheetGridPane);
//
//        // Set the scene and display the pop-up window
//        Scene scene = new Scene(vbox, 600, 400); // Adjust size as needed
//        mainController.setTheme(scene);
//        popupStage.setScene(scene);
//        popupStage.showAndWait();
//    }

    Boolean isCellInRange(Range range, String cell){
        int startRow = range.getTopLeftCoordinate().getRow();
        int endRow = range.getBottomRightCoordinate().getRow();
        int startCol = range.getTopLeftCoordinate().getColumn();
        int endCol = range.getBottomRightCoordinate().getColumn();

        Coordinate coordinate = CoordinateFactory.from(cell);
        int cellRow = coordinate.getRow();
        int cellCol = coordinate.getColumn();

        return (cellRow <= endRow && cellRow >= startRow && cellCol <= endCol && cellCol >= startCol);
    }

}