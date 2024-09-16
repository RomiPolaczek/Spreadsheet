package app;

import api.Engine;
import dto.DTOcell;
import header.HeaderController;
import impl.EngineImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import left.LeftController;
import sheet.SheetController;

import java.util.List;

public class AppController {

    @FXML
    private ScrollPane headerComponent;
    @FXML
    private HeaderController headerComponentController;
    @FXML
    private ScrollPane sheetComponent;
    @FXML
    private SheetController sheetComponentController;
    @FXML
    private ScrollPane leftComponent;
    @FXML
    private LeftController leftComponentController;

    private Engine engine;


    public Engine getEngine() {
        return engine;
    }

    public HeaderController getHeaderComponentController() {
        return headerComponentController;
    }

    @FXML
    public void initialize() {
        engine = new EngineImpl();
        if (headerComponentController != null && sheetComponentController != null && leftComponentController != null) {
            headerComponentController.setMainController(this);
            sheetComponentController.setMainController(this);
            leftComponentController.setMainController(this);
        } else {
            System.out.println("nuullllll");
        }
    }

//    public void setSheet(){
//        sheetComponentController.setSheet();
//    }

    // Utility method to show alerts
    public void showAlert(String title, String header, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public SheetController getSheetComponentController() {
        return sheetComponentController;
    }

    public SimpleStringProperty getSelectedCellProperty() {
        return headerComponentController.getSelectedCellProperty();
    }

    public Label getCellLabel(String cellID) {
        return sheetComponentController.getCellLabel(cellID);
    }

    public SimpleStringProperty selectedColumnProperty() {
        return leftComponentController.selectedColumnProperty();
    }

    public void addClickEventForSelectedCell(Label label, String cellID, DTOcell dtoCell) {
        headerComponentController.addClickEventForSelectedCell(label, cellID, dtoCell);
    }

    public void addClickEventForSelectedColumn(Label label) {
        headerComponentController.addClickEventForSelectedColumn(label);
    }

    public List<Label> getAllCellLabelsInColumn(String column) {
        return sheetComponentController.getAllCellLabelsInColumn(column);
    }

    public void resetColumnAlignmentComboBox() { leftComponentController.resetColumnAlignmentComboBox(); }

}
