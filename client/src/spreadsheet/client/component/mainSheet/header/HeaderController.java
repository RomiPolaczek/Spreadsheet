package spreadsheet.client.component.mainSheet.header;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.DTOsheet;
import dto.DTOsheetTableDetails;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import spreadsheet.client.component.dashboard.DashboardController;
import spreadsheet.client.component.mainSheet.MainSheetController;
import dto.DTOcell;
import javafx.animation.ScaleTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import spreadsheet.client.theme.ThemeManager;
import spreadsheet.client.util.Constants;
import spreadsheet.client.util.http.HttpClientUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static spreadsheet.client.util.Constants.DASHBOARD_PAGE_FXML_RESOURCE_LOCATION;


public class HeaderController {

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


    private MainSheetController mainSheetController;
    private SimpleStringProperty selectedCellProperty;
    private SimpleStringProperty originalCellValueProperty;
    private SimpleStringProperty lastUpdateVersionCellProperty;
    private SimpleBooleanProperty isAnimationSelectedProperty;
    private List<String> lastHighlightedCells = new ArrayList<>();
    private ThemeManager themeManager;
    private StringBuilder currentExpression;


    public HeaderController() {
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
        updateCellValueButton.disableProperty().bind(selectedCellProperty.isNull());
        themesComboBox.getItems().addAll("Classic", "Pink", "Blue", "Dark");
        themesComboBox.setValue("Classic"); // Set default value
        selectedCellIDLabel.textProperty().bind(selectedCellProperty);
        originalCellValueTextField.promptTextProperty().bind(originalCellValueProperty);
        lastUpdateVersionCellLabel.textProperty().bind(lastUpdateVersionCellProperty);
        formatFunctionButton.disableProperty().bind(selectedCellProperty.isNull());


        // Add listener for changes to the selectedCellProperty
        selectedCellProperty.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                // Reset the style of the previously selected cell
                Label prevCellLabel = mainSheetController.getCellLabel(oldValue);
                if (prevCellLabel != null) {
                    prevCellLabel.setId(null); // Reset to previous style
                }
            }

