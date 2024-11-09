package spreadsheet.client.component.dashboard.commands;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import spreadsheet.client.component.dashboard.DashboardController;
import spreadsheet.client.component.mainSheet.MainSheetController;
import spreadsheet.client.enums.PermissionType;
import spreadsheet.client.util.Constants;
import spreadsheet.client.util.ShowAlert;
import spreadsheet.client.util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Map;

import static spreadsheet.client.util.Constants.MAIN_SHEET_PAGE_FXML_RESOURCE_LOCATION;

public class DashboardCommandsController {

    @FXML
    private Button askDenyPermissionRequestButton;

    @FXML
    private Button requestPermissionButton;

    @FXML
    private Button viewSheetButton;

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;

    }

    @FXML
    void askDenyPermissionRequestButtonOnAction(ActionEvent event) {

    }

    @FXML
    void requestPermissionButtonOnAction(ActionEvent event) {
        // Open a new dialog with radio buttons for selecting "Writer" or "Reader"
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Request Permission");

        // Create description text
        Label descriptionLabel = new Label(
                "Requesting permission for spreadsheet: " + dashboardController.getSelectedSheet() + "\n" +
                        "Choose the type of permission you would like to request:\n\n" +
                        "• READER - Read-Only Permission:\n" +
                        "• WRITER - Edit Permission:\n"
        );
        descriptionLabel.setWrapText(true);

        // Create radio buttons for permission type selection
        RadioButton writerRadio = new RadioButton("Writer");
        RadioButton readerRadio = new RadioButton("Reader");
        ToggleGroup group = new ToggleGroup();
        writerRadio.setToggleGroup(group);
        readerRadio.setToggleGroup(group);

        // Select Writer by default
        writerRadio.setSelected(true);

        // Layout the components vertically with spacing
        VBox vbox = new VBox(10, descriptionLabel, new HBox(10, writerRadio, readerRadio));
        vbox.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(vbox);

        // Add submit and cancel buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the dialog and wait for the user's selection
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Determine selected permission type
                PermissionType selectedPermission = writerRadio.isSelected() ? PermissionType.WRITER : PermissionType.READER;

                // POST request body
                String finalUrl = Constants.REQUEST_PERMISSION;
                RequestBody body = new FormBody.Builder()
                        .add("username", dashboardController.getUserName())
                        .add("selectedSheet", dashboardController.getSelectedSheet())
                        .add("permissionType", selectedPermission.getPermission())
                        .build();

                // Run the async POST request
                HttpClientUtil.runAsyncPost(finalUrl, body, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Platform.runLater(() -> {
                            // Handle failure
                            ShowAlert.showAlert("Error","Failed to request permission: ", e.getMessage(), Alert.AlertType.ERROR);
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            Gson gson = new Gson();
                            String jsonResponse = response.body().string();
                            Map<String, String> result = gson.fromJson(jsonResponse, Map.class);

                            if (response.isSuccessful()) {
                                dashboardController.getTabelsController().fetchPermissionTableDetails(dashboardController.getSelectedSheet());
                            } else {
                                //ShowAlert.showAlert("Error", "", message, Alert.AlertType.ERROR);
                            }



//                            Platform.runLater(() -> {
//                                if ("PERMISSION_REQUESTED".equals(result.get("status"))) {
//                                    AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Success", "Permission request sent successfully.");
//                                } else if ("ERROR".equals(result.get("status"))) {
//                                    showAlert(Alert.AlertType.ERROR, "Error", result.get("message"));
//                                }
//                            });
                        }
                    }
                });
            }
        });
    }

    @FXML
    void viewSheetButtonOnAction(ActionEvent event) {
        try {
            // Load the FXML file for MainSheetController (mainSheet.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_SHEET_PAGE_FXML_RESOURCE_LOCATION));
            BorderPane mainSheetRoot = loader.load();

            // Get the MainSheetController instance and initialize it if needed
            MainSheetController mainSheetController = loader.getController();
            mainSheetController.initialize(dashboardController.getSelectedSheet());  // Ensures any required setup

            // Find the ScrollPane in the dashboard scene
            ScrollPane dashboardScrollPane = (ScrollPane) ((Node) event.getSource()).getScene().lookup("#dashboardScrollPane");

            // Set the content of the ScrollPane to the new root component
            dashboardScrollPane.setContent(mainSheetRoot);

            // Optionally apply the selected theme or any other settings
            mainSheetController.setTheme(dashboardScrollPane.getScene());
            dashboardController.setMainSheetController(mainSheetController);
            dashboardController.viewSheet();

        } catch (IOException e) {
            e.printStackTrace();
//            showAlert("Error", "Failed to load sheet view", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

}
