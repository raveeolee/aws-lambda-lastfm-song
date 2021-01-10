package dev.may_i;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.gson.Gson;
import java.util.Map;

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

    public String getDomainName() {
        Map<String, Object> requestContext = getSubContext("requestContext");
        return "https://" + requestContext.get("domainName") + "/code";
    }

    public <T> T getQueryStringParameter(String key) {
        Map<String, T> queryStringParameters = getSubContext("queryStringParameters");
        if (queryStringParameters == null) {
            throw new ContextException(String.format("Parameter [%s] not found", key));
        }
        return queryStringParameters.get(key);
    }

    @SuppressWarnings("unchecked")
    private <T> Map<String, T> getSubContext(String name) {
        if ((context instanceof Map)) {
            return (Map<String, T>) event.get(name);
        }
        throw new ContextException("Context is not instance of Map.class");
    }
}