            if (newValue != null) {
                // Apply style to the newly selected cell
                Label newCellLabel = mainSheetController.getCellLabel(newValue);
                originalCellValueTextField.clear();
                originalCellValueTextField.promptTextProperty().bind(originalCellValueProperty);
                if (newCellLabel != null) {
                    newCellLabel.setId("selected-cell"); // Apply selected-cell style
                }
            }
        });
    }

    public void setMainSheetController(MainSheetController mainSheetController) {
        this.mainSheetController = mainSheetController;
        mainSheetController.getThemeManager().setMainController(mainSheetController);
    }

    public SimpleStringProperty getSelectedCellProperty(){ return selectedCellProperty; }

    public BooleanProperty isAnimationSelectedProperty() { return isAnimationSelectedProperty; }

    @FXML
    void loadFileButtonAction(ActionEvent event) {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Select words file");
//        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
//        Stage stage = (Stage) fileNameLabel.getScene().getWindow();
//        File selectedFile = fileChooser.showOpenDialog(stage);
//
//        if (selectedFile == null) {
//            return;
//        }
//
//        String absolutePath = selectedFile.getAbsolutePath();
//
//        // Show progress bar pop-up
//      //  Stage progressBarStage = createProgressBarStage();
//     //   showProgressBar(progressBarStage, absolutePath);
    }

    private void showProgressBar(Stage progressBarStage, String absolutePath) {
//        // Create a Task for loading the file in the background
//        Task<Void> loadFileTask = new Task<>() {
//            @Override
//            protected Void call() throws Exception {
//                // Simulate loading process with progress
//                for (int i = 0; i <= 10; i++) {
//                    updateProgress(i, 10);
//                    Thread.sleep(100); // Simulate delay
//                }
//                mainController.getEngine().LoadFile(absolutePath);
//                Platform.runLater(() -> {
//                    selectedFileProperty.set(absolutePath);
//                    isFileSelected.set(true);
//                    DTOsheet dtoSheet = mainController.getEngine().createDTOSheetForDisplay(mainController.getEngine().getSheet());
//                    mainController.setSheet(dtoSheet, false);
//
//                    selectedCellProperty.set("A1");
//                    mainController.selectedColumnProperty().set("A1".replaceAll("\\d", ""));
//                    mainController.selectedRowProperty().set("A1".replaceAll("[^\\d]", ""));
//                    originalCellValueProperty.set(dtoSheet.getCell(1,1).getOriginalValue());
//                    lastUpdateVersionCellProperty.set(String.valueOf(dtoSheet.getCell(1,1).getVersion()));
//
//                    mainController.populateRangeListView();
//
//                });
//                return null;
//            }
//
//            @Override
//            protected void succeeded() {
//                super.succeeded();
//                progressBarStage.close(); // Close progress bar pop-up after success
//            }
//
//            @Override
//            protected void failed() {
//                super.failed();
//                progressBarStage.close(); // Close progress bar pop-up if failed
//                mainController.showAlert("Error", "File Load Error", "An error occurred while loading the file: \n" + getException().getMessage(), Alert.AlertType.ERROR);
//            }
//        };
//
//        // Bind the progress of the progress bar to the task progress
//        ProgressBar progressBar = (ProgressBar) progressBarStage.getScene().lookup("#progressBar");
//        progressBar.progressProperty().bind(loadFileTask.progressProperty());
//
//        // Run the task in a background thread
//        Thread loadFileThread = new Thread(loadFileTask);
//        loadFileThread.setDaemon(true); // Ensure the thread will exit when the application exits
//        loadFileThread.start();
//
//        // Show the progress bar pop-up window
//        progressBarStage.show();
//    }
//
//    private Stage createProgressBarStage() {
//        // Create a new pop-up stage
//        Stage progressBarStage = new Stage();
//        progressBarStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows
//        progressBarStage.setTitle("Loading File");
//
//        // Create a VBox to hold the label and progress bar
//        VBox vbox = new VBox(10);
//        vbox.setPadding(new Insets(20));
//
//        // Create and configure the label
//        Label label = new Label("Loading file, please wait...");
//        vbox.getChildren().add(label);
//
//        // Create and configure the progress bar
//        ProgressBar progressBar = new ProgressBar();
//        progressBar.setId("progressBar");
//        progressBar.setPrefWidth(400); // Set the preferred width of the progress bar
//        vbox.getChildren().add(progressBar);
//
//        // Set the scene
//        Scene scene = new Scene(vbox, 450, 100);
//        progressBarStage.setScene(scene);
//
//        return progressBarStage;
    }

    public void viewSheet(String selectedSheet){
        if (selectedSheet==null ||selectedSheet.isEmpty()) {
            mainSheetController.showAlert("Error", "No sheet selected","Please choose a file from the available sheets table.", Alert.AlertType.ERROR);
            return;
        }

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(Constants.LOAD_SHEET)
                .newBuilder()
                .addQueryParameter("selectedSheet", selectedSheet)
                .build()
                .toString();


        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        mainSheetController.showAlert("Error","","Something went wrong: " + e.getMessage(), Alert.AlertType.WARNING)
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            mainSheetController.showAlert("Error","","Something went wrong: " + responseBody, Alert.AlertType.WARNING)
                    );
                } else {
                    Platform.runLater(() -> {
                        try {
                            String json = response.body().string();
                            Gson gson = new Gson();
                            Type sheet = new TypeToken<DTOsheet>(){}.getType();
                            DTOsheet dtoSheet  = gson.fromJson(json, sheet);
                            mainSheetController.setSheet(dtoSheet, false);
                            selectedCellProperty.set("A1");
                            mainSheetController.selectedColumnProperty().set("A1".replaceAll("\\d", ""));
                            mainSheetController.selectedRowProperty().set("A1".replaceAll("[^\\d]", ""));
                            originalCellValueProperty.set(dtoSheet.getCell(1,1).getOriginalValue());
                            lastUpdateVersionCellProperty.set(String.valueOf(dtoSheet.getCell(1,1).getVersion()));
                            mainSheetController.populateRangeListView();

                        } catch (IOException e) {
                            e.printStackTrace();
                            mainSheetController.showAlert("Error","Failed to load the sheet window.","Something went wrong: " +  e.getMessage(), Alert.AlertType.WARNING);
                        }
                    });
                }
            }
        });
    }

    @FXML
    void themesComboBoxOnAction(ActionEvent event) {
        String selectedTheme = themesComboBox.getValue();
        mainSheetController.setSelectedTheme(selectedTheme);
        // Use ThemeManager to apply the selected theme
        mainSheetController.setTheme(lastUpdateVersionCellLabel.getScene());
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

            mainSheetController.selectedColumnProperty().set(cellID.replaceAll("\\d", ""));
            mainSheetController.selectedRowProperty().set(cellID.replaceAll("[^\\d]", ""));

            mainSheetController.resetColumnAlignmentComboBox();
            mainSheetController.resetColumnSlider();
            mainSheetController.resetRowSlider();

            originalCellValueProperty.set(dtoCell.getOriginalValue());
            lastUpdateVersionCellProperty.set(String.valueOf(dtoCell.getVersion()));
            mainSheetController.updateColorPickersWithCellStyles(label);

            List<String> dependsOn = dtoCell.getDependsOn();
            for (String dependsOnCellID : dependsOn) {
                mainSheetController.getCellLabel(dependsOnCellID).getStyleClass().add("depends-on-cell");
            }

            List<String> influencingOn = dtoCell.getInfluencingOn();
            for (String influencingCellID : influencingOn) {
                mainSheetController.getCellLabel(influencingCellID).getStyleClass().add("influence-on-cell");
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
            Label cellLabel = mainSheetController.getCellLabel(cellID);
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
            mainSheetController.showAlert("Error", "No Cell Selected", "Please select a cell before editing.", Alert.AlertType.ERROR);
            return;
        }
        updateCellValue(selectedCellID, newValue);
        originalCellValueTextField.clear();
        originalCellValueTextField.promptTextProperty().bind(originalCellValueProperty);
    }
  

    @FXML
    void formatFunctionButtonOnAction(ActionEvent event) {
//        String selectedCellID = selectedCellProperty.get();
//        String currentValue = originalCellValueProperty.get();
//
//        Coordinate coordinate = CoordinateFactory.from(selectedCellID);
//        int row = coordinate.getRow();
//        int col = coordinate.getColumn();
//
//        // Show pop-up window to allow user to edit cell value
//        UpdateCellFormat updateCellFormat = new UpdateCellFormat(mainController.getEngine().createDTOSheetForDisplay(mainController.getEngine().getSheet()).getCell(row, col),
//                selectedCellID, mainController,originalCellValueProperty ,lastUpdateVersionCellProperty);
//        updateCellFormat.display();
    }

    public void populateVersionSelector() {
//        // Get available versions from the engine
//        ObservableList<String> availableVersions = FXCollections.observableArrayList();
//        int numOfVersions = mainController.getEngine().getNumberOfVersions();
//        for (int i = 1; i <= numOfVersions; i++) {
//            availableVersions.add(String.valueOf(i));
//        }
//        FXCollections.observableArrayList(mainController.getEngine().getNumberOfVersions());
//        // Set available versions in the ComboBox
//        versionSelectorComboBox.setItems(availableVersions);
    }

    @FXML
    void versionSelectorComboBoxAction(ActionEvent event) {
//        // Get the selected version
//        String selectedVersion = versionSelectorComboBox.getSelectionModel().getSelectedItem();
//
//        // Ensure the selected version is not null or empty
//        if (selectedVersion == null || selectedVersion.isEmpty()) {
//            populateVersionSelector();
//            return;
//        }
//
//        DTOsheet dtoSheet = mainController.getEngine().GetVersionForDisplay(selectedVersion);
//
//        // Use setSheet() from SheetController to display the selected version
//        mainController.displaySheetVersionInPopup(dtoSheet);
//
//        // Reset the prompt text without triggering the action again
//        Platform.runLater(() -> {
//            versionSelectorComboBox.setButtonCell(new ListCell<>() {
//                @Override
//                protected void updateItem(String item, boolean empty) {
//                    super.updateItem(item, empty);
//                    setText(empty ? "Version Selector" : item);
//                }
//            });
//        });
//
//        // Prevent re-triggering the action after resetting the prompt
//        versionSelectorComboBox.getSelectionModel().clearSelection();
    }


    public void updateCellValue(String cellID, String newValue) {
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
    }

}