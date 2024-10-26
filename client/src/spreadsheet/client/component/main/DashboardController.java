package spreadsheet.client.component.main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.*;
import javafx.concurrent.Task;
import static spreadsheet.client.util.Constants.*;
import spreadsheet.client.util.http.SimpleCookieManager;

import java.io.File;
import java.io.IOException;

public class DashboardController {

    @FXML
    private Button loadFileButton;
    private MainSheetController mainSheetController;

    private String userName;
    private OkHttpClient client;
    private SimpleCookieManager cookieManager;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setOkHttpClient(OkHttpClient client) {
        this.client = client;
    }

    public void setCookieManager(SimpleCookieManager cookieManager) {
        this.cookieManager = cookieManager;
    }

//    @FXML
//    void loadFileButtonOnAction(ActionEvent event) {
//
//    }

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
        showProgressBar(progressBarStage, absolutePath);
    }

    private void showProgressBar(Stage progressBarStage, String absolutePath) {
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

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("filePath", absolutePath)
                        .build();

                Request request = new Request.Builder()
                        .url(loadFileUrl)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        Platform.runLater(() -> {
                         //   selectedFileProperty.set(absolutePath);
                       //     isFileSelected.set(true);
                        //    mainController.refreshUIWithNewFile();
                            progressBarStage.close();
                        });
                    } else {
                        String errorMessage = response.body().string();
                        Platform.runLater(() -> {
                 //           mainController.showAlert("Error", "File Load Error", errorMessage, Alert.AlertType.ERROR);
                            progressBarStage.close();
                        });
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> {
                  //      mainController.showAlert("Error", "File Load Error", "An error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
                        progressBarStage.close();
                    });
                }

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
