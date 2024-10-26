package spreadsheet.client.theme;


import spreadsheet.client.component.main.MainSheetController;
import javafx.scene.Scene;

public class ThemeManager {
    private MainSheetController mainController;

    public void setMainController(MainSheetController mainController) {
        this.mainController = mainController;
    }

    public void applyTheme(Scene scene, String themeFileName) {
        try {
            String css = getClass().getResource("/spreadsheet/client/theme/styles/" + themeFileName + ".css").toExternalForm();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(css);
            mainController.setSheetStyle(scene, themeFileName);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

