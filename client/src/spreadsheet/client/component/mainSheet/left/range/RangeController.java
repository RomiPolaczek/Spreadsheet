package spreadsheet.client.component.mainSheet.left.range;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
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
import spreadsheet.client.util.Constants;
import spreadsheet.client.util.ShowAlert;
import spreadsheet.client.util.http.HttpClientUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void setMainController(MainSheetController mainSheetController) {
        this.mainSheetController = mainSheetController;
        rangeObservableList = FXCollections.observableArrayList();
        toggleButtons = new ArrayList<>();
        anyRangePressedProperty = new SimpleBooleanProperty(false);
    }

    public void initializeRangeController() {
        deleteRangeButton.disableProperty().bind(anyRangePressedProperty.not());

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

        // Build the request
        Request request = new Request.Builder()
                .url(finalUrl)
                .post(body) // POST request
                .build();

        // Execute the request asynchronously
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new okhttp3.Callback() {
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
//        List<ToggleButton> pressedButtons = new ArrayList<>();
//
//        // Identify all pressed toggle buttons
//        for (ToggleButton button : toggleButtons) {
//            if (button.isSelected()) {
//                pressedButtons.add(button);
//            }
//        }
//
//        // Check if there are any pressed buttons
//        if (pressedButtons.isEmpty()) {
//            mainController.showAlert("Warning", "No ranges selected", "Please select ranges to delete.", Alert.AlertType.WARNING);
//            return;
//        }
//
//        // Process each pressed button to remove its range
//        for (ToggleButton button : pressedButtons) {
//            String rangeName = button.getText();
//            try {
//                // Remove the range from the engine
//                button.setSelected(false);
//                mainController.getEngine().removeRange(rangeName);
//                rangeObservableList.remove(rangeName);
//            } catch (Exception ex) {
//                mainController.showAlert("Error", "Unable to delete range", ex.getMessage(), Alert.AlertType.ERROR);
//            }
//        }
//
//        // Reset all toggle buttons to the unpressed state
//        resetAllToggleButtons();
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
                    ShowAlert.showAlert("Error", "", e.getMessage(), Alert.AlertType.ERROR);
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Parse the response body as a JSON string
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    List<String> ranges = gson.fromJson(responseBody, new TypeToken<List<String>>() {}.getType());

                    // Update the ListView on the JavaFX Application Thread
                    Platform.runLater(() -> {
                        rangeObservableList.clear();
                        rangeObservableList.addAll(ranges);
                        rangeListView.setItems(rangeObservableList);
                    });
                } else {
                    // Handle unsuccessful response
                    ///////// FIX !!
                    System.err.println("Failed to get ranges: " + response.body().string());
                }
            }
        });
    }



    private void displayRange(String rangeName) {
//        try {
//            // Retrieve the list of cells in the range from the engine
//            List<String> cellsToHighlight = mainController.getEngine().getRangeCellsList(rangeName);
//            for (String cellID : cellsToHighlight) {
//                Label cellLabel = mainController.getCellLabel(cellID);  // Assuming cellLabels is a Map of cell IDs to Labels
//                if (cellLabel != null) {
//                    cellLabel.getStyleClass().add("range-label");  // Add a custom CSS class to highlight the range
//                }
//            }
//        } catch (Exception ex) {
//            mainController.showAlert("Error", "Unable to display range", ex.getMessage(), Alert.AlertType.ERROR);
//        }
    }

    private void removeRangeDisplay(String rangeName) {
//        try {
//            // Retrieve the list of cells in the range from the engine
//            List<String> cellsToUnhighlight = mainController.getEngine().getRangeCellsList(rangeName);
//            for (String cellID : cellsToUnhighlight) {
//                Label cellLabel = mainController.getCellLabel(cellID);  // Assuming cellLabels is a Map of cell IDs to Labels
//                if (cellLabel != null) {
//                    cellLabel.getStyleClass().remove("range-label");  // Remove the custom CSS class to remove highlighting
//                }
//            }
//        } catch (Exception ex) {
//            mainController.showAlert("Error", "Unable to remove range display", ex.getMessage(), Alert.AlertType.ERROR);
//        }
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

}
