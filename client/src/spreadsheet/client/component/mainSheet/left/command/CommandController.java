package spreadsheet.client.component.mainSheet.left.command;

import com.google.gson.GsonBuilder;
import dto.DTOsheet;
import javafx.application.Platform;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.HBox;
import okhttp3.*;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.api.CoordinateDeserializer;
import spreadsheet.client.component.mainSheet.MainSheetController;
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
import javafx.stage.Modality;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.function.Consumer;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import spreadsheet.client.util.Constants;
import spreadsheet.client.util.ShowAlert;
import spreadsheet.client.util.http.HttpClientUtil;


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

    private MainSheetController mainSheetController;
    private SimpleStringProperty selectedColumnProperty;
    private SimpleStringProperty selectedRowProperty;
    private Map<String, String> oldCoordToNewCoord;
    public static final String DEFAULT_CELL_STYLE = "-fx-background-color: white; -fx-text-fill: black;";
    private SimpleBooleanProperty isEditDisabledProperty;

    public void initializeCommandController(){
        BooleanBinding noSelectedCell = mainSheetController.getSelectedCellProperty().isNull();
        isEditDisabledProperty = new SimpleBooleanProperty(false);

        selectedCellLabel.textProperty().bind(mainSheetController.getSelectedCellProperty());
        selectedColumnLabel.textProperty().bind(selectedColumnProperty);
        selectedRowLabel.textProperty().bind(selectedRowProperty);
        cellBackgroundColorPicker.disableProperty().bind(Bindings.or(noSelectedCell, isEditDisabledProperty));
        cellTextColorPicker.disableProperty().bind(Bindings.or(noSelectedCell, isEditDisabledProperty));
//        resetCellDesignButton.disableProperty().bind(noSelectedCell
//                .or(mainSheetController.getSelectedCellProperty().isNotNull().and(isDefaultCellStyle())));
        resetCellDesignButton.disableProperty().bind(
                noSelectedCell
                        .or(mainSheetController.getSelectedCellProperty().isNotNull().and(isDefaultCellStyle()))
                        .or(isEditDisabledProperty));// the new one for the permissions
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

    public void setMainSheetController(MainSheetController mainSheetController) {
        this.mainSheetController = mainSheetController;
    }

    @FXML
    public void initialize(){
        selectedColumnProperty = new SimpleStringProperty();
        selectedRowProperty = new SimpleStringProperty();
        columnAlignmentComboBox.getItems().addAll("Left","Center", "Right");
        oldCoordToNewCoord = new HashMap<>();
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
        SimpleStringProperty selectedCell = mainSheetController.getSelectedCellProperty();
        Label cellLabel = mainSheetController.getCellLabel(selectedCell.get());

        // Update the background color of the cell
        String currentStyle = cellLabel.getStyle();
        String newStyle = currentStyle + backgroundColor;
        cellLabel.setStyle(newStyle);

        // Save the style in the cellStyles map
        mainSheetController.getCellStyles().put(selectedCell.get(), newStyle);
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
        SimpleStringProperty selectedCell = mainSheetController.getSelectedCellProperty();
        Label cellLabel = mainSheetController.getCellLabel(selectedCell.get());

        // Update the text color of the cell
        String currentStyle = cellLabel.getStyle();
        String newStyle = currentStyle + textColor;
        cellLabel.setStyle(newStyle);

        // Save the style in the cellStyles map
        mainSheetController.getCellStyles().put(selectedCell.get(), newStyle);
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
        SimpleStringProperty selectedCell = mainSheetController.getSelectedCellProperty();
        if (selectedCell != null && selectedCell.get() != null) {
            Label cellLabel = mainSheetController.getCellLabel(selectedCell.get());

            // Reset the cell's style to the default
            cellLabel.setStyle("");

            // Save the reset style in the cellStyles map
            mainSheetController.getCellStyles().put(selectedCell.get(), cellLabel.getStyle());

            // Optionally, reset the color pickers to reflect the default colors
            cellBackgroundColorPicker.setValue(Color.WHITE);
            cellTextColorPicker.setValue(Color.BLACK);
            resetCellDesignButton.disableProperty().bind(isDefaultCellStyle());
        }
    }

    public BooleanBinding isDefaultCellStyle() {
        return new BooleanBinding() {
            {
                super.bind(mainSheetController.getSelectedCellProperty());
            }

            @Override
            protected boolean computeValue() {
                SimpleStringProperty selectedCell = mainSheetController.getSelectedCellProperty();
                if (selectedCell == null || selectedCell.get() == null) {
                    return true; // Disable if no cell is selected
                }

                Label cellLabel = mainSheetController.getCellLabel(selectedCell.get());
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

        for (Label cellLabel : mainSheetController.getAllCellLabelsInColumn(selectedColumnLabel.getText())) {
            cellLabel.setAlignment(alignmentStyle);
        }

        mainSheetController.setColumnAlignment(selectedColumnLabel.getText(), alignmentStyle);
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
            mainSheetController.getSelectedCellProperty().set(label.getText() + 1);
            selectedRowProperty.set("1");
            mainSheetController.highlightColumn(selectedColumnLabel.getText());
            resetColumnAlignmentComboBox();
            resetColumnSlider();
            resetRowSlider();
        });
    }

    public void addClickEventForSelectedRow(Label label){
        label.setOnMouseClicked(event -> {
            selectedRowProperty().set(label.getText());
            mainSheetController.getSelectedCellProperty().set('A' + label.getText());
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
        ColumnConstraints column = mainSheetController.getColumnConstraintsByColumn(selectedColumn);
        column.setPrefWidth(newWidth);

        // Optionally, store the new width in the mainController if you want to save the column's state
        mainSheetController.setColumnWidth(selectedColumn, newWidth);
    }

    public void resetColumnSlider(){
        String selectedColumn = selectedColumnLabel.getText();
        if (selectedColumn == null || selectedColumn.isEmpty()) {
            return;
        }

        columnWidthSlider.setValue(mainSheetController.getColumnWidth(selectedColumn));
    }

    public void changeRowHeight(Integer newHeight) {
        // Get the selected column from the label
        String selectedRow = selectedRowLabel.getText();
        if (selectedRow == null || selectedRow.isEmpty()) {
            return;
        }

        // Iterate over all cells in the selected column and set their new width
        RowConstraints row = mainSheetController.getRowConstraintsByRow(selectedRow);
        row.setPrefHeight(newHeight);

        // Optionally, store the new width in the mainController if you want to save the column's state
        mainSheetController.setRowHeight(selectedRow, newHeight);
    }

    public void resetRowSlider(){
        String selectedRow = selectedRowLabel.getText();
        if (selectedRow == null || selectedRow.isEmpty()) {
            return;
        }

        rowHeightSlider.setValue(mainSheetController.getRowHeight(selectedRow));
    }

    @FXML
    void filterButtonOnAction(ActionEvent event) {
       //String column = selectedColumnLabel.getText();
       showFilterPopup();
    }

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
                    // Send request to get available columns
                    getAvailableColumns(rangeStr, columnSections, addColumnButton, okButton);
                } catch (Exception exception) {
                    ShowAlert.showAlert("Error", "Invalid input", exception.getMessage(), Alert.AlertType.ERROR);
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

            String rangeStr = rangeField.getText();

            // Close the popup after filtering
            popupStage.close();

            // Send the request to apply the filter asynchronously
            applyFilter(rangeStr, columnToValues);
        });

        scrollPane.setContent(vbox);
        Scene scene = new Scene(scrollPane, 300, 400);
        mainSheetController.setSheetStyle(scene);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private void getAvailableColumns(String rangeStr, VBox columnSections, Button addColumnButton, Button okButton) {
        String selectedSheetName = mainSheetController.getSheetName();
        String finalUrl = HttpUrl
                .parse(Constants.GET_COLUMNS_FOR_FILTER)
                .newBuilder()
                .addQueryParameter("selectedSheet", selectedSheetName)
                .addQueryParameter("rangeStr", rangeStr)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .get()
                .build();

        HttpClientUtil.runAsync(finalUrl, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure (e.g., network error)
                ShowAlert.showAlert("Error", "Failed to get columns : ", e.getMessage(), Alert.AlertType.ERROR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    // Parse the response body if needed
                    // Assuming the response is a JSON array of column names

                    Platform.runLater(() -> {
                        // Enable the "Add Column" button and clear previous sections
                        List<String> availableColumns = new Gson().fromJson(responseBody, new TypeToken<List<String>>() {}.getType());

                        addColumnButton.setDisable(false);
                        columnSections.getChildren().clear(); // Clear any existing column sections

                        // Create the first column section
                        createColumnSection(columnSections, okButton, rangeStr, availableColumns);

                        // Allow adding more columns
                        addColumnButton.setOnAction(ev -> createColumnSection(columnSections, okButton, rangeStr, availableColumns));
                    });
                } else {
                    // Handle the unsuccessful response
                    Platform.runLater(() -> {
                        ShowAlert.showAlert("Error", "Failed to get columns: ", responseBody, Alert.AlertType.ERROR);
                    });
                }
            }
        });
    }

    private void applyFilter(String rangeStr, Map<String, List<String>> columnToValues) {
        // Create the map for the filter data
        Map<String, Object> filterData = new HashMap<>();
        filterData.put("rangeStr", rangeStr);
        filterData.put("columnToValues", columnToValues);
        filterData.put("selectedSheet", mainSheetController.getSheetName());
        filterData.put("oldCoordToNewCoord", oldCoordToNewCoord);

        // Convert the map to JSON
        Gson gson = new Gson();
        String jsonBody = gson.toJson(filterData);

        String finalUrl = HttpUrl
                .parse(Constants.CREATE_DTO_SHEET_FILTER) // Replace with your actual URL endpoint
                .newBuilder()
                .build()
                .toString();

        // Create the request body
        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.get("application/json; charset=utf-8")
        );

        HttpClientUtil.runAsyncPost(finalUrl, body, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure (e.g., network error)
                ShowAlert.showAlert("Error", "Failed to apply filter: ", e.getMessage(), Alert.AlertType.ERROR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();

                if (response.isSuccessful()) {
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(Coordinate.class, new CoordinateDeserializer())
                            .create();
                    Type sheet = new TypeToken<DTOsheet>(){}.getType();
                    DTOsheet filteredSheet  = gson.fromJson(responseBody, sheet);

                    Platform.runLater(() -> {
                        // Display filtered sheet in a popup or table
                        mainSheetController.displayFilteredSortedSheetInPopup(filteredSheet, "Filtered Sheet", rangeStr);
                    });
                } else {
                    // Handle the unsuccessful response
                    Platform.runLater(() -> {
                        ShowAlert.showAlert("Error", "Failed to apply filter: ", responseBody, Alert.AlertType.ERROR);
                    });
                }
            }
        });
    }


    private void createColumnSection(VBox columnSections, Button okButton, String rangeField, List<String> availableColumns) {
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

            // Create a map for the parameters
            Map<String, String> requestData = new HashMap<>();
            requestData.put("selectedSheet", mainSheetController.getSheetName());
            requestData.put("column", selectedColumn);
            requestData.put("rangeStr", rangeField);

            // Convert the map to JSON
            Gson gson = new Gson();
            String jsonBody = gson.toJson(requestData);

            String finalUrl = HttpUrl
                    .parse(Constants.GET_VALUES_FOR_FILTER)
                    .newBuilder()
                    .build()
                    .toString();

            // Create the request body
            RequestBody body = RequestBody.create(
                    jsonBody,
                    MediaType.get("application/json; charset=utf-8")
            );

            // Send async POST request
            HttpClientUtil.runAsyncPost(finalUrl, body, new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // Handle failure (e.g., network error)
                    Platform.runLater(() ->
                            ShowAlert.showAlert("Error", "Failed to fetch values", e.getMessage(), Alert.AlertType.ERROR)
                            //System.out.println(e.getMessage())
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // Parse the response body
                        String responseBody = response.body().string();
                        Type listType = new TypeToken<List<String>>() {}.getType();
                        List<String> uniqueValues = gson.fromJson(responseBody, listType);

                        // Populate checkboxes with unique values
                        Platform.runLater(() -> {
                            for (String value : uniqueValues) {
                                CheckBox checkBox = new CheckBox(value);
                                checkBoxContainer.getChildren().add(checkBox); // Add checkbox to the container
                            }

                            // Enable OK button if there are checkboxes
                            okButton.setDisable(checkBoxContainer.getChildren().isEmpty());
                        });
                    } else {
                        // Handle unsuccessful response
                        String responseBody = response.body().string();
                        Platform.runLater(() ->
                                ShowAlert.showAlert("Error", "Failed to fetch values", responseBody, Alert.AlertType.ERROR)
                                //System.out.println(responseBody)
                        );
                    }
                }
            });
        });
    }


    @FXML
    private void sortButtonOnAction(ActionEvent event) {
        showSortPopup();
    }

    @FXML
    void dynamicAnalysisButtonAction(ActionEvent event) {
        DynamicAnalysisHandler handler = new DynamicAnalysisHandler(mainSheetController, selectedCellLabel);
        handler.handleDynamicAnalysis(event);
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
        mainSheetController.setTheme(inputScene);
        setGraphStyle(chartScene);
        popupStage.setScene(chartScene);
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
        submitButton.setDisable(true); // Initially disabled

        // Enable the submit button only when both fields are not empty
        xAxisField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(xAxisField.getText().trim().isEmpty() || yAxisField.getText().trim().isEmpty());
        });

        yAxisField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(xAxisField.getText().trim().isEmpty() || yAxisField.getText().trim().isEmpty());
        });

        Scene inputScene = new Scene(inputFormVBox, 400, 300);

        submitButton.setOnAction(e -> {
            String xAxisRange = xAxisField.getText();
            String yAxisRange = yAxisField.getText();
            String selectedGraphType = graphTypeComboBox.getValue();
            String selectedSheetName = mainSheetController.getSheetName();

            try {
                // Fetch numerical values for X-axis
                fetchNumericalValues(selectedSheetName, xAxisRange, xAxisValues -> {
                    // Fetch numerical values for Y-axis
                    fetchNumericalValues(selectedSheetName, yAxisRange, yAxisValues -> {
                        Platform.runLater(() -> {
                            try {
                                if (xAxisValues.size() != yAxisValues.size()) {
                                    throw new IllegalArgumentException("X and Y ranges must have the same number of values.");
                                }

                                Chart chart = createGraph(selectedGraphType, xAxisValues, yAxisValues);
                                showChartInPopup(popupStage, chart, inputScene);
                            } catch (Exception ex) {
                                //ShowAlert.showAlert("Error", "Could not create graph", ex.getMessage(), Alert.AlertType.ERROR);
                                System.out.println(ex.getMessage());
                            }
                        });
                    });
                });
            } catch (Exception ex) {
                ShowAlert.showAlert("Error", "Could not fetch data", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        inputFormVBox.getChildren().add(submitButton);
        popupStage.setScene(inputScene);
        mainSheetController.setTheme(inputScene);
        popupStage.show();
    }

    private void fetchNumericalValues(String sheetName, String rangeStr, Consumer<List<Double>> callback) {
        String finalUrl = HttpUrl
                .parse(Constants.GET_RANGE_NUMERICAL_VALUES)
                .newBuilder()
                .addQueryParameter("selectedSheet", sheetName)
                .addQueryParameter("rangeStr", rangeStr)
                .build()
                .toString();


        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> ShowAlert.showAlert("Error", "Failed to fetch range values", e.getMessage(), Alert.AlertType.ERROR));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();

                if (response.isSuccessful()) {
                    List<Double> numericalValues = new Gson().fromJson(responseBody, new TypeToken<List<Double>>() {}.getType());

                    Platform.runLater(() ->
                            callback.accept(numericalValues)
                    );

                } else {
//                    Platform.runLater(() -> ShowAlert.showAlert("Error", "Failed to fetch range values", responseBody, Alert.AlertType.ERROR));
                    System.out.println(responseBody);
                }
            }
        });
    }

    private Chart createGraph(String graphType, List<Double> xAxisValues, List<Double> yAxisValues) {
        if ("Line Graph".equals(graphType)) {
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
            return lineChart;
        } else {
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
            return barChart;
        }
    }


    public void setGraphStyle(Scene scene){
        String css = getClass().getResource(  "/spreadSheet/client/component/mainSheet/left/command/graph/style/"+ mainSheetController.getSelectedTheme() + "Graph.css").toExternalForm();
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
                fetchColumnsWithinRange(rangeStr, checkBoxContainer, okButton);
            }
        });

        // Set action for OK button
        okButton.setOnAction(e -> {
            List<String> selectedColumns = new ArrayList<>();
            for (Node node : checkBoxContainer.getChildren()) {
                if (node instanceof CheckBox checkBox && checkBox.isSelected()) {
                    selectedColumns.add(checkBox.getText());
                }
            }

            String rangeStr = rangeField.getText();
            popupStage.close(); // Close the popup after sorting
            sendSortRequest(rangeStr, selectedColumns);
        });

        // Set scene and show the popup
        scrollPane.setContent(vbox);
        Scene scene = new Scene(scrollPane, 400, 300);
        mainSheetController.setSheetStyle(scene);
        popupStage.setScene(scene);
        popupStage.show();
    }

    private void fetchColumnsWithinRange(String rangeStr, VBox checkBoxContainer, Button okButton) {
        String finalUrl = HttpUrl
                .parse(Constants.GET_COLUMNS_FOR_FILTER)
                .newBuilder()
                .addQueryParameter("selectedSheet", mainSheetController.getSheetName())
                .addQueryParameter("rangeStr", rangeStr)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> {
                    ShowAlert.showAlert("Error", "Failed to get columns: ", e.getMessage(), Alert.AlertType.ERROR);
                    //System.out.println(e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();

                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<String>>() {}.getType();
                    List<String> availableColumns = gson.fromJson(responseBody, listType);

                    Platform.runLater(() -> {
                        checkBoxContainer.getChildren().clear(); // Clear previous checkboxes
                        for (String column : availableColumns) {
                            CheckBox checkBox = new CheckBox(column);
                            checkBoxContainer.getChildren().add(checkBox);
                        }

                        // Enable the OK button if at least one checkbox is selected
                        okButton.setDisable(false);
                        checkBoxContainer.getChildren().forEach(node -> {
                            if (node instanceof CheckBox checkBox) {
                                checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                                    okButton.setDisable(checkBoxContainer.getChildren().stream()
                                            .filter(n -> n instanceof CheckBox)
                                            .noneMatch(c -> ((CheckBox) c).isSelected()));
                                });
                            }
                        });
                    });
                } else {
                    Platform.runLater(() -> {
                        ShowAlert.showAlert("Error", "Failed to get columns: ", responseBody, Alert.AlertType.ERROR);
                        //System.out.println(responseBody);
                    });
                }
            }
        });
    }

    private void sendSortRequest(String rangeStr, List<String> selectedColumns) {
        Map<String, Object> sortData = new HashMap<>();
        sortData.put("rangeStr", rangeStr);
        sortData.put("selectedColumns", selectedColumns);
        sortData.put("selectedSheet", mainSheetController.getSheetName());
        sortData.put("oldCoordToNewCoord", oldCoordToNewCoord);

        Gson gson = new Gson();
        String jsonBody = gson.toJson(sortData);

        String finalUrl = HttpUrl
                .parse(Constants.CREATE_DTO_SHEET_SORT)
                .newBuilder()
                .build()
                .toString();

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.get("application/json; charset=utf-8")
        );

        HttpClientUtil.runAsyncPost(finalUrl, body, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> {
                    ShowAlert.showAlert("Error", "Failed to sort: ", e.getMessage(), Alert.AlertType.ERROR);
                    //System.out.println(e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();

                if (response.isSuccessful()) {
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(Coordinate.class, new CoordinateDeserializer())
                            .create();
                    Type sheetType = new TypeToken<DTOsheet>() {}.getType();
                    DTOsheet sortedSheet = gson.fromJson(responseBody, sheetType);

                    Platform.runLater(() -> {
                        mainSheetController.displayFilteredSortedSheetInPopup(sortedSheet, "Sorted Sheet", rangeStr);
                    });
                } else {
                    Platform.runLater(() -> {
                        ShowAlert.showAlert("Error", "Failed to sort: ", responseBody, Alert.AlertType.ERROR);
                        //System.out.println(responseBody);
                    });
                }
            }
        });
    }


    public Map<String,String> getNewCoordToOldCoord() {
        return oldCoordToNewCoord;
    }

    public void disableEditFeatures() {
        isEditDisabledProperty.set(true);
        //selectedColumnLabel.setDisable(true);
        //columnAlignmentComboBox.setDisable(true);
        //columnWidthSlider.setDisable(true);
        //selectedRowLabel.setDisable(true);
        //rowHeightSlider.setDisable(true);
    }

    public void close() {

    }
}
