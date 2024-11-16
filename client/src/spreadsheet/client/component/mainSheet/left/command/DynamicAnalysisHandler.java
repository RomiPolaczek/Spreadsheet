package spreadsheet.client.component.mainSheet.left.command;//package spreadsheet.client.component.mainSheet.left.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import dto.DTOsheet;
import okhttp3.Call;
import okhttp3.Callback;
import org.jetbrains.annotations.NotNull;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.api.CoordinateDeserializer;
import spreadsheet.client.component.mainSheet.MainSheetController;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import okhttp3.*;

import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Map;

import spreadsheet.client.component.mainSheet.sheet.SheetController;
import spreadsheet.client.util.Constants;
import spreadsheet.client.util.ShowAlert;
import spreadsheet.client.util.http.HttpClientUtil;

import static spreadsheet.client.util.Constants.*;

public class DynamicAnalysisHandler {

    private MainSheetController mainSheetController;
    private Label selectedCellLabel;

    public DynamicAnalysisHandler(MainSheetController mainSheetController, Label selectedCellLabel) {
        this.mainSheetController = mainSheetController;
        this.selectedCellLabel = selectedCellLabel;
    }

    public void handleDynamicAnalysis(ActionEvent event) {
        BorderPane root = new BorderPane();
        VBox leftVBox = createLeftVBox();

        // Create a new SheetController for the pop-up
        SheetController newSheetController = new SheetController();
        newSheetController.setMainSheetController(mainSheetController);
        GridPane gridPane = new GridPane();
        newSheetController.setDynamicGridPane(gridPane);
        newSheetController.initializeSheetController();

        fetchDTOCopySheet().thenAccept(dtoSheet -> {
            Platform.runLater(() -> {
                newSheetController.setSheet(dtoSheet, false);
                highlightSelectedCell(newSheetController);
            });
        });

        setupValueSliderListener(leftVBox, newSheetController);
        setupStage(root, leftVBox, gridPane);
    }

    private HBox createSelectedCellBox() {
        HBox selectedCellBox = new HBox();
        Label introToSelectedCell = new Label("Cell: ");
        Label cellToDynamicAnalysis = new Label(selectedCellLabel.getText());
        cellToDynamicAnalysis.setStyle("-fx-font-weight: bold");
        cellToDynamicAnalysis.textProperty().bind(selectedCellLabel.textProperty());
        selectedCellBox.getChildren().addAll(introToSelectedCell, cellToDynamicAnalysis);
        return selectedCellBox;
    }

    private VBox createLeftVBox() {
        HBox selectedCellBox = createSelectedCellBox();

        VBox leftVBox = new VBox(3);
        leftVBox.setPadding(new Insets(8, 8, 8, 8));
        leftVBox.setPrefSize(127, 400);

        Label minValueLabel = new Label("Minimum Value:");
        TextField minValueTextField = new TextField();
        VBox.setMargin(minValueLabel, new Insets(10, 0, 0, 0));

        Label maxValueLabel = new Label("Maximum Value:");
        TextField maxValueTextField = new TextField();
        VBox.setMargin(maxValueLabel, new Insets(10, 0, 0, 0));

        Label stepSizeLabel = new Label("Step Size:");
        TextField stepSizeTextField = new TextField();
        VBox.setMargin(stepSizeLabel, new Insets(10, 0, 0, 0));

        Slider valueSlider = new Slider();
        VBox.setMargin(valueSlider, new Insets(10, 0, 0, 0));
        valueSlider.setDisable(true);

        validateAndSetSlider(maxValueTextField, valueSlider, "Please enter a \nvalid max value.", minValueTextField, maxValueTextField, stepSizeTextField);
        validateAndSetSlider(minValueTextField, valueSlider, "Please enter a \nvalid min value.", minValueTextField, maxValueTextField, stepSizeTextField);
        validateAndSetSlider(stepSizeTextField, valueSlider, "Please enter a \nvalid step size.", minValueTextField, maxValueTextField, stepSizeTextField);

        leftVBox.getChildren().addAll(selectedCellBox, minValueLabel, minValueTextField, maxValueLabel, maxValueTextField, stepSizeLabel, stepSizeTextField, valueSlider);
        return leftVBox;
    }

