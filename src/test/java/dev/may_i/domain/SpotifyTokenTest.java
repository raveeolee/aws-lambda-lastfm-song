package dev.may_i.domain;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.util.ImmutableMapParameter;
import dev.may_i.LambdaContext;
import dev.may_i.LambdaContextHelper;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SpotifyTokenTest {

    @Test
    void isExpired() {
        Item item = LambdaContextHelper.fakeItem(
                ImmutableMapParameter.of(
                        "expires_in", 3600L
                )
        );

        SpotifyToken token = new SpotifyToken(item);
        assertThat(token.isExpired()).isTrue();
    }

    @Test
    void isNotExpired() {
        Item item = LambdaContextHelper.fakeItem(
                ImmutableMapParameter.of(
                        "expires_in", System.currentTimeMillis() / 1000 + 3600L
                )
        );
        SpotifyToken token = new SpotifyToken(item);
        assertThat(token.isExpired()).isFalse();

        SpotifyToken token1 = new SpotifyToken(token, 0L);
        assertThat(token1.getExpires_in()).isEqualTo(0L);
    }
}