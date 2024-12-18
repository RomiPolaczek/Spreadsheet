package spreadsheet.client.component.mainSheet.left;

import spreadsheet.client.component.mainSheet.MainSheetController;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import spreadsheet.client.component.mainSheet.left.command.CommandController;
import spreadsheet.client.component.mainSheet.left.range.RangeController;

import java.util.Map;

public class LeftController {
    @FXML
    private VBox commandComponent;
    @FXML
    private CommandController commandComponentController;
    @FXML
    private VBox rangeComponent;
    @FXML
    private RangeController rangeComponentController;
    private MainSheetController mainSheetController;


    public void setMainSheetController(MainSheetController mainSheetController) {
        this.mainSheetController = mainSheetController;
        if (commandComponentController != null && rangeComponentController != null) {
            commandComponentController.setMainSheetController(mainSheetController);
            rangeComponentController.setMainController(mainSheetController);
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

    public Map<String,String> getNewCoordToOldCoord() {
        return commandComponentController.getNewCoordToOldCoord();
    }

    public void disableEditFeatures() {
        commandComponentController.disableEditFeatures();
        rangeComponentController.disableEditFeatures();
    }

    public void close() {
        commandComponentController.close();
        rangeComponentController.close();
    }
}
