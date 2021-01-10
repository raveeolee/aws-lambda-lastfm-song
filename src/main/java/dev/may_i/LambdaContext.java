package dev.may_i;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class LambdaContext {

    private final Gson gson;
    private final Map<String, Object> event;
    private final Context context;

    public LambdaContext(Gson gson, Map<String, Object> event, Context context) {
        this.gson = gson;
        this.event = event;
        this.context =  context;
    }

    public void logEnvironmentVariables(Map<String,Object> event, Context context) {
        LambdaLogger logger = context.getLogger();

        logger.log("ENVIRONMENT VARIABLES: " + gson.toJson(System.getenv()));
        logger.log("CONTEXT: " +               gson.toJson(context));
        logger.log("EVENT: " +                 gson.toJson(event));
        logger.log("EVENT TYPE: " +            event.getClass().toString());
    }

    public void log(String message) {
        context.getLogger().log(message);
    }

    public String getDomainName() {
        Map<String, Object> requestContext = getSubContext("requestContext");
        return "https://" + requestContext.get("domainName") + "/code";
    }

    public <T> Optional<T> getQueryStringParameter(String key) {
        Map<String, T> queryStringParameters = getSubContext("queryStringParameters");
        if (queryStringParameters.isEmpty()) {
            return Optional.empty();
        }

        T value = queryStringParameters.get(key);
        return Optional.ofNullable(value);
    }

    @SuppressWarnings("unchecked")
    private <T> Map<String, T> getSubContext(String name) {
        if ((event != null)) {
            return (Map<String, T>) event.get(name);
        }
        return Collections.emptyMap();
    }
}
