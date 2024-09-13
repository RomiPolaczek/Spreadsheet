package app;

import api.Engine;
import header.HeaderController;
import impl.EngineImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import sheet.SheetController;

public class AppController {

    @FXML private ScrollPane headerComponent;
    @FXML private HeaderController headerComponentController;
    @FXML private ScrollPane sheetComponent;
    @FXML private SheetController sheetComponentController;
    private Engine engine;


    public Engine getEngine() { return engine; }

    public HeaderController getHeaderComponentController() { return headerComponentController; }

    @FXML
    public void initialize() {
        engine = new EngineImpl();
        if (headerComponentController != null && sheetComponentController != null) {
            headerComponentController.setMainController(this);
            sheetComponentController.setMainController(this);
        }
        else {
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

    public SheetController getSheetComponentController() {return sheetComponentController;}
}
