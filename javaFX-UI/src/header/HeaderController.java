package header;

import app.AppController;
import dto.DTOcell;
import expression.parser.Operation;
import javafx.animation.ScaleTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.scene.control.Alert;
import javafx.util.Duration;
import sheet.coordinate.api.Coordinate;
import dto.DTOsheet;
import sheet.coordinate.impl.CoordinateFactory;
import sheet.coordinate.impl.CoordinateImpl;
import theme.ThemeManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HeaderController {

    @FXML
    private Label fileNameLabel;
    @FXML
    private Label originalCellValueLabel;
    @FXML
    private Button loadFileButton;
    @FXML
    private Button updateCellValueButton;
    @FXML
    private Label lastUpdateVersionCellLabel;
    @FXML
    private ComboBox<String> versionSelectorComboBox;
    @FXML
    private Label selectedCellIDLabel;
    @FXML
    private CheckBox animationsCheckBox;
    @FXML
    private ComboBox<String> themesComboBox;
    @FXML
    private TextField originalCellValueTextField;
    @FXML
    private Button formatFunctionButton;




    private AppController mainController;
    private SimpleStringProperty selectedFileProperty;
    private SimpleBooleanProperty isFileSelected;
    private SimpleStringProperty selectedCellProperty;
    private SimpleStringProperty originalCellValueProperty;
    private SimpleStringProperty lastUpdateVersionCellProperty;
    private SimpleBooleanProperty isAnimationSelectedProperty;
    private List<String> lastHighlightedCells = new ArrayList<>();
    private ThemeManager themeManager;
    private Map<String, Integer> functionMap;
    private StringBuilder currentExpression;


    public HeaderController() {
        selectedFileProperty = new SimpleStringProperty();
        isFileSelected = new SimpleBooleanProperty(false);
        selectedCellProperty = new SimpleStringProperty();
        originalCellValueProperty = new SimpleStringProperty();
        lastUpdateVersionCellProperty = new SimpleStringProperty();
        versionSelectorComboBox = new ComboBox<>();
        themesComboBox = new ComboBox<>();
        isAnimationSelectedProperty = new SimpleBooleanProperty(false);
        themeManager = new ThemeManager();
        currentExpression = new StringBuilder();

    }

    @FXML
    private void initialize() {
        fileNameLabel.textProperty().bind(selectedFileProperty);
        updateCellValueButton.disableProperty().bind(selectedCellProperty.isNull());
        versionSelectorComboBox.disableProperty().bind(isFileSelected.not());
        themesComboBox.getItems().addAll("Classic", "Pink", "Blue", "Dark");
        themesComboBox.setValue("Classic"); // Set default value
        themesComboBox.disableProperty().bind(isFileSelected.not());
        animationsCheckBox.disableProperty().bind(isFileSelected.not());
        selectedCellIDLabel.textProperty().bind(selectedCellProperty);
        originalCellValueLabel.textProperty().bind(originalCellValueProperty);
        originalCellValueTextField.promptTextProperty().bind(originalCellValueProperty);
        lastUpdateVersionCellLabel.textProperty().bind(lastUpdateVersionCellProperty);
        formatFunctionButton.disableProperty().bind(selectedCellProperty.isNull());


        // Add listener for changes to the selectedCellProperty
        selectedCellProperty.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                // Reset the style of the previously selected cell
                Label prevCellLabel = mainController.getCellLabel(oldValue);
                if (prevCellLabel != null) {
                    prevCellLabel.setId(null); // Reset to previous style
                }
            }

            if (newValue != null) {
                // Apply style to the newly selected cell
                Label newCellLabel = mainController.getCellLabel(newValue);
                if (newCellLabel != null) {
                    newCellLabel.setId("selected-cell"); // Apply selected-cell style
                }
            }
        });

//        originalCellValueTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
//            if (!newValue) {
//                // TextField lost focus, trigger the action
//           //     originalCellValueTextFieldOnFocusLost();
//            }
//        });
//
//        originalCellValueTextField.textProperty().addListener((observable, oldValue, newValue) -> {
//            if(newValue != null || !newValue.isEmpty()) {
//                updateCellValue(selectedCellProperty.getValue(), newValue);
//            }
//        });
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
        mainController.getThemeManager().setMainController(mainController);
    }

    public SimpleStringProperty getSelectedCellProperty(){ return selectedCellProperty; }

    public SimpleBooleanProperty isFileSelectedProperty() { return isFileSelected; }

    public Boolean isAnimationSelectedProperty() { return isAnimationSelectedProperty.getValue(); }


