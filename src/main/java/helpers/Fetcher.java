package helpers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;

import org.json.JSONArray;

import services.sentry.SentryService;

public class Fetcher {
    static final String POST = "POST";
    static final String GET = "GET";
    static final String PUT = "PUT";
    static final String DELETE = "DELETE";


    public static String fetch(String method, String url, Optional<Object> data, Optional<Map<String, String>> additionalHeaders) {
        String responseContent = null;
        try {
            method = useTypeSwitch(method);
            HttpClient client = HttpClient.newHttpClient();
            Builder builder = HttpRequest.newBuilder();
            HttpRequest request = null;

            builder.uri(URI.create(url).normalize());
            builder.header("Accept", "*");
            builder.header("Method", method);
            builder.header("Content-Type", "*");

            if (additionalHeaders != null && additionalHeaders.isPresent()) {
                additionalHeaders.get().forEach((k, v) -> builder.header(k, v));
            }

            request = builder.build();
            responseContent = client.sendAsync(request, BodyHandlers.ofString())
            .thenApply(HttpResponse::body).join();

        } catch (Exception e) {
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
            if(!responseContent.startsWith("[")) {
                responseContent = "[" + responseContent + "]";
            }
            return new JSONArray(responseContent);
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
        return null;
    }

}
