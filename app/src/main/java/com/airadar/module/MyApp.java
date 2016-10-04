package com.airadar.module;

import android.app.Application;
import android.content.Context;


/**
 * Created by Lenovo on 2016-07-19.
 */
public class MyApp extends Application {

    private AiRadarComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        DaggerAiRadarComponent.Builder builder = DaggerAiRadarComponent.builder()
                .aiRadarModule(new AiRadarModule(this));

        this.component = builder.build();
    }

    public AiRadarComponent getComponent() {
        return component;
    }

}
