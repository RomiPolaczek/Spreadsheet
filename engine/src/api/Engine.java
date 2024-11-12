package api;

import SingleSheetManager.api.SingleSheetManager;
import dto.DTOpermissionRequest;
import dto.DTOsheet;
import dto.DTOsheetTableDetails;
import permissions.PermissionStatus;
import permissions.PermissionType;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface Engine {
    Map<String, SingleSheetManager> getSheetNameToSheet();
    void LoadFile(InputStream inputStream, String owner) throws Exception;
    List<DTOsheetTableDetails> getDTOsheetTableDetailsList(String userName);
    List<DTOpermissionRequest> getDTOpermissionTableDetailsList(String sheetName);
    DTOsheet createDTOSheet(String sheetName);
    List<String> getExistingRangesBySheetName(String sheetName);
    void addRange(String sheetName, String rangeName, String rangeStr);
    DTOsheet EditCell(String coordinateStr, String inputValue, String sheetName);
    void askForPermission(String userName, String selectedSheet, PermissionType permissionType);
    int getNumberOfVersions(String sheetName);
    DTOsheet GetVersionForDisplay(String sheetName, String version);
    void handlePermissionRequest(String connectedUserName, String applicantUsername, PermissionStatus newStatus, PermissionType requestedPermission, String sheetName);
    String getUserPermission(String username, String sheetName);


}
