package left.command;

import app.AppController;
import dto.DTOsheet;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sheet.SheetController;
import sheet.api.EffectiveValue;
import sheet.coordinate.impl.CoordinateFactory;

import java.io.IOException;
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

    private AppController mainController;
    private SimpleStringProperty selectedColumnProperty;
    private SimpleStringProperty selectedRowProperty;
    public static final String DEFAULT_CELL_STYLE = "-fx-background-color: white; -fx-text-fill: black;";

    public void initializeCommandController(){
        selectedCellLabel.textProperty().bind(mainController.getSelectedCellProperty());
        selectedColumnLabel.textProperty().bind(selectedColumnProperty);
        selectedRowLabel.textProperty().bind(selectedRowProperty);
        cellBackgroundColorPicker.disableProperty().bind(mainController.getSelectedCellProperty().isNull());
        cellTextColorPicker.disableProperty().bind(mainController.getSelectedCellProperty().isNull());
        resetCellDesignButton.disableProperty().bind(mainController.getSelectedCellProperty().isNull()
                .or(mainController.getSelectedCellProperty().isNotNull().and(isDefaultCellStyle())));
        columnAlignmentComboBox.disableProperty().bind(selectedColumnProperty.isNull());
        columnWidthSlider.disableProperty().bind(selectedColumnProperty.isNull());
        rowHeightSlider.disableProperty().bind(selectedRowProperty.isNull());

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
//        selectedColumnLabel.setStyle("-fx-font-size: 14px;");
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
       String column = selectedColumnLabel.getText();
       showFilterPopup(column);
    }

    private void showFilterPopup(String column) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Filter");

        VBox vbox = new VBox();

        vbox.setSpacing(10);  // Adds space between elements
        vbox.setPadding(new Insets(15, 15, 15, 15));

        vbox.getChildren().add(new Label("Enter range for filter: "));

        // TextField to enter range
        TextField rangeField = new TextField();
        rangeField.setPromptText("Enter cell range (e.g., A1..A10)");
        vbox.getChildren().add(rangeField);

        // Create a list of unique values in the column
        List<String> values = mainController.getEngine().createListOfValuesForFilter(column);

        // A list to hold all checkboxes for the values
        List<CheckBox> checkBoxes = new ArrayList<>();

        // Add checkboxes for unique values
        for (String uniqueValue : values) {
            CheckBox checkBox = new CheckBox(uniqueValue);
            //checkBox.setPadding(new Insets(5, 0, 5, 0));
            vbox.getChildren().add(checkBox);
            checkBoxes.add(checkBox); // Keep track of the checkboxes
        }

        // OK Button (initially disabled)
        Button okButton = new Button("OK");
        okButton.setDisable(true); // Disable the button initially
        vbox.getChildren().add(okButton);

        // Add listeners to the TextField and checkboxes to enable the OK button when conditions are met
        rangeField.textProperty().addListener((observable, oldValue, newValue) ->
                updateOkButtonState(rangeField, checkBoxes, okButton));

        for (CheckBox checkBox : checkBoxes) {
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) ->
                    updateOkButtonState(rangeField, checkBoxes, okButton));
        }

        // OK Button action
        okButton.setOnAction(e -> {
            List<String> selectedValues = new ArrayList<>();

            for (CheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    selectedValues.add(checkBox.getText());
                }
            }

            // Logic to filter based on selected values and range
            //mainController.getEngine().filterColumnBasedOnSelection(rangeField.getText(), selectedValues, column);

            popupStage.close(); // Close the popup after filtering

            DTOsheet dtoSheet = mainController.getEngine().filterColumnBasedOnSelection(rangeField.getText(), selectedValues, column);
            mainController.displaySheetVersionInPopup(dtoSheet);
        });

        // Set the scene and show the popup
        Scene scene = new Scene(vbox, 300, 400);
        popupStage.setScene(scene);
        popupStage.sizeToScene();
        popupStage.showAndWait();
    }

    private void updateOkButtonState(TextField rangeField, List<CheckBox> checkBoxes, Button okButton) {
        // Check if the range is entered (not empty) and if at least one checkbox is selected
        boolean rangeEntered = !rangeField.getText().trim().isEmpty();
        boolean atLeastOneSelected = checkBoxes.stream().anyMatch(CheckBox::isSelected);

        // Enable the OK button only if both conditions are true
        okButton.setDisable(!(rangeEntered && atLeastOneSelected));
    }


    @FXML
    void dynamicAnalysisButtonAction(ActionEvent event) {
        BorderPane root = new BorderPane();

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


        minValueTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            valueSlider.setDisable(minValueTextField.getText().trim().isEmpty() ||
                    maxValueTextField.getText().trim().isEmpty() ||
                    stepSizeTextField.getText().trim().isEmpty());
            if(!minValueTextField.getText().trim().isEmpty())
            {
                double minValue = Double.parseDouble(minValueTextField.getText());
                valueSlider.setMin(minValue);
            }
        });

        maxValueTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            valueSlider.setDisable(minValueTextField.getText().trim().isEmpty() ||
                    maxValueTextField.getText().trim().isEmpty() ||
                    stepSizeTextField.getText().trim().isEmpty());
            if(!maxValueTextField.getText().trim().isEmpty())
            {
                double maxValue = Double.parseDouble(maxValueTextField.getText());
                valueSlider.setMax(maxValue);
            }
        });

        stepSizeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            valueSlider.setDisable(minValueTextField.getText().trim().isEmpty() ||
                    maxValueTextField.getText().trim().isEmpty() ||
                    stepSizeTextField.getText().trim().isEmpty());
            if(!stepSizeTextField.getText().trim().isEmpty())
            {
                double stepSize = Double.parseDouble(stepSizeTextField.getText());
                valueSlider.setBlockIncrement(stepSize);
            }
        });

        // Add components to the VBox
        leftVBox.getChildren().addAll(selectedCellLabel, minValueLabel, minValueTextField,
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
        mainController.setSheet(dtoSheet, false);   // Populate the grid with sheet data

        root.getStylesheets().add(getClass().getResource("/sheet/sheet.css").toExternalForm());
        SimpleStringProperty selectedCell = mainController.getSelectedCellProperty();;

        selectedCell.addListener((observable, oldValue, newValue) -> {
            newSheetController.getCellLabel(oldValue).setStyle("");
            newSheetController.getCellLabel(newValue).setStyle("-fx-background-color: yellow");
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
        popupStage.setScene(scene);
        popupStage.showAndWait();
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
                    // Create a line chart
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
//                    chart.setAnimated(); DO WHEN CREATE ANIMATIONS
                    chart.setAnimated(false);

                }
                else if ("Bar Graph".equals(selectedGraphType)) {
                    // Create a bar chart
                    CategoryAxis xAxis = new CategoryAxis();
                    xAxis.setLabel("X Axis");

                    NumberAxis yAxis = new NumberAxis();
                    yAxis.setLabel("Y Axis");

                    BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
//                    barChart.setTitle("Bar Graph");

                    XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
                    for (int i = 0; i < xAxisValues.size(); i++) {
                        dataSeries.getData().add(new XYChart.Data<>(xAxisValues.get(i).toString(), yAxisValues.get(i)));
                    }

                    barChart.getData().add(dataSeries);
                    chart = barChart;
                    chart.setAnimated(false); //////changeeeee
                }

                // Display the chart with a "Back" button
                showChartInPopup(popupStage, chart, inputScene);

            } catch (Exception ex) {
                mainController.showAlert("Error", "Could not create graph", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        inputFormVBox.getChildren().add(submitButton);
        popupStage.setScene(inputScene);
        popupStage.getScene().getStylesheets().add("/left/command/graph.css");
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
        chartScene.getStylesheets().add("/left/command/graph.css");
        popupStage.setScene(chartScene);
    }

}
