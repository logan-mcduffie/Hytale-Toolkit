package com.hypixel.hytale.builtin.hytalegenerator.materialproviders.spaceanddepth.conditions;

import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.spaceanddepth.SpaceAndDepthMaterialProvider;

public class AlwaysTrueCondition implements SpaceAndDepthMaterialProvider.Condition {
   public static final AlwaysTrueCondition INSTANCE = new AlwaysTrueCondition();

   private AlwaysTrueCondition() {
   }

   @Override
   public boolean qualifies(int x, int y, int z, int depthIntoFloor, int depthIntoCeiling, int spaceAboveFloor, int spaceBelowCeiling) {
      return true;
   }
}
