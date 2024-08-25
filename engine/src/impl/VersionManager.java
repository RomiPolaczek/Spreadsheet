package impl;

import dto.DTOsheet;
import sheet.api.Sheet;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class VersionManager {
    private Map<Integer, DTOsheet> sheetsVersions;

    public VersionManager() {
        sheetsVersions = new HashMap<Integer, DTOsheet>();
    }

    public void AddSheetVersionToMap(DTOsheet dtoSheet) {
        //DTOsheet dtoSheet = createDTOSheetForDisplay(sheet);
        //Sheet newSheet = sheet.copySheet();
        sheetsVersions.put(dtoSheet.getVersion(), dtoSheet);
        //System.out.println("added version number " + dtoSheet.getVersion());
    }

    public DTOsheet getSheetVersion(Integer versionNumber) {
        if (!sheetsVersions.containsKey(versionNumber)) {
            throw new NoSuchElementException("There is no sheet with version number " + versionNumber);
        }
        return sheetsVersions.get(versionNumber);
    }
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
