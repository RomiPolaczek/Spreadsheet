package dto;

import permissions.PermissionType;

public class DTOsheetTableDetails {
    private String sheetName;
    private String owner;
    private String size;
    private PermissionType permission;

    public DTOsheetTableDetails(String sheetName, String owner, String size, PermissionType permission) {
        this.sheetName = sheetName;
        this.owner = owner;
        this.size = size;
        this.permission = permission;
    }

    public String getSheetName() {
        return sheetName;
    }

    public String getOwner() {
        return owner;
    }

    public String getSize() {
        return size;
    }

    public PermissionType getPermission() {
        return permission;
    }

}
