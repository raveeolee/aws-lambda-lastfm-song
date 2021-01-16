package dev.may_i;

import java.util.HashMap;
import java.util.Map;

public class LambdaContextHelper {

    public Map<String, Object> event(Map<String, Object> params,
                                     Map<String, Object> requestContext) {
        HashMap<String, Object> event = new HashMap<>();
        event.put("queryStringParameters", params);
        event.put("requestContext", requestContext);
        return event;
    }

    public Map<String, Object> event() {
        return event(params(), requestContext());
    }

    public Map<String, Object> params() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("user", "test");
        return params;
    }

    public Map<String, Object> requestContext() {
        HashMap<String, Object> requestContext = new HashMap<>();
        requestContext.put("domainName", "test.com");
        return requestContext;
    }
}
