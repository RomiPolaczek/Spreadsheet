package theme;

import javafx.scene.Scene;

public class ThemeManager {

    public ThemeManager() {}

    public void applyTheme(Scene scene, String themeFileName) {
        String css = getClass().getResource("/theme/styles/" + themeFileName + ".css").toExternalForm();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(css);
    }
}

