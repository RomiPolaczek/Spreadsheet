package left;

import app.AppController;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import left.command.CommandController;

public class LeftController {
    @FXML private VBox commandComponent;
    @FXML private CommandController commandComponentController;
    private AppController mainController;


    public void setMainController(AppController mainController) {
        this.mainController = mainController;
        // If the commandComponentController is already initialized, set the mainController there too
        if (commandComponentController != null) {
            commandComponentController.setMainController(mainController);
        }
    }

    public SimpleStringProperty selectedColumnProperty() { return commandComponentController.selectedColumnProperty(); }

    public void addClickEventForSelectedColumn(Label label) { commandComponentController.addClickEventForSelectedColumn(label);}

}
