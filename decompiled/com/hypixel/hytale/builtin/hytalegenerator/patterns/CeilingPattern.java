package com.hypixel.hytale.builtin.hytalegenerator.patterns;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.SpaceSize;
import com.hypixel.hytale.math.vector.Vector3i;
import javax.annotation.Nonnull;

public class CeilingPattern extends Pattern {
   @Nonnull
   private final Pattern ceilingPattern;
   @Nonnull
   private final Pattern airPattern;
   @Nonnull
   private final SpaceSize readSpaceSize;

   public CeilingPattern(@Nonnull Pattern ceilingPattern, @Nonnull Pattern airPattern) {
      this.ceilingPattern = ceilingPattern;
      this.airPattern = airPattern;
      SpaceSize ceilingSpace = ceilingPattern.readSpace();
      ceilingSpace.moveBy(new Vector3i(0, 1, 0));
      this.readSpaceSize = SpaceSize.merge(ceilingSpace, airPattern.readSpace());
   }

   @Override
   public boolean matches(@Nonnull Pattern.Context context) {
      Vector3i ceilingPosition = new Vector3i(context.position.x, context.position.y + 1, context.position.z);
      if (context.materialSpace.isInsideSpace(context.position) && context.materialSpace.isInsideSpace(ceilingPosition)) {
         Pattern.Context ceilingContext = new Pattern.Context(context);
         ceilingContext.position = ceilingPosition;
         return this.airPattern.matches(context) && this.ceilingPattern.matches(ceilingContext);
      } else {
         return false;
      }
   }

   @Nonnull
   @Override
   public SpaceSize readSpace() {
      return this.readSpaceSize.clone();
   }
}
