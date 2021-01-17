package dev.may_i;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.util.ImmutableMapParameter;
import dev.may_i.domain.SpotifyToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
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

        SpotifyToken accessToken = spotifyAuthService.getAccessToken(context);
        assertThat(accessToken.toString())
                .isEqualTo("SpotifyToken{access_token='access_token', token_type='token_type', expires_in=0, refresh_token='refresh_token', scope='scope'}");
    }

    @Test
    void getOrSaveToken() {
    }

    @Test
    void requestAuthCode() {
    }

    @Test
    void makeSureClientIdAndSecretPresent() {
    }

    @Test
    void saveCodeWhenOauthCodeCallback() {
    }



    private Item fakeItem() {
        Map<String, String> params = ImmutableMapParameter.of(
                "access_token", "access_token",
                "token_type", "token_type",
                "refresh_token", "refresh_token",
                "scope", "scope"
        );

        return new Item() {

            @Override
            public String getString(String attrName) {
                return params.get(attrName);
            }

            @Override
            public long getLong(String attrName) {
                return 0L;
            }
        };
    }
}