package api;

import dto.DTOsheet;
import sheet.api.EffectiveValue;
import sheet.api.Sheet;
import sheet.coordinate.api.Coordinate;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Engine {
    void LoadFile(String fileName) throws Exception;
    Sheet getSheet();
    DTOsheet createDTOSheetForDisplay(Sheet sheet);
    DTOsheet GetVersionForDisplay(String version);
    void EditCell(Coordinate coordinate, String inputValue);
    int getNumberOfVersions();
    int getChangesAccordingToVersionNumber(int version);
    File getFile();
    void saveSystemState(String filePath) throws IOException;
    Coordinate checkAndConvertInputToCoordinate(String inputCell);
    void addRange(String name, String rangeStr);
    void removeRange(String name);
    List<String> getExistingRanges();
    List<String> getRangeCellsList(String name);
    List<Double> getNumericalValuesFromRange(String range) throws IllegalArgumentException;
    List<String> createListOfValuesForFilter(String column);
    DTOsheet filterColumnBasedOnSelection(String rangeStr, List<String> checkBoxesValues, String selectedColumn);
    DTOsheet createDTOCopySheet();
    Map<String, EffectiveValue> getCellsThatHaveChangedAfterUpdateCell(String cellID, String newValue);
}
