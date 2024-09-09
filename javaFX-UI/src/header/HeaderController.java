package header;

import app.AppController;
import dto.DTOsheet;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
            selectedFileProperty.set(absolutePath);
            mainController.getEngine().LoadFile(absolutePath);
            isFileSelected.set(true);
            populateCellsComboBox();

        } catch (Exception e) {
            // Create an alert to show the error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("File Load Error");
            alert.setContentText("An error occurred while loading the file: \n" + e.getMessage());

            // Show the alert dialog
            alert.showAndWait();
        }
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
