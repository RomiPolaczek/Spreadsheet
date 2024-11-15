package impl;

import SingleSheetManager.api.SingleSheetManager;
import SingleSheetManager.impl.SingleSheetManagerImpl;
import api.Engine;
import dto.*;
import permissions.PermissionStatus;
import permissions.PermissionType;
import sheet.api.EffectiveValue;
import sheet.api.Sheet;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.impl.CoordinateFactory;
import users.UserManager;

import java.io.*;
import java.util.*;

public class EngineImpl implements Engine, Serializable {
    private Map<String, SingleSheetManager> sheetNameToSheet;
    //private UserManager userManager;

    public EngineImpl() {
        sheetNameToSheet = new HashMap<>();
    }

    public Map<String, SingleSheetManager> getSheetNameToSheet() {
        return sheetNameToSheet;
    }

    @Override
    public List<DTOsheetTableDetails> getDTOsheetTableDetailsList(String userName) {
        List<DTOsheetTableDetails> list = new ArrayList<>();

        synchronized (this) {
            for (SingleSheetManager singleSheetManager : sheetNameToSheet.values()) {
                String sheetName = singleSheetManager.getSheet().getName();
                String owner = singleSheetManager.getOwner();
                String size = singleSheetManager.getSheet().getLayout().toString();
                PermissionType permissionType = getPermissionTypeOfUser(sheetName, userName);
                list.add(new DTOsheetTableDetails(sheetName, owner, size, permissionType));

                System.out.println(permissionType);
            }
        }

        return list;
    }

    private PermissionType getPermissionTypeOfUser(String sheetName, String userName) {
        return sheetNameToSheet.get(sheetName).getPermissionTypeForUser(userName);
    }

    @Override
    public synchronized List<DTOpermissionRequest> getDTOpermissionTableDetailsList(String sheetName) {
        return sheetNameToSheet.get(sheetName).getPermissionManager().getAllPermissionsRequests().values().stream().toList();
    }

    public void LoadFile(InputStream inputStream, String owner) throws Exception {
        SingleSheetManager singleSheetManager = new SingleSheetManagerImpl(owner);
        singleSheetManager.LoadFile(inputStream, owner);
        String sheetName = singleSheetManager.getSheet().getName();

        if (sheetNameToSheet.containsKey(sheetName)) {
            throw new RuntimeException("The sheet " + sheetName + " already exists");
        }

        sheetNameToSheet.put(sheetName, singleSheetManager);
    }

    @Override
    public DTOsheet createDTOSheet(String sheetName) {
        ensureSheetExists(sheetName);
        synchronized (this) {
            Sheet sheet = sheetNameToSheet.get(sheetName).getSheet();
            return sheetNameToSheet.get(sheetName).createDTOSheetForDisplay(sheet);
        }
    }

//    @Override
//    public DTOcell getDTOcell(String sheetName, String cellID) {
//        synchronized (this) {
//            Sheet sheet = sheetNameToSheet.get(sheetName).getSheet();
//            Coordinate coordinate = CoordinateFactory.from(cellID);
//            return sheetNameToSheet.get(sheetName).createDTOSheetForDisplay(sheet).getCell(coordinate.getRow(), coordinate.getColumn());
//        }
//    }

    @Override
    public List<String> getExistingRangesBySheetName(String sheetName) {
        ensureSheetExists(sheetName);
        return sheetNameToSheet.get(sheetName).getExistingRanges();
    }

    @Override
    public void addRange(String sheetName, String rangeName, String rangeStr) {
        ensureSheetExists(sheetName);
        sheetNameToSheet.get(sheetName).addRange(rangeName, rangeStr);
    }

    public DTOsheet EditCell(String coordinateStr, String inputValue, String sheetName) {
        synchronized (this) {
            ensureSheetExists(sheetName);
            return sheetNameToSheet.get(sheetName).EditCell(coordinateStr, inputValue);
        }
    }

    @Override
    public void askForPermission(String userName, String sheetName, PermissionType permissionType) {
        ensureSheetExists(sheetName);
        sheetNameToSheet.get(sheetName).askForPermission(userName, permissionType);
    }

    @Override
    public int getNumberOfVersions(String sheetName) {
        synchronized (this) {
            ensureSheetExists(sheetName);
            return sheetNameToSheet.get(sheetName).getNumberOfVersions();
        }
    }

    @Override
    public DTOsheet GetVersionForDisplay(String sheetName, String version) {
        synchronized (this) {
            ensureSheetExists(sheetName);
            return sheetNameToSheet.get(sheetName).GetVersionForDisplay(version);
        }
    }

    private void ensureSheetExists(String sheetName) {
        if (sheetName == null || sheetName.isEmpty()) {
            throw new IllegalArgumentException("Sheet name cannot be null or empty");
        }
        if (!sheetNameToSheet.containsKey(sheetName)) {
            throw new NoSuchElementException("The sheet '" + sheetName + "' does not exist");
        }
    }

    @Override
    public void handlePermissionRequest(String connectedUserName, String applicantUsername, PermissionStatus newStatus, PermissionType requestedPermission, String sheetName) {
        sheetNameToSheet.get(sheetName).handlePermissionRequest(connectedUserName, applicantUsername, newStatus, requestedPermission);
    }

    @Override
    public String getUserPermission(String username, String sheetName) {
        return sheetNameToSheet.get(sheetName).getPermissionManager().getUserPermission(username).getPermission();
    }

    @Override
    public void addUser(String username) {
        for (SingleSheetManager singleSheetManager : sheetNameToSheet.values()) {
            singleSheetManager.updateNewUserPermissionToNone(username);
        }
    }

    @Override
    public Map<String, String> getCellsThatHaveChangedAfterUpdateCell(String sheetName, String cellID, String newValue) {
        synchronized (this) {
            ensureSheetExists(sheetName);
            return sheetNameToSheet.get(sheetName).getCellsThatHaveChangedAfterUpdateCell(cellID, newValue);
        }
    }

    @Override
    public DTOsheet createDTOCopySheet(String sheetName) {
        synchronized (this) {
            ensureSheetExists(sheetName);
            return sheetNameToSheet.get(sheetName).createDTOCopySheet();
        }
    }
}