//    private void resetHeaderControllerForNewFile(){
//        selectedCellProperty = new SimpleStringProperty();
//        originalCellValueProperty = new SimpleStringProperty();
//        lastUpdateVersionCellProperty = new SimpleStringProperty();
//
//        updateCellValueButton.disableProperty().bind(selectedCellProperty.isNull());
//        versionSelectorComboBox.disableProperty().bind(isFileSelected.not());
//        selectedCellIDLabel.textProperty().bind(selectedCellProperty);
//        originalCellValueLabel.textProperty().bind(originalCellValueProperty);
//        lastUpdateVersionCellLabel.textProperty().bind(lastUpdateVersionCellProperty);
//    }

    @FXML
    void loadFileButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select words file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        Stage stage = (Stage) fileNameLabel.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile == null) {
            return;
        }

        String absolutePath = selectedFile.getAbsolutePath();

        // Show progress bar pop-up
        Stage progressBarStage = createProgressBarStage();
        showProgressBar(progressBarStage, absolutePath);
    }

    private void showProgressBar(Stage progressBarStage, String absolutePath) {
        // Create a Task for loading the file in the background
        Task<Void> loadFileTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Simulate loading process with progress
                for (int i = 0; i <= 10; i++) {
                    updateProgress(i, 10);
                    Thread.sleep(100); // Simulate delay
                }
                mainController.getEngine().LoadFile(absolutePath);
                Platform.runLater(() -> {
                    selectedFileProperty.set(absolutePath);
                    isFileSelected.set(true);
                    DTOsheet dtoSheet = mainController.getEngine().createDTOSheetForDisplay(mainController.getEngine().getSheet());

                    mainController.setSheet(dtoSheet, false);

                    selectedCellProperty.set("A1");
                    mainController.selectedColumnProperty().set("A1".replaceAll("\\d", ""));
                    mainController.selectedRowProperty().set("A1".replaceAll("[^\\d]", ""));
                    originalCellValueProperty.set(dtoSheet.getCell(1,1).getOriginalValue());
                    lastUpdateVersionCellProperty.set(String.valueOf(dtoSheet.getCell(1,1).getVersion()));

                    mainController.populateRangeListView();

                });
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                progressBarStage.close(); // Close progress bar pop-up after success
            }

            @Override
            protected void failed() {
                super.failed();
                progressBarStage.close(); // Close progress bar pop-up if failed
                mainController.showAlert("Error", "File Load Error", "An error occurred while loading the file: \n" + getException().getMessage(), Alert.AlertType.ERROR);
            }
        };

        // Bind the progress of the progress bar to the task progress
        ProgressBar progressBar = (ProgressBar) progressBarStage.getScene().lookup("#progressBar");
        progressBar.progressProperty().bind(loadFileTask.progressProperty());

        // Run the task in a background thread
        Thread loadFileThread = new Thread(loadFileTask);
        loadFileThread.setDaemon(true); // Ensure the thread will exit when the application exits
        loadFileThread.start();

        // Show the progress bar pop-up window
        progressBarStage.show();
    }

    private Stage createProgressBarStage() {
        // Create a new pop-up stage
        Stage progressBarStage = new Stage();
        progressBarStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows
        progressBarStage.setTitle("Loading File");

        // Create a VBox to hold the label and progress bar
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        // Create and configure the label
        Label label = new Label("Loading file, please wait...");
        vbox.getChildren().add(label);

        // Create and configure the progress bar
        ProgressBar progressBar = new ProgressBar();
        progressBar.setId("progressBar");
        progressBar.setPrefWidth(400); // Set the preferred width of the progress bar
        vbox.getChildren().add(progressBar);

        // Set the scene
        Scene scene = new Scene(vbox, 450, 100);
        progressBarStage.setScene(scene);

        return progressBarStage;
    }

    @FXML
    void themesComboBoxOnAction(ActionEvent event) {
        String selectedTheme = themesComboBox.getValue();
        mainController.setSelectedTheme(selectedTheme);
        // Use ThemeManager to apply the selected theme
        mainController.setTheme(fileNameLabel.getScene());
    }

    @FXML
    void animationsCheckBoxOnAction(ActionEvent event) {
        boolean isSelected = animationsCheckBox.isSelected();
        isAnimationSelectedProperty.set(isSelected);
    }

    public void addClickEventForSelectedCell(Label label, String cellID, DTOcell dtoCell) {
        label.setOnMouseClicked(event -> {
            resetPreviousStyles();
            selectedCellProperty.set(cellID);
            animateSelectedCell(label);

            mainController.selectedColumnProperty().set(cellID.replaceAll("\\d", ""));
            mainController.selectedRowProperty().set(cellID.replaceAll("[^\\d]", ""));

            mainController.resetColumnAlignmentComboBox();
            mainController.resetColumnSlider();
            mainController.resetRowSlider();

            originalCellValueProperty.set(dtoCell.getOriginalValue());
            lastUpdateVersionCellProperty.set(String.valueOf(dtoCell.getVersion()));
            mainController.updateColorPickersWithCellStyles(label);

            List<String> dependsOn = dtoCell.getDependsOn();
            for (String dependsOnCellID : dependsOn) {
                mainController.getCellLabel(dependsOnCellID).getStyleClass().add("depends-on-cell");
            }

            List<String> influencingOn = dtoCell.getInfluencingOn();
            for (String influencingCellID : influencingOn) {
                mainController.getCellLabel(influencingCellID).getStyleClass().add("influence-on-cell");
            }

            lastHighlightedCells.clear();
            lastHighlightedCells.addAll(dependsOn);
            lastHighlightedCells.addAll(influencingOn);
        });
    }

    private void animateSelectedCell(Label label) {
        if(isAnimationSelectedProperty.get()) {

            ScaleTransition scaleTransition = new ScaleTransition();
            scaleTransition.setNode(label); // Set the label to animate
            scaleTransition.setDuration(Duration.millis(200)); // Duration of the animation
            scaleTransition.setFromX(1.0); // Starting scale (normal size)
            scaleTransition.setFromY(1.0);
            scaleTransition.setToX(1.12); // Scale factor (15% larger)
            scaleTransition.setToY(1.12);
            scaleTransition.setCycleCount(2); // 2 cycles: enlarge, then shrink
            scaleTransition.setAutoReverse(true); // Reverse to shrink back to original size

            // Set translation adjustments to mimic a pivot from the center
            double pivotX = label.getWidth() / 2;
            double pivotY = label.getHeight() / 2;

            // Apply translation before scaling to simulate pivot effect
            label.setTranslateX(-pivotX * 0.075); // Adjust X to pivot from center
            label.setTranslateY(-pivotY * 0.075); // Adjust Y to pivot from center

            // Play the animation
            scaleTransition.play();

            // After animation, reset the translation to ensure it returns to normal position
            scaleTransition.setOnFinished(e -> {
                label.setTranslateX(0);
                label.setTranslateY(0);
            });
        }
    }

    private void resetPreviousStyles() {
        // Reset styles for all previously highlighted cells
        for (String cellID : lastHighlightedCells) {
            Label cellLabel = mainController.getCellLabel(cellID);
            cellLabel.getStyleClass().removeAll("depends-on-cell", "influence-on-cell");
        }
        // Clear the list after resetting
        lastHighlightedCells.clear();
    }

    @FXML
    void updateCellValueButtonAction(ActionEvent event) {
        // Get the current value from the TextField
        String newValue = originalCellValueTextField.getText();

        // Ensure there is a selected cell
        String selectedCellID = selectedCellProperty.get();
        if (selectedCellID == null || selectedCellID.isEmpty()) {
            mainController.showAlert("Error", "No Cell Selected", "Please select a cell before editing.", Alert.AlertType.ERROR);
            return;
        }
        updateCellValue(selectedCellID, newValue);
        originalCellValueTextField.clear();
        originalCellValueTextField.promptTextProperty().bind(originalCellValueProperty);
    }
  

    @FXML
    void formatFunctionButtonOnAction(ActionEvent event) {
        String selectedCellID = selectedCellProperty.get();
        String currentValue = originalCellValueProperty.get();

        Coordinate coordinate = CoordinateFactory.from(selectedCellID);
        int row = coordinate.getRow();
        int col = coordinate.getColumn();

        // Show pop-up window to allow user to edit cell value
        UpdateCellFormat updateCellFormat = new UpdateCellFormat(mainController.getEngine().createDTOSheetForDisplay(mainController.getEngine().getSheet()).getCell(row, col),
                selectedCellID, mainController,originalCellValueProperty ,lastUpdateVersionCellProperty);
        updateCellFormat.display();
    }

    //original edit cell
