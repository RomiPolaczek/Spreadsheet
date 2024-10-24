package SingleSheetManager.impl;

import dto.DTOsheet;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class VersionManager implements Serializable {
    private Map<Integer, DTOsheet> sheetsVersions;
    private Map<Integer, Integer> versionToChanges;

    public VersionManager() {
        sheetsVersions = new HashMap<Integer, DTOsheet>();
        versionToChanges = new HashMap<Integer, Integer>();
    }

    public void AddSheetVersionToMap(DTOsheet dtoSheet, Integer changes) {
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

