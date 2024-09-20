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
        // If the commandComponentController is already initialized, set the mainController there too
        if (commandComponentController != null && rangeComponentController != null) {
            commandComponentController.setMainController(mainController);
            rangeComponentController.setMainController(mainController);
            initializeCommandAndRangeControllers();
        } else {
            System.out.println("null");
        }
    }

    public SimpleStringProperty selectedColumnProperty() {
        return commandComponentController.selectedColumnProperty();
    }

    public void resetColumnAlignmentComboBox() {
        commandComponentController.resetColumnAlignmentComboBox();
    }

    public void initializeCommandAndRangeControllers() {
        commandComponentController.initializeCommandController();
        rangeComponentController.initializeRangeController();
    }

    public void updateColorPickersWithCellStyles(Label cell) {
        commandComponentController.updateColorPickersWithCellStyles(cell);
    }
}