//    private void showEditCellPopup(String cellID, String currentValue) {
//        // Create a new pop-up stage
//        Stage popupStage = new Stage();
//        popupStage.initModality(Modality.APPLICATION_MODAL);
//        popupStage.setTitle("Update Cell " + cellID);
//
//        // Create a VBox to hold the label, text field, and buttons
//        VBox vbox = new VBox(10);
//        vbox.setPadding(new Insets(20));
//
//        // Create and configure the label for the current cell value
//        Label currentValueLabel;
//
//        if (Objects.equals(currentValue, ""))
//            currentValueLabel = new Label("Current Value:  empty cell");
//        else
//            currentValueLabel = new Label("Current Value:  " + currentValue);
//
//        vbox.getChildren().add(currentValueLabel);
//
//        // Create and configure the text field for the new value
//        vbox.getChildren().add(new Label("Enter new value: "));
//
//        TextField newValueTextField = new TextField();
//        vbox.getChildren().add(newValueTextField);
//
//        // Create and configure the submit button
//        Button submitButton = new Button("Update");
//        submitButton.setOnAction(e -> {
//            String newValue = newValueTextField.getText();
//            try {
//                updateCellValue(cellID, newValue);
//                popupStage.close();
//            } catch (Exception ex) {
//                mainController.showAlert("Error", "Invalid input", ex.getMessage(), Alert.AlertType.ERROR);
//            }
//        });
//
//        vbox.getChildren().add(submitButton);
//
//        // Set the scene
//        Scene scene = new Scene(vbox, 300, 150);
//        popupStage.setScene(scene);
//
//        // Show the pop-up window
//        popupStage.showAndWait();
//    }

    //Bonus edit cell
