package com.sparkcompass.tobaccodock;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.sparkcompass.tobaccodock.di.components.ApplicationComponent;
import com.sparkcompass.tobaccodock.di.modules.ApplicationModule;
import com.sparkcompass.tobaccodock.di.modules.BusModule;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Daniel Alcantara on 8/28/15.
 */
public class SparkCompassApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

    }

}
