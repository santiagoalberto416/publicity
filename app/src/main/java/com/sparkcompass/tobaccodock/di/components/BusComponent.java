package com.sparkcompass.tobaccodock.di.components;


import com.sparkcompass.tobaccodock.common.TCSService;
import com.sparkcompass.tobaccodock.di.modules.BusModule;
import com.sparkcompass.tobaccodock.home.MainActivity;
import com.sparkcompass.tobaccodock.welcome.SwitchAppActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Aaron Vega on 5/12/16.
 */

@Singleton
@Component(modules = BusModule.class)
public interface BusComponent {

    void inject(MainActivity mainActivity);

    void inject(TCSService ecosTCSService);

    void inject(SwitchAppActivity switchAppActivity);
}
