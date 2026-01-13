package com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages;

import com.hypixel.hytale.builtin.hytalegenerator.biome.BiomeType;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.GridUtils;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.NBufferBundle;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NCountedPixelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NSimplePixelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NBufferType;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NParametrizedBufferType;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.views.NPixelBufferView;
import com.hypixel.hytale.builtin.hytalegenerator.tintproviders.TintProvider;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public class NTintStage implements NStage {
   public static final Class<NCountedPixelBuffer> biomeBufferClass = NCountedPixelBuffer.class;
   public static final Class<BiomeType> biomeTypeClass = BiomeType.class;
   public static final Class<NSimplePixelBuffer> tintBufferClass = NSimplePixelBuffer.class;
   public static final Class<Integer> tintClass = Integer.class;
   private final NParametrizedBufferType biomeInputBufferType;
   private final NParametrizedBufferType tintOutputBufferType;
   private final Bounds3i inputBounds_bufferGrid;
   private final String stageName;

   public NTintStage(@Nonnull String stageName, @Nonnull NParametrizedBufferType biomeInputBufferType, @Nonnull NParametrizedBufferType tintOutputBufferType) {
      assert biomeInputBufferType.isValidType(biomeBufferClass, biomeTypeClass);

      assert tintOutputBufferType.isValidType(tintBufferClass, tintClass);

      this.biomeInputBufferType = biomeInputBufferType;
      this.tintOutputBufferType = tintOutputBufferType;
      this.stageName = stageName;
      this.inputBounds_bufferGrid = GridUtils.createUnitBounds3i(Vector3i.ZERO);
   }

   @Override
   public void run(@Nonnull NStage.Context context) {
      NBufferBundle.Access.View biomeAccess = context.bufferAccess.get(this.biomeInputBufferType);
      NPixelBufferView<BiomeType> biomeSpace = new NPixelBufferView<>(biomeAccess, biomeTypeClass);
      NBufferBundle.Access.View tintAccess = context.bufferAccess.get(this.tintOutputBufferType);
      NPixelBufferView<Integer> tintSpace = new NPixelBufferView<>(tintAccess, tintClass);
      Bounds3i outputBounds_voxelGrid = tintSpace.getBounds();
      Vector3i position_voxelGrid = new Vector3i(outputBounds_voxelGrid.min);
      position_voxelGrid.setY(0);
      TintProvider.Context tintContext = new TintProvider.Context(position_voxelGrid, context.workerId);

      for (position_voxelGrid.x = outputBounds_voxelGrid.min.x; position_voxelGrid.x < outputBounds_voxelGrid.max.x; position_voxelGrid.x++) {
         for (position_voxelGrid.z = outputBounds_voxelGrid.min.z; position_voxelGrid.z < outputBounds_voxelGrid.max.z; position_voxelGrid.z++) {
            BiomeType biome = biomeSpace.getContent(position_voxelGrid.x, 0, position_voxelGrid.z);

            assert biome != null;

            TintProvider tintProvider = biome.getTintProvider();
            TintProvider.Result tintResult = tintProvider.getValue(tintContext);
            if (!tintResult.hasValue) {
               tintSpace.set(TintProvider.DEFAULT_TINT, position_voxelGrid);
            } else {
               tintSpace.set(tintResult.tint, position_voxelGrid);
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
      return List.of(this.tintOutputBufferType);
   }

   @Nonnull
   @Override
   public String getName() {
      return this.stageName;
   }
}
