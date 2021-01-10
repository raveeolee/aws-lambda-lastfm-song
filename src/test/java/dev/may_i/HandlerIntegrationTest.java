package dev.may_i;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

        //handler.handleRequest(event, context);


    }

    @Test
    void onUserMissing() {

    }
}
