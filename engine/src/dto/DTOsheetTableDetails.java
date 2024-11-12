package dto;

import permissions.PermissionType;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DTOsheetTableDetails that = (DTOsheetTableDetails) o;
        return Objects.equals(sheetName, that.sheetName) && Objects.equals(owner, that.owner) && Objects.equals(size, that.size) && Objects.equals(permission, that.permission);
    }
}
