package api;

import SingleSheetManager.api.SingleSheetManager;
import dto.DTOpermissionRequest;
import dto.DTOsheet;
import dto.DTOsheetTableDetails;
import permissions.PermissionStatus;
import permissions.PermissionType;
import sheet.api.EffectiveValue;
import sheet.layout.api.Layout;

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
    void deleteRange(String sheetName, String rangeName);
    DTOsheet EditCell(String coordinateStr, String inputValue, String sheetName, String username);
    void askForPermission(String userName, String selectedSheet, PermissionType permissionType);
    int getNumberOfVersions(String sheetName);
    DTOsheet GetVersionForDisplay(String sheetName, String version);
    void handlePermissionRequest(String connectedUserName, String applicantUsername, PermissionStatus newStatus, PermissionType requestedPermission, String sheetName);
    String getUserPermission(String username, String sheetName);
    void addUser(String username);
    Map<String, String> getCellsThatHaveChangedAfterUpdateCell(String sheetName, String cellID, String newValue, String username);
    DTOsheet createDTOCopySheet(String sheetName);
    int getLatestSheetVersion(String sheetName);
    List<String> getRangeCellsList(String rangeName, String sheetName);
    List<String> getColumnsWithinRange(String sheetName, String rangeStr);
    DTOsheet filterColumnBasedOnSelection(String sheetName, String rangeStr, Map<String, List<String>> columnToValues, Map<String, String> oldCoordToNewCoord);
    List<String> createListOfValuesForFilter(String sheetName, String column, String range);
    Layout getSheetLayout(String sheetName);
    DTOsheet sortColumnBasedOnSelection(String shhetName, String rangeStr, List<String> selectedColumns, Map<String, String> newCoordToOldCoord);



}
