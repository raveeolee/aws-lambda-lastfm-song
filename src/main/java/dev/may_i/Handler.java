package dev.may_i;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Map;
import java.util.stream.Collectors;

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

            SpotifyAuthService authService = new SpotifyAuthService(lambdaContext, dynamoDB, requestExecutor);
            SpotifyToken accessToken = authService.getAccessToken();

            SpotifyService spotifyService = new SpotifyService(accessToken, authService, requestExecutor);
            SpotifyTrack spotifyTrack = spotifyService.currentTrack();
            String artists = spotifyTrack.getItem().getArtists().stream()
                    .map(SpotifyTrack.Artist::getName)
                    .collect(Collectors.joining(","));

            return gson.toJson(new ArtistResponseJson(artists, spotifyTrack.getItem().getName()));

        } catch (Throwable e) {
            return gson.toJson(new ApiError(e.getMessage()));
        }
    }
}