package impl;

import SingleSheetManager.api.SingleSheetManager;
import SingleSheetManager.impl.SingleSheetManagerImpl;
import api.Engine;
import dto.*;
import permissions.PermissionStatus;
import permissions.PermissionType;
import sheet.api.Sheet;
import users.UserManager;

import java.io.*;
import java.util.*;

public class EngineImpl implements Engine, Serializable {
    private Map<String, SingleSheetManager> sheetNameToSheet;
    private UserManager userManager;

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

        if(sheetNameToSheet.containsKey(sheetName)){
            throw new RuntimeException("The sheet " + sheetName + " already exists");
        }

        sheetNameToSheet.put(sheetName, singleSheetManager);
    }

    @Override
    public DTOsheet createDTOSheet(String sheetName) {
        synchronized (this) {
            Sheet sheet = sheetNameToSheet.get(sheetName).getSheet();
            return sheetNameToSheet.get(sheetName).createDTOSheetForDisplay(sheet);
        }
    }

    @Override
    public List<String> getExistingRangesBySheetName(String sheetName) {
        return sheetNameToSheet.get(sheetName).getExistingRanges();
    }

    @Override
    public void addRange(String sheetName, String rangeName, String rangeStr) {
        sheetNameToSheet.get(sheetName).addRange(rangeName, rangeStr);
    }

    public void EditCell(String coordinateStr, String inputValue, String sheetName){
        sheetNameToSheet.get(sheetName).EditCell(coordinateStr, inputValue);
    }

    @Override
    public void askForPermission(String userName, String selectedSheet, PermissionType permissionType) {
        sheetNameToSheet.get(selectedSheet).askForPermission(userName, permissionType);
    }

    @Override
    public void handlePermissionRequest(String userName, PermissionStatus newStatus, PermissionType requestedPermission, String sheetName) {
        sheetNameToSheet.get(sheetName).handlePermissionRequest(userName, newStatus, requestedPermission);
    }
}