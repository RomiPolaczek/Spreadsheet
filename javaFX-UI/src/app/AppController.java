package app;

import api.Engine;
import dto.DTOcell;
import dto.DTOsheet;
import header.HeaderController;
import impl.EngineImpl;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import left.LeftController;
import sheet.SheetController;
import theme.ThemeManager;

import java.util.List;
import java.util.Map;

public class AppController {

    @FXML
    private ScrollPane headerComponent;
    @FXML
    private HeaderController headerComponentController;
    @FXML
    private ScrollPane sheetComponent;
    @FXML
    private SheetController sheetComponentController;
    @FXML
    private ScrollPane leftComponent;
    @FXML
    private LeftController leftComponentController;

    private Engine engine;
    private String selectedTheme = "Classic";
    private ThemeManager themeManager;


    @FXML
    public void initialize() {
        engine = new EngineImpl();
        themeManager = new ThemeManager();

        if (headerComponentController != null && sheetComponentController != null && leftComponentController != null) {
            headerComponentController.setMainController(this);
            sheetComponentController.setMainController(this);
            leftComponentController.setMainController(this);
        }
    }

    // Utility method to show alerts
    public void showAlert(String title, String header, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setSelectedTheme(String selectedTheme) {
        this.selectedTheme = selectedTheme;
    }

    public String getSelectedTheme() {
        return selectedTheme;
    }

    public ThemeManager getThemeManager(){ return themeManager; }


    public void setSheet(DTOsheet dtoSheet, Boolean applyCustomStyles) {
        sheetComponentController.setSheet(dtoSheet, applyCustomStyles);
    }

    public void displaySheetVersionInPopup(DTOsheet dtoSheet) {
        sheetComponentController.displaySheetVersionInPopup(dtoSheet);
    }

    public void displayFilteredSheetInPopup(DTOsheet dtoSheet) {
        sheetComponentController.displayFilteredSheetInPopup(dtoSheet);
    }

    public void displaySortedSheetInPopup(DTOsheet dtoSheet) {
        sheetComponentController.displaySortedSheetInPopup(dtoSheet);
    }



    public SimpleStringProperty getSelectedCellProperty() {
        return headerComponentController.getSelectedCellProperty();
    }

    public Label getCellLabel(String cellID) {
        return sheetComponentController.getCellLabel(cellID);
    }

    public Engine getEngine() {
        return engine;
    }

    public void populateVersionSelector() {
        headerComponentController.populateVersionSelector();
    }

    public SimpleStringProperty selectedColumnProperty() {
        return leftComponentController.selectedColumnProperty();
    }

    public SimpleStringProperty selectedRowProperty() {
        return leftComponentController.selectedRowProperty();
    }

    public void addClickEventForSelectedCell(Label label, String cellID, DTOcell dtoCell) {
        headerComponentController.addClickEventForSelectedCell(label, cellID, dtoCell);
    }

    public void addClickEventForSelectedColumn(Label label) {
        leftComponentController.addClickEventForSelectedColumn(label);
    }

    public void addClickEventForSelectedRow(Label label) {
        leftComponentController.addClickEventForSelectedRow(label);
    }

    public List<Label> getAllCellLabelsInColumn(String column) {
        return sheetComponentController.getAllCellLabelsInColumn(column);
    }

    public void resetColumnAlignmentComboBox() {
        leftComponentController.resetColumnAlignmentComboBox();
    }

    public SimpleBooleanProperty isFileSelectedProperty() {
        return headerComponentController.isFileSelectedProperty();
    }

    public void initializeCommandAndRangeControllers() {
        leftComponentController.initializeCommandAndRangeControllers();
    }

//    public Map<String, String> getCellStyles() {
//        return sheetComponentController.getCellStyles();
//    }

    public void updateColorPickersWithCellStyles(Label cell) {
        leftComponentController.updateColorPickersWithCellStyles(cell);
    }

    public void setColumnAlignment(String column, Pos alignment) {
        sheetComponentController.setColumnAlignment(column, alignment);
    }

    public void setColumnWidth(String column, Integer newWidth) {
        sheetComponentController.setColumnWidth(column, newWidth);
    }

    public int getColumnWidth(String column) {
        return sheetComponentController.getColumnWidth(column);
    }

    public void setRowHeight(String row, Integer newHeight) {
        sheetComponentController.setRowHeight(row, newHeight);
    }

    public int getRowHeight(String row) {
        return sheetComponentController.getRowHeight(row);
    }

    public void resetColumnSlider() {
        leftComponentController.resetColumnSlider();
    }

    public void resetRowSlider() {
        leftComponentController.resetRowSlider();
    }

    public ColumnConstraints getColumnConstraintsByColumn(String column) {
        return sheetComponentController.getColumnConstraintsByColumn(column);
    }

    public RowConstraints getRowConstraintsByRow(String row) {
        return sheetComponentController.getRowConstraintsByRow(row);
    }

    public void populateRangeListView() {
        leftComponentController.populateRangeListView();
    }

    public void highlightColumn(String column) {
        sheetComponentController.highlightColumn(column);
    }

    public Boolean isAnimationSelectedProperty() {
        return headerComponentController.isAnimationSelectedProperty();
    }

    public void setSheetStyle(Scene scene, String theme) {
        sheetComponentController.setSheetStyle(scene, theme);
    }

    public void setTheme(Scene scene) {
        themeManager.applyTheme(scene, selectedTheme);
    }

    public void updateCellValue(String cellID, String newValue) {
        headerComponentController.updateCellValue(cellID, newValue);
    }
  
    public Map<String,String> getNewCoordToOldCoord() {return leftComponentController.getNewCoordToOldCoord(); }
}
