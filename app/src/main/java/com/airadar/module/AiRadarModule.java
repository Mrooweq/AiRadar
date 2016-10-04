package com.airadar.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.airadar.domain.Algorithm;
import com.airadar.domain.CustomDialog;
import com.airadar.domain.MainScreen;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by kerry on 14/02/15.
 */

@Module
public class AiRadarModule {

    Application mApplication;
    public static final String strPreferences = "MyPrefs";

    public AiRadarModule(Application application) {
        mApplication = application;
    }

    @Provides @Singleton
    Algorithm provideAlgorithm(){
        return new Algorithm();
    }

    @Provides @Singleton
    CustomDialog provideCustomDialog(){
        return new CustomDialog();
    }

    @Provides @Singleton
    SharedPreferences provideSharedPreferences(){
        return mApplication.getSharedPreferences(strPreferences, Context.MODE_PRIVATE);
    }
}