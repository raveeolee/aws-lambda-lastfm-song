package dev.may_i;

import dev.may_i.domain.SpotifyToken;
import dev.may_i.domain.SpotifyTrack;
import okhttp3.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SpotifyServiceTest {

    @Mock private SpotifyAuthService authService;
    @Mock private RequestExecutor requestExecutor;
    @Mock private LambdaContext lambdaContext;
    @Mock private SpotifyToken spotifyToken;

    @InjectMocks private SpotifyService spotifyService;

    @Test
    void currentTrack() {
        given(authService.token(eq(lambdaContext), eq(spotifyToken))).willReturn("token");
        spotifyService.currentTrack(lambdaContext, spotifyToken);

        ArgumentCaptor<Request> requestArgumentCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestExecutor).executeRequest(requestArgumentCaptor.capture(), eq(SpotifyTrack.class));

        Request value = requestArgumentCaptor.getValue();
        assertThat(value.header("Authorization")).isEqualTo("Bearer token");
        assertThat(value.header("Content-Type")).isEqualTo("application/json");
        assertThat(value.url().toString()).isEqualTo("https://api.spotify.com/v1/me/player/currently-playing?market=GB");
    }
}