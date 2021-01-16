package dev.may_i.configuration;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import dev.may_i.RequestExecutor;
import dev.may_i.SpotifyAuthService;

import javax.inject.Singleton;

@Module
public class LambdaModule {

    @Provides @Singleton
    public Gson gson() {
        return new GsonBuilder().create();
    }

    @Provides @Singleton
    public DynamoDB dynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        return new DynamoDB(client);
    }

    @Provides @Singleton
    public RequestExecutor requestExecutor(Gson gson) {
        return new RequestExecutor(gson);
    }

    @Provides @Singleton
    public SpotifyAuthService authService(DynamoDB dynamoDB, RequestExecutor requestExecutor) {
        return new SpotifyAuthService(dynamoDB, requestExecutor);
    }

}
