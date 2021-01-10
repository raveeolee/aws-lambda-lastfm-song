package dev.may_i;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import okhttp3.*;

public class SpotifyService {
    private static final String GET_CODE_URL =
            "https://accounts.spotify.com/authorize?client_id=%s&response_type=code&scope=user-read-playback-state&redirect_uri=%s";

    private static final String TOKEN_URL =
            "https://accounts.spotify.com/api/token";

    public static final MediaType FORM =
            MediaType.parse("application/x-www-form-urlencoded");
    private LambdaContext context;
    private DynamoDB dynamoDB;
    private RequestExecutor requester;

    public SpotifyService(LambdaContext context,
                          DynamoDB dynamoDB,
                          RequestExecutor requester
                          ) {
        this.context = context;
        this.dynamoDB = dynamoDB;
        this.requester = requester;
    }

    public SpotifyToken getOrSaveToken(Table db,
                                       String code,
                                       UserCredentials credentials,
                                       String redirect) {

        Item item = db.getItem("id", "token");
        if (item == null) {
            SpotifyToken token = getAccessToken(code, credentials, redirect);
            db.putItem(token.toItem());
            return token;
        }
        return new SpotifyToken(item);
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

    public String getAuthCode(Table db, String clientId, String redirectUrl) {
        Item item = db.getItem("id", "code");
        if (item != null) {
            return item.get("code").toString();
        }

        String url = String.format(GET_CODE_URL, clientId, redirectUrl);
        throw new ApiException(url);
    }

    public <T> UserCredentials checkClientIdSecretPresent(Table db) {
        T client_id = context.getQueryStringParameter("client_id");
        T secret    = context.getQueryStringParameter("secret");

        Item dbItem = db.getItem("id", "client_id");
        if (dbItem == null && (client_id == null || secret == null)) {
            throw new ApiException("Please provide client id and secret");
        }

        Object clientFromDb = dbItem == null ? null : dbItem.get("client_id");
        if (dbItem == null || clientFromDb == null) {
            db.putItem(new Item().with("id", "client_id")
                    .with("client_id", client_id)
                    .with("secret",    secret)
            );

            return new UserCredentials(
                    client_id.toString(),
                    secret.toString()
            );
        }

        return new UserCredentials(
                clientFromDb.toString(),
                dbItem.get("secret").toString()
        );
    }

    public void checkThisIsCallBack(Table db) {
        Object code = context.getQueryStringParameter("code");
        if (code == null) {
            return;
        }

        db.putItem(new Item().with("id", "code").with("code", code));
    }

    public SpotifyToken getAccessToken() {
        Table accessKeyTbl = dynamoDB.getTable(System.getenv().get("DDB_TABLE"));
        checkThisIsCallBack(accessKeyTbl);
        UserCredentials credentials = checkClientIdSecretPresent(accessKeyTbl);
        String redirectUrl          = context.getDomainName();
        String code                 = getAuthCode(accessKeyTbl, credentials.getClientId(), redirectUrl);

        return getOrSaveToken(
                accessKeyTbl,
                code,
                credentials,
                redirectUrl
        );
    }
}
