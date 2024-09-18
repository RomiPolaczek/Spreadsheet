package left.range;

import app.AppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.List;

public class RangeController {

    @FXML private Button addRangeButton;
    @FXML private ComboBox<String> deleteRangeComboBox;
    @FXML private ComboBox<String> displayRangeComboBox;

    private AppController mainController;

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void initializeRangeController() {
        addRangeButton.disableProperty().bind(mainController.isFileSelectedProperty().not());
        deleteRangeComboBox.disableProperty().set(true);
        displayRangeComboBox.disableProperty().set(true);
    }

    private void populateRangeComboBoxes() {
        // Get the list of existing ranges from the engine
        List<String> ranges = mainController.getEngine().getExistingRanges();

        // Clear existing items
        deleteRangeComboBox.getItems().clear();
        displayRangeComboBox.getItems().clear();

        // Add new items
        deleteRangeComboBox.getItems().addAll(ranges);
        displayRangeComboBox.getItems().addAll(ranges);
    }


    @FXML
    void addRangeButtonOnAction(ActionEvent event) {
        // Create a new Stage (popup window)
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Add Range");

        // Create a VBox to hold the label, text fields, and buttons
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        // Create and configure the text fields for user input
        Label nameLabel = new Label("Range's Name: ");
        TextField nameField = new TextField();
        vbox.getChildren().addAll(nameLabel, nameField);

        Label rangeLabel = new Label("Range: ");
        TextField rangeField = new TextField();
        rangeField.setPromptText("e.g., \"A1..A3\", \"B3..D4\"");
        vbox.getChildren().addAll(rangeLabel, rangeField);

        // Create and configure the submit button
        Button submitButton = new Button("Submit");
        submitButton.setDisable(true);  // Initially disabled

        // Enable the submit button only when both fields are not empty
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(nameField.getText().trim().isEmpty() || rangeField.getText().trim().isEmpty());
        });

        rangeField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(nameField.getText().trim().isEmpty() || rangeField.getText().trim().isEmpty());
        });

        submitButton.setOnAction(e -> {
            String name = nameField.getText();
            String rangeStr = rangeField.getText();
            try {
                mainController.getEngine().addRange(name, rangeStr);  // Assuming the engine is accessed through mainController
                popupStage.close();
            } catch (Exception ex) {
                mainController.showAlert("Error", "Invalid input", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        vbox.getChildren().add(submitButton);

        // Set the scene
        Scene scene = new Scene(vbox, 300, 200);
        popupStage.setScene(scene);

        // Show the pop-up window
        popupStage.showAndWait();
        populateRangeComboBoxes();
        displayRangeComboBox.disableProperty().set(false);
        deleteRangeComboBox.disableProperty().set(false);
    }


    @FXML
    void deleteRangeComboBoxOnAction(ActionEvent event) {
        // Get the selected range from the combo box
        String selectedRange = deleteRangeComboBox.getSelectionModel().getSelectedItem();

        if (selectedRange != null) {
            try {
                mainController.getEngine().removeRange(selectedRange); // Assuming the engine is accessed through mainController
                deleteRangeComboBox.getItems().remove(selectedRange);// Remove the range from the combo box

//                // Clear the selection and set the prompt text
//                deleteRangeComboBox.getSelectionModel().clearSelection();
//                deleteRangeComboBox.setPromptText("Delete Range");
            } catch (Exception ex) {
                mainController.showAlert("Error", "Unable to delete range", ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }


    @FXML
    void displayRangeComboBoxOnAction(ActionEvent event) {
        String selectedRangeName = displayRangeComboBox.getValue();

        if (selectedRangeName != null && !selectedRangeName.isEmpty()) {
            try {
                List<String> cellsToHighlight = mainController.getEngine().getRangeCellsList(selectedRangeName);
                for (String cellID : cellsToHighlight) {
                    // Assuming each cell is represented by a JavaFX Label or similar node

                    Label cellLabel = mainController.getCellLabel(cellID); // Assuming cellLabels is a Map of cell IDs to Labels
                    if (cellLabel != null) {
                        cellLabel.getStyleClass().add("Range-label");
                    }
                }
            } catch (Exception ex) {
                mainController.showAlert("Error", "Unable to display range", ex.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            mainController.showAlert("Error", "No range selected", "Please select a range to display.", Alert.AlertType.WARNING);
        }
    }

    private void resetDeleteRangeComboBox(){
        deleteRangeComboBox.getSelectionModel().clearSelection();

        deleteRangeComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "Column Alignment" : item);
            }
        });
    }

}
