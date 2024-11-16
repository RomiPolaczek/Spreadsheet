package spreadsheet.client.component.mainSheet.header;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import dto.DTOsheet;
import javafx.application.Platform;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.api.CoordinateDeserializer;
import spreadsheet.client.component.dashboard.DashboardController;
import spreadsheet.client.component.dashboard.tables.AvailableSheetsRefresher;
import spreadsheet.client.component.mainSheet.MainSheetController;
import dto.DTOcell;
import javafx.animation.ScaleTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import spreadsheet.client.theme.ThemeManager;
import spreadsheet.client.util.Constants;
import spreadsheet.client.util.ShowAlert;
import spreadsheet.client.util.http.HttpClientUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;

import static spreadsheet.client.util.Constants.*;

public class HeaderController {

    @FXML
    private Button updateCellValueButton;
    @FXML
    private Label lastUpdateVersionCellLabel;
    @FXML
    private ComboBox<String> versionSelectorComboBox;
    @FXML
    private Label selectedCellIDLabel;
    @FXML
    private CheckBox animationsCheckBox;
    @FXML
    private ComboBox<String> themesComboBox;
    @FXML
    private TextField originalCellValueTextField;
    @FXML
    private Button formatFunctionButton;
    @FXML
    private Button backButton;
    @FXML
    private Label userNameLabel;
    @FXML
    private Button updateToLatestVersionButton;

    private MainSheetController mainSheetController;
    private SimpleStringProperty selectedCellProperty;
    private SimpleStringProperty originalCellValueProperty;
    private SimpleStringProperty lastUpdateVersionCellProperty;
    private SimpleBooleanProperty isAnimationSelectedProperty;
    private List<String> lastHighlightedCells = new ArrayList<>();
    private ThemeManager themeManager;
    private StringBuilder currentExpression;
    private BooleanProperty isEditDisabledProperty;
    private BooleanProperty isSheetVersionSynced;
    private TimerTask sheetVersionsRefresher;
    private Timer timer;


    public HeaderController() {
        selectedCellProperty = new SimpleStringProperty();
        originalCellValueProperty = new SimpleStringProperty();
        lastUpdateVersionCellProperty = new SimpleStringProperty();
        versionSelectorComboBox = new ComboBox<>();
        themesComboBox = new ComboBox<>();
        isAnimationSelectedProperty = new SimpleBooleanProperty(false);
        themeManager = new ThemeManager();
        currentExpression = new StringBuilder();
        isEditDisabledProperty = new SimpleBooleanProperty(false);
        isSheetVersionSynced = new SimpleBooleanProperty(true);
    }

    @FXML
    private void initialize() {
        updateCellValueButton.disableProperty().bind(Bindings.or(selectedCellProperty.isNull(), isEditDisabledProperty));
        themesComboBox.getItems().addAll("Classic", "Pink", "Blue", "Dark");
        themesComboBox.setValue("Classic"); // Set default value
        selectedCellIDLabel.textProperty().bind(selectedCellProperty);
        originalCellValueTextField.promptTextProperty().bind(originalCellValueProperty);
        lastUpdateVersionCellLabel.textProperty().bind(lastUpdateVersionCellProperty);
        formatFunctionButton.disableProperty().bind(Bindings.or(selectedCellProperty.isNull(),isEditDisabledProperty));
        animationsCheckBox.disableProperty().bind(isEditDisabledProperty); //maybe shouldn't be
        themesComboBox.disableProperty().bind(isEditDisabledProperty); //maybe shouldn't be
        updateToLatestVersionButton.setVisible(false);
        updateToLatestVersionButton.visibleProperty().bind(isSheetVersionSynced.not());

        // Add listener for changes to the selectedCellProperty
        selectedCellProperty.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && !oldValue.equals(newValue)) {
                // Reset the style of the previously selected cell
                Label prevCellLabel = mainSheetController.getCellLabel(oldValue);
                if (prevCellLabel != null) {
                    prevCellLabel.setId(null); // Reset to previous style
                }
            }

