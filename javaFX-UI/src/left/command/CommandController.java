package left.command;

import app.AppController;
import dto.DTOsheet;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sheet.SheetController;
import sheet.api.EffectiveValue;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandController {

    @FXML private Label selectedCellLabel;
    @FXML private ColorPicker cellBackgroundColorPicker;
    @FXML private ColorPicker cellTextColorPicker;
    @FXML private Button resetCellDesignButton;
    @FXML private Label selectedColumnLabel;
    @FXML private ComboBox<String> columnAlignmentComboBox;
    @FXML private Slider columnWidthSlider;
    @FXML private Label selectedRowLabel;
    @FXML private Slider rowHeightSlider;
    @FXML private Button filterButton;
    @FXML private Button dynamicAnalysisButton;
    @FXML private Button createGraphButton;
    @FXML private Button sortButton;

    private AppController mainController;
    private SimpleStringProperty selectedColumnProperty;
    private SimpleStringProperty selectedRowProperty;
    private Map<String, String> newCoordToOldCoord;
    public static final String DEFAULT_CELL_STYLE = "-fx-background-color: white; -fx-text-fill: black;";

    public void initializeCommandController(){
        BooleanBinding noSelectedCell = mainController.getSelectedCellProperty().isNull();

        selectedCellLabel.textProperty().bind(mainController.getSelectedCellProperty());
        selectedColumnLabel.textProperty().bind(selectedColumnProperty);
        selectedRowLabel.textProperty().bind(selectedRowProperty);
        cellBackgroundColorPicker.disableProperty().bind(noSelectedCell);
        cellTextColorPicker.disableProperty().bind(noSelectedCell);
        resetCellDesignButton.disableProperty().bind(noSelectedCell
                .or(mainController.getSelectedCellProperty().isNotNull().and(isDefaultCellStyle())));
        columnAlignmentComboBox.disableProperty().bind(selectedColumnProperty.isNull());
        columnWidthSlider.disableProperty().bind(selectedColumnProperty.isNull());
        rowHeightSlider.disableProperty().bind(selectedRowProperty.isNull());
        filterButton.disableProperty().bind(noSelectedCell);
        dynamicAnalysisButton.disableProperty().bind(noSelectedCell);
        sortButton.disableProperty().bind(noSelectedCell);
        createGraphButton.disableProperty().bind(noSelectedCell);

        columnWidthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            changeColumnWidth(newValue.intValue());
        });

        rowHeightSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            changeRowHeight(newValue.intValue());
        });
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize(){
        selectedColumnProperty = new SimpleStringProperty();
        selectedRowProperty = new SimpleStringProperty();
        columnAlignmentComboBox.getItems().addAll("Left","Center", "Right");
        newCoordToOldCoord = new HashMap<>();
    }

    public SimpleStringProperty selectedColumnProperty() {return selectedColumnProperty;}

    public SimpleStringProperty selectedRowProperty() {return selectedRowProperty;}

    @FXML
    void cellBackgroundColorPickerOnAction(ActionEvent event) {
        // Get selected color from the ColorPicker
        Color selectedColor = cellBackgroundColorPicker.getValue();
        String backgroundColor = String.format("-fx-background-color: #%02x%02x%02x;",
                (int) (selectedColor.getRed() * 255),
                (int) (selectedColor.getGreen() * 255),
                (int) (selectedColor.getBlue() * 255));

        // Get selected cell property
        SimpleStringProperty selectedCell = mainController.getSelectedCellProperty();
        Label cellLabel = mainController.getCellLabel(selectedCell.get());

        // Update the background color of the cell
        String currentStyle = cellLabel.getStyle();
        String newStyle = currentStyle + backgroundColor;
        cellLabel.setStyle(newStyle);

        // Save the style in the cellStyles map
        mainController.getCellStyles().put(selectedCell.get(), newStyle);

        resetCellDesignButton.disableProperty().bind(isDefaultCellStyle());
    }

    @FXML
    void cellTextColorPickerOnAction(ActionEvent event) {
        // Get selected color from the ColorPicker
        Color selectedColor = cellTextColorPicker.getValue();
        String textColor = String.format("-fx-text-fill: #%02x%02x%02x;",
                (int) (selectedColor.getRed() * 255),
                (int) (selectedColor.getGreen() * 255),
                (int) (selectedColor.getBlue() * 255));

        // Get selected cell property
        SimpleStringProperty selectedCell = mainController.getSelectedCellProperty();
        Label cellLabel = mainController.getCellLabel(selectedCell.get());

        // Update the text color of the cell
        String currentStyle = cellLabel.getStyle();
        String newStyle = currentStyle + textColor;
        cellLabel.setStyle(newStyle);

        // Save the style in the cellStyles map
        mainController.getCellStyles().put(selectedCell.get(), newStyle);

        resetCellDesignButton.disableProperty().bind(isDefaultCellStyle());
    }

    public void updateColorPickersWithCellStyles(Label cell) {
        // Parse current background color from the cell style
        String backgroundColor = extractStyleValue(cell.getStyle(), "-fx-background-color");
        if (backgroundColor != null) {
            cellBackgroundColorPicker.setValue(Color.web(backgroundColor));
        } else {
            cellBackgroundColorPicker.setValue(Color.WHITE);  // Default to white if no background color is set
        }

        // Parse current text color from the cell style
        String textColor = extractStyleValue(cell.getStyle(), "-fx-text-fill");
        if (textColor != null) {
            cellTextColorPicker.setValue(Color.web(textColor));
        } else {
            cellTextColorPicker.setValue(Color.BLACK);  // Default to black if no text color is set
        }

    }

    private String extractStyleValue(String style, String property) {
        if (style == null || property == null) {
            return null;
        }
        for (String s : style.split(";")) {
            if (s.startsWith(property)) {
                return s.split(":")[1].trim();
            }
        }
        return null;
    }

    @FXML
    void resetCellDesignButtonOnAction(ActionEvent event) {
        // Get the selected cell property
        SimpleStringProperty selectedCell = mainController.getSelectedCellProperty();
        if (selectedCell != null && selectedCell.get() != null) {
            Label cellLabel = mainController.getCellLabel(selectedCell.get());

            // Reset the cell's style to the default
            cellLabel.setStyle("");

            // Save the reset style in the cellStyles map
            mainController.getCellStyles().put(selectedCell.get(), cellLabel.getStyle());

            // Optionally, reset the color pickers to reflect the default colors
            cellBackgroundColorPicker.setValue(Color.WHITE);
            cellTextColorPicker.setValue(Color.BLACK);
            resetCellDesignButton.disableProperty().bind(isDefaultCellStyle());
        }
    }

    public BooleanBinding isDefaultCellStyle() {
        return new BooleanBinding() {
            {
                super.bind(mainController.getSelectedCellProperty());
            }

            @Override
            protected boolean computeValue() {
                SimpleStringProperty selectedCell = mainController.getSelectedCellProperty();
                if (selectedCell == null || selectedCell.get() == null) {
                    return true; // Disable if no cell is selected
                }

                Label cellLabel = mainController.getCellLabel(selectedCell.get());
                if (cellLabel == null) {
                    return true;
                }

                // Check if the cell's current style matches the default style
                String currentStyle = cellLabel.getStyle();

                if(currentStyle.isBlank() || currentStyle.equals(DEFAULT_CELL_STYLE))
                    return true;
                else
                    return false;
            }
        };
    }

    @FXML
    void columnAlignmentComboBoxOnAction(ActionEvent event) {
        // Get selected alignment from ComboBox
        String alignment = columnAlignmentComboBox.getValue();

        if (alignment == null) {
            return; // No selection made
        }

        Pos alignmentStyle;

        switch (alignment) {
            case "Left":
                alignmentStyle = Pos.CENTER_LEFT;
                break;
            case "Right":
                alignmentStyle = Pos.CENTER_RIGHT;
                break;
            default:
                alignmentStyle = Pos.CENTER;
        }

        for (Label cellLabel : mainController.getAllCellLabelsInColumn(selectedColumnLabel.getText())) {
            cellLabel.setAlignment(alignmentStyle);
        }

        mainController.setColumnAlignment(selectedColumnLabel.getText(), alignmentStyle);
    }

    public void resetColumnAlignmentComboBox(){
        columnAlignmentComboBox.getSelectionModel().clearSelection();
        columnAlignmentComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "Column Alignment" : item);
            }
        });
    }

    public void addClickEventForSelectedColumn(Label label){
        label.setOnMouseClicked(event -> {
            selectedColumnProperty().set(label.getText());
            mainController.getSelectedCellProperty().set(label.getText() + 1);
            selectedRowProperty.set("1");
            mainController.highlightColumn(selectedColumnLabel.getText());
            resetColumnAlignmentComboBox();
            resetColumnSlider();
            resetRowSlider();
        });
    }

    public void addClickEventForSelectedRow(Label label){
        label.setOnMouseClicked(event -> {
            selectedRowProperty().set(label.getText());
            mainController.getSelectedCellProperty().set('A' + label.getText());
            selectedColumnProperty.set("A");
            resetRowSlider();
            resetColumnSlider();
        });
    }

    public void changeColumnWidth(Integer newWidth) {
        // Get the selected column from the label
        String selectedColumn = selectedColumnLabel.getText();
        if (selectedColumn == null || selectedColumn.isEmpty()) {
            return;
        }

        // Iterate over all cells in the selected column and set their new width
        ColumnConstraints column = mainController.getColumnConstraintsByColumn(selectedColumn);
        column.setPrefWidth(newWidth);

        // Optionally, store the new width in the mainController if you want to save the column's state
        mainController.setColumnWidth(selectedColumn, newWidth);
    }

    public void resetColumnSlider(){
        String selectedColumn = selectedColumnLabel.getText();
        if (selectedColumn == null || selectedColumn.isEmpty()) {
            return;
        }

        columnWidthSlider.setValue(mainController.getColumnWidth(selectedColumn));
    }

    public void changeRowHeight(Integer newHeight) {
        // Get the selected column from the label
        String selectedRow = selectedRowLabel.getText();
        if (selectedRow == null || selectedRow.isEmpty()) {
            return;
        }

        // Iterate over all cells in the selected column and set their new width
        RowConstraints row = mainController.getRowConstraintsByRow(selectedRow);
        row.setPrefHeight(newHeight);

        // Optionally, store the new width in the mainController if you want to save the column's state
        mainController.setRowHeight(selectedRow, newHeight);
    }

    public void resetRowSlider(){
        String selectedRow = selectedRowLabel.getText();
        if (selectedRow == null || selectedRow.isEmpty()) {
            return;
        }

        rowHeightSlider.setValue(mainController.getRowHeight(selectedRow));
    }

    @FXML
    void filterButtonOnAction(ActionEvent event) {
       //String column = selectedColumnLabel.getText();
       showFilterPopup();
    }

    @FXML
    private void sortButtonOnAction(ActionEvent event) {
        showSortPopup();
    }

    //////Filtering one column
