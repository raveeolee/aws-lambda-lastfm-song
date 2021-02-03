package dev.may_i;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;
import dev.may_i.configuration.LambdaModule;
import dev.may_i.domain.SpotifyToken;
import dev.may_i.domain.SpotifyTrack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SpotifyMusicHandlerTest {

    private SpotifyMusicHandler musicHandler;
    private Gson gson;

    @Mock
    private SpotifyAuthService spotifyAuthService;

    @Mock
    private SpotifyService spotifyService;

    private final LambdaContextHelper contextHelper = new LambdaContextHelper();

    @BeforeEach
    void setUp() {
        gson = new LambdaModule().gson();
        musicHandler = new SpotifyMusicHandler(
                gson,
                spotifyAuthService,
                spotifyService
        );
    }

    @Test
    void handleRequest_should_create_context() {
        // given
        Map<String, Object> event = contextHelper.event(contextHelper.params(), contextHelper.requestContext());
        Context context = new TestContext();

        // when
        musicHandler.handleRequest(event, context);

        // then
        ArgumentCaptor<LambdaContext> lambdaContextCaptor = ArgumentCaptor.forClass(LambdaContext.class);
        verify(spotifyAuthService).requestAccessToken(lambdaContextCaptor.capture());
        assertThat(lambdaContextCaptor.getValue()).isNotNull();
        assertThat(lambdaContextCaptor.getValue()
                .getDomainName())
                .isEqualTo("https://test.com/");
    }

    @Test
    void should_use_the_token_to_display_current_track() {
        // given
        SpotifyToken token = mock(SpotifyToken.class);
        given(spotifyAuthService.requestAccessToken(any())).willReturn(token);
        Map<String, Object> event = contextHelper.event(contextHelper.params(), contextHelper.requestContext());
        Context context = new TestContext();

        // when
        musicHandler.handleRequest(event, context);
        verify(spotifyService).currentTrack(any(), eq(token));
    }

    @Test
    void should_display_current_artist() {
        // given
        Map<String, Object> event = contextHelper.event(contextHelper.params(), contextHelper.requestContext());
        Context context = new TestContext();
        given(spotifyService.currentTrack(any(), any()))
                .willReturn(new SpotifyTrack(new SpotifyTrack.Item("Song",
                        Arrays.asList(
                                new SpotifyTrack.Artist("Artist1"),
                                new SpotifyTrack.Artist("Artist2")
                        ))));

        // when
        String json = musicHandler.handleRequest(event, context);
        assertThat(json).isEqualTo(
                "{\"artist\":\"Artist1,Artist2\",\"track\":\"Song\"}");
    }

    @Test
    void onError_should_return_error_response() {
        given(spotifyService.currentTrack(any(), any()))
                .willThrow(new RuntimeException("Some Issue"));

        assertThat(musicHandler.handleRequest(Collections.emptyMap(), new TestContext()))
                .isEqualTo("{\"error\":\"Some Issue\"}");
    }

}