package com.airadar.module;

import android.content.SharedPreferences;
import dagger.internal.Factory;
import javax.annotation.Generated;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class AiRadarModule_ProvideSharedPreferencesFactory implements Factory<SharedPreferences> {
  private final AiRadarModule module;

  public AiRadarModule_ProvideSharedPreferencesFactory(AiRadarModule module) {  
    assert module != null;
    this.module = module;
  }

  @Override
  public SharedPreferences get() {  
    SharedPreferences provided = module.provideSharedPreferences();
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<SharedPreferences> create(AiRadarModule module) {  
    return new AiRadarModule_ProvideSharedPreferencesFactory(module);
  }
}

