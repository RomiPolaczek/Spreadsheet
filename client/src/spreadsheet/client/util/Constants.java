package spreadsheet.client.util;

//import com.google.gson.Gson;

public class Constants {

    // global constants
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    public final static int REFRESH_RATE = 2000;
    public final static String CHAT_LINE_FORMATTING = "%tH:%tM:%tS | %.10s: %s%n";

    // fxml locations
    public final static String DASHBOARD_PAGE_FXML_RESOURCE_LOCATION = "/spreadsheet/client/component/dashboard/dashboard.fxml";
    public final static String MAIN_SHEET_PAGE_FXML_RESOURCE_LOCATION = "/spreadsheet/client/component/mainSheet/mainSheet.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/spreadsheet/client/component/login/login.fxml";
//    public final static String CHAT_ROOM_FXML_RESOURCE_LOCATION = "/chat/client/component/chatroom/chat-room-main.fxml";

    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/spreadsheet";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;


    // Pages
    public final static String DASHBOARD_PAGE = "/dashboard";
    public final static String MAIN_SHEET_PAGE = "/mainSheet";

    // Login
    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";

    // Dashboard commands
    public final static String LOAD_FILE = FULL_SERVER_PATH + "/loadFile";
    public final static String LOAD_SHEET = FULL_SERVER_PATH + DASHBOARD_PAGE + "/displaySheet";
    public final static String REQUEST_PERMISSION = FULL_SERVER_PATH + "/requestPermission";
    public final static String GET_SHEET_DETAILS = FULL_SERVER_PATH + DASHBOARD_PAGE + "/getSheetDetails";
    public final static String GET_PERMISSION_TABLE_DETAILS = FULL_SERVER_PATH + DASHBOARD_PAGE + "/getPermissionTableDetails";


    // Main sheet commands
    public final static String GET_ALL_RANGES = FULL_SERVER_PATH + MAIN_SHEET_PAGE + "/getRanges";
    public final static String ADD_RANGE = FULL_SERVER_PATH + MAIN_SHEET_PAGE + "/addRange";
    public final static String UPDATE_CELL = FULL_SERVER_PATH + MAIN_SHEET_PAGE + "/updateCellValue";





    //    public final static String USERS_LIST = FULL_SERVER_PATH + "/userslist";


    // GSON instance
    //public final static Gson GSON_INSTANCE = new Gson();
}
