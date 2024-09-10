package app;

import api.Engine;
import header.HeaderController;
import impl.EngineImpl;
import javafx.fxml.FXML;
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
    //private Stage primaryStage;

//    public void setPrimaryStage(Stage primaryStage) {
//        this.primaryStage = primaryStage;
//    }

    public Engine getEngine() { return engine; }

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

    public void makeSheet(){
        sheetComponentController.initialSheetAccordingToSize();
    }
}
