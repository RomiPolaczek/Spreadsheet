package spreadsheet.client.component.dashboard;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.*;
import javafx.concurrent.Task;
import org.jetbrains.annotations.NotNull;
import spreadsheet.client.component.dashboard.commands.DashboardCommandsController;
import spreadsheet.client.component.dashboard.tables.TabelsController;
import spreadsheet.client.component.mainSheet.MainSheetController;
import spreadsheet.client.enums.PermissionType;
import spreadsheet.client.theme.ThemeManager;
import spreadsheet.client.util.Constants;
import spreadsheet.client.util.ShowAlert;
import spreadsheet.client.util.http.HttpClientUtil;
import static spreadsheet.client.util.Constants.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class DashboardController {

    @FXML private BorderPane dashboardBorderPane;
    @FXML private ScrollPane dashboardScrollPane;
    @FXML private Label userNameLabel;
    @FXML private Button loadFileButton;
    @FXML private ComboBox<String> themesComboBox;
    @FXML private CheckBox animationsCheckBox;
    @FXML private Button logoutButton;
    @FXML private VBox tabelsComponent;
    @FXML private TabelsController tabelsComponentController;
    @FXML private VBox dashboardCommandsComponent;
    @FXML private DashboardCommandsController dashboardCommandsComponentController;

    private MainSheetController mainSheetController;
    private SimpleStringProperty userName;
    private SimpleStringProperty selectedSheet;
    private SimpleStringProperty selectedRequestUserName;
    private BooleanProperty isEditDisabledProperty;
    private ThemeManager themeManager;
    private String selectedTheme = "Classic";
    private SimpleBooleanProperty isAnimationSelectedProperty;


    @FXML
    public void initialize() {
        userName = new SimpleStringProperty();
        selectedSheet = new SimpleStringProperty();
        selectedRequestUserName = new SimpleStringProperty();
        isEditDisabledProperty = new SimpleBooleanProperty(false);
        userNameLabel.textProperty().bind(userName);

        isAnimationSelectedProperty = new SimpleBooleanProperty(false);
        themeManager = new ThemeManager(this);

        themesComboBox.getItems().addAll("Classic", "Pink", "Blue", "Dark");
        themesComboBox.setValue("Classic"); // Set default value

        if (tabelsComponentController != null && dashboardCommandsComponentController != null) {
            tabelsComponentController.setDashboardController(this);
            dashboardCommandsComponentController.setDashboardController(this);
        }

        else{
            System.out.println("nullll");
        }
    }
    public BorderPane getDashboardBorderPane() {
        return dashboardBorderPane;
    }

    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    public void setSelectedSheet(String selectedSheet) {
        this.selectedSheet.set(selectedSheet);
    }

    public SimpleStringProperty getSelectedSheet() {
        return selectedSheet;
    }

    public void setMainSheetController(MainSheetController mainSheetController) {
        this.mainSheetController = mainSheetController;
    }

    public SimpleStringProperty getUserName() {
        return userName;
    }

    public TabelsController getTabelsController() {
        return tabelsComponentController;
    }

    public void setSelectedRequestUserName(String selectedRequestUserName) {
        this.selectedRequestUserName.set(selectedRequestUserName);
    }

    public SimpleStringProperty getSelectedRequestUserName() {
        return selectedRequestUserName;
    }

    public DashboardCommandsController getDashboardCommandsComponentController() {
        return dashboardCommandsComponentController;
    }

    @FXML
    void loadFileButtonOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        Stage stage = (Stage) loadFileButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile == null) {
            return;
        }

        String absolutePath = selectedFile.getAbsolutePath();

        // Show progress bar pop-up
        Stage progressBarStage = createProgressBarStage();
        showProgressBar(selectedFile, progressBarStage, absolutePath);
    }

    private void showProgressBar(File selectedFile, Stage progressBarStage, String absolutePath) {
        Task<Void> loadFileTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Simulate loading process with progress
                for (int i = 0; i <= 10; i++) {
                    updateProgress(i, 10);
                    Thread.sleep(100); // Simulate delay
                }

                String loadFileUrl = HttpUrl
                        .parse(LOAD_FILE)
                        .newBuilder()
                        .build()
                        .toString();

                RequestBody fileBody =  RequestBody.create(selectedFile, MediaType.parse("application/xml"));

                MultipartBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM) // Ensure the type is set to FORM
                        .addFormDataPart("file", selectedFile.getName(), fileBody)
                        .build();


                HttpClientUtil.runAsyncPost(loadFileUrl, requestBody, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Platform.runLater(() -> {
                            ShowAlert.showAlert("Error", "File Load Error", e.getMessage(), Alert.AlertType.ERROR);
                            progressBarStage.close();
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                        try {
                            String jsonResponse = response.body().string();
                            Gson gson = new Gson();
                            Map<String, Object> result = gson.fromJson(jsonResponse, Map.class);

                            String message = (String) result.get("message");

                            Platform.runLater(() -> {
                                if (!response.isSuccessful()) {
                                    ShowAlert.showAlert("Error", "File Load Error", message, Alert.AlertType.ERROR);
                                }
                                progressBarStage.close();
                            });
                        } catch (Exception e) {
                            Platform.runLater(() -> {
                                ShowAlert.showAlert("Error", "File Load Error", "Error processing response: " + e.getMessage(), Alert.AlertType.ERROR);
                                progressBarStage.close();
                            });
                        } finally {
                            response.close();
                        }
                    }
                });

                return null;
            }
        };

        Thread loadFileThread = new Thread(loadFileTask);

        ProgressBar progressBar = (ProgressBar) progressBarStage.getScene().lookup("#progressBar");
        progressBar.progressProperty().bind(loadFileTask.progressProperty());

        loadFileThread.setDaemon(true);
        loadFileThread.start();

        progressBarStage.show();
    }

    private Stage createProgressBarStage() {
        // Create a new pop-up stage
        Stage progressBarStage = new Stage();
        progressBarStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows
        progressBarStage.setTitle("Loading File");

        // Create a VBox to hold the label and progress bar
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        // Create and configure the label
        Label label = new Label("Loading file, please wait...");
        vbox.getChildren().add(label);

        // Create and configure the progress bar
        ProgressBar progressBar = new ProgressBar();
        progressBar.setId("progressBar");
        progressBar.setPrefWidth(400); // Set the preferred width of the progress bar
        vbox.getChildren().add(progressBar);

        // Set the scene
        Scene scene = new Scene(vbox, 450, 100);
        progressBarStage.setScene(scene);

        return progressBarStage;
    }

    public void displaySheet(Boolean loadSheetFromDashboard){
        mainSheetController.displaySheet(loadSheetFromDashboard);
    }

    public void getUserPermissionAndDisableIfNecessary(String spreadsheetName, String username) {
        // URL for the GET request
        String finalUrl = HttpUrl.parse(Constants.GET_USER_PERMISSIONS)
                .newBuilder()
                .addQueryParameter("sheetName", spreadsheetName)
                .addQueryParameter("username", username)
                .build()
                .toString();

        // Create a request object
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        // Run the async GET request
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    ShowAlert.showAlert("Error", "Failed to get permissions: ", e.getMessage(), Alert.AlertType.ERROR);
                });
            }

            @Override
            public void onResponse(@NotNull Call call,@NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Parse the response to get the PermissionType using Gson
                    String jsonResponse = response.body().string();
                    Gson gson = new Gson();
                    PermissionType permissionType = gson.fromJson(jsonResponse.toUpperCase(), PermissionType.class);

                    // Check if the PermissionType is READER, then disable the edit buttons
                    Platform.runLater(() -> {
                        if (permissionType.equals(PermissionType.READER)) {
                            disableEditFeatures();
                        }
                    });
                } else {
                    Platform.runLater(() -> {
                        ShowAlert.showAlert("Error", "Failed to get permissions: ", response.message(), Alert.AlertType.ERROR);
                    });
                }
            }
        });
    }

    private void disableEditFeatures() {
        isEditDisabledProperty.set(true); // Use this to disable all bound elements.
        mainSheetController.disableEditFeatures();
    }

    public BooleanProperty getEditDisabledProperty() {
        return isEditDisabledProperty;
    }

    private void disableViewSheet() {
        dashboardCommandsComponentController.disableViewSheet();
    }

    public TabelsController getTabelsComponentController() {
        return tabelsComponentController;
    }

    @FXML
    void animationsCheckBoxOnAction(ActionEvent event) {
        boolean isSelected = animationsCheckBox.isSelected();
        isAnimationSelectedProperty.set(isSelected);
    }

    public BooleanProperty isAnimationSelectedProperty() { return isAnimationSelectedProperty; }

    @FXML
    void themesComboBoxOnAction(ActionEvent event) {
        selectedTheme = themesComboBox.getValue();
        themeManager.applyTheme(loadFileButton.getScene(), selectedTheme);
    }
  
    public void close() {
        if(dashboardCommandsComponentController != null)
            dashboardCommandsComponentController.close();
        if(tabelsComponentController != null)
            tabelsComponentController.close();
        if(mainSheetController != null)
            mainSheetController.close();
    }

    @FXML
    void logoutButtonOnAction(ActionEvent event) {
        String finalUrl = HttpUrl
                .parse(Constants.LOGOUT)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    ShowAlert.showAlert("Error", "Failed to logout", e.getMessage(), Alert.AlertType.ERROR);
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (response.body() == null) {
                    return;
                }

                try {
                    String jsonResponse = response.body().string();
                    if (response.isSuccessful()) {
                        String message = jsonResponse.trim();
                        System.out.println("See you next time");
                        close();
                        Platform.exit();
                    } else {
                        String message = jsonResponse.trim();
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> {
                        ShowAlert.showAlert("Error", "Failed to logout", e.getMessage(), Alert.AlertType.ERROR);
                    });
                } finally {
                    response.close();
                    HttpClientUtil.shutdown();
                }
            }
        });
    }

    public String getSelectedTheme() {
        return selectedTheme;
    }

    public void setTheme(Scene scene) {
        themeManager.applyTheme(scene, selectedTheme);
    }
}
