package com.hypixel.hytale.builtin.hytalegenerator.materialproviders;

import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.functions.DoubleFunctionXZ;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HorizontalMaterialProvider<V> extends MaterialProvider<V> {
   @Nonnull
   private final MaterialProvider<V> materialProvider;
   @Nonnull
   private final DoubleFunctionXZ topY;
   @Nonnull
   private final DoubleFunctionXZ bottomY;

   public HorizontalMaterialProvider(@Nonnull MaterialProvider<V> materialProvider, @Nonnull DoubleFunctionXZ topY, @Nonnull DoubleFunctionXZ bottomY) {
      this.materialProvider = materialProvider;
      this.topY = topY;
      this.bottomY = bottomY;
   }

   @Nullable
   @Override
   public V getVoxelTypeAt(@Nonnull MaterialProvider.Context context) {
      double topY = this.topY.apply(context.position.x, context.position.z);
      double bottomY = this.bottomY.apply(context.position.x, context.position.z);
      return !(context.position.y >= topY) && !(context.position.y < bottomY) ? this.materialProvider.getVoxelTypeAt(context) : null;
   }
}
