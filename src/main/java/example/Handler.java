package example;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;

import java.io.IOException;
import java.util.*;

public class Handler implements RequestHandler<Map<String,Object>, String> {
    private static final String GET_CODE_URL =
            "https://accounts.spotify.com/authorize?client_id=%s&response_type=code&scope=user-read-playback-state&redirect_uri=%s";

    private static final String TOKEN_URL =
            "https://accounts.spotify.com/api/token";

    public static final MediaType FORM =
            MediaType.parse("application/x-www-form-urlencoded");

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private DynamoDB initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        return new DynamoDB(client);
    }

    @Override
    public String handleRequest(Map<String,Object> event, Context context) {
        try {

            logEnvironmentVariables(event, context);
            DynamoDB dynamoDB = initDynamoDbClient();
            Table accessKeyTbl = dynamoDB.getTable(System.getenv().get("DDB_TABLE"));

            checkThisIsCallBack(accessKeyTbl, event);
            UserCredentials credentials = checkClientIdSecretPresent(accessKeyTbl, event);
            String redirectUrl = getDomainName(event);
            String code = getAuthCode(accessKeyTbl, credentials.getClientId(), redirectUrl);

            SpotifyToken accessToken =
                    getOrSaveToken(
                            accessKeyTbl,
                            code,
                            credentials,
                            redirectUrl
                    );

            return gson.toJson(accessToken);
        } catch (Exception e) {
            return gson.toJson(new ApiError(e.getMessage()));
        }
    }

    private SpotifyToken getOrSaveToken(Table db,
                                        String code,
                                        UserCredentials credentials,
                                        String redirect) throws IOException {

        Item item = db.getItem("id", "token");
        if (item == null) {
            SpotifyToken token = getAccessToken(code, credentials, redirect);
            db.putItem(token.toItem());
            return token;
        }
        return new SpotifyToken(item);
    }

    private SpotifyToken getAccessToken(
                                        String code,
                                        UserCredentials credentials,
                                        String redirect) throws IOException {

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .addEncoded("grant_type", "authorization_code")
                .addEncoded("code", code)
                .addEncoded("redirect_uri", redirect)
                .build();

        Request request = new Request.Builder()
                .addHeader("Authorization",
                        Credentials.basic(credentials.getClientId(), credentials.getSecret()))
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url(TOKEN_URL)
                .post(body)
                .build();

        return executeRequest(request, client, SpotifyToken.class);
    }

    private <T> T executeRequest(Request request, OkHttpClient client, Class<T> tClass) throws IOException {
        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            return gson.fromJson(response.body().string(), tClass);
        }
    }



    private void checkThisIsCallBack(Table db, Map<String, Object> event) {
        Object code = getQueryStringParameter(event, "code");
        if (code == null) {
            return;
        }

        db.putItem(new Item().with("id", "code").with("code", code));
    }

    private <T> UserCredentials checkClientIdSecretPresent(Table db, Map<String, T> event) {
        T client_id = getQueryStringParameter(event, "client_id");
        T secret    = getQueryStringParameter(event, "secret");

        Item dbItem = db.getItem("id", "client_id");
        if (dbItem == null && (client_id == null || secret == null)) {
            throw new ApiException("Please provide client id and secret");
        }

        Object clientFromDb = dbItem == null ? null : dbItem.get("client_id");
        if (dbItem == null || clientFromDb == null) {
            db.putItem(new Item().with("id", "client_id")
                    .with("client_id", client_id)
                    .with("secret",    secret)
            );

            return new UserCredentials(
                    client_id.toString(),
                    secret.toString()
            );
        }

        return new UserCredentials(
                clientFromDb.toString(),
                dbItem.get("secret").toString()
        );
    }

    private String getAuthCode(Table db, String clientId, String redirectUrl) throws Exception {
        Item item = db.getItem("id", "code");
        if (item != null) {
            return item.get("code").toString();
        }

        String url = String.format(GET_CODE_URL, clientId, redirectUrl);
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