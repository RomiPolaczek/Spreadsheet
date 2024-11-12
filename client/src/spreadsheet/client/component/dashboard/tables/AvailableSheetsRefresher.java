package spreadsheet.client.component.dashboard.tables;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.DTOsheetTableDetails;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import spreadsheet.client.util.Constants;
import spreadsheet.client.util.ShowAlert;
import spreadsheet.client.util.http.HttpClientUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

public class AvailableSheetsRefresher extends TimerTask {
    private final Consumer<List<DTOsheetTableDetails>> availableSheetsList;
    private List<DTOsheetTableDetails> lastFetchedList;// To store last fetched list

    public AvailableSheetsRefresher(Consumer<List<DTOsheetTableDetails>> availableSheetsList) {
         this.availableSheetsList = availableSheetsList;
         lastFetchedList = new ArrayList<>();
    }

    @Override
    public void run() {

        HttpClientUtil.runAsync(Constants.GET_SHEET_DETAILS, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Handle failure
                Platform.runLater(() -> {
                    ShowAlert.showAlert("Error", "Failed to sheets details: ", e.getMessage(), Alert.AlertType.ERROR);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<DTOsheetTableDetails>>(){}.getType();
                    List<DTOsheetTableDetails> sheetsDetailsList = gson.fromJson(json, listType);
                    if (hasListChanged(sheetsDetailsList)) {
                        lastFetchedList = new ArrayList<>(sheetsDetailsList); // Update the last fetched list
                        availableSheetsList.accept(sheetsDetailsList); // Notify consumer to update table
                    }
                } else {
                    System.err.println("Request failed: " + response.code());
                    Platform.runLater(() -> {
                        ShowAlert.showAlert("Error", "Failed to load sheets details: ", response.message(), Alert.AlertType.ERROR);
                    });
                }
                response.close();
            }
        });
    }

    private boolean hasListChanged(List<DTOsheetTableDetails> newList) {
        if (lastFetchedList.size() != newList.size()) {
            return true; // If the size is different, the list has changed
        }

        // Compare each item in the lists
        for (int i = 0; i < lastFetchedList.size(); i++) {
            if (!lastFetchedList.get(i).equals(newList.get(i))) {
                return true; // If any item is different, the list has changed
            }
        }

        return false; // No changes
    }
}
