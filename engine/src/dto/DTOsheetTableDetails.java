package dto;

public class DTOsheetTableDetails {
    private String sheetName;
    private String owner;
    private String size;
    private String permission;

    public DTOsheetTableDetails(String sheetName, String owner, String size, String permission) {
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

    public String getPermission() {
        return permission;
    }

}
