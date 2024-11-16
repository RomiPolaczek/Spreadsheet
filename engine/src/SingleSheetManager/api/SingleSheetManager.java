package SingleSheetManager.api;

import dto.DTOpermissionRequest;
import dto.DTOsheet;
import permissions.PermissionManager;
import permissions.PermissionStatus;
import permissions.PermissionType;
import sheet.api.EffectiveValue;
import sheet.api.Sheet;
import sheet.layout.api.Layout;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface SingleSheetManager {
    void LoadFile(InputStream inputStream, String owner) throws Exception;
    Sheet getSheet();
    String getOwner();
    PermissionManager getPermissionManager();
    DTOsheet createDTOSheetForDisplay(Sheet sheet);
    List<String> getExistingRanges();
    DTOsheet EditCell(String coordinateStr, String inputValue, String username);
    DTOsheet GetVersionForDisplay(String version);
    int getNumberOfVersions();
//    int getChangesAccordingToVersionNumber(int version);
//    File getFile();
//    void saveSystemState(String filePath) throws IOException;
//    Coordinate checkAndConvertInputToCoordinate(String inputCell);
    void addRange(String name, String rangeStr);
    void removeRange(String rangeName);
//    List<String> getExistingRanges();
    List<String> getRangeCellsList(String name);
//    List<Double> getNumericalValuesFromRange(String range) throws IllegalArgumentException;
    List<String> createListOfValuesForFilter(String column, String range);
    DTOsheet filterColumnBasedOnSelection(String rangeStr, Map<String, List<String>> columnToValues, Map<String, String> oldCoordToNewCoord);
    List<String> getColumnsWithinRange(String range);
    DTOsheet sortColumnBasedOnSelection(String rangeStr, List<String> selectedColumns, Map<String, String> newCoordToOldCoord);
//    DTOsheet createDTOCopySheet();
//    Map<String, EffectiveValue> getCellsThatHaveChangedAfterUpdateCell(String cellID, String newValue);
//    List<String> createListOfValuesForFilter(String column, String range);
//    DTOsheet filterColumnBasedOnSelection(String rangeStr, Map<String, List<String>> columnToValues, Map<String, String> oldCoordToNewCoord);
//    List<String> getColumnsWithinRange(String range);
//    DTOsheet sortColumnBasedOnSelection(String rangeStr, List<String> selectedColumns, Map<String, String> newCoordToOldCoord);
    DTOsheet createDTOCopySheet();
    Map<String, String> getCellsThatHaveChangedAfterUpdateCell(String cellID, String newValue, String username);
//    Map<String, Integer> createListOfFunctions();
    void askForPermission(String userName, PermissionType permissionType);
    PermissionType getPermissionTypeForUser(String userName);
    void handlePermissionRequest(String connectedUserName, String applicantUsername, PermissionStatus newStatus, PermissionType requestedPermission);
    void updateNewUserPermissionToNone(String username);
    Layout getLayout();



}
