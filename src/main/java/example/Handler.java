package example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Handler implements RequestHandler<Map<String,Object>, String> {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String handleRequest(Map<String,Object> event, Context context) {
        logEnvironmentVariables(event, context);
        String url = createUrl(event, context);

        try {
            Document doc = scrapePage(url);

            String track   = doc.select(".chartlist-row .chartlist-name").first().text();
            String artist  = doc.select(    ".chartlist-row .chartlist-artist").first().text();

            return gson.toJson(new ArtistResponseJson(artist, track));

        } catch (Exception e) {
            throw error(e.getMessage(), 500, context);
        }
    }

    private Document scrapePage(String url) throws Exception {
        return Jsoup.connect(url).get();
    }
    
    private void logEnvironmentVariables(Map<String,Object> event, Context context) {
        LambdaLogger logger = context.getLogger();

        logger.log("ENVIRONMENT VARIABLES: " + gson.toJson(System.getenv()));
        logger.log("CONTEXT: " + gson.toJson(context));
        logger.log("EVENT: " + gson.toJson(event));
        logger.log("EVENT TYPE: " + event.getClass().toString());
    }

    private String createUrl(Map<String, Object> event, Context context) {
        Map<String, Object> queryStringParameters = (Map<String, Object>) event.getOrDefault("queryStringParameters", new LinkedHashMap<>());
        Object user = queryStringParameters.get("user");

        if (user == null) {
            throw error("User must be specified", 400, context);
        }

        return "https://www.last.fm/user/" + user.toString();
    }

    private RuntimeException error(String errorMessage, int code, Context context) {
        Map<String, Object> errorPayload = new HashMap();
        errorPayload.put("errorType", "BadRequest");
        errorPayload.put("httpStatus", code);
        errorPayload.put("requestId", context.getAwsRequestId());
        errorPayload.put("message", errorMessage);
        String message = gson.toJson(errorPayload);
        return new RuntimeException(message);
    }
}