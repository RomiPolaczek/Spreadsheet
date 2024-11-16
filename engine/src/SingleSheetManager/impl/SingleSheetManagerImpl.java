package SingleSheetManager.impl;

import SingleSheetManager.api.SingleSheetManager;
import dto.DTOcell;
import dto.DTOlayout;
import dto.DTOrange;
import dto.DTOsheet;
import exception.InvalidFileFormatException;
import expression.parser.Operation;
import impl.EngineImpl;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import sheet.api.EffectiveValue;
import permissions.PermissionManager;
import permissions.PermissionStatus;
import permissions.PermissionType;
import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.impl.CoordinateFactory;
import sheet.coordinate.impl.CoordinateImpl;
import sheet.impl.SheetImpl;
import sheet.layout.api.Layout;
import sheet.layout.impl.LayoutImpl;
import sheet.range.Range;
import xmlGenerated.STLCell;
import xmlGenerated.STLRange;
import xmlGenerated.STLSheet;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleSheetManagerImpl implements SingleSheetManager, Serializable {
    private Sheet sheet;
    private STLSheet stlSheet;
    private String owner;
    private VersionManager versionManager;
    private PermissionManager permissionManager;

    public SingleSheetManagerImpl(String owner) {
        this.owner = owner;
        versionManager = new VersionManager();
        permissionManager = new PermissionManager(owner);
    }

    @Override
    public void LoadFile(InputStream inputStream, String owner) throws Exception {
       // File newFile = checkFileValidation(inputStream);
        fromXmlFileToObject(inputStream);
        fromStlSheetToOurSheet(owner);
        this.owner = owner;
        permissionManager.addOwnerToPermissions(owner);
       // file = newFile;
    }

    @Override
    public Sheet getSheet() {
        return sheet;
    }

    @Override
    public String getOwner()
    { return owner; }

    @Override
    public PermissionManager getPermissionManager(){
        return permissionManager;
    }

    @Override
    public int getNumberOfVersions() {
        return versionManager.getVersionToChanges().size();
    }
//
//    @Override
//    public int getChangesAccordingToVersionNumber(int version) {
//        return versionManager.getVersionToChanges().get(version);
//    }
//
//    @Override
//    public File getFile() {
//        return file;
//    }

//    private File checkFileValidation(InputStream inputStream) throws Exception {
//        File newFile = new File(fileName);
//
//        if (!newFile.exists()) {
//            throw new FileNotFoundException("File does not exist at path: " + fileName);
//        }
//        if (!newFile.canRead()) {
//            throw new IOException("File cannot be read at path: " + fileName);
//        }
//        // Check if file has .xml extension
//        if (!fileName.toLowerCase().endsWith(".xml")) {
//            throw new InvalidFileFormatException("The file does not have an XML extension.");
//        }
//
//        return newFile;
//    }

    private void fromXmlFileToObject(InputStream inputStream) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(STLSheet.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        stlSheet = (STLSheet) jaxbUnmarshaller.unmarshal(inputStream);
    }

    private void fromStlSheetToOurSheet(String owner) {
        sheet = new SheetImpl();

        LayoutImpl layout = new LayoutImpl();
        layout.setRowsHeightUnits(stlSheet.getSTLLayout().getSTLSize().getRowsHeightUnits());
        layout.setColumnsWidthUnits(stlSheet.getSTLLayout().getSTLSize().getColumnWidthUnits());
        layout.setRows(stlSheet.getSTLLayout().getRows());
        layout.setColumns(stlSheet.getSTLLayout().getColumns());
        sheet.setLayout(layout);

        sheet.setName(stlSheet.getName());

        for (int row = 1; row <= stlSheet.getSTLLayout().getRows(); row++) {
            for (int column = 1; column <= stlSheet.getSTLLayout().getColumns(); column++) {
                sheet.setEmptyCell(row, column, owner);
            }
        }

        for (STLRange stlRange : stlSheet.getSTLRanges().getSTLRange()) {
            String rangeName = stlRange.getName();
            String rangeStr = stlRange.getSTLBoundaries().getFrom() + ".." + stlRange.getSTLBoundaries().getTo();
            sheet.addRange(rangeName, rangeStr);
        }

        for (STLCell stlCell : stlSheet.getSTLCells().getSTLCell()) {
            int row = stlCell.getRow();
            int col = CoordinateImpl.convertStringColumnToNumber(stlCell.getColumn());
            sheet.setCell(row, col, stlCell.getSTLOriginalValue());
        }

        versionManager.AddSheetVersionToMap(createDTOSheetForDisplay(sheet), sheet.getNumberCellsThatHaveChanged());
    }

    @Override
    public DTOsheet createDTOSheetForDisplay(Sheet sheet) {
        String name = sheet.getName();
        int version = sheet.getVersion();
        Map<Coordinate, Cell> cellsMap = sheet.getActiveCells();
        Map<Coordinate, DTOcell> dtoCellsMap = new HashMap<>();
        DTOlayout dtoLayout = new DTOlayout(sheet.getLayout().getRowsHeightUnits(), sheet.getLayout().getColumnsWidthUnits(),
                sheet.getLayout().getRows(), sheet.getLayout().getColumns());
        Map<String, Range> rangesMap = sheet.getStringToRange();
        Map<String, DTOrange> dtoRangeMap = new HashMap<>();

        for (Cell cell : cellsMap.values()) {
            DTOcell dtoCell = new DTOcell(cell.getCoordinate().getRow(), cell.getCoordinate().getColumn(),
                    cell.getEffectiveValue().getValue().toString(), cell.getOriginalValue(), cell.getVersion(), cell.getDependsOn(), cell.getInfluencingOn(), cell.getUserName());

            dtoCellsMap.put(CoordinateFactory.createCoordinate(cell.getCoordinate().getRow(), cell.getCoordinate().getColumn()), dtoCell);
        }

        for (Range range : rangesMap.values()) {
            DTOrange dtoRange = new DTOrange(range.getCells(), range.getName());
            dtoRangeMap.put(dtoRange.getName(), dtoRange);
        }

        return new DTOsheet(name, version, dtoCellsMap, dtoLayout, dtoRangeMap);
    }

    @Override
    public DTOsheet GetVersionForDisplay(String version) {
        Integer versionNumber = Integer.parseInt(version);
        DTOsheet dtoSheet = versionManager.getSheetVersion(versionNumber);
        return dtoSheet;
    }

    @Override
    public DTOsheet EditCell(String coordinateStr, String inputValue, String username) {
        Coordinate coordinate = CoordinateFactory.from(coordinateStr);
        sheet = sheet.updateCellValueAndCalculate(coordinate.getRow(), coordinate.getColumn(), inputValue, username);
        DTOsheet dtoSheet = createDTOSheetForDisplay(sheet);
        versionManager.AddSheetVersionToMap(dtoSheet, sheet.getNumberCellsThatHaveChanged());
        return dtoSheet;
    }
//
//    @Override
//    public void saveSystemState(String filePath) throws IOException {
//        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath + ".ser"))) {
//            oos.writeObject(this);
//        }
//    }
//
//    public static EngineImpl loadSystemState(String filePath) throws IOException, ClassNotFoundException {
//        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath + ".ser"))) {
//            return (EngineImpl) ois.readObject();
//        }
//    }
//
//    @Override
//    public Coordinate checkAndConvertInputToCoordinate(String inputCell) {
//        Coordinate coordinate = CoordinateFactory.from(inputCell);
//
//        if (coordinate == null)
//            throw new IllegalArgumentException("Invalid coordinate provided, please provide a valid cell identity (e.g., A4 or B7).");
//
//        CoordinateFactory.isValidCoordinate(coordinate, sheet.getLayout());
//        return coordinate;
//    }
//
    @Override
    public void addRange(String name, String rangeStr) {
        sheet.addRange(name, rangeStr);
    }

    @Override
    public void removeRange(String rangeName) {
        sheet.removeRange(rangeName);
    }

    @Override
    public List<String> getExistingRanges() {
        DTOsheet dtoSheet = createDTOSheetForDisplay(sheet);
        return dtoSheet.getExistingRangeNames();
    }

    @Override
    public List<String> getRangeCellsList(String rangeName) {
        DTOsheet dtoSheet = createDTOSheetForDisplay(sheet);
        return dtoSheet.getRangeCellsList(rangeName);
    }

//    @Override
//    public List<Double> getNumericalValuesFromRange(String range) throws IllegalArgumentException {
//        return sheet.getNumericalValuesFromRange(range);
//    }

    @Override
    public List<String> createListOfValuesForFilter(String column, String range) {
        return sheet.createListOfValuesForFilter(column, range);
    }

    @Override
    public DTOsheet filterColumnBasedOnSelection(String rangeStr, Map<String, List<String>> columnToValues, Map<String, String> oldCoordToNewCoord) {
        DTOsheet dtoSheet = createDTOSheetForDisplay(sheet.filterColumnBasedOnSelection(rangeStr, columnToValues, oldCoordToNewCoord));
        return dtoSheet;
    }

    @Override
    public List<String> getColumnsWithinRange(String rangeStr) {
        return sheet.getColumnsWithinRange(rangeStr);
    }

    @Override
    public DTOsheet sortColumnBasedOnSelection(String rangeStr, List<String> selectedColumns, Map<String, String> newCoordToOldCoord) {
        DTOsheet dtoSheet = createDTOSheetForDisplay(sheet.sortColumnBasedOnSelection(rangeStr, selectedColumns, newCoordToOldCoord));
        return dtoSheet;
    }

//    @Override
//    public DTOsheet createDTOCopySheet() {
//        Sheet copySheet = getSheet().copySheet();
//        DTOsheet dtoSheet = createDTOSheetForDisplay(copySheet);
//        return dtoSheet;
//    }
//
//    @Override
//    public Map<String, EffectiveValue> getCellsThatHaveChangedAfterUpdateCell(String cellID, String newValue) {
//        Sheet copySheet = getSheet().copySheet();
//        Coordinate coordinate = CoordinateFactory.from(cellID);
//        copySheet.updateCellValueAndCalculate(coordinate.getRow(), coordinate.getColumn(), newValue);
//        copySheet.getCellsThatHaveChanged();
//        Map<String, EffectiveValue> cellsValues = new HashMap<>();
//
//        for (Cell cell : copySheet.getCellsThatHaveChanged()) {
//            cellsValues.put(cell.getCoordinate().toString(), cell.getEffectiveValue());
//        }
//        return cellsValues;
//    }
//
//
//    @Override
//    public List<String> createListOfValuesForFilter(String column, String range) {
//        return sheet.createListOfValuesForFilter(column, range);
//    }
//
//    @Override
//    public DTOsheet filterColumnBasedOnSelection(String rangeStr, Map<String, List<String>> columnToValues, Map<String, String> oldCoordToNewCoord) {
//        DTOsheet dtoSheet = createDTOSheetForDisplay(sheet.filterColumnBasedOnSelection(rangeStr, columnToValues, oldCoordToNewCoord));
//        return dtoSheet;
//    }
//
//    @Override
//    public List<String> getColumnsWithinRange(String range) {
//        return sheet.getColumnsWithinRange(range);
//    }
//
//    @Override
//    public DTOsheet sortColumnBasedOnSelection(String rangeStr, List<String> selectedColumns, Map<String, String> newCoordToOldCoord) {
//        DTOsheet dtoSheet = createDTOSheetForDisplay(sheet.sortColumnBasedOnSelection(rangeStr, selectedColumns, newCoordToOldCoord));
//        return dtoSheet;
//    }
//
    @Override
    public DTOsheet createDTOCopySheet() {
        Sheet copySheet = getSheet().copySheet();
        DTOsheet dtoSheet = createDTOSheetForDisplay(copySheet);
        return dtoSheet;
    }

    @Override
    public Map<String, String> getCellsThatHaveChangedAfterUpdateCell(String cellID, String newValue, String username) {
        Sheet copySheet = getSheet().copySheet();
        Coordinate coordinate = CoordinateFactory.from(cellID);
        copySheet.updateCellValueAndCalculate(coordinate.getRow(), coordinate.getColumn(), newValue, username);
        copySheet.getCellsThatHaveChanged();
        Map<String, String> cellsValues = new HashMap<>();

        for (Cell cell : copySheet.getCellsThatHaveChanged()) {
            cellsValues.put(cell.getCoordinate().toString(), cell.getEffectiveValue().getValue().toString());
        }
        return cellsValues;
    }

//    @Override
//    public Map<String, Integer> createListOfFunctions() {
//        Map<String, Integer> functionMap = new HashMap<>();
//        for (Operation operation : Operation.values()) {
//            functionMap.put(operation.name(), operation.getNumArgs()); // Add function name and argument count
//        }
//        return functionMap;
//    }

    @Override
    public void askForPermission(String userName, PermissionType permissionType) {
        permissionManager.askForPermission(userName, permissionType);
    }

    @Override
    public PermissionType getPermissionTypeForUser(String userName) {
//        PermissionType permissionType;
//        if (owner.equals(userName)) {
//            permissionType = PermissionType.OWNER;
//        }
//        else {
//            permissionType = PermissionType.NONE;
//        }
        return permissionManager.getPermissionTypeForUser(userName);
    }


    @Override
    public void handlePermissionRequest(String connectedUserName, String applicantUsername, PermissionStatus newStatus, PermissionType requestedPermission) {
        permissionManager.handlePermissionRequest(connectedUserName, applicantUsername, newStatus, requestedPermission);
    }

    @Override
    public void updateNewUserPermissionToNone(String username) {
        permissionManager.updateNewUserPermissionToNone(username);
    }

    @Override
    public Layout getLayout() {
        return sheet.getLayout();
    }

}
