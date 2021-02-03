package dev.may_i;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import dev.may_i.domain.SpotifyToken;
import dev.may_i.domain.UserCredentials;
import dev.may_i.exception.ApiException;
import okhttp3.*;

import javax.inject.Inject;
import java.util.Optional;

public class SpotifyAuthService {
    private static final String GET_CODE_URL =
            "https://accounts.spotify.com/authorize?client_id=CLIENT&response_type=code&scope=user-read-playback-state%20user-modify-playback-state&redirect_uri=URI";

    private static final String TOKEN_URL =
            "https://accounts.spotify.com/api/token";

    private final DynamoDB dynamoDB;
    private final RequestExecutor requester;

    @Inject
    public SpotifyAuthService(DynamoDB dynamoDB,
                              RequestExecutor requester) {
        this.dynamoDB = dynamoDB;
        this.requester = requester;
    }

    public SpotifyToken getOrSaveToken(Table db,
                                       String code,
                                       UserCredentials credentials,
                                       String redirect
    ) {
        return getExistingTokenFromDb(db)
                .orElseGet(() -> getNewToken(db, code, credentials, redirect));
    }

    public SpotifyToken getNewToken(Table db,
                                    String code,
                                    UserCredentials credentials,
                                    String redirect) {
        SpotifyToken token = requestAccessToken(code, credentials, redirect);
        db.putItem(token.toItem());
        return token;
    }

    private Optional<SpotifyToken> getExistingTokenFromDb(Table db) {
        return Optional.ofNullable(db.getItem("id", "token"))
                .map(SpotifyToken::new);
    }

    private SpotifyToken requestAccessToken(String code, UserCredentials credentials, String redirect) {
        RequestBody body = new FormBody.Builder()
                .addEncoded("grant_type", "authorization_code")
                .addEncoded("code", code)
                .addEncoded("redirect_uri", redirect)
                .build();

        return requestToken(credentials, body);
    }

    private SpotifyToken refreshAccessToken(UserCredentials credentials, String refreshToken) {
        RequestBody body = new FormBody.Builder()
                .addEncoded("grant_type", "refresh_token")
                .addEncoded("refresh_token", refreshToken)
                .build();

        return requestToken(credentials, body);
    }

    private SpotifyToken requestToken(UserCredentials credentials, RequestBody body) {
        Request request = new Request.Builder()
                .addHeader("Authorization",
                        Credentials.basic(credentials.getClientId(), credentials.getSecret()))
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url(TOKEN_URL)
                .post(body)
                .build();

        SpotifyToken tokenRaw = requester.executeRequest(request, new OkHttpClient(), SpotifyToken.class);
        return new SpotifyToken(tokenRaw,
                System.currentTimeMillis() / 1000 + tokenRaw.getExpires_in()
        );
    }

    public String requestAuthCodeIfNotPreviouslySaved(Table db, String clientId, String redirectUrl) {
        Item item = db.getItem("id", "code");
        if (item != null) {
            return item.getString("code");
        }

        String url =  GET_CODE_URL.replace("CLIENT", clientId).replace("URI", redirectUrl);
        throw new ApiException(url);
    }

    private <T> UserCredentials makeSureClientIdAndSecretPresent(LambdaContext context, Table db) {
        Optional<T> client_id = context.getQueryStringParameter("client_id");
        Optional<T> secret    = context.getQueryStringParameter("secret");

        Item dbClientIdItem = db.getItem("id", "client_id");
        if (dbClientIdItem == null && (! client_id.isPresent() || ! secret.isPresent())) {
            throw new ApiException("Please provide client id and secret");
        }

        String clientFromDb = dbClientIdItem == null ? null : dbClientIdItem.getString("client_id");
        if (dbClientIdItem == null || clientFromDb == null) {
            db.putItem(
                    new Item()
                            .with("id", "client_id")
                            .with("client_id", client_id.get())
                            .with("secret",    secret.get())
            );

            return new UserCredentials(client_id.get().toString(), secret.toString());
        }

        return new UserCredentials(clientFromDb, dbClientIdItem.getString("secret"));
    }

    private void saveCodeWhenOauthCodeCallback(LambdaContext context, Table db) {
        context.getQueryStringParameter("code")
                .ifPresent(code -> db.putItem(new Item().with("id", "code").with("code", code)));
    }

    public SpotifyToken requestAccessToken(LambdaContext context) {
        Table accessKeyTbl = dynamoDB.getTable(context.getDbTableName());
        Optional<SpotifyToken> existingTokenFromDb = getExistingTokenFromDb(accessKeyTbl);
        if (existingTokenFromDb.isPresent()) {
            return existingTokenFromDb.get();
        }

        saveCodeWhenOauthCodeCallback(context, accessKeyTbl);
        UserCredentials credentials = makeSureClientIdAndSecretPresent(context, accessKeyTbl);

        String redirectUrl = context.getDomainName() + "code";
        String code = requestAuthCodeIfNotPreviouslySaved(
                accessKeyTbl, credentials.getClientId(), redirectUrl
        );

        return getOrSaveToken(accessKeyTbl, code, credentials, redirectUrl);
    }

    public SpotifyToken refreshToken(LambdaContext context, SpotifyToken accessToken) {
        Table db = dynamoDB.getTable(context.getDbTableName());
        saveCodeWhenOauthCodeCallback(context, db);
        UserCredentials credentials = makeSureClientIdAndSecretPresent(context, db);

        SpotifyToken token = refreshAccessToken(credentials, accessToken.getRefresh_token());
        db.putItem(token.toItem());
        return token;
    }

    public String token(LambdaContext context, SpotifyToken accessToken) {
        if (accessToken.isExpired()) {
            context.log("Token is expired!");
            return refreshToken(context, accessToken).getAccess_token();
        }
        return accessToken.getAccess_token();
    }
}
