package helpers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.json.JSONArray;
import org.json.JSONObject;

import services.sentry.SentryService;

public class BypassedFetcher {
    static final String POST = "POST";
    static final String GET = "GET";
    static final String PUT = "PUT";
    static final String DELETE = "DELETE";

    public static String fetch(String method, String url, Optional<String> data,
            Optional<Map<String, String>> additionalHeaders) {
        String responseContent = null;

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL"); // OR TLS
            sslContext.init(null, new TrustManager[] { new helpers.TrustManager() }, new SecureRandom());
            HttpClient client = HttpClient.newBuilder().sslContext(sslContext).build();

            method = useTypeSwitch(method);
            Builder builder = HttpRequest.newBuilder();
            HttpRequest request = null;

            builder.uri(URI.create(url).normalize());
            builder.header("Accept", "*");
            builder.header("Method", method);
            builder.header("Content-Type", "*");

            if (additionalHeaders != null && additionalHeaders.isPresent()) {
                additionalHeaders.get().forEach((k, v) -> builder.header(k, v));
            }

            if (data != null && data.isPresent()) {
                final String sampleData = data.get();
                builder.method(method, HttpRequest.BodyPublishers.ofString(sampleData));
            }

            request = builder.build();
            responseContent = client.sendAsync(request, BodyHandlers.ofString())
                    .thenApply(HttpResponse::body).join();

        } catch (Exception e) {
            Logger logger = Logger.getLogger("WDMC:" + BypassedFetcher.class.getSimpleName());
            if (responseContent == null) {
                var resp = new JSONObject();
                resp.put("error", "Server did not respond...");
                resp.put("http", 404);

                logger.warning("Server did not respond...");
                responseContent = resp.toString();
            }
            
            SentryService.captureEx(e);
        }

        return responseContent;
    }

    static String useTypeSwitch(String type) {
        String resolved = "get";
        final String key = type.toUpperCase();
        switch (key) {
            case GET:
                resolved = GET;
                break;
            case POST:
                resolved = POST;
                break;
            case PUT:
                resolved = PUT;
                break;
            case DELETE:
                resolved = DELETE;
                break;
            default:
                break;
        }
        return resolved;
    }

    public static final JSONArray toJson(String responseContent) {
        try {
            if (responseContent != null && !responseContent.startsWith("[")) {
                responseContent = "[" + responseContent + "]";
            }
            return new JSONArray(responseContent);
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
        return null;
    }

}
