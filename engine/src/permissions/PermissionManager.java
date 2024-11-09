package permissions;

import dto.DTOpermissionRequest;
import java.util.*;

public class PermissionManager {
    private final String owner;
    private final Map<String, PermissionType> permissionsForUser;  // Maps a username to their permission type
    private final List<DTOpermissionRequest> allPermissionsRequests;  // Holds all permission requests (including duplicates)

    public PermissionManager(String owner) {
        this.owner = owner;
        this.permissionsForUser = new HashMap<>();
        this.allPermissionsRequests = new ArrayList<>();

        // By default, the owner is added to the permissions map with OWNER permission
        permissionsForUser.put(owner, PermissionType.OWNER);
    }

    public String getOwner() {
        return owner;
    }

    public synchronized Map<String, PermissionType> getPermissions() {
        return permissionsForUser;
    }

    public synchronized List<DTOpermissionRequest> getAllPermissionsRequests() {
        return allPermissionsRequests;
    }

    // Method to submit a new permission request
    public synchronized void askForPermission(String username, PermissionType permissionType) {
        DTOpermissionRequest newRequest = new DTOpermissionRequest(username, permissionType, PermissionStatus.PENDING);
        allPermissionsRequests.add(newRequest);
    }

//    public synchronized void handlePermissionRequest(String applicantName, String handlerName, int requestNumber, PermissionStatus status, PermissionType requestedPermission) {
//        // Check if the handler is the owner
//        if (!owner.equals(handlerName)) {
//            throw new IllegalStateException("Only the owner can handle permission requests.");
//        }
//
//        // Validate if the request exists in the allPermissionsRequests by its number
//        if (requestNumber < 0 || requestNumber >= allPermissionsRequests.size()) {
//            throw new IllegalArgumentException("Invalid request number.");
//        }
//
//        // Get the request from the requestHistory
//        PermissionRequestDTO request = requestHistory.get(requestNumber);
//
//        // Check if the request corresponds to the applicant
//        if (!request.getUsername().equals(applicantName)) {
//            throw new IllegalArgumentException("Applicant name does not match the request.");
//        }
//
//        // Handle the permission status update
//        if (status == PermissionStatus.APPROVED) {
//            // Insert or update the user in the permissions map with the approved permission type
//            permissionsForUser.put(applicantName, requestedPermission);
//
//            // Update the request status to APPROVED in the request history
//            request.setStatus(PermissionStatus.APPROVED);
//        } else if (status == PermissionStatus.REJECTED) {
//            // Only update the request status to REJECTED in the request history
//            request.setStatus(PermissionStatus.REJECTED);
//        } else {
//            throw new IllegalArgumentException("Invalid permission status.");
//        }
//    }

    // Helper method to check if a user has a specific permission
    public synchronized PermissionType getUserPermission(String username) {
        return permissionsForUser.getOrDefault(username, PermissionType.NONE);
    }

}