//    private void showFilterPopup() {
//        Stage popupStage = new Stage();
//        popupStage.initModality(Modality.APPLICATION_MODAL);
//        popupStage.setTitle("Filter");
//
//        VBox vbox = new VBox();
//        vbox.setSpacing(10);
//        vbox.setPadding(new Insets(15, 15, 15, 15));
//
//        Label rangeLabel = new Label("Enter range for filter: ");
//        vbox.getChildren().add(rangeLabel);
//
//        // TextField to enter range
//        TextField rangeField = new TextField();
//        rangeField.setPromptText("Enter cell range (e.g., A1..A10)");
//
//        // Add "Choose range" button
//        Button chooseRangeButton = new Button("Choose range");
//        chooseRangeButton.setDisable(false); // Initially enabled
//
//        HBox rangeBox = new HBox(10); // Horizontal box to hold the TextField and Button
//        rangeBox.getChildren().addAll(rangeField, chooseRangeButton);
//        vbox.getChildren().add(rangeBox);
//
//        // ComboBox to select column (initially disabled)
//        Label columnLabel = new Label("Select column to filter:");
//        ComboBox<String> columnComboBox = new ComboBox<>();
//        columnComboBox.setDisable(true); // Initially disabled
//
//        vbox.getChildren().add(columnLabel);
//        vbox.getChildren().add(columnComboBox);
//
//        // VBox for checkboxes
//        VBox checkBoxContainer = new VBox();
//        vbox.getChildren().add(checkBoxContainer);
//
//        // OK Button (initially disabled)
//        Button okButton = new Button("OK");
//        okButton.setDisable(true); // Disable the button initially
//        vbox.getChildren().add(okButton);
//
//        // Add listener for the "Choose range" button
//        chooseRangeButton.setOnAction(e -> {
//            String rangeStr = rangeField.getText();
//            if (!rangeStr.isEmpty()) {
//
//                columnComboBox.setDisable(false);
//
//                try {
//                    List<String> availableColumns = mainController.getEngine().getColumnsWithinRange(rangeStr);
//                    columnComboBox.getItems().clear(); // Clear previous items
//                    columnComboBox.getItems().addAll(availableColumns); // Populate ComboBox with columns
//                }
//                catch (Exception exception){
//                    mainController.showAlert("Error", "Invalid input", exception.getMessage(), Alert.AlertType.ERROR);
//                }
//            }
//        });
//
//        // Add listener for the ComboBox to enable the OK button
//        columnComboBox.setOnAction(e -> {
//            // Clear the previous checkboxes
//            checkBoxContainer.getChildren().clear();
//
//            // Get selected column and populate checkboxes
//            String selectedColumn = columnComboBox.getValue();
//            List<String> uniqueValues = mainController.getEngine().createListOfValuesForFilter(selectedColumn, rangeField.getText());
//
//            for (String value : uniqueValues) {
//                CheckBox checkBox = new CheckBox(value);
//                checkBoxContainer.getChildren().add(checkBox); // Add checkbox to the container
//            }
//
//            // Enable the OK button if checkboxes are present
//            okButton.setDisable(checkBoxContainer.getChildren().isEmpty());
//        });
//
//        // OK Button action
//        okButton.setOnAction(e -> {
//            List<String> selectedValues = new ArrayList<>();
//            // Gather selected checkbox values
//            for (Node node : checkBoxContainer.getChildren()) {
//                if (node instanceof CheckBox) {
//                    CheckBox checkBox = (CheckBox) node;
//                    if (checkBox.isSelected()) {
//                        selectedValues.add(checkBox.getText());
//                    }
//                }
//            }
//
//            popupStage.close(); // Close the popup after filtering
//
//            DTOsheet dtoSheet = mainController.getEngine().filterColumnBasedOnSelection(rangeField.getText(), selectedValues, columnComboBox.getValue());
//            mainController.displayFilteredSheetInPopup(dtoSheet);
//        });
//
//        Scene scene = new Scene(vbox, 300, 400);
//        popupStage.setScene(scene);
//        popupStage.showAndWait();
//    }

    ////////Bonus for filtering another coloumn
    private void showFilterPopup() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Filter");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(15, 15, 15, 15));

        Label rangeLabel = new Label("Enter range for filter: ");
        vbox.getChildren().add(rangeLabel);

        // TextField to enter range
        TextField rangeField = new TextField();
        rangeField.setPromptText("Enter cell range (e.g., A1..A10)");

        // Add "Choose range" button
        Button chooseRangeButton = new Button("Choose range");
        chooseRangeButton.setDisable(false);

        HBox rangeBox = new HBox(10); // Horizontal box to hold the TextField and Button
        rangeBox.getChildren().addAll(rangeField, chooseRangeButton);
        vbox.getChildren().add(rangeBox);

        // VBox to hold multiple column filter sections
        VBox columnSections = new VBox();
        vbox.getChildren().add(columnSections);

        // Button to add additional columns (initially disabled)
        Button addColumnButton = new Button("Add Column");
        addColumnButton.setDisable(true);
        vbox.getChildren().add(addColumnButton);

        // OK Button (initially disabled)
        Button okButton = new Button("OK");
        okButton.setDisable(true);
        vbox.getChildren().add(okButton);

        // Add listener for "Choose range" button
        chooseRangeButton.setOnAction(e -> {
            String rangeStr = rangeField.getText();
            if (!rangeStr.isEmpty()) {
                try {
                    // Validate the range and get available columns within it
                    List<String> availableColumns = mainController.getEngine().getColumnsWithinRange(rangeStr);

                    // Enable the "Add Column" button and clear previous sections
                    addColumnButton.setDisable(false);
                    columnSections.getChildren().clear(); // Clear any existing column sections

                    // Create the first column section
                    createColumnSection(columnSections, okButton, rangeField, availableColumns);

                    // Allow adding more columns
                    addColumnButton.setOnAction(ev -> createColumnSection(columnSections, okButton, rangeField, availableColumns));

                } catch (Exception exception) {
                    mainController.showAlert("Error", "Invalid input", exception.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });

        // OK Button action
        okButton.setOnAction(e -> {
            Map<String, List<String>> columnToValues = new HashMap<>();

            // Collect selected columns and values from checkboxes
            for (Node node : columnSections.getChildren()) {
                if (node instanceof HBox) {
                    HBox columnSection = (HBox) node;
                    ComboBox<String> columnComboBox = (ComboBox<String>) columnSection.getChildren().get(1); // Column ComboBox
                    VBox checkBoxContainer = (VBox) columnSection.getChildren().get(2); // Checkbox container

                    String selectedColumn = columnComboBox.getValue();
                    List<String> selectedValues = new ArrayList<>();
                    for (Node checkBoxNode : checkBoxContainer.getChildren()) {
                        CheckBox checkBox = (CheckBox) checkBoxNode;
                        if (checkBox.isSelected()) {
                            selectedValues.add(checkBox.getText());
                        }
                    }
                    columnToValues.put(selectedColumn, selectedValues); // Add column and its selected values
                }
            }

            // Close the popup after filtering
            popupStage.close();

            // Call the engine method to filter based on the selected columns and values
            newCoordToOldCoord.clear();
            DTOsheet dtoSheet = mainController.getEngine().filterColumnBasedOnSelection(rangeField.getText(), columnToValues, newCoordToOldCoord);
            mainController.displayFilteredSheetInPopup(dtoSheet);
        });

        scrollPane.setContent(vbox);
        Scene scene = new Scene(scrollPane, 300, 400);
        mainController.setTheme(scene);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private void createColumnSection(VBox columnSections, Button okButton, TextField rangeField, List<String> availableColumns) {
        HBox columnSection = new HBox(10); // Horizontal box for column and checkboxes
        ComboBox<String> columnComboBox = new ComboBox<>(); // ComboBox for selecting the column
        VBox checkBoxContainer = new VBox(); // VBox for checkboxes

        // Populate the column ComboBox with available columns
        columnComboBox.getItems().addAll(availableColumns);
        columnComboBox.setDisable(availableColumns.isEmpty()); // Disable if no columns available

        columnSection.getChildren().addAll(new Label("Select column:"), columnComboBox, checkBoxContainer);
        columnSections.getChildren().add(columnSection);

        // Add listener for ComboBox to populate checkboxes
        columnComboBox.setOnAction(e -> {
            checkBoxContainer.getChildren().clear();
            String selectedColumn = columnComboBox.getValue();
            List<String> uniqueValues = mainController.getEngine().createListOfValuesForFilter(selectedColumn, rangeField.getText());

            for (String value : uniqueValues) {
                CheckBox checkBox = new CheckBox(value);
                checkBoxContainer.getChildren().add(checkBox); // Add checkbox to the container
            }

            // Enable OK button if there are checkboxes
            okButton.setDisable(checkBoxContainer.getChildren().isEmpty());
        });
    }



//    private void updateOkButtonState(TextField rangeField, ComboBox<String> columnComboBox, List<CheckBox> checkBoxes, Button okButton) {
//        boolean isRangeFilled = !rangeField.getText().trim().isEmpty();
//        boolean isColumnSelected = columnComboBox.getValue() != null && !columnComboBox.getValue().trim().isEmpty();
//        boolean isCheckboxSelected = checkBoxes.stream().anyMatch(CheckBox::isSelected);
//
//        // Enable the OK button if all conditions are met
//        okButton.setDisable(!(isRangeFilled && isColumnSelected && isCheckboxSelected));
//    }


    @FXML
    void dynamicAnalysisButtonAction(ActionEvent event) {
        BorderPane root = new BorderPane();

        HBox selectedCellBox = new HBox();
        Label introToSelectedCell = new Label("Cell: ");
        Label cellToDynamicAnalysis = new Label(selectedCellLabel.getText());
        cellToDynamicAnalysis.setStyle("-fx-font-weight: bold");
        cellToDynamicAnalysis.textProperty().bind(selectedCellLabel.textProperty());
        selectedCellBox.getChildren().addAll(introToSelectedCell, cellToDynamicAnalysis);

        // Left VBox
        VBox leftVBox = new VBox(3);
        leftVBox.setPadding(new Insets(8, 8, 8, 8));
        leftVBox.setPrefSize(127, 400);

        // Minimum Value Label and TextField
        Label minValueLabel = new Label("Minimum Value:");
        TextField minValueTextField = new TextField();
        VBox.setMargin(minValueLabel, new Insets(10, 0, 0, 0));

        // Maximum Value Label and TextField
        Label maxValueLabel = new Label("Maximum Value:");
        TextField maxValueTextField = new TextField();
        VBox.setMargin(maxValueLabel, new Insets(10, 0, 0, 0));

        // Step Size Label and TextField
        Label stepSizeLabel = new Label("Step Size:");
        TextField stepSizeTextField = new TextField();
        VBox.setMargin(stepSizeLabel, new Insets(10, 0, 0, 0));

        // Slider
        Slider valueSlider = new Slider();
        VBox.setMargin(valueSlider, new Insets(10, 0, 0, 0));

        valueSlider.setDisable(true);  // Initially disabled

        // Call the validate function for each text field, passing the other text fields for the slider enable/disable check
        validateAndSetSlider(maxValueTextField, valueSlider, "Please enter a \nvalid max value.", minValueTextField, maxValueTextField, stepSizeTextField);
        validateAndSetSlider(minValueTextField, valueSlider, "Please enter a \nvalid min value.", minValueTextField, maxValueTextField, stepSizeTextField);
        validateAndSetSlider(stepSizeTextField, valueSlider, "Please enter a \nvalid step size.", minValueTextField, maxValueTextField, stepSizeTextField);

        // Add components to the VBox
        leftVBox.getChildren().addAll(selectedCellBox, minValueLabel, minValueTextField,
                maxValueLabel, maxValueTextField, stepSizeLabel,
                stepSizeTextField);

        // Set VBox to the left of BorderPane
        root.setLeft(leftVBox);

        // Center ScrollPane with GridPane inside
  //      ScrollPane scrollPane = new ScrollPane();
        GridPane gridPane = new GridPane();


        // Create a new SheetController instance for the pop-up
        SheetController newSheetController = new SheetController();
        newSheetController.setMainController(this.mainController);
        newSheetController.setDynamicGridPane(gridPane); // Set gridPane to be used in the setSheet method
        newSheetController.initializeSheetController();
        DTOsheet dtoSheet = mainController.getEngine().createDTOCopySheet();
        SimpleStringProperty selectedCell = mainController.getSelectedCellProperty();
        newSheetController.setSheet(dtoSheet, false);   // Populate the grid with sheet data
        newSheetController.getCellLabel(selectedCell.getValue()).setStyle("-fx-background-color: yellow; -fx-text-fill: black");


        mainController.setTheme(root.getScene());

        selectedCell.addListener((observable, oldValue, newValue) -> {
            newSheetController.getCellLabel(oldValue).setStyle("");
            newSheetController.getCellLabel(newValue).setStyle("-fx-background-color: yellow; -fx-text-fill: black");
            minValueTextField.clear();
            maxValueTextField.clear();
            stepSizeTextField.clear();

        });

        valueSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (selectedCell != null && selectedCell.get() != null) {
                Map<String, EffectiveValue> cellsNewValues =  mainController.getEngine().getCellsThatHaveChangedAfterUpdateCell(selectedCellLabel.getText(), String.valueOf(newValue.intValue()));
                for(Map.Entry<String, EffectiveValue> entry : cellsNewValues.entrySet())
                {
                    Label newCellLabel = newSheetController.getCellLabel(entry.getKey());
                    newCellLabel.setText(cellsNewValues.get(entry.getKey()).getValue().toString());
                }
            }
        });

  //      scrollPane.setContent(gridPane);
        root.setCenter(gridPane);
        leftVBox.getChildren().add(valueSlider);
        valueSlider.setShowTickMarks(true);
        valueSlider.setShowTickLabels(true);

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Sheet Version Popup");

        Scene scene = new Scene(root);
        mainController.setTheme(scene);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }


    private void validateAndSetSlider(TextField textField, Slider valueSlider, String labelText, TextField minValueTextField, TextField maxValueTextField, TextField stepSizeTextField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            valueSlider.setDisable(minValueTextField.getText().trim().isEmpty() ||
                    maxValueTextField.getText().trim().isEmpty() ||
                    stepSizeTextField.getText().trim().isEmpty());

            VBox parentVBox = (VBox) textField.getParent();
            Label errorLabel = null;

            for (Node node : parentVBox.getChildren()) {
                if (node instanceof Label && "errorLabel".equals(node.getId())) {
                    errorLabel = (Label) node;
                    break;
                }
            }

            // Create a new error label if it doesn't already exist
            if (errorLabel == null) {
                errorLabel = new Label();
                errorLabel.setId("errorLabel");
                errorLabel.setTextFill(Color.RED);
            }

            // Clear previous styles
            textField.setStyle("");

            // Check if the TextField is not empty
            if (!textField.getText().trim().isEmpty()) {
                try {
                    // Attempt to parse the double value from the TextField
                    double value = Double.parseDouble(textField.getText());

                    // Set the value as the slider's max and apply green border
                    if(textField == maxValueTextField)
                        valueSlider.setMax(value);
                    else if(textField == minValueTextField)
                        valueSlider.setMin(value);
                    else if(textField == stepSizeTextField)
                        valueSlider.setBlockIncrement(value);

                    textField.setStyle("-fx-border-color: green; -fx-background-color: #dcfbdc; -fx-border-width: 2px; -fx-text-fill: black");

                    // Remove error label if the value is valid
                    if (parentVBox.getChildren().contains(errorLabel)) {
                        parentVBox.getChildren().remove(errorLabel);
                    }
                }
                catch (NumberFormatException e) {
                    // Invalid number: show red border and add the error label
                    textField.setStyle("-fx-border-color: red; -fx-background-color: #ffdddd; -fx-border-width: 2px; -fx-text-fill: black ");
                    errorLabel.setText(labelText);

                    // Add the error label if it's not already present
                    if (!parentVBox.getChildren().contains(errorLabel)) {
                        parentVBox.getChildren().add(errorLabel);
                    }
                    valueSlider.setDisable(true);
                }
            } else {
                // If the field is empty, reset the styles and remove any error label
                textField.setStyle("");
                if (parentVBox.getChildren().contains(errorLabel)) {
                    parentVBox.getChildren().remove(errorLabel);
                }
            }
        });
    }


    @FXML
    void createGraphButtonOnAction(ActionEvent event) {
        // Create a new Stage (popup window)
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Create Graph");

        // Create a VBox for the input form
        VBox inputFormVBox = new VBox(10);
        inputFormVBox.setPadding(new Insets(20));

        // Create and configure the text fields for X-axis and Y-axis range input
        Label xAxisLabel = new Label("X-axis (Cells Range):");
        TextField xAxisField = new TextField();
        xAxisField.setPromptText("e.g., \"A1..A3\", \"D2..D5\"");
        inputFormVBox.getChildren().addAll(xAxisLabel, xAxisField);

        Label yAxisLabel = new Label("Y-axis (Cells Range): ");
        TextField yAxisField = new TextField();
        yAxisField.setPromptText("e.g., \"A1..A3\", \"D2..D5\"");
        inputFormVBox.getChildren().addAll(yAxisLabel, yAxisField);

        // Create a combo box to allow selection of graph type (Line Graph or Bar Graph)
        Label graphTypeLabel = new Label("Select Graph Type: ");
        ComboBox<String> graphTypeComboBox = new ComboBox<>();
        graphTypeComboBox.getItems().addAll("Line Graph", "Bar Graph");
        graphTypeComboBox.setValue("Line Graph"); // Default selection
        inputFormVBox.getChildren().addAll(graphTypeLabel, graphTypeComboBox);

        // Create and configure the submit button
        Button submitButton = new Button("Create Graph");
        submitButton.setDisable(true);  // Initially disabled

        // Enable the submit button only when both fields are not empty
        xAxisField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(xAxisField.getText().trim().isEmpty() || yAxisField.getText().trim().isEmpty());
        });

        yAxisField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(xAxisField.getText().trim().isEmpty() || yAxisField.getText().trim().isEmpty());
        });

        // Scene for graph display
        Scene inputScene = new Scene(inputFormVBox, 400, 300);

        submitButton.setOnAction(e -> {
            String xAxisRange = xAxisField.getText();
            String yAxisRange = yAxisField.getText();
            String selectedGraphType = graphTypeComboBox.getValue();

            try {
                // Parse the ranges
                List<Double> xAxisValues = mainController.getEngine().getNumericalValuesFromRange(xAxisRange);
                List<Double> yAxisValues = mainController.getEngine().getNumericalValuesFromRange(yAxisRange);

                if (xAxisValues.size() != yAxisValues.size()) {
                    throw new IllegalArgumentException("X and Y ranges must have the same number of values.");
                }

                Chart chart = null;
                if ("Line Graph".equals(selectedGraphType)) {
                    NumberAxis xAxis = new NumberAxis();
                    xAxis.setLabel("X Axis");

                    NumberAxis yAxis = new NumberAxis();
                    yAxis.setLabel("Y Axis");

                    LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
                    XYChart.Series<Number, Number> dataSeries = new XYChart.Series<>();
                    for (int i = 0; i < xAxisValues.size(); i++) {
                        dataSeries.getData().add(new XYChart.Data<>(xAxisValues.get(i), yAxisValues.get(i)));
                    }

                    lineChart.getData().add(dataSeries);
                    chart = lineChart;
                    lineChart.getStyleClass().add("visible");
                    if(!mainController.isAnimationSelectedProperty())
                        chart.setAnimated(false);

                }
                else if ("Bar Graph".equals(selectedGraphType)) {
                    CategoryAxis xAxis = new CategoryAxis();
                    xAxis.setLabel("X Axis");

                    NumberAxis yAxis = new NumberAxis();
                    yAxis.setLabel("Y Axis");

                    BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

                    XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
                    for (int i = 0; i < xAxisValues.size(); i++) {
                        dataSeries.getData().add(new XYChart.Data<>(xAxisValues.get(i).toString(), yAxisValues.get(i)));
                    }

                    barChart.getData().add(dataSeries);
                    chart = barChart;

                    if(!mainController.isAnimationSelectedProperty())
                        chart.setAnimated(false);
                }

                // Display the chart with a "Back" button
                showChartInPopup(popupStage, chart, inputScene);

            } catch (Exception ex) {
                mainController.showAlert("Error", "Could not create graph", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        inputFormVBox.getChildren().add(submitButton);
        popupStage.setScene(inputScene);
        mainController.setTheme(inputScene);
        popupStage.show();
    }

    private void showChartInPopup(Stage popupStage, Chart chart, Scene inputScene) {
        VBox chartVBox = new VBox(chart);
        chartVBox.setPadding(new Insets(10));

        // Add a "Back" button to go back to the input scene
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            // Set the input scene again when "Back" is clicked
            popupStage.setScene(inputScene);
        });

        chartVBox.getChildren().add(backButton);

        Scene chartScene = new Scene(chartVBox, 800, 500);
        mainController.setTheme(inputScene);
        setGraphStyle(chartScene);
        popupStage.setScene(chartScene);
    }

    public void setGraphStyle(Scene scene){
        String css = getClass().getResource(  "/left/command/graph/style/"+ mainController.getSelectedTheme() + "Graph.css").toExternalForm();
        scene.getStylesheets().add(css);
    }

    private void showSortPopup() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Sort");

        ScrollPane scrollPane = new ScrollPane();

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(15, 15, 15, 15));

        Label rangeLabel = new Label("Enter range for sorting: ");
        vbox.getChildren().add(rangeLabel);

        // TextField to enter range
        TextField rangeField = new TextField();
        rangeField.setPromptText("Enter cell range (e.g., A1..A10)");

        // Add "Choose range" button
        Button chooseRangeButton = new Button("Choose range");
        chooseRangeButton.setDisable(false); // Initially enabled

        HBox rangeBox = new HBox(10); // Horizontal box to hold the TextField and Button
        rangeBox.getChildren().addAll(rangeField, chooseRangeButton);
        vbox.getChildren().add(rangeBox);

        // Add a label before checkboxes
        Label selectColumnsLabel = new Label("Select columns to sort by: ");
        vbox.getChildren().add(selectColumnsLabel);

        // VBox for checkboxes
        VBox checkBoxContainer = new VBox();
        vbox.getChildren().add(checkBoxContainer);

        // OK Button (initially disabled)
        Button okButton = new Button("OK");
        okButton.setDisable(true); // Disable the button initially
        vbox.getChildren().add(okButton);

        // Add listener for the "Choose range" button
        chooseRangeButton.setOnAction(e -> {
            String rangeStr = rangeField.getText();
            if (!rangeStr.isEmpty()) {

                try{
                List<String> availableColumns = mainController.getEngine().getColumnsWithinRange(rangeStr);
                checkBoxContainer.getChildren().clear(); // Clear previous checkboxes

                for (String column : availableColumns) {
                    CheckBox checkBox = new CheckBox(column);
                    checkBoxContainer.getChildren().add(checkBox);
                }

                // Enable the OK button if at least one checkbox is selected
                okButton.setDisable(false);
                // Add a listener to enable the OK button only if at least one checkbox is checked
                checkBoxContainer.getChildren().forEach(node -> {
                    if (node instanceof CheckBox checkBox) {
                        checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                            okButton.setDisable(checkBoxContainer.getChildren().stream()
                                    .filter(n -> n instanceof CheckBox)
                                    .noneMatch(c -> ((CheckBox) c).isSelected()));
                        });
                    }
                });
                }
                catch (Exception exception){
                    mainController.showAlert("Error", "Invalid input", exception.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });

        // Set action for OK button
        okButton.setOnAction(e -> {
            // Handle sorting logic based on selected checkboxes
            List<String> selectedColumns = new ArrayList<>();
            for (Node node : checkBoxContainer.getChildren()) {
                if (node instanceof CheckBox checkBox && checkBox.isSelected()) {
                    selectedColumns.add(checkBox.getText());
                }
            }

            popupStage.close(); // Close the popup after sorting

            DTOsheet dtoSheet = mainController.getEngine().sortColumnBasedOnSelection(rangeField.getText(), selectedColumns);
            mainController.displaySortedSheetInPopup(dtoSheet);
        });

        // Set scene and show the popup
        scrollPane.setContent(vbox);
        Scene scene = new Scene(scrollPane, 400, 300);
        mainController.setTheme(scene);
        popupStage.setScene(scene);
        popupStage.show();
    }

    public Map<String,String> getNewCoordToOldCoord() {return newCoordToOldCoord; }

}
