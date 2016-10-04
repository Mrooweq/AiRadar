package com.airadar.module;

import com.airadar.domain.CustomDialog;
import dagger.internal.Factory;
import javax.annotation.Generated;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class AiRadarModule_ProvideCustomDialogFactory implements Factory<CustomDialog> {
  private final AiRadarModule module;

  public AiRadarModule_ProvideCustomDialogFactory(AiRadarModule module) {  
    assert module != null;
    this.module = module;
  }

  @Override
  public CustomDialog get() {  
    CustomDialog provided = module.provideCustomDialog();
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<CustomDialog> create(AiRadarModule module) {  
    return new AiRadarModule_ProvideCustomDialogFactory(module);
  }
}

