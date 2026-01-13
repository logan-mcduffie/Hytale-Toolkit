package com.hypixel.hytale.builtin.hytalegenerator.materialproviders.spaceanddepth.layers;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.spaceanddepth.SpaceAndDepthMaterialProvider;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NoiseThickness<V> extends SpaceAndDepthMaterialProvider.Layer<V> {
   @Nonnull
   private final Density density;
   @Nullable
   private final MaterialProvider<V> materialProvider;

   public NoiseThickness(@Nonnull Density density, @Nullable MaterialProvider<V> materialProvider) {
      this.density = density;
      this.materialProvider = materialProvider;
   }

   @Override
   public int getThicknessAt(
      int x, int y, int z, int depthIntoFloor, int depthIntoCeiling, int spaceAboveFloor, int spaceBelowCeiling, double distanceToBiomeEdge
   ) {
      Density.Context childContext = new Density.Context();
      childContext.position = new Vector3d(x, y, z);
      childContext.distanceToBiomeEdge = distanceToBiomeEdge;
      return (int)this.density.process(childContext);
   }

   @Nullable
   @Override
   public MaterialProvider<V> getMaterialProvider() {
      return this.materialProvider;
   }
}
