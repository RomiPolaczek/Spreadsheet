package dto;

import permissions.PermissionStatus;
import permissions.PermissionType;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DTOpermissionRequest that = (DTOpermissionRequest) o;
        return Objects.equals(userName, that.userName) && requestedPermissionType == that.requestedPermissionType && requestPermissionStatus == that.requestPermissionStatus;
    }

}
