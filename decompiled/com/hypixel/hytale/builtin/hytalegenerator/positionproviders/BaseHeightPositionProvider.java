package com.hypixel.hytale.builtin.hytalegenerator.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.VectorUtil;
import com.hypixel.hytale.builtin.hytalegenerator.framework.interfaces.functions.BiDouble2DoubleFunction;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class BaseHeightPositionProvider extends PositionProvider {
   @Nonnull
   private final BiDouble2DoubleFunction baseHeightFunction;
   private final double maxYInput;
   private final double minYInput;
   @Nonnull
   private final PositionProvider positionProvider;

   public BaseHeightPositionProvider(
      @Nonnull BiDouble2DoubleFunction baseHeightFunction, @Nonnull PositionProvider positionProvider, double minYInput, double maxYInput
   ) {
      maxYInput = Math.max(minYInput, maxYInput);
      this.baseHeightFunction = baseHeightFunction;
      this.positionProvider = positionProvider;
      this.maxYInput = maxYInput;
      this.minYInput = minYInput;
   }

   @Override
   public void positionsIn(@Nonnull PositionProvider.Context context) {
      PositionProvider.Context childContext = new PositionProvider.Context(context);
      childContext.consumer = position -> {
         Vector3d offsetP = position.clone();
         offsetP.y = offsetP.y + this.baseHeightFunction.apply(position.x, position.z);
         if (VectorUtil.isInside(offsetP, context.minInclusive, context.maxExclusive)) {
            context.consumer.accept(offsetP);
         }
      };
      this.positionProvider.positionsIn(childContext);
   }
}
