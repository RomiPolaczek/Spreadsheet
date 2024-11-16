package spreadsheet.client.component.mainSheet.header;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import spreadsheet.client.util.ShowAlert;
import java.util.TimerTask;

public class SheetVersionsRefresher extends TimerTask {
    private HeaderController headerController;

    public SheetVersionsRefresher(HeaderController headerController) {
        this.headerController = headerController;
    }

    @Override
    public void run() {
        headerController.fetchNumOfLatestSheetVersion(
                latestVersion -> {
                    if(headerController.getLastKnownVersion() != latestVersion){
                        headerController.getIsSheetVersionSynced().set(false);
                        headerController.disableEditFeatures();
                    }
                    else{
                        headerController.getIsSheetVersionSynced().set(true);
                    }
                },
                errorMessage -> Platform.runLater(() -> ShowAlert.showAlert("Error", "Sheet Version Refresher Error", "Error: " + errorMessage, Alert.AlertType.ERROR))
        );
    }
}
