package spreadsheet.client.enums;

public enum PermissionType {
    OWNER("Owner"),
    READER("Reader"),
    WRITER("Writer"),
    NONE("None");
    //NOT_AUTHORIZED("Not Authorized");

    private final String permission;

    PermissionType(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
