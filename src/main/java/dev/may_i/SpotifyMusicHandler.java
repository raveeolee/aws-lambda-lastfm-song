package dev.may_i;


import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;

import javax.inject.Inject;
import java.util.Map;
import java.util.stream.Collectors;

public class SpotifyMusicHandler implements RequestHandler<Map<String,Object>, String> {

    private final Gson gson;
    private final DynamoDB dynamoDB;
    private final RequestExecutor requestExecutor;
    private SpotifyAuthService authService;

    @Inject
    public SpotifyMusicHandler(Gson gson,
                               DynamoDB dynamoDB,
                               RequestExecutor requestExecutor,
                               SpotifyAuthService authService
                               ) {
        this.gson = gson;
        this.dynamoDB = dynamoDB;
        this.requestExecutor = requestExecutor;
        this.authService = authService;
    }

    @Override
    public String handleRequest(Map<String,Object> event, Context context) {
        try {
            LambdaContext lambdaContext = new LambdaContext(gson, event, context);
            lambdaContext.logEnvironmentVariables(event, context);
            SpotifyToken accessToken = authService.getAccessToken(lambdaContext);

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