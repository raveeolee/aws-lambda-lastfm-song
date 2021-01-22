package dev.may_i;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.util.ImmutableMapParameter;
import dev.may_i.domain.SpotifyToken;
import dev.may_i.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpotifyAuthServiceTest {

    @Mock private DynamoDB dynamoDB;
    @Mock private Table table;
    @Mock private RequestExecutor requester;
    @InjectMocks private SpotifyAuthService spotifyAuthService;
    @Mock private LambdaContext context;

    @Test
    void getAccessToken_should_return_access_token_when_exists_in_the_db() {
        given(context.getDbTableName()).willReturn("DDB_TABLE");
        given(dynamoDB.getTable("DDB_TABLE")).willReturn(table);
        given(table.getItem(eq("id"), eq("token"))).willReturn(fakeItem());

        SpotifyToken accessToken = spotifyAuthService.getOrRequestAccessToken(context);
        assertThat(accessToken.toString())
                .isEqualTo("SpotifyToken{access_token='access_token', token_type='token_type', expires_in=0, refresh_token='refresh_token', scope='scope'}");
    }

    @Test
    @DisplayName(
            "When user gives the permission to the application spotify will return call back url. " +
            "When callback url is executed on lambda - it should saved the oauth code to the db" +
            "But client id and client secret must be provided"
    )
    void should_save_code_when_oauth_code_callback() {
        given(context.getDbTableName()).willReturn("DDB_TABLE");
        given(dynamoDB.getTable("DDB_TABLE")).willReturn(table);
        given(table.getItem(eq("id"), eq("token"))).willReturn(null);
        given(context.getQueryStringParameter("code"))
                .willReturn(Optional.of("12345"));

        assertThatThrownBy(() ->
                spotifyAuthService.getOrRequestAccessToken(context))
                .isInstanceOf(ApiException.class)
                .hasMessage("Please provide client id and secret");

        ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
        verify(table).putItem(itemCaptor.capture());

        assertThat(itemCaptor.getValue().get("id")).isEqualTo("code");
        assertThat(itemCaptor.getValue().get("code")).isEqualTo("12345");
    }

    @Test
    void should_save_clientId_and_secret() {
        given(dynamoDB.getTable("DDB_TABLE")).willReturn(table);
        given(table.getItem(eq("id"), eq("token"))).willReturn(null);

        LambdaContext context = context(
                ImmutableMapParameter.of(
                        "code", "code_value",
                        "client_id", "client_id_value",
                        "secret", "secret_value"
                ));

        assertThatThrownBy(() -> spotifyAuthService.getOrRequestAccessToken(context))
                .isInstanceOf(ApiException.class);

        ArgumentCaptor<Item> itemArgumentCaptor = ArgumentCaptor.forClass(Item.class);
        verify(table, times(2)).putItem(itemArgumentCaptor.capture());

        assertThat(itemArgumentCaptor.getAllValues().get(1).toString())
                .isEqualTo("{ Item: {id=client_id, client_id=client_id_value, secret=secret_value} }");
    }

    @Test
    void should_requestAuthCode_from_user() {
        given(dynamoDB.getTable("DDB_TABLE")).willReturn(table);
        given(table.getItem(eq("id"), eq("token"))).willReturn(null);

        LambdaContext context = context(
                ImmutableMapParameter.of(
                        "code", "code_value",
                        "client_id", "client_id_value",
                        "secret", "secret_value"
                ));

        assertThatThrownBy(() -> spotifyAuthService.getOrRequestAccessToken(context))
                .isInstanceOf(ApiException.class)
                .hasMessage("https://accounts.spotify.com/authorize?client_id=client_id_value&response_type=code&scope=user-read-playback-state%20user-modify-playback-state&redirect_uri=https://test.org/code");
    }

    @Test
    void should_use_previously_saved_code_if_present() {
        given(dynamoDB.getTable("DDB_TABLE")).willReturn(table);
        tableConfigurationForMissingTokenButPresentCode();
        SpotifyToken token = new SpotifyToken();
        token.setExpires_in(100L);

        given(requester.executeRequest(any(), any(), any())).willReturn(token);

        LambdaContext context = context(
                ImmutableMapParameter.of(
                        "code", "code_value",
                        "client_id", "client_id_value",
                        "secret", "secret_value"
                ));

        spotifyAuthService.getOrRequestAccessToken(context);
    }

    @Test
    void should_be_able_to_use_with_previously_saved_client_id() {
        given(context.getDbTableName()).willReturn("DDB_TABLE");
        given(dynamoDB.getTable("DDB_TABLE")).willReturn(table);
        tableConfigurationForClientIdAndCodePresent();
        SpotifyToken token = new SpotifyToken();
        token.setExpires_in(100L);
        given(requester.executeRequest(any(), any(), any())).willReturn(token);

        SpotifyToken tokenResult = spotifyAuthService.getOrRequestAccessToken(context);
        assertThat(tokenResult.isExpired()).isFalse();
    }

    @Test
    void get_or_request_token() {
        SpotifyToken token = mock(SpotifyToken.class);
        given(token.isExpired()).willReturn(false);
        given(token.getAccess_token()).willReturn("token-123");

        String access_token = spotifyAuthService.token(context, token);
        assertThat(access_token).isEqualTo("token-123");
    }

    @Test
    void get_or_request_token_when_token_expired() {
        SpotifyToken token = mock(SpotifyToken.class);
        given(token.isExpired()).willReturn(true);
        given(token.getAccess_token()).willReturn("token-123");
        given(context.getDbTableName()).willReturn("DDB_TABLE");
        given(dynamoDB.getTable("DDB_TABLE")).willReturn(table);
        given(table.getItem(eq("id"), eq("token"))).willReturn(fakeItem());

        SpotifyAuthService service = spy(spotifyAuthService);
        service.token(context, token);
        verify(service).getOrRequestAccessToken(eq(context));
    }

    private Item fakeItem() {
        Map<String, Object> params = ImmutableMapParameter.of(
                "access_token", "access_token",
                "token_type", "token_type",
                "refresh_token", "refresh_token",
                "scope", "scope"
        );
        return LambdaContextHelper.fakeItem(params);
    }

    private LambdaContext context(Map<String, String> params) {
        return new LambdaContext(null, null, null) {
            @Override
            public <T> Optional<T> getQueryStringParameter(String key) {
                return Optional.ofNullable((T) params.get(key));
            }

            @Override
            public String getDbTableName() {
                return "DDB_TABLE";
            }

            @Override
            public String getDomainName() {
                return "https://test.org/";
            }
        };
    }

    private void tableConfigurationForMissingTokenButPresentCode() {
        doAnswer(invocation -> {
            String param = invocation.getArgument(1).toString();
            if (param.equals("code")) {
                Item mock = mock(Item.class);
                given(mock.getString("code")).willReturn("1235");
                return mock;
            }
            return null;
        }).when(table).getItem(anyString(), anyString());
    }

    private void tableConfigurationForClientIdAndCodePresent() {
        doAnswer(invocation -> {
            String id = invocation.getArgument(1).toString();
            if (id.equals("code")) {
                Item mock = mock(Item.class);
                given(mock.getString("code")).willReturn("1235");
                return mock;
            }
            if (id.equals("client_id")) {
                return LambdaContextHelper.fakeItem(ImmutableMapParameter.of(
                        "client_id", "client_id_value",
                        "secret", "secret_value"));
            }
            return null;
        }).when(table).getItem(anyString(), anyString());
    }
}