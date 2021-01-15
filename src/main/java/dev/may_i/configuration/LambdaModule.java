package dev.may_i.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class LambdaModule {

    @Provides @Singleton
    public Gson gson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }
}
