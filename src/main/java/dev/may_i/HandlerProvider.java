package dev.may_i;

import dagger.Component;
import dev.may_i.configuration.LambdaModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = LambdaModule.class)
interface HandlerProvider {

    SpotifyMusicHandler handler();
}
