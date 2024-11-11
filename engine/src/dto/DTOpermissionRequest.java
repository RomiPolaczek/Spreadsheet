package dto;

import permissions.PermissionStatus;
import permissions.PermissionType;

public class DTOpermissionRequest {
    private final String userName;
    private final PermissionType requestedPermissionType;
    private PermissionStatus requestPermissionStatus;

    // Constructor
    public DTOpermissionRequest(String username, PermissionType requestedPermission, PermissionStatus status) {
        this.userName = username;
        this.requestedPermissionType = requestedPermission;
        this.requestPermissionStatus = status;
    }

    public String getUserName() {return userName;}

    public PermissionType getRequestedPermissionType() {return requestedPermissionType;}

    public PermissionStatus getRequestPermissionStatus() {return requestPermissionStatus;}

    public void setRequestPermissionStatus(PermissionStatus requestPermissionStatus) {
        this.requestPermissionStatus = requestPermissionStatus;
    }
}
