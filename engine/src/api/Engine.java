package api;

import SingleSheetManager.api.SingleSheetManager;
import dto.DTOpermissionRequest;
import dto.DTOsheet;
import dto.DTOsheetTableDetails;
import permissions.PermissionType;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface Engine {
    Map<String, SingleSheetManager> getSheetNameToSheet();
    void LoadFile(InputStream inputStream, String owner) throws Exception;
    List<DTOsheetTableDetails> getDTOsheetTableDetailsList();
    List<DTOpermissionRequest> getDTOpermissionTableDetailsList(String sheetName);
    DTOsheet createDTOSheet(String sheetName);
    List<String> getExistingRangesBySheetName(String sheetName);
    void addRange(String sheetName, String rangeName, String rangeStr);
    void askForPermission(String userName, String selectedSheet, PermissionType permissionType);
}
