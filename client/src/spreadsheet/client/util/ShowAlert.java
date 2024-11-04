package spreadsheet.client.util;

public class ShowAlert {

    // Utility method to show alerts
    public static void showAlert(String title, String header, String message, javafx.scene.control.Alert.AlertType type) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
