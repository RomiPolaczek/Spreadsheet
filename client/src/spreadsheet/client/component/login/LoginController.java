package spreadsheet.client.component.login;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import spreadsheet.client.component.main.DashboardController;
import spreadsheet.client.util.Constants;
import spreadsheet.client.util.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import spreadsheet.client.util.http.SimpleCookieManager;

import java.io.IOException;
import java.util.List;

import static spreadsheet.client.util.Constants.*;

public class LoginController {

    @FXML
    public TextField userNameTextField;

    @FXML
    public Label errorMessageLabel;

    private DashboardController dashboardController;

    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    // Create an instance of SimpleCookieManager
    private SimpleCookieManager cookieManager = new SimpleCookieManager();

    private OkHttpClient client = new OkHttpClient
            .Builder()
            .cookieJar(cookieManager)
            .build();

    @FXML
    public void initialize() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);
//        HttpClientUtil.setCookieManagerLoggingFacility(line ->
//                Platform.runLater(() ->
//                        updateHttpStatusLine(line)));
    }

    @FXML
    private void loginButtonClicked(ActionEvent event) {

        String userName = userNameTextField.getText();
        if (userName.isEmpty()) {
            errorMessageProperty.set("User name is empty. You can't login with empty user name");
            return;
        }

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .build()
                .toString();

       // updateHttpStatusLine("New request is launched for: " + finalUrl);

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessageProperty.set("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            errorMessageProperty.set("Something went wrong: " + responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                        try {
                            // Extract cookies from the response and save them
                            HttpUrl loginUrl = HttpUrl.parse(finalUrl);
                            List<Cookie> responseCookies = Cookie.parseAll(loginUrl, response.headers());
                            cookieManager.saveFromResponse(loginUrl, responseCookies);

                            // Set up the FXMLLoader for the main window
                            FXMLLoader loader = new FXMLLoader(getClass().getResource(DASHBOARD_PAGE_FXML_RESOURCE_LOCATION));
                            Parent root = loader.load();

                            DashboardController dashboardController = loader.getController();

                            // Set the username in the menu window
                            dashboardController.setUserName(userNameTextField.getText());

                            // Pass the OkHttpClient instance
                            dashboardController.setOkHttpClient(client);

                            // Pass the SimpleCookieManager instance
                            dashboardController.setCookieManager(cookieManager);

                            // Open the main window in a new stage
                            Stage mainStage = new Stage();
                            mainStage.setScene(new Scene(root));
                      //      dashboardController.setTheme(mainStage.getScene());
                            mainStage.show();

                            // Close the login window
                            userNameTextField.getScene().getWindow().hide();

                        } catch (IOException e) {
                            e.printStackTrace();
                            errorMessageProperty.set("Failed to load the main window.");
                        }
                    });
                }
            }
        });
    }

    @FXML
    private void userNameKeyTyped(KeyEvent event) {
        errorMessageProperty.set("");
    }

    @FXML
    private void quitButtonClicked(ActionEvent e) {
        Platform.exit();
    }

//    private void updateHttpStatusLine(String data) {
//        chatAppMainController.updateHttpLine(data);
//    }
//

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }
}
