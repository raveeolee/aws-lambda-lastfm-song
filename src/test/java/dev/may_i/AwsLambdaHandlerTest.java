package dev.may_i;

import com.amazonaws.services.lambda.runtime.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AwsLambdaHandlerTest {

    @Spy
    private AwsLambdaHandler lambdaHandler;

    @Mock
    Context context;

    @Mock
    Handler handler;

    @Test
    void handleRequest() {
        AwsLambdaHandler awsLambdaHandler = new AwsLambdaHandler() {
            @Override
            protected Handler provider() {
                return handler;
            }
        };

        HashMap<String, Object> event = new HashMap<>();
        awsLambdaHandler.handleRequest(event, context);

        verify(handler).handleRequest(eq(event), eq(context));
    }

    @Test
    void test_handler_creation() {
        Handler provider = new AwsLambdaHandler().provider();
        assertThat(provider).isNotNull().isInstanceOf(Handler.class);
    }
}