            if (newValue != null) {
                // Apply style to the newly selected cell
                Label newCellLabel = mainSheetController.getCellLabel(newValue);
                originalCellValueTextField.clear();
                originalCellValueTextField.promptTextProperty().bind(originalCellValueProperty);
                if (newCellLabel != null) {
                    newCellLabel.setId("selected-cell"); // Apply selected-cell style
                }
            }
        });

    }

    public void setMainSheetController(MainSheetController mainSheetController) {
        this.mainSheetController = mainSheetController;
        mainSheetController.getThemeManager().setMainController(mainSheetController);
        userNameLabel.textProperty().bind(mainSheetController.getUserName());
    }

    public SimpleStringProperty getSelectedCellProperty(){ return selectedCellProperty; }

    public BooleanProperty isAnimationSelectedProperty() { return isAnimationSelectedProperty; }

    public BooleanProperty getIsSheetVersionSynced() { return isSheetVersionSynced; }

    public void displaySheet(String selectedSheet, Boolean loadSheetFromDashboard){
        if (selectedSheet==null ||selectedSheet.isEmpty()) {
            ShowAlert.showAlert("Error", "No sheet selected","Please choose a file from the available sheets table.", Alert.AlertType.ERROR);
        }

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(LOAD_SHEET)
                .newBuilder()
                .addQueryParameter("selectedSheet", selectedSheet)
                .build()
                .toString();


        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        ShowAlert.showAlert("Error","","Something went wrong: " + e.getMessage(), Alert.AlertType.WARNING)
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            ShowAlert.showAlert("Error","","Something went wrong: " + responseBody, Alert.AlertType.WARNING)
                    );
                } else {
                    Platform.runLater(() -> {
                        try {
                            String json = response.body().string();
                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(Coordinate.class, new CoordinateDeserializer())
                                    .create();
                            Type sheet = new TypeToken<DTOsheet>(){}.getType();
                            DTOsheet dtoSheet  = gson.fromJson(json, sheet);
                            mainSheetController.setCurrentDTOSheet(dtoSheet);

                            if(loadSheetFromDashboard){
                                mainSheetController.setSheet(dtoSheet, false);
                            } else{
                                mainSheetController.setSheet(dtoSheet, true);
                            }
                            selectedCellProperty.set("A1");
                            mainSheetController.selectedColumnProperty().set("A1".replaceAll("\\d", ""));
                            mainSheetController.selectedRowProperty().set("A1".replaceAll("[^\\d]", ""));
                            originalCellValueProperty.set(dtoSheet.getCell(1,1).getOriginalValue());
                            lastUpdateVersionCellProperty.set(String.valueOf(dtoSheet.getCell(1,1).getVersion()));
                            mainSheetController.populateRangeListView();
                            startSheetVersionsRefresher();
                        } catch (IOException e) {
                            ShowAlert.showAlert("Error","Failed to load the sheet window.","Something went wrong: " +  e.getMessage(), Alert.AlertType.WARNING);
                        }
                    });
                }
            }
        });
    }

    @FXML
    void themesComboBoxOnAction(ActionEvent event) {
        String selectedTheme = themesComboBox.getValue();
        mainSheetController.setSelectedTheme(selectedTheme);
        // Use ThemeManager to apply the selected theme
        mainSheetController.setTheme(lastUpdateVersionCellLabel.getScene());
    }

    @FXML
    void animationsCheckBoxOnAction(ActionEvent event) {
        boolean isSelected = animationsCheckBox.isSelected();
        isAnimationSelectedProperty.set(isSelected);
    }

    public void addClickEventForSelectedCell(Label label, String cellID, DTOcell dtoCell) {
        label.setOnMouseClicked(event -> {
            resetPreviousStyles();
            selectedCellProperty.set(cellID);
            animateSelectedCell(label);

            mainSheetController.selectedColumnProperty().set(cellID.replaceAll("\\d", ""));
            mainSheetController.selectedRowProperty().set(cellID.replaceAll("[^\\d]", ""));

            mainSheetController.resetColumnAlignmentComboBox();
            mainSheetController.resetColumnSlider();
            mainSheetController.resetRowSlider();

            originalCellValueProperty.set(dtoCell.getOriginalValue());
            lastUpdateVersionCellProperty.set(String.valueOf(dtoCell.getVersion()));
            mainSheetController.updateColorPickersWithCellStyles(label);

            List<String> dependsOn = dtoCell.getDependsOn();
            for (String dependsOnCellID : dependsOn) {
                mainSheetController.getCellLabel(dependsOnCellID).getStyleClass().add("depends-on-cell");
            }

            List<String> influencingOn = dtoCell.getInfluencingOn();
            for (String influencingCellID : influencingOn) {
                mainSheetController.getCellLabel(influencingCellID).getStyleClass().add("influence-on-cell");
            }

            lastHighlightedCells.clear();
            lastHighlightedCells.addAll(dependsOn);
            lastHighlightedCells.addAll(influencingOn);
        });
    }

    private void animateSelectedCell(Label label) {
        if(isAnimationSelectedProperty.get()) {

            ScaleTransition scaleTransition = new ScaleTransition();
            scaleTransition.setNode(label); // Set the label to animate
            scaleTransition.setDuration(Duration.millis(200)); // Duration of the animation
            scaleTransition.setFromX(1.0); // Starting scale (normal size)
            scaleTransition.setFromY(1.0);
            scaleTransition.setToX(1.12); // Scale factor (15% larger)
            scaleTransition.setToY(1.12);
            scaleTransition.setCycleCount(2); // 2 cycles: enlarge, then shrink
            scaleTransition.setAutoReverse(true); // Reverse to shrink back to original size

            // Set translation adjustments to mimic a pivot from the center
            double pivotX = label.getWidth() / 2;
            double pivotY = label.getHeight() / 2;

            // Apply translation before scaling to simulate pivot effect
            label.setTranslateX(-pivotX * 0.075); // Adjust X to pivot from center
            label.setTranslateY(-pivotY * 0.075); // Adjust Y to pivot from center

            // Play the animation
            scaleTransition.play();

            // After animation, reset the translation to ensure it returns to normal position
            scaleTransition.setOnFinished(e -> {
                label.setTranslateX(0);
                label.setTranslateY(0);
            });
        }
    }

    private void resetPreviousStyles() {
        // Reset styles for all previously highlighted cells
        for (String cellID : lastHighlightedCells) {
            Label cellLabel = mainSheetController.getCellLabel(cellID);
            cellLabel.getStyleClass().removeAll("depends-on-cell", "influence-on-cell");
        }
        // Clear the list after resetting
        lastHighlightedCells.clear();
    }

    @FXML
    void updateCellValueButtonAction(ActionEvent event) {
        // Validate that a cell is selected before proceeding
        String selectedCellID = selectedCellProperty.get();
        if (selectedCellID == null || selectedCellID.isEmpty()) {
            ShowAlert.showAlert("Error", "No Cell Selected", "Please select a cell before editing.", Alert.AlertType.ERROR);
            return;
        }

        // Get the new value to update
        String newValue = originalCellValueTextField.getText();

        // Check for the latest version and handle user decision
        fetchNumOfLatestSheetVersion(
            latestVersion -> {
                if(mainSheetController.getCurrentDTOSheet().getVersion() != latestVersion)
                    handleDifferentVersionsSheetPopup();
                else{
                    updateCellValue(selectedCellID, newValue);
                }
            },
            errorMessage -> Platform.runLater(() -> ShowAlert.showAlert("Error", "Update Failed", "Error: " + errorMessage, Alert.AlertType.ERROR))
        );
    }

    public void updateCellValue(String cellID, String newValue) {
        String updateCellUrl = UPDATE_CELL; // Replace with your endpoint URL

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("selectedSheet", mainSheetController.getSheetName());
        jsonBody.addProperty("cellID", cellID);
        jsonBody.addProperty("newValue", newValue);

        RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(updateCellUrl)
                .put(body)
                .build();

        HttpClientUtil.runAsyncPut(updateCellUrl, request, new Callback(){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> ShowAlert.showAlert("Error", "Update Failed", "Failed to update cell: " + e.getMessage(), Alert.AlertType.ERROR));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 200) {
                    try {
                        String json = response.body().string();
                        Gson gson = new GsonBuilder()
                                .registerTypeAdapter(Coordinate.class, new CoordinateDeserializer())
                                .create();
                        Type sheet = new TypeToken<DTOsheet>() {}.getType();
                        DTOsheet dtoSheet = gson.fromJson(json, sheet);
                        Platform.runLater(() -> {
                            mainSheetController.setCurrentDTOSheet(dtoSheet);
                            originalCellValueProperty.set(newValue);
                            mainSheetController.setSheet(dtoSheet, true);
                            lastUpdateVersionCellProperty.set(String.valueOf(dtoSheet.getCell(cellID).getVersion()));

                            mainSheetController.getCellLabel(cellID).setId(null);
                            mainSheetController.getCellLabel(cellID).setId("selected-cell");

                            originalCellValueTextField.clear();
                            originalCellValueTextField.promptTextProperty().bind(originalCellValueProperty);
                        });
                    }catch (IOException e) {
                        Platform.runLater(() -> ShowAlert.showAlert("Error", "Update Failed", "Error: " + e.getMessage(), Alert.AlertType.ERROR));
                    }
                }
                else {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> ShowAlert.showAlert("Error", "Update Failed", "Error: " + responseBody, Alert.AlertType.ERROR));
                }
                response.close();
            }
        });
    }

    @FXML
    void formatFunctionButtonOnAction(ActionEvent event) {
        String selectedCellID = selectedCellProperty.get();

        // Show pop-up window to allow user to edit cell value
        UpdateCellFormat updateCellFormat = new UpdateCellFormat(mainSheetController.getCurrentDTOSheet().getCell(selectedCellID),
                selectedCellID, mainSheetController ,originalCellValueProperty , lastUpdateVersionCellProperty);
        updateCellFormat.display();
    }

    public void populateVersionSelector() {
        String numVersionsUrl = HttpUrl
                .parse(GET_NUM_VERSIONS)
                .newBuilder()
                .addQueryParameter("selectedSheet", mainSheetController.getSheetName())
                .build()
                .toString();

        HttpClientUtil.runAsync(numVersionsUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        ShowAlert.showAlert("Error", "", "Request failed: " + e.getMessage(), Alert.AlertType.WARNING)
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            ShowAlert.showAlert("Error", "", "Failed to fetch versions: " + responseBody, Alert.AlertType.WARNING)
                    );
                } else {
                    Platform.runLater(() -> {
                        try {
                            String json = response.body().string();
                            Gson gson = new Gson();
                            Type versionListType = new TypeToken<List<String>>() {}.getType();
                            List<String> versions = gson.fromJson(json, versionListType);
                            ObservableList<String> availableVersions = FXCollections.observableArrayList(versions);
                            versionSelectorComboBox.setItems(availableVersions);

                        } catch (IOException e) {
                            ShowAlert.showAlert("Error", "Failed to parse the version list.", "Something went wrong: " + e.getMessage(), Alert.AlertType.WARNING);
                        }
                    });
                }
            }
        });
    }

    @FXML
    void versionSelectorComboBoxAction(ActionEvent event) {
        String selectedVersion = versionSelectorComboBox.getSelectionModel().getSelectedItem();

        if (selectedVersion == null || selectedVersion.isEmpty()) {
            populateVersionSelector();
            return;
        }

        String finalUrl = HttpUrl
                .parse(GET_DTO_SHEET_VERSION)
                .newBuilder()
                .addQueryParameter("selectedVersion", selectedVersion)
                .addQueryParameter("selectedSheet", mainSheetController.getSheetName())
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        ShowAlert.showAlert("Error", "", "Request failed: " + e.getMessage(), Alert.AlertType.WARNING)
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            ShowAlert.showAlert("Error", "", "Failed to fetch version details: " + responseBody, Alert.AlertType.WARNING)
                    );
                } else {
                    try {
                        String json = response.body().string();
                        Gson gson = new GsonBuilder()
                                .registerTypeAdapter(Coordinate.class, new CoordinateDeserializer())
                                .create();
                        Type sheetType = new TypeToken<DTOsheet>() {}.getType();
                        DTOsheet dtoSheet = gson.fromJson(json, sheetType);

                        Platform.runLater(() ->{
                            mainSheetController.displaySheetVersionInPopup(dtoSheet);
                        });

                    } catch (IOException e) {
                        ShowAlert.showAlert("Error", "Failed to parse the version details.", "Something went wrong: " + e.getMessage(), Alert.AlertType.WARNING);
                    }
                }
            }
        });

        Platform.runLater(() -> {
            if (versionSelectorComboBox.getItems().size() > 0) {
                versionSelectorComboBox.getSelectionModel().clearSelection();
                versionSelectorComboBox.setPromptText("Version Selector");
            }
        });
    }

    @FXML
    void backButtonOnAction(ActionEvent event) {
        try {
            // Find the main scroll pane in the current scene
            ScrollPane mainScrollPane = (ScrollPane) ((Node) event.getSource()).getScene().lookup("#dashboardScrollPane");
            stopSheetVersionsRefresher();

            if (mainScrollPane != null) {
                System.out.println("mainScrollPane found.");
                BorderPane dashboardBorderPane = mainSheetController.getDashboardController().getDashboardBorderPane();

                if (dashboardBorderPane != null) {
                    // Set the existing dashboard BorderPane as the content
                    mainScrollPane.setContent(dashboardBorderPane);
                    System.out.println("Dashboard content switched successfully.");
                } else {
                    System.out.println("dashboardBorderPane is null.");
                }
            } else {
                System.out.println("mainScrollPane not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            ShowAlert.showAlert("Error", "Failed to display the dashboard", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void disableEditFeatures() {
        isEditDisabledProperty.set(true);
    }

    private void handleDifferentVersionsSheetPopup() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Version Mismatch");
            alert.setHeaderText("A New Version of the Sheet is Available");
            alert.setContentText("You cannot make updates to this sheet as a newer version is available. Please retrieve the latest version before proceeding with any changes.");

            ButtonType retrieveButton = new ButtonType("Retrieve Latest Version");
            ButtonType cancelButton = new ButtonType("Cancel");

            alert.getButtonTypes().setAll(retrieveButton, cancelButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == retrieveButton) {
                mainSheetController.displaySheet(false);
            } else {
                disableEditFeatures();
                System.out.println("Operation canceled by the user.");
            }
        });
    }

    public void fetchNumOfLatestSheetVersion(Consumer<Integer> onSuccess, Consumer<String> onError){
        String url = HttpUrl
                .parse(GET_NUM_LATEST_SHEET_VERSION)
                .newBuilder()
                .addQueryParameter(SELECTED_SHEET_NAME, mainSheetController.getSheetName())
                .build()
                .toString();

        HttpClientUtil.runAsync(url ,new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onError.accept("Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    int latestVersion = GSON_INSTANCE.fromJson(response.body().string(), Integer.class);
                    onSuccess.accept(latestVersion);
                } else {
                    onError.accept("Error: " + response.code() + " - " + response.message());
                }
            }
        });
    }

    @FXML
    void updateToLatestVersionButton(ActionEvent event) {
        mainSheetController.displaySheet(false);
        isEditDisabledProperty.set(false);
    }

    private void startSheetVersionsRefresher() {
        sheetVersionsRefresher = new SheetVersionsRefresher(this);
        timer = new Timer();
        timer.schedule(sheetVersionsRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    public int getLastKnownVersion(){
        return mainSheetController.getCurrentDTOSheet().getVersion();
    }


    public void stopSheetVersionsRefresher() throws IOException {
        if (sheetVersionsRefresher != null && timer != null) {
            sheetVersionsRefresher.cancel();
            timer.cancel();
        }
    }

    public void close() {
        if (sheetVersionsRefresher != null && timer != null) {
            sheetVersionsRefresher.cancel();
            timer.cancel();
        }
    }
}