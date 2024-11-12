package spreadsheet.client.component.dashboard.tables;

import com.google.gson.reflect.TypeToken;
import dto.DTOpermissionRequest;
import dto.DTOsheetTableDetails;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import spreadsheet.client.component.dashboard.DashboardController;
import spreadsheet.client.util.Constants;
import spreadsheet.client.util.ShowAlert;
import spreadsheet.client.util.http.HttpClientUtil;
import com.google.gson.Gson;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static spreadsheet.client.util.Constants.REFRESH_RATE;

public class TabelsController {

    @FXML
    private TableView<DTOsheetTableDetails> availableSheetsTable;

    @FXML
    private TableView<DTOpermissionRequest> permissionsTable;

    @FXML
    private TableColumn<DTOsheetTableDetails, String> ownerColumn;

    @FXML
    private TableColumn<DTOsheetTableDetails, String> sheetPermissionColumn;

    @FXML
    private TableColumn<DTOsheetTableDetails, String> sheetNameColumn;

    @FXML
    private TableColumn<DTOsheetTableDetails, String> sizeColumn;

    @FXML
    private TableColumn<DTOpermissionRequest, String> userNameColumn;

    @FXML
    private TableColumn<DTOpermissionRequest, String> requestedPermissionColumn;

    @FXML
    private TableColumn<DTOpermissionRequest, String> requestStatusColumn;

    private DashboardController dashboardController;
    private Timer timer;
    private TimerTask availableSheetsRefresher;



    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
        startAvailableSheetsTableRefresher();
    }

    @FXML
    public void initialize() {
        availableSheetsTable.setItems(FXCollections.observableArrayList()); // Initialize items list
        permissionsTable.setItems(FXCollections.observableArrayList());
        setupAvailableSheetsTableColumns();
        setupPermissionTableColumns();
        // Listener to handle row click events
        availableSheetsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                String selectedSheet = newSelection.getSheetName();
                dashboardController.setSelectedSheet(selectedSheet);
                fetchPermissionTableDetails(selectedSheet);
                //dashboardController.getUserPermissionAndDisableIfNecessary(dashboardController.getSelectedSheet().get(), dashboardController.getUserName().get());

            }
        });

        permissionsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                String selectedRequest = newSelection.getUserName();
                dashboardController.setSelectedRequestUserName(selectedRequest);
                dashboardController.getDashboardCommandsComponentController().setApproveAndRejectButtons();
            }
        });
//        fetchSheetDetails();
    }

    private void setupAvailableSheetsTableColumns() {
        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("owner"));
        sheetPermissionColumn.setCellValueFactory(new PropertyValueFactory<>("permission"));
        sheetNameColumn.setCellValueFactory(new PropertyValueFactory<>("sheetName"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
    }

    private void setupPermissionTableColumns() {
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        requestedPermissionColumn.setCellValueFactory(new PropertyValueFactory<>("requestedPermissionType"));
        requestStatusColumn.setCellValueFactory(new PropertyValueFactory<>("requestPermissionStatus"));
    }

    public String getSelectedSheetOwnerName() {
        DTOsheetTableDetails selectedSheet = availableSheetsTable.getSelectionModel().getSelectedItem(); // Get the selected item

        if (selectedSheet != null) {
            return selectedSheet.getOwner(); // Return the sheet name of the selected item
        }

        return null; // Return null if no row is selected
    }

    public String getSelectedSheetPermissionType() {
        DTOsheetTableDetails selectedSheet = availableSheetsTable.getSelectionModel().getSelectedItem(); // Get the selected item

        if (selectedSheet != null) {
            return selectedSheet.getPermission().getPermission(); // Return the sheet name of the selected item
        }

        return null; // Return null if no row is selected
    }


    public DTOpermissionRequest getSelectedRequest() {
        return permissionsTable.getSelectionModel().getSelectedItem();
    }

    public void startAvailableSheetsTableRefresher() {
        availableSheetsRefresher = new AvailableSheetsRefresher(this::updateSheetTableAvailableDetails);
        timer = new Timer();
        timer.schedule(availableSheetsRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    public void updateSheetTableAvailableDetails(List<DTOsheetTableDetails> sheetsDetailsList) {

        availableSheetsTable.getItems().setAll(sheetsDetailsList);
    }

//    public void fetchSheetTableDetails() {
//        String url = HttpUrl
//                .parse(Constants.GET_SHEET_DETAILS)
//                .newBuilder()
//                .build()
//                .toString();
//
//        HttpClientUtil.runAsync(url, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//                // Handle failure
//                //POP UP ALERT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    String json = response.body().string();
//                    Gson gson = new Gson();
//                    Type listType = new TypeToken<List<DTOsheetTableDetails>>(){}.getType();
//                    List<DTOsheetTableDetails> sheetsDetailsList = gson.fromJson(json, listType);
//
//                    Platform.runLater(() -> {
//                        availableSheetsTable.getItems().setAll(sheetsDetailsList);
//                    });
//                } else {
//                    System.err.println("Request failed: " + response.code());
//                    //POP UP ALERT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//
//                }
//                response.close();
//            }
//        });
//    }

    public void fetchPermissionTableDetails(String sheetName) {
        String url = HttpUrl
                .parse(Constants.GET_PERMISSION_TABLE_DETAILS)
                .newBuilder()
                .addQueryParameter("selectedSheet", sheetName) // Pass the selected sheet name if required
                .build()
                .toString();

        HttpClientUtil.runAsync(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //e.printStackTrace();
                // Display a popup alert for failure
                Platform.runLater(() -> {
                    ShowAlert.showAlert("Error", "Failed to load permissions: ", e.getMessage(), Alert.AlertType.ERROR);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<DTOpermissionRequest>>(){}.getType();
                    List<DTOpermissionRequest> permissionsDetailsList = gson.fromJson(json, listType);

                    Platform.runLater(() -> {
                        permissionsTable.getItems().setAll(permissionsDetailsList);
                    });
                } else {
                    Platform.runLater(() -> {
                        ShowAlert.showAlert("Error", "Failed to load permissions: ", response.message(), Alert.AlertType.ERROR);
                    });
                }
                response.close();
            }
        });
    }

}
