package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AnchorDensity extends Density {
   @Nullable
   private Density input;
   private final boolean isReversed;

   public AnchorDensity(Density input, boolean isReversed) {
      this.input = input;
      this.isReversed = isReversed;
   }

   @Override
   public double process(@Nonnull Density.Context context) {
      Vector3d anchor = context.densityAnchor;
      if (anchor == null) {
         return this.input.process(context);
      } else if (this.isReversed) {
         Vector3d childPosition = new Vector3d(context.position.x + anchor.x, context.position.y + anchor.y, context.position.z + anchor.z);
         Density.Context childContext = new Density.Context(context);
         childContext.position = childPosition;
         return this.input.process(childContext);
      } else {
         Vector3d childPosition = new Vector3d(context.position.x - anchor.x, context.position.y - anchor.y, context.position.z - anchor.z);
         Density.Context childContext = new Density.Context(context);
         childContext.position = childPosition;
         return this.input.process(childContext);
      }
   }

   @Override
   public void setInputs(@Nonnull Density[] inputs) {
      if (inputs.length == 0) {
         this.input = null;
      }

      this.input = inputs[0];
   }
}
