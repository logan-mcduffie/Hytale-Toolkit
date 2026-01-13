package com.hypixel.hytale.builtin.hytalegenerator.vectorproviders;

import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class ConstantVectorProvider extends VectorProvider {
   @Nonnull
   private final Vector3d value;

   public ConstantVectorProvider(@Nonnull Vector3d value) {
      this.value = value.clone();
   }

   @Nonnull
   @Override
   public Vector3d process(@Nonnull VectorProvider.Context context) {
      return this.value.clone();
   }
}
