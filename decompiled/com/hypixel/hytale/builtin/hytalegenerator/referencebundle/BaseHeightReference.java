package com.hypixel.hytale.builtin.hytalegenerator.referencebundle;

import com.hypixel.hytale.builtin.hytalegenerator.framework.interfaces.functions.BiDouble2DoubleFunction;
import javax.annotation.Nonnull;

public class BaseHeightReference extends Reference {
   @Nonnull
   private final BiDouble2DoubleFunction heightFunction;

   public BaseHeightReference(@Nonnull BiDouble2DoubleFunction heightFunction) {
      this.heightFunction = heightFunction;
   }

   @Nonnull
   public BiDouble2DoubleFunction getHeightFunction() {
      return this.heightFunction;
   }
}
