package header;

import app.AppController;
import dto.DTOsheet;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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


import java.io.File;


public class HeaderController {

    @FXML
    private Label fileNameLabel;
    @FXML
    private Button loadFileButton;
    @FXML
    private Button updateCellValueButton;
    @FXML
    private Button versionSelectorButton;
    @FXML
    private ComboBox<String> cellsComboBox;

    // private Stage primaryStage;

    private AppController mainController;
    private SimpleStringProperty selectedFileProperty;
    private SimpleBooleanProperty isFileSelected;

    public HeaderController() {
        selectedFileProperty = new SimpleStringProperty();
        isFileSelected = new SimpleBooleanProperty(false);
    }

    @FXML
    private void initialize() {
        fileNameLabel.textProperty().bind(selectedFileProperty);
        updateCellValueButton.disableProperty().bind(isFileSelected.not());
        versionSelectorButton.disableProperty().bind(isFileSelected.not());

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


        } catch (Exception e) {
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
                    populateCellsComboBox(); /// delete
                    mainController.makeSheet();
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
        vbox.setPadding(new javafx.geometry.Insets(20));

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

    @FXML
    void updateCellValueButtonAction(ActionEvent event) {

    }

    @FXML
    void versionSelectorButtonAction(ActionEvent event) {

    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @FXML
    void cellsComboBoxAction(ActionEvent event) {
        // Get the selected cell
        String selectedCell = cellsComboBox.getSelectionModel().getSelectedItem();

        // Perform your action based on the selected cell
        System.out.println("Selected cell: " + selectedCell);
    }

    private void populateCellsComboBox() {
        // Assuming your spreadsheet has 26 columns (A-Z) and 10 rows (1-10)
        ObservableList<String> cellReferences = FXCollections.observableArrayList();
        DTOsheet dtoSheet = mainController.getEngine().createDTOSheetForDisplay(mainController.getEngine().getSheet());
        int totalColumns = dtoSheet.getLayout().getColumns();  // A to Z
        int totalRows = dtoSheet.getLayout().getRows();     // Rows 1 to 10

        // Generate cell references like A1, A2, B1, B2, ..., Z10
        for (char col = 'A'; col < 'A' + totalColumns; col++) {
            for (int row = 1; row <= totalRows; row++) {
                cellReferences.add(col + String.valueOf(row));
            }
        }
        cellsComboBox.setItems(cellReferences);
    }
}
