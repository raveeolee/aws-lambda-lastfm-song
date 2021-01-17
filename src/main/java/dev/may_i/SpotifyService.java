package dev.may_i;

import dev.may_i.domain.SpotifyToken;
import dev.may_i.domain.SpotifyTrack;
import okhttp3.Request;

import javax.inject.Inject;

public class SpotifyService {
    private static final String CURRENT_TRACK = "https://api.spotify.com/v1/me/player/currently-playing?market=GB";

    private SpotifyAuthService authService;
    private RequestExecutor requestExecutor;

    @Inject
    public SpotifyService(SpotifyAuthService authService, RequestExecutor requestExecutor) {
        this.authService = authService;
        this.requestExecutor = requestExecutor;
    }

    public SpotifyTrack currentTrack(LambdaContext context, SpotifyToken accessToken) {
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + token(context, accessToken))
                .addHeader("Content-Type", "application/json")
                .url(CURRENT_TRACK)
                .get()
                .build();
        return requestExecutor.executeRequest(request, SpotifyTrack.class);
    }

    private String token(LambdaContext context, SpotifyToken accessToken) {
        if (accessToken.isExpired()) {
            authService.getAccessToken(context).getAccess_token();
        }
        return accessToken.getAccess_token();
    }
}
