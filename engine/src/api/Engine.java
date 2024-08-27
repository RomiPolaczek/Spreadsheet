package api;

import dto.DTOsheet;
import sheet.api.Sheet;
import sheet.coordinate.api.Coordinate;

public interface Engine {
    void LoadFile(String fileName) throws Exception;
    Sheet getSheet();
    DTOsheet createDTOSheetForDisplay(Sheet sheet);
    DTOsheet GetVersionForDisplay(String version);
    void EditCell(Coordinate coordinate, String inputValue);
}
