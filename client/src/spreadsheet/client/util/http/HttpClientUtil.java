package spreadsheet.client.util.http;

import okhttp3.*;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpClientUtil {

    private final static SimpleCookieManager simpleCookieManager = new SimpleCookieManager();

    private final static OkHttpClient HTTP_CLIENT =
            new OkHttpClient.Builder()
                    .cookieJar(simpleCookieManager)
                    .followRedirects(false)
                    .connectTimeout(30, TimeUnit.SECONDS) // זמן חיבור
                    .readTimeout(30, TimeUnit.SECONDS)    // זמן קריאה
                    .writeTimeout(30, TimeUnit.SECONDS)   // זמן כתיבה
                    .build();

    static {
        Logger okHttpLogger = Logger.getLogger(OkHttpClient.class.getName());
        okHttpLogger.setLevel(Level.FINE);

        // Ensure there is a handler to print to the console
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINE);
        okHttpLogger.addHandler(consoleHandler);
    }

    public static void setCookieManagerLoggingFacility(Consumer<String> logConsumer) {
        simpleCookieManager.setLogData(logConsumer);
    }

    public static void removeCookiesOf(String domain) {
        simpleCookieManager.removeCookiesOf(domain);
    }


    public static void runAsync(String finalUrl, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);

        call.enqueue(callback);
    }

    public static void runAsyncPost(String finalUrl, RequestBody requestBody, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .post(requestBody) // Specify that this is a POST request
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);

        call.enqueue(callback); // Enqueue the call for execution
    }

    public static void runAsyncDelete(String finalUrl, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .delete()
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);

        call.enqueue(callback);
    }

    public static void runAsyncPut(String finalUrl, Request request, Callback callback) {
        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);

        call.enqueue(callback); // Enqueue the call for execution
    }


    public static void shutdown() {
        System.out.println("Shutting down HTTP CLIENT");
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
    }
}

