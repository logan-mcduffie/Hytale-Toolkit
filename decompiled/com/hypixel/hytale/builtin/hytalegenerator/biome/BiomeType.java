package com.hypixel.hytale.builtin.hytalegenerator.biome;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import javax.annotation.Nonnull;

public interface BiomeType extends MaterialSource, PropsSource, EnvironmentSource, TintSource {
   String getBiomeName();

   @Nonnull
   Density getTerrainDensity();
}
