package spreadsheet.client.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import spreadsheet.client.component.login.LoginController;
import spreadsheet.client.util.Constants;
import spreadsheet.client.util.Constants.*;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {
    //private ChatAppMainController chatAppMainController;

    @Override
    public void start(Stage primaryStage) {

//        primaryStage.setMinHeight(600);
//        primaryStage.setMinWidth(600);
        primaryStage.setTitle("Spread Sheet Client");

        URL loginPage = getClass().getResource(Constants.LOGIN_PAGE_FXML_RESOURCE_LOCATION);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(loginPage);
            Parent root = fxmlLoader.load();
            //chatAppMainController = fxmlLoader.getController();

            LoginController loginController = fxmlLoader.getController();

            Scene scene = new Scene(root, 300, 200);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    @Override
//    public void stop() throws Exception {
//        HttpClientUtil.shutdown();
//        chatAppMainController.close();
//    }

    public static void main(String[] args) {
        launch(args);
    }

}
