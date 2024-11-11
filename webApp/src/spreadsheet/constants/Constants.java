package spreadsheet.constants;

import com.google.gson.Gson;

public class Constants {
    public static final String USER_NAME = "username";
    public static final String SELECTED_SHEET_NAME = "selectedSheet";
    public static final String RANGE_NAME = "rangeName";
    public static final String RANGE_STR = "rangeStr";
    public static final String PERMISSION_TYPE = "permissionType";
    public static final String SELECTED_VERSION = "selectedVersion";
    public static final String CELL_ID = "cellID";
    public static final String NEW_VALUE = "newValue";



    public static final String USER_NAME_ERROR = "username_error";

    public final static Gson GSON_INSTANCE = new Gson();

    public static final int INT_PARAMETER_ERROR = Integer.MIN_VALUE;
}