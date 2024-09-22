package left.command;

import app.AppController;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.paint.Color;

public class CommandController {

    @FXML private ColorPicker cellBackgroundColorPicker;
    @FXML private ColorPicker cellTextColorPicker;
    @FXML private Button resetCellDesignButton;
    @FXML private ComboBox<String> columnAlignmentComboBox;
    @FXML private Label selectedColumnLabel;
    @FXML private Button dynamicAnalysisButton;
    @FXML private Slider columnWidthSlider;

    private AppController mainController;
    private SimpleStringProperty selectedColumnProperty;
    public static final String DEFAULT_CELL_STYLE = "-fx-background-color: white; -fx-text-fill: black;";


    public void initializeCommandController(){
        selectedColumnLabel.textProperty().bind(selectedColumnProperty);
        cellBackgroundColorPicker.disableProperty().bind(mainController.getSelectedCellProperty().isNull());
        cellTextColorPicker.disableProperty().bind(mainController.getSelectedCellProperty().isNull());
        resetCellDesignButton.disableProperty().bind(mainController.getSelectedCellProperty().isNull()
                .or(mainController.getSelectedCellProperty().isNotNull().and(isDefaultCellStyle())));
        columnAlignmentComboBox.disableProperty().bind(selectedColumnProperty.isNull());
        columnWidthSlider.disableProperty().bind(selectedColumnProperty.isNull());

        // Add a listener to the column width slider to change the column width dynamically
        columnWidthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            changeColumnWidth(newValue.intValue());
        });
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize(){
        selectedColumnProperty = new SimpleStringProperty();
        columnAlignmentComboBox.getItems().addAll("Left","Center", "Right");
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
            cellLabel.setStyle(DEFAULT_CELL_STYLE);

            // Save the reset style in the cellStyles map
            mainController.getCellStyles().put(selectedCell.get(), DEFAULT_CELL_STYLE);

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

    @FXML
    void dynamicAnalysisButtonAction(ActionEvent event) {

    }

//    @FXML
//    void columnWidthSliderOnDragDetected(MouseEvent event) {
//
//    }


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


}
