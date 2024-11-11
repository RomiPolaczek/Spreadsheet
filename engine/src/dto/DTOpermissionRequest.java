package dto;

import permissions.PermissionStatus;
import permissions.PermissionType;

public class DTOpermissionRequest {
    private final String username;
    private final PermissionType requestedPermission;
    private PermissionStatus status;

    // Constructor
    public DTOpermissionRequest(String username, PermissionType requestedPermission, PermissionStatus status) {
        this.username = username;
        this.requestedPermission = requestedPermission;
        this.status = status;
    }
}
