package dev.may_i;

import dev.may_i.domain.SpotifyToken;
import dev.may_i.domain.SpotifyTrack;
import okhttp3.Request;
import javax.inject.Inject;

import java.util.Map;

import static okhttp3.internal.Util.EMPTY_REQUEST;

public class SpotifyService {
    private static final String CURRENT_TRACK = "https://api.spotify.com/v1/me/player/currently-playing?market=GB";
    private static final String NEXT_TRACK = "https://api.spotify.com/v1/me/player/next";
    private static final String PREVIOUS_TRACK = "https://api.spotify.com/v1/me/player/previous";

    private SpotifyAuthService authService;
    private RequestExecutor requestExecutor;

    @Inject
    public SpotifyService(SpotifyAuthService authService, RequestExecutor requestExecutor) {
        this.authService = authService;
        this.requestExecutor = requestExecutor;
    }

    public SpotifyTrack currentTrack(LambdaContext context, SpotifyToken accessToken) {
        Request request = request(context, accessToken)
                .url(CURRENT_TRACK)
                .get()
                .build();
        return requestExecutor.executeRequest(request, SpotifyTrack.class);
    }

    private Request.Builder request(LambdaContext context, SpotifyToken accessToken) {
        return new Request.Builder()
                .addHeader("Authorization", "Bearer " + authService.token(context, accessToken))
                .addHeader("Content-Type", "application/json");
    }

    public void nextTrack(LambdaContext context, SpotifyToken accessToken) {
        requestExecutor.executeRequest(request(context, accessToken).url(NEXT_TRACK).post(EMPTY_REQUEST).build(), Object.class);
    }

    public void previousTrack(LambdaContext context, SpotifyToken accessToken) {
        requestExecutor.executeRequest(request(context, accessToken).url(PREVIOUS_TRACK).post(EMPTY_REQUEST).build(), Object.class);
    }

    public void switchTrack(LambdaContext lambdaContext, SpotifyToken token) {
        lambdaContext.getQueryStringParameter("switch").ifPresent(switch_track -> {
            if ("next".equals(switch_track)) {
                nextTrack(lambdaContext, token);
            }
            if ("previous".equals(switch_track)) {
                previousTrack(lambdaContext, token);
            }
        });
    }
}
