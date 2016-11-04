package com.sparkcompass.tobaccodock.di.components;

import android.content.Context;


import com.sparkcompass.tobaccodock.di.modules.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by adalberto on 5/27/16.
 */

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    Context context();
}
