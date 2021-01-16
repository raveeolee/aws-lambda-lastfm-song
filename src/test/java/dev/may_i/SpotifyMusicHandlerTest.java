package dev.may_i;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;
import dev.may_i.configuration.LambdaModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SpotifyMusicHandlerTest {

    private SpotifyMusicHandler musicHandler;
    private Gson gson;

    @Mock
    private DynamoDB dynamoDB;

    @Mock
    private RequestExecutor requestExecutor;

    @Mock
    private SpotifyAuthService spotifyAuthService;

    @BeforeEach
    void setUp() {
        gson = new LambdaModule().gson();
        musicHandler = new SpotifyMusicHandler(gson, dynamoDB, requestExecutor, spotifyAuthService);
    }

    @Test
    void handleRequest() {
        HashMap<String, Object> event = new HashMap<>();
        HashMap<String, Object> params = new HashMap<>();

        params.put("user", "test");
        event.put("queryStringParameters", params);

        Context context = new TestContext();

        musicHandler.handleRequest(event, context);
    }
}