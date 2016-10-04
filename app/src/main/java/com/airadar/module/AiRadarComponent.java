package com.airadar.module;

import com.airadar.domain.Algorithm;
import com.airadar.domain.CameraScreen;
import com.airadar.domain.CustomDialog;
import com.airadar.domain.MainScreen;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by kerry on 14/02/15.
 */

@Singleton
@Component(modules = {AiRadarModule.class})
public interface AiRadarComponent {

    void inject(MainScreen activity);
    void inject(Algorithm algo);
    void inject(CameraScreen activity);
    void inject(CustomDialog customDialog);
}