package theme;

import app.AppController;
import javafx.scene.Scene;

public class ThemeManager {
    private AppController mainController;

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void applyTheme(Scene scene, String themeFileName) {
        try {
            String css = getClass().getResource("/theme/styles/" + themeFileName + ".css").toExternalForm();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(css);
            mainController.setSheetStyle(scene, themeFileName);
        }
        catch (Exception e) {
        }
    }
}

