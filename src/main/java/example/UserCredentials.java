package example;

public class UserCredentials {
    private String clientId;
    private String secret;

    public UserCredentials(String clientId, String secret) {
        this.clientId = clientId;
        this.secret = secret;
    }

    public String getClientId() {
        return clientId;
    }

    public String getSecret() {
        return secret;
    }
}
