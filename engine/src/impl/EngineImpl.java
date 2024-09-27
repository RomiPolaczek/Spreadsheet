package impl;

import api.Engine;
import dto.DTOcell;
import dto.DTOlayout;
import dto.DTOrange;
import dto.DTOsheet;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import sheet.api.EffectiveValue;
import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.cell.impl.CellImpl;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.impl.CoordinateFactory;
import sheet.coordinate.impl.CoordinateImpl;
import sheet.impl.SheetImpl;
import sheet.layout.impl.LayoutImpl;
import sheet.range.Range;
import xmlGenerated.STLCell;
import xmlGenerated.STLRange;
import xmlGenerated.STLSheet;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class EngineImpl implements Engine, Serializable {

    private Sheet sheet;
    private STLSheet stlSheet;
    private File file;
    private VersionManager versionManager;

    public EngineImpl() {
        versionManager = new VersionManager();
    }

    @Override
    public void LoadFile(String fileName) throws Exception {
        File newFile = checkFileValidation(fileName);
        fromXmlFileToObject(newFile);
        fromStlSheetToOurSheet();
        file = newFile;
    }

    @Override
    public Sheet getSheet() {
        return sheet;
    }

    @Override
    public int getNumberOfVersions() {
        return versionManager.getVersionToChanges().size();
    }

    @Override
    public int getChangesAccordingToVersionNumber(int version) {
        return versionManager.getVersionToChanges().get(version);
    }

    @Override
    public File getFile() {
        return file;
    }

    private File checkFileValidation(String fileName) throws Exception {
        File newFile = new File(fileName);

        if (!newFile.exists()) {
            throw new FileNotFoundException("File does not exist at path: " + fileName);
        }
        if (!newFile.canRead()) {
            throw new IOException("File cannot be read at path: " + fileName);
        }
        // Check if file has .xml extension
        if (!fileName.toLowerCase().endsWith(".xml")) {
            throw new IllegalArgumentException("The file does not have an XML extension.");
        }

        return newFile;
    }

    private void fromXmlFileToObject(File file) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(STLSheet.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        stlSheet = (STLSheet) jaxbUnmarshaller.unmarshal(file);
    }

    private void fromStlSheetToOurSheet() {
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
                sheet.setEmptyCell(row, column);
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
                    cell.getEffectiveValue().getValue().toString(), cell.getOriginalValue(), cell.getVersion(), cell.getDependsOn(), cell.getInfluencingOn());

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
    public void EditCell(Coordinate coordinate, String inputValue) {
        sheet = sheet.updateCellValueAndCalculate(coordinate.getRow(), coordinate.getColumn(), inputValue);
        DTOsheet dtoSheet = createDTOSheetForDisplay(sheet);
        versionManager.AddSheetVersionToMap(dtoSheet, sheet.getNumberCellsThatHaveChanged());
    }

    @Override
    public void saveSystemState(String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath + ".ser"))) {
            oos.writeObject(this);
        }
    }

    public static EngineImpl loadSystemState(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath + ".ser"))) {
            return (EngineImpl) ois.readObject();
        }
    }

    @Override
    public Coordinate checkAndConvertInputToCoordinate(String inputCell) {
        Coordinate coordinate = CoordinateFactory.from(inputCell);

        if (coordinate == null)
            throw new IllegalArgumentException("Invalid coordinate provided, please provide a valid cell identity (e.g., A4 or B7).");

        CoordinateFactory.isValidCoordinate(coordinate, sheet.getLayout());
        return coordinate;
    }

    @Override
    public void addRange(String name, String rangeStr) {
        sheet.addRange(name, rangeStr);
    }

    @Override
    public void removeRange(String name) {
        sheet.removeRange(name);
    }

    @Override
    public List<String> getExistingRanges() {
        DTOsheet dtoSheet = createDTOSheetForDisplay(sheet);
        return dtoSheet.getExistingRangeNames();
    }

    @Override
    public List<String> getRangeCellsList(String name) {
        DTOsheet dtoSheet = createDTOSheetForDisplay(sheet);
        return dtoSheet.getRangeCellsList(name);
    }

    @Override
    public List<Double> getNumericalValuesFromRange(String range) throws IllegalArgumentException {
        return sheet.getNumericalValuesFromRange(range);
    }

    @Override
    public List<String> createListOfValuesForFilter(String column, String range) {
       return sheet.createListOfValuesForFilter(column, range);
    }

    @Override
    public DTOsheet filterColumnBasedOnSelection(String rangeStr, List<String> checkBoxesValues, String selectedColumn) {
        DTOsheet dtoSheet = createDTOSheetForDisplay(sheet.filterColumnBasedOnSelection(rangeStr, checkBoxesValues, selectedColumn));
        return dtoSheet;
    }

    @Override
    public List<String> getColumnsWithinRange(String range) {
        return sheet.getColumnsWithinRange(range);
    }

    @Override
    public DTOsheet sortColumnBasedOnSelection(String rangeStr, List<String> selectedColumns) {
        DTOsheet dtoSheet = createDTOSheetForDisplay(sheet.sortColumnBasedOnSelection(rangeStr, selectedColumns));
        return dtoSheet;
    }

}
