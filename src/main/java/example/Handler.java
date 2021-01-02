package example;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;

import java.util.*;

public class Handler implements RequestHandler<Map<String,Object>, String> {
    private static final String GET_CODE_URL =
            "https://accounts.spotify.com/authorize?client_id=%s&response_type=code&scope=user-read-playback-state&redirect_uri=%s";


    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private DynamoDB initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        //standard.setRegion(Region.getRegion(Regions.EU_WEST_2).getName());
        return new DynamoDB(client);
    }

    @Override
    public String handleRequest(Map<String,Object> event, Context context) {
        try {

            logEnvironmentVariables(event, context);
            DynamoDB dynamoDB = initDynamoDbClient();
            Table access_key = dynamoDB.getTable(System.getenv().get("DDB_TABLE"));

            String clientId = checkClientIdIsPresent(access_key, event);
            String code = getAuthCode(access_key, clientId, event);

            Map<String, Object> map = new HashMap<>();
            map.put("client_id", clientId);
            map.put("auth_code", code);

            return gson.toJson(map);
        } catch (Exception e) {
            return gson.toJson(new ApiError(e.getMessage()));
        }
    }

    private <T> String checkClientIdIsPresent(Table db, Map<String, T> event) {
        T client_id = getQueryStringParameter(event, "client_id");
        Item item = db.getItem("id", "client_id");
        if (item == null && client_id == null) {
            throw new ApiException("Please provide client id");
        }

        Object clientFromDb = item == null ? null : item.get("client_id");
        if (item == null || clientFromDb == null) { // override
            db.putItem(new Item().with("id", "client_id").with("client_id", client_id));
            return client_id.toString();
        }

        return clientFromDb.toString();
    }

    private String getAuthCode(Table db, String clientId, Map<String, Object> event) throws Exception {
        Item item = db.getItem("id", "code");
        if (item != null) {
            return item.get("code").toString();
        }

        String url = String.format(GET_CODE_URL, clientId, getDomainName(event));
        /*OkHttpClient client = new OkHttpClient();*/
        /*Request request = new Request.Builder()
                .url(url)
                .build(); // defaults to GET

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }*/
        // Create Redirect?
        throw new ApiException(url);
    }

    private <T> T getQueryStringParameter(Map<String, T> event, String key) {
        Map<String, T> queryStringParameters = (Map<String, T>) event.get("queryStringParameters");
        if (queryStringParameters == null) {
            return null;
        }
        return queryStringParameters.get(key);
    }

    private void logEnvironmentVariables(Map<String,Object> event, Context context) {
        LambdaLogger logger = context.getLogger();

        logger.log("ENVIRONMENT VARIABLES: " + gson.toJson(System.getenv()));
        logger.log("CONTEXT: " + gson.toJson(context));
        logger.log("EVENT: " + gson.toJson(event));
        logger.log("EVENT TYPE: " + event.getClass().toString());
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

    private String getDomainName(Map<String, Object> event) {
        Map<String, Object> requestContext = (Map<String, Object>) event.get("requestContext");
        return "https://" + requestContext.get("domainName") + "/code";
    }
}