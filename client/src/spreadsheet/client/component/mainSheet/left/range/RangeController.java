package spreadsheet.client.component.mainSheet.left.range;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import spreadsheet.client.component.mainSheet.MainSheetController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import spreadsheet.client.util.ShowAlert;
import spreadsheet.client.util.http.HttpClientUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static spreadsheet.client.util.Constants.*;

public class RangeController {

    @FXML
    private Button addRangeButton;
    @FXML
    private Button deleteRangeButton;
    @FXML
    private ListView<String> rangeListView;

    private MainSheetController mainSheetController;
    private ObservableList<String> rangeObservableList;
    private List<ToggleButton> toggleButtons;
    private BooleanProperty anyRangePressedProperty;
    private BooleanProperty isEditDisabledProperty;

    public void setMainController(MainSheetController mainSheetController) {
        this.mainSheetController = mainSheetController;
        rangeObservableList = FXCollections.observableArrayList();
        toggleButtons = new ArrayList<>();
        anyRangePressedProperty = new SimpleBooleanProperty(false);
    }

    public void initializeRangeController() {
        isEditDisabledProperty = new SimpleBooleanProperty(false);
        deleteRangeButton.disableProperty().bind(Bindings.or(anyRangePressedProperty.not(), isEditDisabledProperty));
        addRangeButton.disableProperty().bind(isEditDisabledProperty);

        // Set a custom CellFactory to display a label for each range
        rangeListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String rangeName, boolean empty) {
                        super.updateItem(rangeName, empty);
                        if (empty || rangeName == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            ToggleButton toggleButton = new ToggleButton(rangeName);
                            toggleButton.getStyleClass().add("toggle-button");// Add custom styling if needed
                            toggleButton.setMaxWidth( Double.MAX_VALUE );

                            // Add listener to handle button selection (pressed)
                            toggleButton.selectedProperty().addListener((observable, oldValue, isSelected) -> {
                                if (isSelected) {
                                    displayRange(rangeName);  // Display the range when button is pressed
                                } else {
                                    removeRangeDisplay(rangeName);  // Remove the range display when released
                                }
                                updateAnyRangePressedProperty();
                            });

                            toggleButtons.add(toggleButton); // Keep track of the toggle button
                            setGraphic(toggleButton);  // Set the label as the graphic of the cell
                        }
                    }
                };
            }
        });
    }


    @FXML
    void addRangeButtonOnAction(ActionEvent event) {
        // Create a new Stage (popup window)
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Add Range");

        // Create a VBox to hold the label, text fields, and buttons
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        // Create and configure the text fields for user input
        Label nameLabel = new Label("Range's Name: ");
        TextField nameField = new TextField();
        vbox.getChildren().addAll(nameLabel, nameField);

        Label rangeLabel = new Label("Range: ");
        TextField rangeField = new TextField();
        rangeField.setPromptText("e.g., \"A1..A3\", \"B3..D4\"");
        vbox.getChildren().addAll(rangeLabel, rangeField);

        // Create and configure the submit button
        Button submitButton = new Button("Submit");
        submitButton.setDisable(true);  // Initially disabled

        // Enable the submit button only when both fields are not empty
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(nameField.getText().trim().isEmpty() || rangeField.getText().trim().isEmpty());
        });

        rangeField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(nameField.getText().trim().isEmpty() || rangeField.getText().trim().isEmpty());
        });

        submitButton.setOnAction(e -> {
            String name = nameField.getText();
            String rangeStr = rangeField.getText();
            try {
                addRangeInEngine(name, rangeStr);
                resetAllToggleButtons(); // Unpress all toggle buttons when a new range is added
                popupStage.close();
            } catch (Exception ex) {
                ShowAlert.showAlert("Error", "Invalid input", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        vbox.getChildren().add(submitButton);

        // Set the scene
        Scene scene = new Scene(vbox, 300, 200);
        mainSheetController.setTheme(scene);
        popupStage.setScene(scene);

        // Show the pop-up window
        popupStage.showAndWait();
    }

//    private void addRangeInEngine(String rangeName, String rangeStr) {
//        // Create a Map to hold the parameters
//        Map<String, String> rangeData = new HashMap<>();
//        rangeData.put("rangeName", rangeName);
//        rangeData.put("rangeStr", rangeStr);
//
//        // Convert the map to JSON
//        Gson gson = new Gson();
//        String jsonBody = gson.toJson(rangeData);
//
//        String finalUrl = HttpUrl
//                .parse(ADD_RANGE)
//                .newBuilder()
//                .build()
//                .toString();
//
//        // Execute the request asynchronously
//        HttpClientUtil.runAsync(finalUrl, new okhttp3.Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                // Handle failure (e.g., network error)
//                System.err.println("Request failed: " + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    // Parse the response body if needed
//                    String responseBody = response.body().string();
//                    System.out.println("Response: " + responseBody);
//                } else {
//                    // Handle the unsuccessful response
//                    System.err.println("Failed to add range: " + response.body().string());
//                }
//            }
//        });
//    }

    private void addRangeInEngine(String rangeName, String rangeStr) {
        // Create a Map to hold the parameters
        Map<String, String> rangeData = new HashMap<>();
        rangeData.put("selectedSheet", mainSheetController.getSheetName());
        rangeData.put("rangeName", rangeName);
        rangeData.put("rangeStr", rangeStr);

        // Convert the map to JSON
        Gson gson = new Gson();
        String jsonBody = gson.toJson(rangeData);

        String finalUrl = HttpUrl
                .parse(ADD_RANGE)
                .newBuilder()
                .build()
                .toString();

        // Create the request body
        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.get("application/json; charset=utf-8")
        );

        HttpClientUtil.runAsyncPost(finalUrl,body, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure (e.g., network error)
                System.err.println("Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Parse the response body if needed
                    String responseBody = response.body().string();
                   // System.out.println("Response: " + responseBody);
                    populateRangeListView();
                } else {
                    // Handle the unsuccessful response
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        ShowAlert.showAlert("Error", "Failed to add range: ", responseBody , Alert.AlertType.ERROR);
                    });
                }
            }
        });
    }

    @FXML
    void deleteRangeButtonOnAction(ActionEvent event) {
        List<ToggleButton> pressedButtons = new ArrayList<>();

        // Identify all pressed toggle buttons
        for (ToggleButton button : toggleButtons) {
            if (button.isSelected()) {
                pressedButtons.add(button);
            }
        }

        // Check if there are any pressed buttons
        if (pressedButtons.isEmpty()) {
            ShowAlert.showAlert("Warning", "No ranges selected", "Please select a range to delete.", Alert.AlertType.WARNING);
            return;
        }

        // Process each pressed button to remove its range
        for (ToggleButton button : pressedButtons) {
            String rangeName = button.getText();
            try {
                // Remove the range from the engine
                button.setSelected(false);
                //mainController.getEngine().removeRange(rangeName);
                removeRangeInEngine(rangeName);
                rangeObservableList.remove(rangeName);
            } catch (Exception ex) {
                //ShowAlert.showAlert("Error", "Unable to delete range", ex.getMessage(), Alert.AlertType.ERROR);
                System.out.println(ex.getMessage());
            }
        }

        // Reset all toggle buttons to the unpressed state
        resetAllToggleButtons();
    }

    public void removeRangeInEngine(String rangeName) {
        String finalUrl = HttpUrl
                .parse(DELETE_RANGE)
                .newBuilder()
                .addQueryParameter("rangeName", rangeName)
                .addQueryParameter("selectedSheet", mainSheetController.getSheetName())
                .build()
                .toString();

        HttpClientUtil.runAsyncDelete(finalUrl, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    // Handle failure
                    ShowAlert.showAlert("Error", "Failed to delete range: ", e.getMessage(), Alert.AlertType.ERROR);
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    String jsonResponse = response.body().string(); // Get the response as a raw string

                    Platform.runLater(() -> {
                        if (response.isSuccessful()) {
                            Platform.runLater(() -> {
                                populateRangeListView();
                            });
                        } else {
                            ShowAlert.showAlert("Error", "Failed to delete range:", jsonResponse, Alert.AlertType.ERROR);
                            //System.out.println(jsonResponse);
                        }
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        ShowAlert.showAlert("Error", "Failed to delete range: ", e.getMessage(), Alert.AlertType.ERROR);
                        //System.out.println(e.getMessage());
                    });
                }
            }
        });
    }

    public void populateRangeListView() {
        // Construct the URL with query parameters for a GET request
        String finalUrl = HttpUrl
                .parse(GET_ALL_RANGES)
                .newBuilder()
                .addQueryParameter("selectedSheet", mainSheetController.getSheetName())
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    ShowAlert.showAlert("Error", "Failed to get ranges", e.getMessage(), Alert.AlertType.ERROR);
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();

                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    List<String> ranges = gson.fromJson(responseBody, new TypeToken<List<String>>() {}.getType());

                    // Update the ListView on the JavaFX Application Thread
                    Platform.runLater(() -> {
                        rangeObservableList.clear();
                        rangeObservableList.addAll(ranges);
                        rangeListView.setItems(rangeObservableList);
                    });
                } else {
                    Platform.runLater(() -> {
                        ShowAlert.showAlert("Error", "Failed to get ranges: ", responseBody, Alert.AlertType.ERROR);
                    });
                }
            }
        });
    }

    private void displayRange(String rangeName) {
        handleRangeDisplay(rangeName,
                label -> label.getStyleClass().add("range-label"),
                "Failed to get range's cells to display."
        );
    }

    private void removeRangeDisplay(String rangeName) {
        handleRangeDisplay(rangeName,
                label -> label.getStyleClass().remove("range-label"),
                "Failed to get range's cells to remove display."
        );
    }

    private void handleRangeDisplay(String rangeName, Consumer<Label> cellAction, String errorMessage) {
        // Construct the URL with query parameters for a GET request
        String finalUrl = HttpUrl
                .parse(GET_RANGE_CELLS_LIST)
                .newBuilder()
                .addQueryParameter("selectedSheet", mainSheetController.getSheetName())
                .addQueryParameter("rangeName", rangeName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    ShowAlert.showAlert("Error", errorMessage, e.getMessage(), Alert.AlertType.ERROR);
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();

                if (response.isSuccessful()) {
                    // Parse the response body as a JSON string
                    Gson gson = new Gson();
                    List<String> rangeCellsList = gson.fromJson(responseBody, new TypeToken<List<String>>() {}.getType());

                    // Update the ListView on the JavaFX Application Thread
                    Platform.runLater(() -> {
                        for (String cellID : rangeCellsList) {
                            Label cellLabel = mainSheetController.getCellLabel(cellID); // Assuming cellLabels is a Map of cell IDs to Labels
                            if (cellLabel != null) {
                                cellAction.accept(cellLabel); // Apply the passed action to the cell label
                            }
                        }
                    });
                } else {
                    Platform.runLater(() -> {
                        ShowAlert.showAlert("Error", errorMessage, responseBody, Alert.AlertType.ERROR);
                    });
                }
            }
        });
    }


    private void resetAllToggleButtons() {
        for (ToggleButton button : toggleButtons) {
            button.setSelected(false);
        }
        updateAnyRangePressedProperty(); // Update the property after resetting
    }

    private void updateAnyRangePressedProperty() {
        boolean anyPressed = toggleButtons.stream().anyMatch(ToggleButton::isSelected);
        anyRangePressedProperty.set(anyPressed);
    }

    public void disableEditFeatures() {
        isEditDisabledProperty.set(true);
    }

    public void close() {
        rangeListView.getItems().clear();
    }

}
