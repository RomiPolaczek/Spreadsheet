package spreadsheet.client.util;

import com.google.gson.Gson;

public class Constants {

    // global constants
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    public final static int REFRESH_RATE = 2000;
    public final static String CHAT_LINE_FORMATTING = "%tH:%tM:%tS | %.10s: %s%n";

    // fxml locations
    public final static String DASHBOARD_PAGE_FXML_RESOURCE_LOCATION = "/spreadsheet/client/component/dashboard/dashboard.fxml";
    public final static String MAIN_SHEET_PAGE_FXML_RESOURCE_LOCATION = "/spreadsheet/client/component/mainSheet/mainSheet.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/spreadsheet/client/component/login/login.fxml";

    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/spreadsheet";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    // Pages
    public final static String DASHBOARD_PAGE = "/dashboard";
    public final static String MAIN_SHEET_PAGE = "/mainSheet";
    public final static String DYNAMIC_ANALYSIS = "/dynamicAnalysis";

    // Login
    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
    public final static String LOGOUT = FULL_SERVER_PATH + "/logout";

    // Dashboard commands
    public final static String LOAD_FILE = FULL_SERVER_PATH + "/loadFile";
    public final static String LOAD_SHEET = FULL_SERVER_PATH + DASHBOARD_PAGE + "/displaySheet";
    public final static String REQUEST_PERMISSION = FULL_SERVER_PATH + DASHBOARD_PAGE + "/requestPermission";
    public final static String GET_SHEET_DETAILS = FULL_SERVER_PATH + DASHBOARD_PAGE + "/getSheetDetails";
    public final static String GET_PERMISSION_TABLE_DETAILS = FULL_SERVER_PATH + DASHBOARD_PAGE + "/getPermissionTableDetails";
    public final static String HANDLE_PERMISSION_REQUEST = FULL_SERVER_PATH + DASHBOARD_PAGE + "/handlePermissionRequest";
    public final static String AVAILABLE_SHEETS = FULL_SERVER_PATH + DASHBOARD_PAGE + "/availableSheets";
    public final static String GET_USER_PERMISSIONS = FULL_SERVER_PATH + DASHBOARD_PAGE + "/getUserPermissions";


    // Main sheet commands
    public final static String GET_ALL_RANGES = FULL_SERVER_PATH + MAIN_SHEET_PAGE + "/getRanges";
    public final static String ADD_RANGE = FULL_SERVER_PATH + MAIN_SHEET_PAGE + "/addRange";
    public final static String DELETE_RANGE = FULL_SERVER_PATH + MAIN_SHEET_PAGE + "/deleteRange";
    public final static String GET_RANGE_CELLS_LIST = FULL_SERVER_PATH + MAIN_SHEET_PAGE + "/getRangeCellsList";
    public final static String UPDATE_CELL = FULL_SERVER_PATH + MAIN_SHEET_PAGE + "/updateCellValue";
    public final static String GET_NUM_VERSIONS = FULL_SERVER_PATH + MAIN_SHEET_PAGE + "/getNumSheetVersions";
    public final static String GET_DTO_SHEET_VERSION = FULL_SERVER_PATH + MAIN_SHEET_PAGE + "/getDTOSheetVersion";
    public final static String GET_COLUMNS_FOR_FILTER = FULL_SERVER_PATH + MAIN_SHEET_PAGE + "/getColumnsForFilter";
    public final static String CREATE_DTO_SHEET_FILTER = FULL_SERVER_PATH + MAIN_SHEET_PAGE + "/createDTOSheetFilter";
    public final static String GET_VALUES_FOR_FILTER = FULL_SERVER_PATH + MAIN_SHEET_PAGE + "/getValuesForFilter";
    public final static String GET_LAYOUT_FOR_FILTER = FULL_SERVER_PATH + MAIN_SHEET_PAGE + "/getLayoutForFilter";
    public final static String CREATE_DTO_SHEET_SORT = FULL_SERVER_PATH + MAIN_SHEET_PAGE + "/createDTOSheetSort";
    public final static String GET_NUM_LATEST_SHEET_VERSION = FULL_SERVER_PATH + MAIN_SHEET_PAGE + "/getNumLatestSheetVersion";
    public final static String GET_RANGE_NUMERICAL_VALUES = FULL_SERVER_PATH + MAIN_SHEET_PAGE + "/getRangeNumericalValues";

    public final static String UPDATED_CELLS_DYNAMIC_ANALYSIS = FULL_SERVER_PATH + MAIN_SHEET_PAGE + DYNAMIC_ANALYSIS + "/updatedCells";
    public final static String CREATE_DTO_COPY_SHEET = FULL_SERVER_PATH + MAIN_SHEET_PAGE + DYNAMIC_ANALYSIS + "/getDTOCopySheet";

    public static final String SELECTED_SHEET_NAME = "selectedSheet";
    public static final String CELL_ID = "cellID";
    public static final String NEW_VALUE = "newValue";

    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}