//    private void showEditCellPopup(String cellID, String currentValue) {
//        // Create a new pop-up stage
//        Stage popupStage = new Stage();
//        popupStage.initModality(Modality.APPLICATION_MODAL);
//        popupStage.setTitle("Update Cell " + cellID);
//
//        // Create a VBox to hold the elements
//        VBox vbox = new VBox(10);
//        vbox.setPadding(new Insets(20));
//
//        // Create and configure the label for the current cell value
//        Label currentValueLabel = new Label(Objects.equals(currentValue, "")
//                ? "Current Value: empty cell"
//                : "Current Value: " + currentValue);
//        vbox.getChildren().add(currentValueLabel);
//
//        // Create a label and ComboBox for selecting input type
//        HBox inputTypeBox = new HBox(10);
//        Label inputTypeLabel = new Label("Select input type:");
//        ComboBox<String> inputTypeComboBox = new ComboBox<>();
//        inputTypeComboBox.getItems().addAll("Function", "Argument");
//        inputTypeBox.getChildren().addAll(inputTypeLabel, inputTypeComboBox);
//        vbox.getChildren().add(inputTypeBox);
//
//        // Create a ComboBox for functions
//        HBox functionBox = new HBox(10);
//        Label functionLabel = new Label("Select Operation:");
//        ComboBox<String> functionComboBox = new ComboBox<>();
//        functionComboBox.setPromptText("Operation");
//        functionBox.getChildren().addAll(functionLabel, functionComboBox);
//        functionBox.setVisible(false);  // Initially hidden
//
//        // Populate function names from the engine's createListOfFunctions method
//        Map<String, Integer> functionMap = mainController.getEngine().createListOfFunctions();
//        functionComboBox.getItems().addAll(functionMap.keySet()); // Add function names to the combo box
//
//        // Add the function ComboBox to the VBox
//        vbox.getChildren().add(functionBox);
//
//        // Create a VBox to hold argument selections
//        VBox argumentsBox = new VBox(10);
//        vbox.getChildren().add(argumentsBox);
//
//        // Create a Region to push the selected value text field to the bottom
//        Region spacer = new Region();
//        VBox.setVgrow(spacer, Priority.ALWAYS); // Allow spacer to grow and push other elements down
//        vbox.getChildren().add(spacer);
//
//        // Create a TextField for user input (always visible)
//        TextField selectedValueTextField = new TextField();
//        selectedValueTextField.setPromptText("Selected value will appear here");
//
//        // Create a button to submit the new value
//        Button submitButton = new Button("Update");
//        submitButton.setOnAction(e -> {
//            String newValue = selectedValueTextField.getText();  // Use value from the text field
//            try {
//                updateCellValue(cellID, newValue);
//                popupStage.close();
//            } catch (Exception ex) {
//                mainController.showAlert("Error", "Invalid input", ex.getMessage(), Alert.AlertType.ERROR);
//            }
//        });
//
//        // Add components to the VBox
//        vbox.getChildren().addAll(selectedValueTextField, submitButton);
//
//        // Add listeners to the input type ComboBox
//        inputTypeComboBox.setOnAction(e -> {
//            String selectedType = inputTypeComboBox.getValue();
//            if ("Function".equals(selectedType)) {
//                functionBox.setVisible(true);
//                selectedValueTextField.setText("");  // Clear selected value field
//            } else {
//                functionBox.setVisible(false);
//                selectedValueTextField.setPromptText("Enter argument value");  // Prompt for argument
//            }
//        });
//
//        // Handle function selection
//        functionComboBox.setOnAction(f -> {
//            String selectedFunction = functionComboBox.getValue();
//            int numArgs = functionMap.get(selectedFunction); // Get the number of arguments required
//
//            selectedValueTextField.setText(selectedFunction);  // Update selected value field
//
//            // Clear previous arguments
//            argumentsBox.getChildren().clear();
//
//            // Create argument combo boxes based on the number of arguments
//            for (int i = 0; i < numArgs; i++) {
//                createArgumentSelector(argumentsBox, selectedValueTextField, functionMap);
//            }
//        });
//
//        // Set the scene with a preferred width and height
//        Scene scene = new Scene(vbox, 400, 500);  // Set preferred size
//        popupStage.setScene(scene);
//
//        // Optionally, you can set minimum size
//        popupStage.setMinWidth(400);
//        popupStage.setMinHeight(300);
//
//        // Show the pop-up window
//        popupStage.showAndWait();
//    }
//
//    private void createArgumentSelector(VBox argumentsBox, TextField selectedValueTextField, Map<String, Integer> functionMap) {
//        HBox argBox = new HBox(10);
//        Label argLabel = new Label("Argument:");
//
//        // Create a VBox to hold the argument type selector and nested arguments
//        VBox nestedArgBox = new VBox(5);
//        ComboBox<String> argComboBox = new ComboBox<>();
//        argComboBox.getItems().addAll("Function", "Argument");
//        nestedArgBox.getChildren().add(argComboBox);
//
//        argBox.getChildren().addAll(argLabel, nestedArgBox);
//        argumentsBox.getChildren().add(argBox);
//
//        // Add action listener for the argument combo box
//        argComboBox.setOnAction(argEvent -> {
//            String selectedArgType = argComboBox.getValue();
//
//            if ("Function".equals(selectedArgType)) {
//                // Show a function selection box for this argument
//                ComboBox<String> innerFunctionComboBox = new ComboBox<>();
//                innerFunctionComboBox.getItems().addAll(functionMap.keySet());
//                nestedArgBox.getChildren().add(innerFunctionComboBox);
//
//                // Handle the inner function selection
//                innerFunctionComboBox.setOnAction(innerEvent -> {
//                    String innerFunction = innerFunctionComboBox.getValue();
//                    // Update selected value field
//                    String currentExpression = selectedValueTextField.getText();
//                    selectedValueTextField.setText(currentExpression + "{" + innerFunction + "}"); // Concatenate the function
//
//                    // Clear previous arguments if any
//                    nestedArgBox.getChildren().clear(); // Clear any previous inner arguments
//                    nestedArgBox.getChildren().add(innerFunctionComboBox); // Keep the function combo
//
//                    // Recreate the argument selectors for this inner function if needed
//                    int innerNumArgs = functionMap.get(innerFunction); // Get number of arguments
//                    if (innerNumArgs > 0) {
//                        for (int j = 0; j < innerNumArgs; j++) {
//                            createArgumentSelector(nestedArgBox, selectedValueTextField, functionMap);
//                        }
//                    }
//                });
//            } else {
//                // Show a text field for entering the argument value
//                TextField argTextField = new TextField();
//                argTextField.setPromptText("Enter argument value");
//                nestedArgBox.getChildren().add(argTextField);
//
//                // Update selected value field when argument is entered
//                argTextField.textProperty().addListener((observable, oldValue, newValue) -> {
//                    String currentExpression = selectedValueTextField.getText();
//                    // Update the expression with the argument value
//                    selectedValueTextField.setText(currentExpression + newValue); // Concatenate the argument value
//                });
//            }
//        });
//    }




