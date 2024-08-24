package api;

import dto.DTOsheet;
import sheet.api.Sheet;

public interface Engine {
    void LoadFile(String fileName) throws Exception;

    Sheet getSheet();

    DTOsheet createDTOSheetForDisplay(Sheet sheet);

    void AddSheetVersionToMap(Sheet sheet);

    DTOsheet getSheetVersion(Integer versionNumber);
}
