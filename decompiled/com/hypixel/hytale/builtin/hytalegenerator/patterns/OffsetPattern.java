package com.hypixel.hytale.builtin.hytalegenerator.patterns;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.SpaceSize;
import com.hypixel.hytale.math.vector.Vector3i;
import javax.annotation.Nonnull;

public class OffsetPattern extends Pattern {
   @Nonnull
   private final Pattern pattern;
   @Nonnull
   private final Vector3i offset;
   @Nonnull
   private final SpaceSize readSpaceSize;

   public OffsetPattern(@Nonnull Pattern pattern, @Nonnull Vector3i offset) {
      this.pattern = pattern;
      this.offset = offset;
      this.readSpaceSize = pattern.readSpace().moveBy(offset);
   }

   @Override
   public boolean matches(@Nonnull Pattern.Context context) {
      Pattern.Context childContext = new Pattern.Context(context);
      childContext.position = context.position.clone().add(this.offset);
      return this.pattern.matches(childContext);
   }

   @Nonnull
   @Override
   public SpaceSize readSpace() {
      return this.readSpaceSize.clone();
   }
}
