package dev.may_i.domain;

import com.amazonaws.services.dynamodbv2.document.Item;

import java.math.BigDecimal;

public class SpotifyToken {
    private String access_token;
    private String token_type;
    private Long expires_in;
    private String refresh_token;
    private String scope;

    public SpotifyToken(String access_token, String token_type, Long expires_in, String refresh_token, String scope) {
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.refresh_token = refresh_token;
        this.scope = scope;
    }

    public SpotifyToken() {
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public Long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Long expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Item toItem() {
        return new Item()
                .with("id", "token")
                .with("access_token", access_token)
                .with("token_type", token_type)
                .with("expires_in", expires_in)
                .with("refresh_token", refresh_token)
                .with("scope", scope);
    }

    public SpotifyToken(Item item) {
        this.access_token = item.get("access_token").toString();
        this.token_type = item.get("token_type").toString();
        this.expires_in = ((BigDecimal) item.get("expires_in")).longValue();
        this.refresh_token = item.get("refresh_token").toString();
        this.scope = item.get("scope").toString();
    }

    public boolean isExpired() {
        return false;
    }
}