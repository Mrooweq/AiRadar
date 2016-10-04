package com.airadar.module;

import com.airadar.domain.Algorithm;
import dagger.internal.Factory;
import javax.annotation.Generated;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class AiRadarModule_ProvideAlgorithmFactory implements Factory<Algorithm> {
  private final AiRadarModule module;

  public AiRadarModule_ProvideAlgorithmFactory(AiRadarModule module) {  
    assert module != null;
    this.module = module;
  }

  @Override
  public Algorithm get() {  
    Algorithm provided = module.provideAlgorithm();
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<Algorithm> create(AiRadarModule module) {  
    return new AiRadarModule_ProvideAlgorithmFactory(module);
  }
}

