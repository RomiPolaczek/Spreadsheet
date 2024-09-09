package app;

import api.Engine;
import impl.EngineImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;


public class Main extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();

        // load main fxml
        URL mainFXML = getClass().getResource("app.fxml");
        loader.setLocation(mainFXML);
        Parent root = loader.load();

        // wire up controller
        AppController appController = loader.getController();
        Engine engine = new EngineImpl();
//        appController.setPrimaryStage(primaryStage);
//        appController.setBusinessLogic(engine);

        // set stage
        Scene scene = new Scene(root, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}