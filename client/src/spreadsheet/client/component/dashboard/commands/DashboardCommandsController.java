package spreadsheet.client.component.dashboard.commands;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import spreadsheet.client.component.dashboard.DashboardController;

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

    }

}
