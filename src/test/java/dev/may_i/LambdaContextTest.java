package dev.may_i;

import com.amazonaws.services.dynamodbv2.xspec.L;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LambdaContextTest {

    private LambdaContext context;
    private LambdaContextHelper lambdaContextHelper = new LambdaContextHelper();
    private Gson gson = new GsonBuilder().create();

    @Spy
    private TestContext testContext;

    @BeforeEach
    void setUp() {
        this.context = new LambdaContext(
               gson,
               lambdaContextHelper.event(
                       lambdaContextHelper.params(),
                       lambdaContextHelper.requestContext()
               ),
               testContext
        );
    }

    @Test
    void logEnvironmentVariables() {
        Context mock = new TestContext();
        context.logEnvironmentVariables(lambdaContextHelper.event(), mock);
    }

    @Test
    void log() {
        LambdaLogger mock = mock(LambdaLogger.class);
        given(testContext.getLogger()).willReturn(mock);
        context.log("Boom");

        verify(testContext).getLogger();
        verify(mock).log(eq("Boom"));
    }

    @Test
    void getDomainName() {
        String domainName = context.getDomainName();
        assertThat(domainName).isEqualTo("https://test.com/");
    }

    @Test
    void getQueryStringParameter_missing() {
        assertThat(context.getQueryStringParameter("non_existing"))
                .isEmpty();
    }

    @Test
    void getQueryStringParameter() {
        assertThat(context.getQueryStringParameter("user"))
                .isEqualTo(Optional.of("test"));
    }

    @Test
    void getQueryStringParameter_empty_context() {
        LambdaContext emptyContext = new LambdaContext(gson, Collections.emptyMap(), new TestContext());
        assertThat(emptyContext.getQueryStringParameter("user"))
                .isEmpty();
    }
}