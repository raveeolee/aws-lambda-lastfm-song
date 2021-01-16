package dev.may_i;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

public class AwsLambdaHandler implements RequestHandler<Map<String,Object>, String> {
    private final SpotifyMusicHandler handler;
    private final HandlerProvider injector;

    public AwsLambdaHandler() {
        this.injector = DaggerHandlerProvider.create();
        this.handler = provider();
    }

    protected SpotifyMusicHandler provider() {
        return injector.handler();
    }

    @Override
    public String handleRequest(Map<String, Object> input, Context context) {
        return handler.handleRequest(input, context);
    }
}
