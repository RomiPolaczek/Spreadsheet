package spreadsheet.client.theme;


import spreadsheet.client.component.dashboard.DashboardController;
import spreadsheet.client.component.mainSheet.MainSheetController;
import javafx.scene.Scene;

public class ThemeManager {
    private DashboardController dashboardController;

    public ThemeManager(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

//    public void setDashboardController(DashboardController dashboardController) {
//        this.dashboardController = dashboardController;
//    }

    public void applyTheme(Scene scene, String themeFileName) {
        try {
            String css = getClass().getResource("/spreadsheet/client/theme/styles/" + themeFileName + ".css").toExternalForm();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(css);
//            dashboardController.setSheetStyle(scene, themeFileName);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

