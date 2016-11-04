package com.sparkcompass.tobaccodock.di.modules;

import android.content.Context;

import com.sparkcompass.tobaccodock.SparkCompassApp;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by adalberto on 5/27/16.
 */

@Module
public class ApplicationModule {

    private SparkCompassApp application;

    public ApplicationModule(SparkCompassApp application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Context provideApplicationContext(){
        return application;
    }
}
