package left.command;

import app.AppController;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

public class CommandController {

    @FXML private ColorPicker cellBackgroundColorPicker;
    @FXML private ColorPicker cellTextColorPicker;
    @FXML private ComboBox<String> columnAlignmentComboBox;
    @FXML private Label selectedColumnLabel;
    @FXML private Button dynamicAnalysisButton;

    private AppController mainController;
    private SimpleStringProperty selectedColumnProperty;


    public CommandController(){
        selectedColumnProperty = new SimpleStringProperty();
    }

    @FXML
    public void initialize() {
        columnAlignmentComboBox.getItems().addAll("Left", "Center", "Right");
        selectedColumnLabel.textProperty().bind(selectedColumnProperty);
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public SimpleStringProperty selectedColumnProperty() {return selectedColumnProperty;}


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
        cellLabel.setStyle(cellLabel.getStyle() + backgroundColor);
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
        cellLabel.setStyle(cellLabel.getStyle() + textColor);
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
    }


//    public void addClickEventForSelectedColumn(Label label) {
//        label.setOnMouseClicked(event -> {
//           selectedColumnProperty.set(label.getText());
//
//            // Reset the ComboBox prompt text
//            columnAlignmentComboBox.getSelectionModel().clearSelection();
//            columnAlignmentComboBox.setPromptText("Column Alignment");
//        });
//    }

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

    @FXML
    void dynamicAnalysisButtonAction(ActionEvent event) {

    }

}
