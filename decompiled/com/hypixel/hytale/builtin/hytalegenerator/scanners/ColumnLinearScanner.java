package com.hypixel.hytale.builtin.hytalegenerator.scanners;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.SpaceSize;
import com.hypixel.hytale.builtin.hytalegenerator.framework.interfaces.functions.BiDouble2DoubleFunction;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ColumnLinearScanner extends Scanner {
   private final int minY;
   private final int maxY;
   private final boolean isRelativeToPosition;
   @Nullable
   private final BiDouble2DoubleFunction baseHeightFunction;
   private final int resultsCap;
   private final boolean topDownOrder;
   @Nonnull
   private final SpaceSize scanSpaceSize;

   public ColumnLinearScanner(
      int minY, int maxY, int resultsCap, boolean topDownOrder, boolean isRelativeToPosition, @Nullable BiDouble2DoubleFunction baseHeightFunction
   ) {
      if (resultsCap < 0) {
         throw new IllegalArgumentException();
      } else {
         this.baseHeightFunction = baseHeightFunction;
         this.minY = minY;
         this.maxY = maxY;
         this.isRelativeToPosition = isRelativeToPosition;
         this.resultsCap = resultsCap;
         this.topDownOrder = topDownOrder;
         this.scanSpaceSize = new SpaceSize(new Vector3i(0, 0, 0), new Vector3i(1, 0, 1));
      }
   }

   @Nonnull
   @Override
   public List<Vector3i> scan(@Nonnull Scanner.Context context) {
      ArrayList<Vector3i> validPositions = new ArrayList<>(this.resultsCap);
      int scanMinY;
      int scanMaxY;
      if (this.isRelativeToPosition) {
         scanMinY = Math.max(context.position.y + this.minY, context.materialSpace.minY());
         scanMaxY = Math.min(context.position.y + this.maxY, context.materialSpace.maxY());
      } else if (this.baseHeightFunction != null) {
         int bedY = (int)this.baseHeightFunction.apply(context.position.x, context.position.z);
         scanMinY = Math.max(bedY + this.minY, context.materialSpace.minY());
         scanMaxY = Math.min(bedY + this.maxY, context.materialSpace.maxY());
      } else {
         scanMinY = Math.max(this.minY, context.materialSpace.minY());
         scanMaxY = Math.min(this.maxY, context.materialSpace.maxY());
      }

      Vector3i patternPosition = context.position.clone();
      Pattern.Context patternContext = new Pattern.Context(patternPosition, context.materialSpace, context.workerId);
      if (this.topDownOrder) {
         for (patternPosition.y = scanMaxY - 1; patternPosition.y >= scanMinY; patternPosition.y--) {
            if (context.pattern.matches(patternContext)) {
               validPositions.add(patternPosition.clone());
               if (validPositions.size() >= this.resultsCap) {
                  return validPositions;
               }
            }
         }
      } else {
         for (patternPosition.y = scanMinY; patternPosition.y < scanMaxY; patternPosition.y++) {
            if (context.pattern.matches(patternContext)) {
               validPositions.add(patternPosition.clone());
               if (validPositions.size() >= this.resultsCap) {
                  return validPositions;
               }
            }
         }
      }

      return validPositions;
   }

   @Nonnull
   @Override
   public SpaceSize scanSpace() {
      return this.scanSpaceSize.clone();
   }
}
