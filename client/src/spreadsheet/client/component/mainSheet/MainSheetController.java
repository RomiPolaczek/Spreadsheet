package spreadsheet.client.component.mainSheet;

import api.Engine;
import dto.DTOcell;
import dto.DTOsheet;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import spreadsheet.client.component.dashboard.DashboardController;
import spreadsheet.client.component.mainSheet.header.HeaderController;
import impl.EngineImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import spreadsheet.client.component.mainSheet.left.LeftController;
import spreadsheet.client.component.mainSheet.sheet.SheetController;
import spreadsheet.client.theme.ThemeManager;

import java.util.List;
import java.util.Map;

public class MainSheetController {

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
    private DashboardController dashboardController;
    private String selectedTheme = "Classic";
    private ThemeManager themeManager;
    private String sheetName;
    private DTOsheet currentDTOSheet;
    private BooleanProperty isEditDisabledProperty;


    @FXML
    public void initialize(String sheetName, DashboardController dashboardController  /*SimpleStringProperty userName*/) {
        engine = new EngineImpl();
        themeManager = new ThemeManager();
        if (dashboardController != null) {
            this.dashboardController = dashboardController;
        }

        if (headerComponentController != null && sheetComponentController != null && leftComponentController != null) {
            headerComponentController.setMainSheetController(this);
            sheetComponentController.setMainController(this);
            leftComponentController.setMainSheetController(this);
            this.sheetName = sheetName;
            isEditDisabledProperty = new SimpleBooleanProperty(false);
        }
    }

    public DashboardController getDashboardController() {
        return dashboardController;
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    public void setCurrentDTOSheet(DTOsheet currentDTOSheet) {
        this.currentDTOSheet = currentDTOSheet;
    }

    public DTOsheet getCurrentDTOSheet() {
        return currentDTOSheet;
    }

    public void setSelectedTheme(String selectedTheme) {
        this.selectedTheme = selectedTheme;
    }

    public String getSelectedTheme() {
        return selectedTheme;
    }

    public ThemeManager getThemeManager() {
        return themeManager;
    }


    public void setSheet(DTOsheet dtoSheet, Boolean applyCustomStyles) {
        sheetComponentController.setSheet(dtoSheet, applyCustomStyles);
    }

    public void displaySheetVersionInPopup(DTOsheet dtoSheet) {
        sheetComponentController.displaySheetVersionInPopup(dtoSheet);
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

    public String getSheetName() {return sheetName; }

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


    public Map<String, String> getCellStyles() {
        return sheetComponentController.getCellStyles();
    }

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
        return headerComponentController.isAnimationSelectedProperty().getValue();
    }

    public void setSheetStyle(Scene scene, String theme) {
        sheetComponentController.setSheetStyle(scene, theme);
    }

    public void setTheme(Scene scene) {
        themeManager.applyTheme(scene, selectedTheme);
    }

    public Map<String, String> getNewCoordToOldCoord() {
        return leftComponentController.getNewCoordToOldCoord();
    }

    public void displayFilteredSortedSheetInPopup(DTOsheet dtoSheet, String title, String range) {
//        sheetComponentController.displayFilteredSortedSheetInPopup(dtoSheet, title, range);
    }

    public void updateCellValue(String cellID, String newValue) {
        headerComponentController.updateCellValue(cellID, newValue);
    }

    public void displaySheet(Boolean loadSheetFromDashboard){
        headerComponentController.displaySheet(sheetName, loadSheetFromDashboard);
    }


    public SimpleStringProperty getUserName(){
        return dashboardController.getUserName();
    }


//    public void disableEditFeatures() {
//        if(headerComponentController!=null)
//            headerComponentController.disableEditFeatures();
//        if(leftComponentController!=null)
//            leftComponentController.disableEditFeatures();
////        if(sheetComponentController!=null)
////            sheetComponentController.disableEditFeatures();
//    }

    public void disableEditFeatures() {
        isEditDisabledProperty.set(true);
        headerComponentController.disableEditFeatures();
        leftComponentController.disableEditFeatures();
    }





//    public void showMainWindow(){
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("client/src/spreadsheet/client/component/main/mainSheet.fxml"));
//            Parent root = loader.load();
//            Stage mainStage = new Stage();
//            mainStage.setScene(new Scene(root));
//            mainStage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
