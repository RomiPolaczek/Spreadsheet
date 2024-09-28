package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

        // set stage
        Scene scene = new Scene(root, 1000, 700);
        appController.setTheme(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
