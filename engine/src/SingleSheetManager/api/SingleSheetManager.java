package SingleSheetManager.api;

import dto.DTOsheet;
import sheet.api.EffectiveValue;
import sheet.api.Sheet;
import sheet.coordinate.api.Coordinate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface SingleSheetManager {
    void LoadFile(InputStream inputStream, String owner) throws Exception;
    Sheet getSheet();
    String getOwner();
   DTOsheet createDTOSheetForDisplay(Sheet sheet);
//    DTOsheet GetVersionForDisplay(String version);
//    void EditCell(Coordinate coordinate, String inputValue);
//    int getNumberOfVersions();
//    int getChangesAccordingToVersionNumber(int version);
//    File getFile();
//    void saveSystemState(String filePath) throws IOException;
//    Coordinate checkAndConvertInputToCoordinate(String inputCell);
//    void addRange(String name, String rangeStr);
//    void removeRange(String name);
//    List<String> getExistingRanges();
//    List<String> getRangeCellsList(String name);
//    List<Double> getNumericalValuesFromRange(String range) throws IllegalArgumentException;
//    List<String> createListOfValuesForFilter(String column, String range);
//    DTOsheet filterColumnBasedOnSelection(String rangeStr, Map<String, List<String>> columnToValues, Map<String, String> oldCoordToNewCoord);
//    List<String> getColumnsWithinRange(String range);
//    DTOsheet sortColumnBasedOnSelection(String rangeStr, List<String> selectedColumns, Map<String, String> newCoordToOldCoord);
//    DTOsheet createDTOCopySheet();
//    Map<String, EffectiveValue> getCellsThatHaveChangedAfterUpdateCell(String cellID, String newValue);
//    Map<String, Integer> createListOfFunctions();
}
