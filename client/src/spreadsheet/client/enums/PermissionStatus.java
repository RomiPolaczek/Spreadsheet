package spreadsheet.client.enums;

import java.util.Arrays;
import java.util.List;

public enum PermissionStatus {
    //NEW_SUBMISSION("New Submission"),
    //SUBMITTED("Submitted"),
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected");
    //UNKNOWN("Unknown");

    private final String status;

    PermissionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static List<PermissionStatus> getStatusTypesList(){
        return Arrays.stream(PermissionStatus.values()).toList();
    }
}
