package dev.may_i;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import dev.may_i.domain.SpotifyToken;
import dev.may_i.domain.SpotifyTrack;
import dev.may_i.exception.ApiError;

import javax.inject.Inject;
import java.util.Map;

public class SpotifyMusicHandler implements RequestHandler<Map<String,Object>, String> {

    private final Gson gson;
    private final SpotifyAuthService authService;
    private final SpotifyService spotifyService;

    @Inject
    public SpotifyMusicHandler(Gson gson, SpotifyAuthService authService, SpotifyService spotifyService) {
        this.gson = gson;
        this.authService = authService;
        this.spotifyService = spotifyService;
    }

    @Override
    public String handleRequest(Map<String,Object> event, Context context) {
        try {
            LambdaContext lambdaContext = new LambdaContext(gson, event, context);
            lambdaContext.logEnvironmentVariables(event, context);

            SpotifyToken accessToken = authService.getOrRequestAccessToken(lambdaContext);
            SpotifyTrack spotifyTrack = spotifyService.currentTrack(lambdaContext, accessToken);

            return gson.toJson(spotifyTrack.toJson());

        } catch (Throwable e) {
            return gson.toJson(new ApiError(e.getMessage()));
        }
    }
}