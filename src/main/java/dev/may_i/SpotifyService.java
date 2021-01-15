package dev.may_i;

import okhttp3.Request;

public class SpotifyService {
    private static final String CURRENT_TRACK = "https://api.spotify.com/v1/me/player/currently-playing?market=GB";

    private SpotifyToken accessToken;
    private SpotifyAuthService authService;
    private RequestExecutor requestExecutor;

    public SpotifyService(SpotifyToken accessToken, SpotifyAuthService authService, RequestExecutor requestExecutor) {
        this.accessToken = accessToken;
        this.authService = authService;
        this.requestExecutor = requestExecutor;
    }

    public SpotifyTrack currentTrack() {
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + token())
                .addHeader("Content-Type", "application/json")
                .url(CURRENT_TRACK)
                .get()
                .build();
        return requestExecutor.executeRequest(request, SpotifyTrack.class);
    }

    private String token() {
        if (accessToken.isExpired()) {
            authService.getAccessToken().getAccess_token();
        }
        return accessToken.getAccess_token();
    }
}
