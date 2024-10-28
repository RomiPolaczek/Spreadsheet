package spreadsheet.client.component.dashboard.tables;

import com.google.gson.reflect.TypeToken;
import dto.DTOsheetTableDetails;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import spreadsheet.client.component.dashboard.DashboardController;
import spreadsheet.client.util.Constants;
import spreadsheet.client.util.Constants.*;
import spreadsheet.client.util.http.HttpClientUtil;
import com.google.gson.Gson;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class TabelsController {

    @FXML
    private TableView<DTOsheetTableDetails> availableSheetsTable;

    @FXML
    private TableView<DTOsheetTableDetails> permissionsTable;

    @FXML
    private TableColumn<DTOsheetTableDetails, String> ownerColumn;

    @FXML
    private TableColumn<DTOsheetTableDetails, String> permissionColumn;

    @FXML
    private TableColumn<DTOsheetTableDetails, String> sheetNameColumn;

    @FXML
    private TableColumn<DTOsheetTableDetails, String> sizeColumn;

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    public void initialize() {
//        availableSheetsTable = new TableView<>();
        availableSheetsTable.setItems(FXCollections.observableArrayList()); // Initialize items list
        // Listener to handle row click events
        availableSheetsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                String selectedSheet = newSelection.getSheetName();
                dashboardController.setSelectedSheet(selectedSheet);
            }
        });
        setupTableColumns();
//        fetchSheetDetails();
    }

    private void setupTableColumns() {
        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("owner"));
        permissionColumn.setCellValueFactory(new PropertyValueFactory<>("permission"));
        sheetNameColumn.setCellValueFactory(new PropertyValueFactory<>("sheetName"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
    }

    public void fetchSheetDetails() {

        String url = HttpUrl
                .parse(Constants.GET_SHEET_DETAILS)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Handle failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<DTOsheetTableDetails>>(){}.getType();
                    List<DTOsheetTableDetails> sheetsDetailsList = gson.fromJson(json, listType);

                    Platform.runLater(() -> {
                        availableSheetsTable.getItems().setAll(sheetsDetailsList);
                    });
                } else {
                    System.err.println("Request failed: " + response.code());
                }
                response.close();
            }
        });
    }




}
