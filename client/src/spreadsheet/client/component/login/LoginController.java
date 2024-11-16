package spreadsheet.client.component.login;

import com.google.gson.Gson;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import spreadsheet.client.component.dashboard.DashboardController;
import spreadsheet.client.util.Constants;
import spreadsheet.client.util.ShowAlert;
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

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static spreadsheet.client.util.Constants.*;

public class LoginController {

    @FXML
    public TextField userNameTextField;

    @FXML
    public Label errorMessageLabel;

    private DashboardController dashboardController;

    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    @FXML
    public void initialize() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);
    }

    @FXML
    private void loginButtonClicked(ActionEvent event) {

        String userName = userNameTextField.getText();
        if (userName.isEmpty()) {
            errorMessageProperty.set("User name is empty. You can't login with empty user name");
            return;
        }

        CompletableFuture<Void> loginRequest = new CompletableFuture<>();
        Gson gson = new Gson();

                //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .build()
                .toString();


        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        ShowAlert.showAlert("Error", "Failed to login", e.getMessage(), Alert.AlertType.ERROR)
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();

                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        String message = gson.fromJson(responseBody, String.class);
                        loginRequest.completeExceptionally(new Exception("Something went wrong: " + message));
                    });
                } else {
                    Platform.runLater(() -> {
                        loginRequest.complete(null);
                        ShowAlert.showAlert("Error", "Failed to login", responseBody, Alert.AlertType.ERROR);
                    });
                }
                response.close();
            }
        });

        loginRequest.thenRun(() -> {
            Platform.runLater(() -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(DASHBOARD_PAGE_FXML_RESOURCE_LOCATION));
                    Parent root = loader.load();

                    DashboardController dashboardController = loader.getController();
                    dashboardController.setUserName(userNameTextField.getText());

                    Stage mainStage = new Stage();
                    Scene scene = new Scene(root);
                    mainStage.setScene(scene);
                    dashboardController.setTheme(scene);
                    mainStage.show();

                    // Close the login window
                    userNameTextField.getScene().getWindow().hide();

                } catch (IOException e) {
                    e.printStackTrace();
                    ShowAlert.showAlert("Error", "Failed to load main window", e.getMessage(), Alert.AlertType.ERROR);

                }
            });
        }).exceptionally(e -> {
            ShowAlert.showAlert("Error", "", e.getMessage(), Alert.AlertType.ERROR);
            return null;
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

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

}
