package com.airadar.module;

import android.content.SharedPreferences;
import com.airadar.domain.Algorithm;
import com.airadar.domain.CameraScreen;
import com.airadar.domain.CameraScreen_MembersInjector;
import com.airadar.domain.CustomDialog;
import com.airadar.domain.MainScreen;
import com.airadar.domain.MainScreen_MembersInjector;
import dagger.MembersInjector;
import dagger.internal.MembersInjectors;
import dagger.internal.ScopedProvider;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class DaggerAiRadarComponent implements AiRadarComponent {
  private Provider<Algorithm> provideAlgorithmProvider;
  private Provider<CustomDialog> provideCustomDialogProvider;
  private Provider<SharedPreferences> provideSharedPreferencesProvider;
  private MembersInjector<MainScreen> mainScreenMembersInjector;
  private MembersInjector<CameraScreen> cameraScreenMembersInjector;

  private DaggerAiRadarComponent(Builder builder) {  
    assert builder != null;
    initialize(builder);
  }

  public static Builder builder() {  
    return new Builder();
  }

  private void initialize(final Builder builder) {  
    this.provideAlgorithmProvider = ScopedProvider.create(AiRadarModule_ProvideAlgorithmFactory.create(builder.aiRadarModule));
    this.provideCustomDialogProvider = ScopedProvider.create(AiRadarModule_ProvideCustomDialogFactory.create(builder.aiRadarModule));
    this.provideSharedPreferencesProvider = ScopedProvider.create(AiRadarModule_ProvideSharedPreferencesFactory.create(builder.aiRadarModule));
    this.mainScreenMembersInjector = MainScreen_MembersInjector.create((MembersInjector) MembersInjectors.noOp(), provideAlgorithmProvider, provideCustomDialogProvider, provideSharedPreferencesProvider);
    this.cameraScreenMembersInjector = CameraScreen_MembersInjector.create((MembersInjector) MembersInjectors.noOp(), provideAlgorithmProvider, provideCustomDialogProvider, provideSharedPreferencesProvider);
  }

  @Override
  public void inject(MainScreen activity) {  
    mainScreenMembersInjector.injectMembers(activity);
  }

  @Override
  public void inject(Algorithm algo) {  
    MembersInjectors.noOp().injectMembers(algo);
  }

  @Override
  public void inject(CameraScreen activity) {  
    cameraScreenMembersInjector.injectMembers(activity);
  }

  @Override
  public void inject(CustomDialog customDialog) {  
    MembersInjectors.noOp().injectMembers(customDialog);
  }

  public static final class Builder {
    private AiRadarModule aiRadarModule;
  
    private Builder() {  
    }
  
    public AiRadarComponent build() {  
      if (aiRadarModule == null) {
        throw new IllegalStateException("aiRadarModule must be set");
      }
      return new DaggerAiRadarComponent(this);
    }
  
    public Builder aiRadarModule(AiRadarModule aiRadarModule) {  
      if (aiRadarModule == null) {
        throw new NullPointerException("aiRadarModule");
      }
      this.aiRadarModule = aiRadarModule;
      return this;
    }
  }
}

