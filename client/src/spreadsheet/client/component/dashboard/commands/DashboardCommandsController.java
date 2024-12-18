package spreadsheet.client.component.dashboard.commands;

import dto.DTOpermissionRequest;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import permissions.PermissionStatus;
import spreadsheet.client.component.dashboard.DashboardController;
import spreadsheet.client.component.mainSheet.MainSheetController;
import spreadsheet.client.enums.PermissionType;
import spreadsheet.client.util.Constants;
import spreadsheet.client.util.ShowAlert;
import spreadsheet.client.util.http.HttpClientUtil;

import java.io.IOException;

import static spreadsheet.client.util.Constants.MAIN_SHEET_PAGE_FXML_RESOURCE_LOCATION;

public class DashboardCommandsController {

    @FXML
    private Button requestPermissionButton;

    @FXML
    private Button viewSheetButton;

    @FXML
    private Button approvePermissionRequestButton;

    @FXML
    private Button rejectPermissionRequestButton;

    @FXML
    private Button RefreshRequestButton;

    private DashboardController dashboardController;
    private BooleanProperty isViewSheetDisabledProperty;


    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
        isViewSheetDisabledProperty = new SimpleBooleanProperty(false);
        viewSheetButton.disableProperty().bind(Bindings.or(dashboardController.getSelectedSheet().isNull(), isViewSheetDisabledProperty));
        requestPermissionButton.disableProperty().bind(dashboardController.getSelectedSheet().isNull());
        setApproveAndRejectButtons();
    }

    public void setApproveAndRejectButtons() {
        BooleanBinding canApproveOrRejectRequest = dashboardController.getSelectedRequestUserName().isNotNull()
                .and(isOwner());

        approvePermissionRequestButton.disableProperty().bind(canApproveOrRejectRequest.not());
        rejectPermissionRequestButton.disableProperty().bind(canApproveOrRejectRequest.not());
    }

    private BooleanBinding isOwner() {
        return dashboardController.getUserName().isEqualTo(dashboardController.getTabelsController().getSelectedSheetOwnerName());
    }

    @FXML
    void requestPermissionButtonOnAction(ActionEvent event) {
        // Open a new dialog with radio buttons for selecting "Writer" or "Reader"
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Request Permission");

        // Create description text
        Label descriptionLabel = new Label(
                "Requesting permission for spreadsheet: " + dashboardController.getSelectedSheet().get() + "\n" +
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
                        .add("username", dashboardController.getUserName().get())
                        .add("selectedSheet", dashboardController.getSelectedSheet().getValue())
                        .add("permissionType", selectedPermission.getPermission())
                        .build();

                // Run the async POST request
                HttpClientUtil.runAsyncPost(finalUrl, body, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Platform.runLater(() -> {
                            // Handle failure
                            ShowAlert.showAlert("Error", "Failed to request permission: ", e.getMessage(), Alert.AlertType.ERROR);
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String jsonResponse = response.body().string();
                        if (response.isSuccessful()) {
                            Platform.runLater(() -> {
                                dashboardController.getTabelsController().fetchPermissionTableDetails(dashboardController.getSelectedSheet().getValue());
                            });
                        }
                        else {
                            Platform.runLater(() -> {
                                ShowAlert.showAlert("Error", "Failed to request permission: ", jsonResponse, Alert.AlertType.ERROR);
                            });
                        }
                    }
                });
            }
        });
    }

    @FXML
    void viewSheetButtonOnAction(ActionEvent event) {
        try {
            String permissionType = dashboardController.getTabelsComponentController().getSelectedSheetPermissionType().toUpperCase();
            if (permissionType.equals(PermissionType.NONE.toString())) {
                throw new IllegalStateException("You are not allowed to view this sheet");
            }

            viewSheetButtonHelper(event);
        } catch (IOException e) {
            e.printStackTrace();
            ShowAlert.showAlert("Error", "View Sheet Error", e.getMessage(), Alert.AlertType.ERROR);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            ShowAlert.showAlert("Error", "View Sheet Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void viewSheetButtonHelper(ActionEvent event) throws IOException {
        String sheetName = dashboardController.getSelectedSheet().getValue();

        // Load the FXML file for MainSheetController (mainSheet.fxml)
        FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_SHEET_PAGE_FXML_RESOURCE_LOCATION));
        BorderPane mainSheetRoot = loader.load();

        // Get the MainSheetController instance and initialize it if needed
        MainSheetController mainSheetController = loader.getController();
        mainSheetController.setDashboardController(dashboardController);
        mainSheetController.initialize(sheetName, dashboardController);  // Ensures any required setup

        // Find the ScrollPane in the dashboard scene
        ScrollPane dashboardScrollPane = (ScrollPane) ((Node) event.getSource()).getScene().lookup("#dashboardScrollPane");

        // Set the content of the ScrollPane to the new root component
        dashboardScrollPane.setContent(mainSheetRoot);
        dashboardController.getUserPermissionAndDisableIfNecessary(sheetName, dashboardController.getUserName().get());

        // Optionally apply the selected theme or any other settings
        mainSheetController.setTheme(dashboardScrollPane.getScene());
        mainSheetController.setSheetStyle(dashboardScrollPane.getScene());
        dashboardController.setMainSheetController(mainSheetController);
        dashboardController.displaySheet(true);
    }


    @FXML
    void approvePermissionRequestButtonOnAction(ActionEvent event) {
        approveAndRejectHelper(PermissionStatus.APPROVED);
    }


    @FXML
    void rejectPermissionRequestButtonOnAction(ActionEvent event) {
        approveAndRejectHelper(PermissionStatus.REJECTED);
    }

    private void approveAndRejectHelper(PermissionStatus permissionStatus) {
        DTOpermissionRequest currentRequest = dashboardController.getTabelsController().getSelectedRequest();

        String finalUrl = Constants.HANDLE_PERMISSION_REQUEST;
        RequestBody body = new FormBody.Builder()
                .add("username", dashboardController.getUserName().get()) //connected user
                .add("applicantName",currentRequest.getUserName()) //applicant name
                .add("ownerName", dashboardController.getTabelsController().getSelectedSheetOwnerName())
                .add("sheetName", dashboardController.getSelectedSheet().getValue())
                .add("permissionStatus", permissionStatus.getStatus().toUpperCase())
                .add("permissionType", currentRequest.getRequestedPermissionType().getPermission().toUpperCase())
                .build();


        // Run the async POST request
        HttpClientUtil.runAsyncPost(finalUrl, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    // Handle failure
                    ShowAlert.showAlert("Error", "Failed to hande permission request: ", e.getMessage(), Alert.AlertType.ERROR);
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    String jsonResponse = response.body().string(); // Get the response as a raw string

                    Platform.runLater(() -> {
                        if (response.isSuccessful()) {
                            Platform.runLater(() -> {
                                dashboardController.getTabelsController().fetchPermissionTableDetails(dashboardController.getSelectedSheet().getValue());
                            });
                        } else {
                            ShowAlert.showAlert("Error", "Permission Request Handling Error", jsonResponse, Alert.AlertType.ERROR);
                        }
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        ShowAlert.showAlert("Error", "Permission Request Handling Error", "Error processing response: " + e.getMessage(), Alert.AlertType.ERROR);
                    });
                }
            }
        });
    }

    @FXML
    void RefreshRequestButtonOnAction(ActionEvent event) {
        dashboardController.getTabelsController().fetchPermissionTableDetails(dashboardController.getSelectedSheet().getValue());
    }

    public void disableViewSheet() {
        isViewSheetDisabledProperty.set(true);
    }

    public void close() {

    }
}
