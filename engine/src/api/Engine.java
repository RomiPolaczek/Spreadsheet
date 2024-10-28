package api;

import SingleSheetManager.api.SingleSheetManager;
import dto.DTOsheet;
import dto.DTOsheetTableDetails;
import sheet.api.EffectiveValue;
import sheet.api.Sheet;
import sheet.coordinate.api.Coordinate;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface Engine {
    Map<String, SingleSheetManager> getSheetNameToSheet();
    void LoadFile(InputStream inputStream, String owner) throws Exception;
    List<DTOsheetTableDetails> getDTOsheetTableDetailsList();
    DTOsheet createDTOSheet(String sheetName);
}
