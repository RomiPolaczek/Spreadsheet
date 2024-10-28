package spreadsheet.client.component.dashboard.commands;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import spreadsheet.client.component.dashboard.DashboardController;
import spreadsheet.client.component.mainSheet.MainSheetController;

import java.io.IOException;

import static spreadsheet.client.util.Constants.MAIN_SHEET_PAGE_FXML_RESOURCE_LOCATION;

public class DashboardCommandsController {

    @FXML
    private Button askDenyPermissionRequestButton;

    @FXML
    private Button requestPermissionButton;

    @FXML
    private Button viewSheetButton;

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;

    }

    @FXML
    void askDenyPermissionRequestButtonOnAction(ActionEvent event) {

    }

    @FXML
    void requestPermissionButtonOnAction(ActionEvent event) {

    }

    @FXML
    void viewSheetButtonOnAction(ActionEvent event) {
        try {
            // Load the FXML file for MainSheetController (mainSheet.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_SHEET_PAGE_FXML_RESOURCE_LOCATION));
            BorderPane mainSheetRoot = loader.load();

            // Get the MainSheetController instance and initialize it if needed
            MainSheetController mainSheetController = loader.getController();
            mainSheetController.initialize();  // Ensures any required setup

            // Find the ScrollPane in the dashboard scene
            ScrollPane dashboardScrollPane = (ScrollPane) ((Node) event.getSource()).getScene().lookup("#dashboardScrollPane");

            // Set the content of the ScrollPane to the new root component
            dashboardScrollPane.setContent(mainSheetRoot);

            // Optionally apply the selected theme or any other settings
            mainSheetController.setTheme(dashboardScrollPane.getScene());
            dashboardController.setMainSheetController(mainSheetController);
            dashboardController.viewSheet();

        } catch (IOException e) {
            e.printStackTrace();
//            showAlert("Error", "Failed to load sheet view", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

}
