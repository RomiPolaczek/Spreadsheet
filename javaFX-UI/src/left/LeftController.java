package left;

import app.AppController;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import left.command.CommandController;
import left.range.RangeController;

public class LeftController {
    @FXML
    private VBox commandComponent;
    @FXML
    private CommandController commandComponentController;
    @FXML
    private VBox rangeComponent;
    @FXML
    private RangeController rangeComponentController;
    private AppController mainController;


    public void setMainController(AppController mainController) {
        this.mainController = mainController;
        if (commandComponentController != null && rangeComponentController != null) {
            commandComponentController.setMainController(mainController);
            rangeComponentController.setMainController(mainController);
            initializeCommandAndRangeControllers();
        }
    }

    public SimpleStringProperty selectedColumnProperty() {
        return commandComponentController.selectedColumnProperty();
    }

    public SimpleStringProperty selectedRowProperty() { return commandComponentController.selectedRowProperty(); }

    public void addClickEventForSelectedColumn(Label label) {
        commandComponentController.addClickEventForSelectedColumn(label);
    }

    public void addClickEventForSelectedRow(Label label) {
        commandComponentController.addClickEventForSelectedRow(label);
    }

    public void resetColumnAlignmentComboBox() {
        commandComponentController.resetColumnAlignmentComboBox();
    }

    public void resetRowSlider() {
        commandComponentController.resetRowSlider();
    }

    public void initializeCommandAndRangeControllers() {
        commandComponentController.initializeCommandController();
        rangeComponentController.initializeRangeController();
    }

    public void updateColorPickersWithCellStyles(Label cell) {
        commandComponentController.updateColorPickersWithCellStyles(cell);
    }

    public void resetColumnSlider() {
        commandComponentController.resetColumnSlider();
    }

    public void populateRangeListView() {
        rangeComponentController.populateRangeListView();
    }
}
