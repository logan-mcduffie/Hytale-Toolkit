package com.hypixel.hytale.builtin.hytalegenerator.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.VectorUtil;
import javax.annotation.Nonnull;

public class SpherePositionProvider extends PositionProvider {
   @Nonnull
   private final PositionProvider positionProvider;
   private final double range;

   public SpherePositionProvider(@Nonnull PositionProvider positionProvider, double range) {
      this.positionProvider = positionProvider;
      this.range = range;
   }

   @Override
   public void positionsIn(@Nonnull PositionProvider.Context context) {
      PositionProvider.Context childContext = new PositionProvider.Context(context);
      childContext.consumer = position -> {
         double distance = position.length();
         if (VectorUtil.isInside(position, context.minInclusive, context.maxExclusive) && distance <= this.range) {
            context.consumer.accept(position);
         }
      };
      this.positionProvider.positionsIn(childContext);
   }
}
