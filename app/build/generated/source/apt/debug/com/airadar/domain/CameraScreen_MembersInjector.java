package com.airadar.domain;

import android.app.Activity;
import android.content.SharedPreferences;
import dagger.MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class CameraScreen_MembersInjector implements MembersInjector<CameraScreen> {
  private final MembersInjector<Activity> supertypeInjector;
  private final Provider<Algorithm> algorithmProvider;
  private final Provider<CustomDialog> customDialogProvider;
  private final Provider<SharedPreferences> sharedpreferencesProvider;

  public CameraScreen_MembersInjector(MembersInjector<Activity> supertypeInjector, Provider<Algorithm> algorithmProvider, Provider<CustomDialog> customDialogProvider, Provider<SharedPreferences> sharedpreferencesProvider) {  
    assert supertypeInjector != null;
    this.supertypeInjector = supertypeInjector;
    assert algorithmProvider != null;
    this.algorithmProvider = algorithmProvider;
    assert customDialogProvider != null;
    this.customDialogProvider = customDialogProvider;
    assert sharedpreferencesProvider != null;
    this.sharedpreferencesProvider = sharedpreferencesProvider;
  }

  @Override
  public void injectMembers(CameraScreen instance) {  
    if (instance == null) {
      throw new NullPointerException("Cannot inject members into a null reference");
    }
    supertypeInjector.injectMembers(instance);
    instance.algorithm = algorithmProvider.get();
    instance.customDialog = customDialogProvider.get();
    instance.sharedpreferences = sharedpreferencesProvider.get();
  }

  public static MembersInjector<CameraScreen> create(MembersInjector<Activity> supertypeInjector, Provider<Algorithm> algorithmProvider, Provider<CustomDialog> customDialogProvider, Provider<SharedPreferences> sharedpreferencesProvider) {  
      return new CameraScreen_MembersInjector(supertypeInjector, algorithmProvider, customDialogProvider, sharedpreferencesProvider);
  }
}

