package spreadsheet.client.component.dashboard;

import com.google.gson.Gson;
import impl.EngineImpl;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import spreadsheet.client.util.ShowAlert;
import spreadsheet.client.util.http.HttpClientUtil;
import static spreadsheet.client.util.Constants.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class DashboardController {


    @FXML private ScrollPane dashboardScrollPane;
    @FXML private Button loadFileButton;

    @FXML private VBox tabelsComponent;
    @FXML private TabelsController tabelsComponentController;
    @FXML private VBox dashboardCommandsComponent;
    @FXML private DashboardCommandsController dashboardCommandsComponentController;

    private String userName;
    private MainSheetController mainSheetController;
    private String selectedSheet;

    @FXML
    public void initialize() {
        if (tabelsComponentController != null && dashboardCommandsComponentController != null) {
            tabelsComponentController.setDashboardController(this);
            dashboardCommandsComponentController.setDashboardController(this);
        }
        else{
            System.out.println("nullll");
        }
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setSelectedSheet(String selectedSheet) {
        this.selectedSheet = selectedSheet;
        System.out.println(selectedSheet);
    }

    public String getSelectedSheet() {
        return selectedSheet;
    }

    public void setMainSheetController(MainSheetController mainSheetController) {
        this.mainSheetController = mainSheetController;
    }

    public String getUserName() { return userName; }

    public TabelsController getTabelsController() {return tabelsComponentController; }

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

//                            int status = ((Double) result.get("status")).intValue(); // JSON numbers are parsed as Double
                            String message = (String) result.get("message");

                            Platform.runLater(() -> {
                                if (response.isSuccessful()) {
                                    tabelsComponentController.fetchSheetTableDetails();
                                } else {
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

    public void viewSheet(){
        mainSheetController.viewSheet(selectedSheet);
    }
}
