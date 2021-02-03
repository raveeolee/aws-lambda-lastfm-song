package dev.may_i;

import com.amazonaws.services.lambda.runtime.Context;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Spy
    private Handler lambdaHandler;

    @Mock
    Context context;

    @Mock
    SpotifyMusicHandler spotifyMusicHandler;

    @Test
    void handleRequest() {
        Handler handler = new Handler() {
            @Override
            protected SpotifyMusicHandler provider() {
                return spotifyMusicHandler;
            }
        };

        HashMap<String, Object> event = new HashMap<>();
        handler.handleRequest(event, context);

        verify(this.spotifyMusicHandler).handleRequest(eq(event), eq(context));
    }

    @Test
    void test_handler_creation() {
        Handler handler = new Handler();

        SpotifyMusicHandler provider = handler.provider();
        assertThat(provider).isNotNull().isInstanceOf(SpotifyMusicHandler.class);
    }
}