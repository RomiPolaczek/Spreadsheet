package permissions;

import dto.DTOpermissionRequest;
import java.util.*;

public class PermissionManager {
    private final String owner;
    private final Map<String, PermissionType> permissionsForUser;  // Maps a username to their permission type
    private final Map<String, DTOpermissionRequest> allPermissionsRequests;  // Holds all permission requests (including duplicates)

    public PermissionManager(String owner) {
        this.owner = owner;
        this.permissionsForUser = new HashMap<>();
        this.allPermissionsRequests = new HashMap<>();

        // By default, the owner is added to the permissions map with OWNER permission
        permissionsForUser.put(owner, PermissionType.OWNER);
    }

    public String getOwner() {
        return owner;
    }

    public synchronized Map<String, PermissionType> getPermissions() {
        return permissionsForUser;
    }

    public synchronized Map<String, DTOpermissionRequest> getAllPermissionsRequests() {
        return allPermissionsRequests;
    }

    // Method to submit a new permission request
    public synchronized void askForPermission(String username, PermissionType permissionType) {
        if(allPermissionsRequests.containsKey(username)) {
            throw new IllegalStateException("You can only ask for permissions once.");
        }
        if(username.equals(owner)) {
            throw new IllegalStateException("The owner of a spreadsheet does not need permissions.");
        }
        DTOpermissionRequest newRequest = new DTOpermissionRequest(username, permissionType, PermissionStatus.PENDING);
        allPermissionsRequests.put(username, newRequest);
    }

    public synchronized void handlePermissionRequest(String connectedUserName, String applicantUsername, PermissionStatus newStatus, PermissionType requestedPermission) {
        // Check if the handler is the owner
        if (!owner.equals(connectedUserName)) {
            throw new IllegalStateException("Only the owner can handle permission requests.");
        }

        if(allPermissionsRequests.get(applicantUsername).getRequestPermissionStatus().equals(PermissionStatus.APPROVED)) {
            throw new IllegalStateException("This request is already approved.");
        }

        // Get the request from the requestHistory
        DTOpermissionRequest request = allPermissionsRequests.get(applicantUsername);

           // Handle the permission status update
        if (newStatus.equals(PermissionStatus.APPROVED)) {
            // Insert or update the user in the permissions map with the approved permission type
            permissionsForUser.put(applicantUsername, requestedPermission);

            // Update the request status to APPROVED in the request history
            request.setRequestPermissionStatus(PermissionStatus.APPROVED);

        } else if (newStatus.equals(PermissionStatus.REJECTED)) {
            // Only update the request status to REJECTED in the request history
            request.setRequestPermissionStatus(PermissionStatus.REJECTED);
        } else {
            throw new IllegalArgumentException("Invalid permission status.");
        }
    }

    // Helper method to check if a user has a specific permission
    public synchronized PermissionType getUserPermission(String username) {
        return permissionsForUser.getOrDefault(username, PermissionType.NONE);
    }

}
