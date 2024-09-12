package header;

import app.AppController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.scene.control.Alert;
import sheet.coordinate.api.Coordinate;
import dto.DTOsheet;


import java.io.File;
import java.util.Objects;


public class HeaderController {


    @FXML private Label fileNameLabel;
    @FXML private Label originalCellValueLabel;
    @FXML private Button loadFileButton;
    @FXML private Button updateCellValueButton;
    @FXML private Label lastUpdateVersionCellLabel;
    @FXML private ComboBox<String> versionSelectorComboBox;
    @FXML private Label selectedCellIDLabel;


    private AppController mainController;
    private SimpleStringProperty selectedFileProperty;
    private SimpleBooleanProperty isFileSelected;
    private SimpleStringProperty selectedCellProperty;
    private SimpleStringProperty originalCellValueProperty;
    private SimpleStringProperty lastUpdateVersionCellProperty;

    public HeaderController() {
        selectedFileProperty = new SimpleStringProperty();
        isFileSelected = new SimpleBooleanProperty(false);
        selectedCellProperty = new SimpleStringProperty();
        originalCellValueProperty = new SimpleStringProperty();
        lastUpdateVersionCellProperty = new SimpleStringProperty();
        versionSelectorComboBox = new ComboBox<String>();
    }

    @FXML
    private void initialize() {
        fileNameLabel.textProperty().bind(selectedFileProperty);
        updateCellValueButton.disableProperty().bind(selectedCellProperty.isNull());
        versionSelectorComboBox.disableProperty().bind(isFileSelected.not());
        selectedCellIDLabel.textProperty().bind(selectedCellProperty);
        originalCellValueLabel.textProperty().bind(originalCellValueProperty);
        lastUpdateVersionCellLabel.textProperty().bind(lastUpdateVersionCellProperty);
    }

    @FXML
    void loadFileButtonAction(ActionEvent event) {
        try {
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
        catch (Exception e) {
            showErrorAlert(e);
        }
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
                    mainController.getSheetComponentController().setSheet(dtoSheet);
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
                showErrorAlert((Exception) getException());
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

    private void showErrorAlert(Exception e) {
        // Create an alert to show the error message
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("File Load Error");
        alert.setContentText("An error occurred while loading the file: \n" + e.getMessage());

        // Show the alert dialog
        alert.showAndWait();
    }


    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void addClickEventForCell(Label label, String cellID, String originalValue, int version) {
        label.setOnMouseClicked(event -> {
            selectedCellProperty.set(cellID);
            originalCellValueProperty.set(originalValue);
            lastUpdateVersionCellProperty.set(String.valueOf(version));
        });
    }

    @FXML
    void updateCellValueButtonAction(ActionEvent event) {
        // Assuming the selectedCellProperty contains the cell ID like "A1", "B2", etc.
        String selectedCellID = selectedCellProperty.get();
        String currentValue = originalCellValueProperty.get();

        // Show pop-up window to allow user to edit cell value
        showEditCellPopup(selectedCellID, currentValue);
    }

    private void showEditCellPopup(String cellID, String currentValue) {
        // Create a new pop-up stage
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Update Cell " + cellID);

        // Create a VBox to hold the label, text field, and buttons
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        // Create and configure the label for the current cell value
        Label currentValueLabel;

        if(Objects.equals(currentValue, ""))
            currentValueLabel = new Label("Current Value: empty cell");
        else
            currentValueLabel = new Label("Current Value: " + currentValue);

        vbox.getChildren().add(currentValueLabel);

        // Create and configure the text field for the new value
        TextField newValueTextField = new TextField();
        newValueTextField.setPromptText("Enter new value");
        vbox.getChildren().add(newValueTextField);

        // Create and configure the submit button
        Button submitButton = new Button("Update");
        submitButton.setOnAction(e -> {
            String newValue = newValueTextField.getText();
            try{
                if (newValue == null) {
                    mainController.showAlert("Invalid input", "Please enter a valid value.");
                } else {
                    // Call the EditCell function from the Engine class to update the cell
                    updateCellValue(cellID, newValue);
                    popupStage.close();
                }
            }
            catch (Exception ex){
                mainController.showAlert("Invalid input", ex.getMessage());
            }
        });
        vbox.getChildren().add(submitButton);

        // Set the scene
        Scene scene = new Scene(vbox, 300, 150);
        popupStage.setScene(scene);

        // Show the pop-up window
        popupStage.showAndWait();
    }

    private void updateCellValue(String cellID, String newValue) {
        // Parse the cell ID (e.g., "A1", "B2") to get row and column coordinates
        Coordinate coordinate = mainController.getEngine().checkAndConvertInputToCoordinate(cellID);

        // Call the engine's EditCell function to update the cell value
        mainController.getEngine().EditCell(coordinate, newValue);

        // Refresh the sheet display
        DTOsheet dtoSheet = mainController.getEngine().createDTOSheetForDisplay(mainController.getEngine().getSheet());
        mainController.getSheetComponentController().setSheet(dtoSheet);

        originalCellValueProperty.set(newValue);
        lastUpdateVersionCellProperty.set(String.valueOf(dtoSheet.getCell(coordinate).getVersion()));

     //   mainController.showAlert("Success", "Cell " + cellID + " has been updated successfully.");
    }

    public void populateVersionSelector() {
        // Get available versions from the engine
        ObservableList<String> availableVersions = FXCollections.observableArrayList();
        int numOfVersions = mainController.getEngine().getNumberOfVersions();
        for(int i = 1; i<= numOfVersions; i++) {
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

        try {
            DTOsheet dtoSheet = mainController.getEngine().GetVersionForDisplay(selectedVersion);

            // Use setSheet() from SheetController to display the selected version
            mainController.getSheetComponentController().displaySheetVersionInPopup(dtoSheet);

            // Clear the selection to return to the unselected state
            versionSelectorComboBox.getSelectionModel().clearSelection();
        } catch (Exception e) {
            mainController.showAlert("Error", "Failed to load version: " + e.getMessage());
        } finally {
//            // Reset the prompt text without triggering the action again
//            Platform.runLater(() -> {
//                versionSelectorComboBox.setButtonCell(new ListCell<>() {
//                    @Override
//                    protected void updateItem(String item, boolean empty) {
//                        super.updateItem(item, empty);
//                        setText(empty ? "Version Selector" : item);
//                    }
//                });
//            });

            // Prevent re-triggering the action after resetting the prompt
            versionSelectorComboBox.getSelectionModel().clearSelection();
        }
    }




//    @FXML
//    void cellsComboBoxAction(ActionEvent event) {
//        // Get the selected cell
//        String selectedCell = cellsComboBox.getSelectionModel().getSelectedItem();
//
//        // Perform your action based on the selected cell
//        System.out.println("Selected cell: " + selectedCell);
//    }

//    private void populateCellsComboBox() {
//        // Assuming your spreadsheet has 26 columns (A-Z) and 10 rows (1-10)
//        ObservableList<String> cellReferences = FXCollections.observableArrayList();
//        DTOsheet dtoSheet = mainController.getEngine().createDTOSheetForDisplay(mainController.getEngine().getSheet());
//        int totalColumns = dtoSheet.getLayout().getColumns();  // A to Z
//        int totalRows = dtoSheet.getLayout().getRows();     // Rows 1 to 10
//
//        // Generate cell references like A1, A2, B1, B2, ..., Z10
//        for (char col = 'A'; col < 'A' + totalColumns; col++) {
//            for (int row = 1; row <= totalRows; row++) {
//                cellReferences.add(col + String.valueOf(row));
//            }
//        }
//        cellsComboBox.setItems(cellReferences);
//    }
}
