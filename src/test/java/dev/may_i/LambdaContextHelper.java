package dev.may_i;

import com.amazonaws.services.dynamodbv2.document.Item;

import java.util.HashMap;
import java.util.Map;

public class LambdaContextHelper {

    public static Map<String, Object> event(Map<String, Object> params,
                                     Map<String, Object> requestContext) {
        HashMap<String, Object> event = new HashMap<>();
        event.put("queryStringParameters", params);
        event.put("requestContext", requestContext);
        return event;
    }

    public static Map<String, Object> event() {
        return event(params(), requestContext());
    }

    public static Map<String, Object> params() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("user", "test");
        return params;
    }

    public static Map<String, Object> requestContext() {
        HashMap<String, Object> requestContext = new HashMap<>();
        requestContext.put("domainName", "test.com");
        return requestContext;
    }

    public static Item fakeItem(Map<String, Object> params) {
        return new Item() {

            @Override
            public String getString(String attrName) {
                return params.getOrDefault(attrName, "").toString();
            }

            @Override
            public long getLong(String attrName) {
                return Long.parseLong(params.getOrDefault(attrName, 0L).toString());
            }
        };
    }
}