//    private void updateCellValue(String cellID, String newValue) {
//        // Parse the cell ID (e.g., "A1", "B2") to get row and column coordinates
//        Coordinate coordinate = mainController.getEngine().checkAndConvertInputToCoordinate(cellID);
//
//        // Call the engine's EditCell function to update the cell value
//        mainController.getEngine().EditCell(coordinate, newValue);
//
//        // Refresh the sheet display
//        DTOsheet dtoSheet = mainController.getEngine().createDTOSheetForDisplay(mainController.getEngine().getSheet());
//        mainController.setSheet(dtoSheet, true);
//
//        originalCellValueProperty.set(newValue);
//        lastUpdateVersionCellProperty.set(String.valueOf(dtoSheet.getCell(coordinate).getVersion()));
//    }

    public void populateVersionSelector() {
        // Get available versions from the engine
        ObservableList<String> availableVersions = FXCollections.observableArrayList();
        int numOfVersions = mainController.getEngine().getNumberOfVersions();
        for (int i = 1; i <= numOfVersions; i++) {
            availableVersions.add(String.valueOf(i));
        }
        FXCollections.observableArrayList(mainController.getEngine().getNumberOfVersions());
        // Set available versions in the ComboBox
        versionSelectorComboBox.setItems(availableVersions);
    }

    @FXML
    void versionSelectorComboBoxAction(ActionEvent event) {
        // Get the selected version
        String selectedVersion = versionSelectorComboBox.getSelectionModel().getSelectedItem();

        // Ensure the selected version is not null or empty
        if (selectedVersion == null || selectedVersion.isEmpty()) {
            populateVersionSelector();
            return;
        }

        DTOsheet dtoSheet = mainController.getEngine().GetVersionForDisplay(selectedVersion);

        // Use setSheet() from SheetController to display the selected version
        mainController.displaySheetVersionInPopup(dtoSheet);

        // Reset the prompt text without triggering the action again
        Platform.runLater(() -> {
            versionSelectorComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "Version Selector" : item);
                }
            });
        });

        // Prevent re-triggering the action after resetting the prompt
        versionSelectorComboBox.getSelectionModel().clearSelection();
    }


    public void updateCellValue(String cellID, String newValue) {
        // Parse the cell ID (e.g., "A1", "B2") to get row and column coordinates
        Coordinate coordinate = mainController.getEngine().checkAndConvertInputToCoordinate(cellID);

        // Call the engine's EditCell function to update the cell value
        mainController.getEngine().EditCell(coordinate, newValue);

        // Refresh the sheet display
        DTOsheet dtoSheet = mainController.getEngine().createDTOSheetForDisplay(mainController.getEngine().getSheet());
        mainController.setSheet(dtoSheet, true);

        originalCellValueProperty.set(newValue);
        lastUpdateVersionCellProperty.set(String.valueOf(dtoSheet.getCell(coordinate).getVersion()));
    }

}