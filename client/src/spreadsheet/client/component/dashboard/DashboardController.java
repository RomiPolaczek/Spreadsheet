package spreadsheet.client.component.dashboard;

import impl.EngineImpl;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
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
import spreadsheet.client.util.http.HttpClientUtil;
import static spreadsheet.client.util.Constants.*;

import java.io.File;
import java.io.IOException;

public class DashboardController {

    @FXML private Button loadFileButton;
    private MainSheetController mainSheetController;

    @FXML private VBox tabelsComponent;
    @FXML private TabelsController tabelsComponentController;
    @FXML private VBox dashboardCommandsComponent;
    @FXML private DashboardCommandsController dashboardCommandsComponentController;

    private String userName;

    @FXML
    public void initialize() {
        if (tabelsComponentController != null && dashboardCommandsComponentController != null) {
            tabelsComponentController.setDashboardController(this);
            dashboardCommandsComponentController.setDashboardController(this);
        }
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
                            // Handle error, e.g., show an alert
                            System.err.println("File Load Error: " + e.getMessage());
                            progressBarStage.close();
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                        try {
                            Platform.runLater(() -> {
                                if (response.isSuccessful()) {
                                    // Handle successful response
                                    // You can update the UI or process the response here
                                    System.out.println("File loaded successfully.");
                                    tabelsComponentController.fetchSheetDetails();
                                } else {
                                    System.err.println("File Load Error: " + response.code());
                                }
                                progressBarStage.close();
                            });
                        } catch (Exception e) {
                            Platform.runLater(() -> {
                                System.err.println("Error processing response: " + e.getMessage());
                                progressBarStage.close();
                            });
                        } finally {
                            response.close(); // Ensure the response is closed to free resources
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
}
