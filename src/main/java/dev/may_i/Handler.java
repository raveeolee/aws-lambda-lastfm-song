package dev.may_i;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Map;

public class Handler implements RequestHandler<Map<String,Object>, String> {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private DynamoDB initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        return new DynamoDB(client);
    }

    @Override
    public String handleRequest(Map<String,Object> event, Context context) {
        try {
            LambdaContext lambdaContext = new LambdaContext(gson, event, context);
            lambdaContext.logEnvironmentVariables(event, context);

            DynamoDB dynamoDB = initDynamoDbClient();
            RequestExecutor requestExecutor = new RequestExecutor(gson);

            SpotifyService spotifyService = new SpotifyService(lambdaContext, dynamoDB, requestExecutor);
            SpotifyToken accessToken = spotifyService.getAccessToken();

            return gson.toJson(accessToken);
        } catch (Exception e) {
            return gson.toJson(new ApiError(e.getMessage()));
        }
    }
}