package impl;

import SingleSheetManager.api.SingleSheetManager;
import SingleSheetManager.impl.SingleSheetManagerImpl;
import api.Engine;
import dto.*;
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
    public List<DTOsheetTableDetails> getDTOsheetTableDetailsList() {
        List<DTOsheetTableDetails> list = new ArrayList<>();

        synchronized (this) {
            for (SingleSheetManager singleSheetManager : sheetNameToSheet.values()) {
                String sheetName = singleSheetManager.getSheet().getName();
                String owner = singleSheetManager.getOwner();
                String size = singleSheetManager.getSheet().getLayout().toString();
                list.add(new DTOsheetTableDetails(sheetName, owner, size, "owner"));
            }
        }

        return list;
    }

    @Override
    public List<DTOpermissionRequest> getDTOpermissionTableDetailsList(String sheetName) {
        return sheetNameToSheet.get(sheetName).getPermissionManager().getAllPermissionsRequests();
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

    @Override
    public void askForPermission(String userName, String selectedSheet, PermissionType permissionType) {
        sheetNameToSheet.get(selectedSheet).askForPermission(userName, permissionType);
    }
}