    private void highlightSelectedCell(SheetController newSheetController) {
        newSheetController.getCellLabel(mainSheetController.getSelectedCellProperty().getValue())
                .setStyle("-fx-background-color: yellow; -fx-text-fill: black");

        SimpleStringProperty selectedCell = mainSheetController.getSelectedCellProperty();
        selectedCell.addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                newSheetController.getCellLabel(oldValue).setStyle("");
                newSheetController.getCellLabel(newValue).setStyle("-fx-background-color: yellow; -fx-text-fill: black");
            });
        });
    }

    public CompletableFuture<DTOsheet> fetchDTOCopySheet() {
        CompletableFuture<DTOsheet> future = new CompletableFuture<>();
        String dtoCopySheetUrl = Constants.CREATE_DTO_COPY_SHEET; // Replace with the actual endpoint URL

        HttpUrl url = HttpUrl
                .parse(dtoCopySheetUrl)
                .newBuilder()
                .addQueryParameter("selectedSheet", mainSheetController.getSheetName())
                .build();


        HttpClientUtil.runAsync(url.toString(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> ShowAlert.showAlert("Error", "Fetch Failed", "Failed to fetch DTO sheet: " + e.getMessage(), Alert.AlertType.ERROR));
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 200) {
                    try {
                        String json = response.body().string();
                        Gson gson = new GsonBuilder()
                                .registerTypeAdapter(Coordinate.class, new CoordinateDeserializer())
                                .create();
                        DTOsheet dtoSheet = gson.fromJson(json, DTOsheet.class);
                        future.complete(dtoSheet);

                    } catch (IOException e) {
                        Platform.runLater(() -> ShowAlert.showAlert("Error", "Parse Failed", "Error: " + e.getMessage(), Alert.AlertType.ERROR));
                        future.completeExceptionally(e);
                    }
                } else {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> ShowAlert.showAlert("Error", "Fetch Failed", "Error: " + responseBody, Alert.AlertType.ERROR));
                    future.completeExceptionally(new IOException("Failed with code: " + response.code()));
                }
                response.close();
            }
        });

        return future;
    }

    public CompletableFuture<Map<String, String>> getUpdatedCellsAfterCellChange(String cellID, String newValue) {
        CompletableFuture<Map<String, String>> future = new CompletableFuture<>();

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty(SELECTED_SHEET_NAME, mainSheetController.getSheetName());
        jsonBody.addProperty(CELL_ID, cellID);
        jsonBody.addProperty(NEW_VALUE, newValue);

        RequestBody body = RequestBody.create(jsonBody.toString(), okhttp3.MediaType.parse("application/json"));

        HttpClientUtil.runAsyncPost(UPDATED_CELLS_DYNAMIC_ANALYSIS, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> ShowAlert.showAlert("Error", "Request Failed", "Failed to retrieve updated cells: " + e.getMessage(), Alert.AlertType.ERROR));
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 200) {
                    try {
                        String json = response.body().string();
                        Gson gson = new Gson();
                        Type type = new TypeToken<Map<String, String>>() {}.getType();
                        Map<String, String> updatedValues = gson.fromJson(json, type);
                        future.complete(updatedValues);
                    } catch (IOException e) {
                        Platform.runLater(() -> ShowAlert.showAlert("Error", "Parse Failed", "Error parsing response: " + e.getMessage(), Alert.AlertType.ERROR));
                        future.completeExceptionally(e);
                    }
                } else {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> ShowAlert.showAlert("Error", "Request Failed", "Error: " + responseBody, Alert.AlertType.ERROR));
                    future.completeExceptionally(new IOException("Error: " + responseBody));
                }
                response.close();
            }
        });

        return future;
    }


    private void validateAndSetSlider(TextField textField, Slider valueSlider, String labelText, TextField minValueTextField, TextField maxValueTextField, TextField stepSizeTextField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            valueSlider.setDisable(minValueTextField.getText().trim().isEmpty() ||
                    maxValueTextField.getText().trim().isEmpty() ||
                    stepSizeTextField.getText().trim().isEmpty());

            VBox parentVBox = (VBox) textField.getParent();
            Label errorLabel = null;

            for (Node node : parentVBox.getChildren()) {
                if (node instanceof Label && "errorLabel".equals(node.getId())) {
                    errorLabel = (Label) node;
                    break;
                }
            }

            // Create a new error label if it doesn't already exist
            if (errorLabel == null) {
                errorLabel = new Label();
                errorLabel.setId("errorLabel");
                errorLabel.setTextFill(Color.RED);
            }

            // Clear previous styles
            textField.setStyle("");

            // Check if the TextField is not empty
            if (!textField.getText().trim().isEmpty()) {
                try {
                    // Attempt to parse the double value from the TextField
                    double value = Double.parseDouble(textField.getText());

                    // Set the value as the slider's max and apply green border
                    if(textField == maxValueTextField)
                        valueSlider.setMax(value);
                    else if(textField == minValueTextField)
                        valueSlider.setMin(value);
                    else if(textField == stepSizeTextField)
                        valueSlider.setBlockIncrement(value);

                    textField.setStyle("-fx-border-color: green; -fx-background-color: #dcfbdc; -fx-border-width: 2px; -fx-text-fill: black");

                    // Remove error label if the value is valid
                    if (parentVBox.getChildren().contains(errorLabel)) {
                        parentVBox.getChildren().remove(errorLabel);
                    }
                }
                catch (NumberFormatException e) {
                    // Invalid number: show red border and add the error label
                    textField.setStyle("-fx-border-color: red; -fx-background-color: #ffdddd; -fx-border-width: 2px; -fx-text-fill: black ");
                    errorLabel.setText(labelText);

                    // Add the error label if it's not already present
                    if (!parentVBox.getChildren().contains(errorLabel)) {
                        parentVBox.getChildren().add(errorLabel);
                    }
                    valueSlider.setDisable(true);
                }
            } else {
                // If the field is empty, reset the styles and remove any error label
                textField.setStyle("");
                if (parentVBox.getChildren().contains(errorLabel)) {
                    parentVBox.getChildren().remove(errorLabel);
                }
            }
        });
    }

    private void setupValueSliderListener(VBox leftVBox, SheetController newSheetController) {
        Slider valueSlider = (Slider) leftVBox.getChildren().stream()
                .filter(node -> node instanceof Slider)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Value slider not found in VBox"));

        valueSlider.setShowTickMarks(true);
        valueSlider.setShowTickLabels(true);

        valueSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            String cellID = selectedCellLabel.getText(); // Get the selected cell ID
            String formattedValue = String.format("%.2f", newValue.doubleValue());

            getUpdatedCellsAfterCellChange(cellID, formattedValue).thenAccept(updatedCells -> {
                Platform.runLater(() -> {
                    // Update cells in the UI based on the response
                    updatedCells.forEach((cell, value) -> {
                        Label cellLabel = newSheetController.getCellLabel(cell);
                        if (cellLabel != null) {
                            cellLabel.setText(value);
                        }
                    });
                });
            });
        });
    }


    private void setupStage(BorderPane root, VBox leftVBox, GridPane gridPane) {
        root.setLeft(leftVBox);
        root.setCenter(gridPane);
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root);
        mainSheetController.setSheetStyle(scene);
        gridPane.setMinSize(400, 400); // Set a reasonable minimum size
        gridPane.setPrefSize(600, 400); // Set a preferred size

        Platform.runLater(() -> {
            popupStage.setScene(scene);
            popupStage.showAndWait();
        });
    }
}
