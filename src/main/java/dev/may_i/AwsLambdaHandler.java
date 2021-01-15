package dev.may_i;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

public class AwsLambdaHandler implements RequestHandler<Map<String,Object>, String> {
    private final Handler handler;

    public AwsLambdaHandler() {
        this.handler = provider();
    }

    protected Handler provider() {
        return DaggerHandlerProvider.create().handler();
    }

    @Override
    public String handleRequest(Map<String, Object> input, Context context) {
        return handler.handleRequest(input, context);
    }
}
