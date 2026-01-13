package com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages;

import com.hypixel.hytale.builtin.hytalegenerator.biome.BiomeType;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.framework.interfaces.functions.BiCarta;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.NBufferBundle;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NCountedPixelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NBufferType;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NParametrizedBufferType;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.views.NPixelBufferView;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public class NBiomeStage implements NStage {
   public static final Class<NCountedPixelBuffer> bufferClass = NCountedPixelBuffer.class;
   public static final Class<BiomeType> biomeTypeClass = BiomeType.class;
   private final NParametrizedBufferType biomeOutputBufferType;
   private final String stageName;
   private BiCarta<BiomeType> biomeCarta;

   public NBiomeStage(@Nonnull String stageName, @Nonnull NParametrizedBufferType biomeOutputBufferType, @Nonnull BiCarta<BiomeType> biomeCarta) {
      this.stageName = stageName;
      this.biomeOutputBufferType = biomeOutputBufferType;
      this.biomeCarta = biomeCarta;
   }

   @Override
   public void run(@Nonnull NStage.Context context) {
      NBufferBundle.Access.View biomeAccess = context.bufferAccess.get(this.biomeOutputBufferType);
      NPixelBufferView<BiomeType> biomeSpace = new NPixelBufferView<>(biomeAccess, biomeTypeClass);

      for (int x = biomeSpace.minX(); x < biomeSpace.maxX(); x++) {
         for (int z = biomeSpace.minZ(); z < biomeSpace.maxZ(); z++) {
            BiomeType biome = this.biomeCarta.apply(x, z, context.workerId);
            biomeSpace.set(biome, x, 0, z);
         }
      }
   }

   @Nonnull
   @Override
   public Map<NBufferType, Bounds3i> getInputTypesAndBounds_bufferGrid() {
      return Map.of();
   }

   @Nonnull
   @Override
   public List<NBufferType> getOutputTypes() {
      return List.of(this.biomeOutputBufferType);
   }

   @Nonnull
   @Override
   public String getName() {
      return this.stageName;
   }
}
