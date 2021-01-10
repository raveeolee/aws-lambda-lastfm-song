package dev.may_i;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import okhttp3.*;
import java.util.Optional;

public class SpotifyAuthService {
    private static final String GET_CODE_URL =
            "https://accounts.spotify.com/authorize?client_id=%s&response_type=code&scope=user-read-playback-state&redirect_uri=%s";

    private static final String TOKEN_URL =
            "https://accounts.spotify.com/api/token";

    private final LambdaContext context;
    private final DynamoDB dynamoDB;
    private final RequestExecutor requester;

    public SpotifyAuthService(LambdaContext context,
                              DynamoDB dynamoDB,
                              RequestExecutor requester) {
        this.context = context;
        this.dynamoDB = dynamoDB;
        this.requester = requester;
    }

    public SpotifyToken getOrSaveToken(Table db,
                                       String code,
                                       UserCredentials credentials,
                                       String redirect) {

        return getExistingTokenFromDb(db)
                .orElseGet(() -> {
                    SpotifyToken token = getAccessToken(code, credentials, redirect);
                    db.putItem(token.toItem());
                    return token;
                });
    }

    private Optional<SpotifyToken> getExistingTokenFromDb(Table db) {
        return Optional.ofNullable(db.getItem("id", "token"))
                .map(SpotifyToken::new);
    }

    private SpotifyToken getAccessToken(String code, UserCredentials credentials, String redirect) {

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .addEncoded("grant_type", "authorization_code")
                .addEncoded("code", code)
                .addEncoded("redirect_uri", redirect)
                .build();

        Request request = new Request.Builder()
                .addHeader("Authorization",
                        Credentials.basic(credentials.getClientId(), credentials.getSecret()))
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url(TOKEN_URL)
                .post(body)
                .build();

        return requester.executeRequest(request, client, SpotifyToken.class);
    }

    public String requestAuthCode(Table db, String clientId, String redirectUrl) {
        Item item = db.getItem("id", "code");
        if (item != null) {
            return item.get("code").toString();
        }

        String url = String.format(GET_CODE_URL, clientId, redirectUrl);
        throw new ApiException(url);
    }

    public <T> UserCredentials makeSureClientIdAndSecretPresent(Table db) {
        Optional<T> client_id = context.getQueryStringParameter("client_id");
        Optional<T> secret    = context.getQueryStringParameter("secret");

        Item dbClientIdItem = db.getItem("id", "client_id");
        if (dbClientIdItem == null && (! client_id.isPresent() || ! secret.isPresent())) {
            throw new ApiException("Please provide client id and secret");
        }

        Object clientFromDb = dbClientIdItem == null ? null : dbClientIdItem.get("client_id");
        if (dbClientIdItem == null || clientFromDb == null) {
            db.putItem(
                    new Item().with("id", "client_id")
                    .with("client_id", client_id.get())
                    .with("secret",    secret.get())
            );

            return new UserCredentials(
                    client_id.get().toString(),
                    secret.toString()
            );
        }

        return new UserCredentials(
                clientFromDb.toString(),
                dbClientIdItem.get("secret").toString()
        );
    }

    public void saveCodeWhenOauthCodeCallback(Table db) {
        context.getQueryStringParameter("code")
                .ifPresent(code -> db.putItem(new Item().with("id", "code").with("code", code)));
    }

    public SpotifyToken getAccessToken() {
        Table accessKeyTbl = dynamoDB.getTable(System.getenv().get("DDB_TABLE"));
        Optional<SpotifyToken> existingTokenFromDb = getExistingTokenFromDb(accessKeyTbl);
        if (existingTokenFromDb.isPresent()) {
            return existingTokenFromDb.get();
        }

        saveCodeWhenOauthCodeCallback(accessKeyTbl);
        UserCredentials credentials = makeSureClientIdAndSecretPresent(accessKeyTbl);

        String redirectUrl          = context.getDomainName();
        String code                 = requestAuthCode(accessKeyTbl, credentials.getClientId(), redirectUrl);

        return getOrSaveToken(
                accessKeyTbl,
                code,
                credentials,
                redirectUrl
        );
    }
}
