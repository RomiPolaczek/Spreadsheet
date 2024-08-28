package impl;

import dto.DTOsheet;
import sheet.api.Sheet;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class VersionManager {
    private Map<Integer, DTOsheet> sheetsVersions;
    private Map<Integer, Integer> versionToChanges;

    public VersionManager() {
        sheetsVersions = new HashMap<Integer, DTOsheet>();
        versionToChanges = new HashMap<Integer, Integer>();
    }

    public void AddSheetVersionToMap(DTOsheet dtoSheet, Integer changes) {
        //DTOsheet dtoSheet = createDTOSheetForDisplay(sheet);
        //Sheet newSheet = sheet.copySheet();
        sheetsVersions.put(dtoSheet.getVersion(), dtoSheet);
        versionToChanges.put(dtoSheet.getVersion(), changes);
    }

    public DTOsheet getSheetVersion(Integer versionNumber) {
        if (!sheetsVersions.containsKey(versionNumber)) {
            throw new NoSuchElementException("There is no sheet with version number " + versionNumber);
        }
        return sheetsVersions.get(versionNumber);
    }

    public Map<Integer, Integer> getVersionToChanges() { return versionToChanges; }
}

//public class VersionManager {
//    private Map<Integer, Sheet> sheetsVersions;
//
//    public VersionManager() {
//        sheetsVersions = new HashMap<Integer, Sheet>();
//    }
//
//    public void AddSheetVersionToMap(Sheet sheet) {
//        //DTOsheet dtoSheet = createDTOSheetForDisplay(sheet);
//        Sheet newSheet = sheet.copySheet();
//        sheetsVersions.put(sheet.getVersion(),newSheet);
//        System.out.println("added version number"+sheet.getVersion());
//    }
//
//    public Sheet getSheetVersion(Integer versionNumber) {
//        return sheetsVersions.get(versionNumber);
//    }
//}
