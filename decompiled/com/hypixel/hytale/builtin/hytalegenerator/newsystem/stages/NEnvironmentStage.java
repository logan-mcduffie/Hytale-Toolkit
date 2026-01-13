package com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages;

import com.hypixel.hytale.builtin.hytalegenerator.biome.BiomeType;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.environmentproviders.EnvironmentProvider;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.GridUtils;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.NBufferBundle;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NCountedPixelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NVoxelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NBufferType;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NParametrizedBufferType;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.views.NPixelBufferView;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.views.NVoxelBufferView;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public class NEnvironmentStage implements NStage {
   public static final Class<NCountedPixelBuffer> biomeBufferClass = NCountedPixelBuffer.class;
   public static final Class<BiomeType> biomeTypeClass = BiomeType.class;
   public static final Class<NVoxelBuffer> environmentBufferClass = NVoxelBuffer.class;
   public static final Class<Integer> environmentClass = Integer.class;
   private final NParametrizedBufferType biomeInputBufferType;
   private final NParametrizedBufferType environmentOutputBufferType;
   private final Bounds3i inputBounds_bufferGrid;
   private final String stageName;

   public NEnvironmentStage(
      @Nonnull String stageName, @Nonnull NParametrizedBufferType biomeInputBufferType, @Nonnull NParametrizedBufferType environmentOutputBufferType
   ) {
      assert biomeInputBufferType.isValidType(biomeBufferClass, biomeTypeClass);

      assert environmentOutputBufferType.isValidType(environmentBufferClass, environmentClass);

      this.biomeInputBufferType = biomeInputBufferType;
      this.environmentOutputBufferType = environmentOutputBufferType;
      this.stageName = stageName;
      this.inputBounds_bufferGrid = GridUtils.createUnitBounds3i(Vector3i.ZERO);
   }

   @Override
   public void run(@Nonnull NStage.Context context) {
      NBufferBundle.Access.View biomeAccess = context.bufferAccess.get(this.biomeInputBufferType);
      NPixelBufferView<BiomeType> biomeSpace = new NPixelBufferView<>(biomeAccess, biomeTypeClass);
      NBufferBundle.Access.View environmentAccess = context.bufferAccess.get(this.environmentOutputBufferType);
      NVoxelBufferView<Integer> environmentSpace = new NVoxelBufferView<>(environmentAccess, environmentClass);
      Bounds3i outputBounds_voxelGrid = environmentSpace.getBounds();
      Vector3i position_voxelGrid = new Vector3i(outputBounds_voxelGrid.min);
      EnvironmentProvider.Context tintContext = new EnvironmentProvider.Context(position_voxelGrid, context.workerId);

      for (position_voxelGrid.x = outputBounds_voxelGrid.min.x; position_voxelGrid.x < outputBounds_voxelGrid.max.x; position_voxelGrid.x++) {
         for (position_voxelGrid.z = outputBounds_voxelGrid.min.z; position_voxelGrid.z < outputBounds_voxelGrid.max.z; position_voxelGrid.z++) {
            BiomeType biome = biomeSpace.getContent(position_voxelGrid.x, 0, position_voxelGrid.z);

            assert biome != null;

            EnvironmentProvider environmentProvider = biome.getEnvironmentProvider();

            for (position_voxelGrid.y = outputBounds_voxelGrid.min.y; position_voxelGrid.y < outputBounds_voxelGrid.max.y; position_voxelGrid.y++) {
               int environment = environmentProvider.getValue(tintContext);
               environmentSpace.set(environment, position_voxelGrid);
            }
         }
      }
   }

   @Nonnull
   @Override
   public Map<NBufferType, Bounds3i> getInputTypesAndBounds_bufferGrid() {
      return Map.of(this.biomeInputBufferType, this.inputBounds_bufferGrid);
   }

   @Nonnull
   @Override
   public List<NBufferType> getOutputTypes() {
      return List.of(this.environmentOutputBufferType);
   }

   @Nonnull
   @Override
   public String getName() {
      return this.stageName;
   }
}
