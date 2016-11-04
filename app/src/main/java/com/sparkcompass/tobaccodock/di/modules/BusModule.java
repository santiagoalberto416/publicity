package com.sparkcompass.tobaccodock.di.modules;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Aaron Vega on 5/12/16.
 */
@Module
public class BusModule {

    public BusModule() {}

    @Provides
    @Singleton
    Bus provideBus() {
        return new Bus();
    }
}
