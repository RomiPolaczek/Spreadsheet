package spreadsheet.client.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import spreadsheet.client.component.login.LoginController;
import spreadsheet.client.component.dashboard.DashboardController;
import spreadsheet.client.util.Constants;
import spreadsheet.client.util.ShowAlert;
import spreadsheet.client.util.http.HttpClientUtil;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {
    private DashboardController dashboardController;

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Spreadsheet Client");

        URL loginPage = getClass().getResource(Constants.LOGIN_PAGE_FXML_RESOURCE_LOCATION);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(loginPage);
            Parent root = fxmlLoader.load();
            //chatAppMainController = fxmlLoader.getController();

            LoginController loginController = fxmlLoader.getController();
            loginController.setDashboardController(dashboardController);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    @Override
//    public void stop() throws Exception {
//        dashboardController.
//    }


    public static void main(String[] args) {
        launch(args);
    }

}
