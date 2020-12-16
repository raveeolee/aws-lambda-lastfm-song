package example;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class HandlerIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(HandlerIntegrationTest.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    void invokeTest() {
        logger.info("Invoke TEST");
        HashMap<String, Object> event = new HashMap<>();
        HashMap<String, Object> params = new HashMap<>();

        params.put("user", "test");
        event.put("queryStringParameters", params);

        Context context = new TestContext();
        Handler handler = new Handler();

        String result = handler.handleRequest(event, context);

        ArtistResponseJson artistResponseJson = gson.fromJson(result, ArtistResponseJson.class);
        assertNotNull(artistResponseJson.artist);
        assertNotNull(artistResponseJson.track);

        System.out.println(result);
    }

  @Test
  void onUserMissing() {
    HashMap<String, Object> event = new HashMap<>();
    HashMap<String, Object> params = new HashMap<>();

    Context context = new TestContext();
    Handler handler = new Handler();

    String message = Assertions.assertThrows(RuntimeException.class, () -> {
      handler.handleRequest(event, context);
    }).getMessage();

    assertEquals(
            "{\n" +
            "  \"errorType\": \"BadRequest\",\n" +
            "  \"requestId\": \"495b12a8-xmpl-4eca-8168-160484189f99\",\n" +
            "  \"httpStatus\": 400,\n" +
            "  \"message\": \"User must be specified\"\n" +
            "}",
            message);
  }
}
