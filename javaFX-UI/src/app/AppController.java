package app;

import api.Engine;
import header.HeaderController;
import impl.EngineImpl;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class AppController {

    @FXML private ScrollPane headerComponent;
    @FXML private HeaderController headerComponentController;
    private Engine engine;
    //private Stage primaryStage;

//    public void setPrimaryStage(Stage primaryStage) {
//        this.primaryStage = primaryStage;
//    }

    public Engine getEngine() { return engine; }

    @FXML
    public void initialize() {
        engine = new EngineImpl();
        if (headerComponentController != null) {
            headerComponentController.setMainController(this);
        }
    }
}
