package com.hypixel.hytale.server.npc.corecomponents.combat.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.Feature;
import com.hypixel.hytale.server.npc.asset.builder.holder.DoubleHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleRangeValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderHeadMotionBase;
import com.hypixel.hytale.server.npc.corecomponents.combat.HeadMotionAim;
import javax.annotation.Nonnull;

public class BuilderHeadMotionAim extends BuilderHeadMotionBase {
   protected double spread;
   protected boolean deflection;
   protected double hitProbability;
   protected final DoubleHolder relativeTurnSpeed = new DoubleHolder();

   @Nonnull
   public HeadMotionAim build(@Nonnull BuilderSupport builderSupport) {
      return new HeadMotionAim(this, builderSupport);
   }

   @Nonnull
   @Override
   public String getShortDescription() {
      return "Aim at target";
   }

   @Nonnull
   @Override
   public String getLongDescription() {
      return "Aim at target considering weapon in hand.";
   }

   @Nonnull
   @Override
   public BuilderDescriptorState getBuilderDescriptorState() {
      return BuilderDescriptorState.Stable;
   }

   @Nonnull
   public BuilderHeadMotionAim readConfig(@Nonnull JsonElement data) {
      this.getDouble(
         data, "Spread", d -> this.spread = d, 1.0, DoubleRangeValidator.between(0.0, 5.0), BuilderDescriptorState.Experimental, "Random targeting error", null
      );
      this.getDouble(
         data,
         "HitProbability",
         d -> this.hitProbability = d,
         0.33,
         DoubleRangeValidator.between01(),
         BuilderDescriptorState.Experimental,
         "Probability of shot being straight on target",
         null
      );
      this.getBoolean(data, "Deflection", b -> this.deflection = b, true, BuilderDescriptorState.Experimental, "Compute deflection for moving targets", null);
      this.getDouble(
         data,
         "RelativeTurnSpeed",
         this.relativeTurnSpeed,
         1.0,
         DoubleRangeValidator.fromExclToIncl(0.0, 2.0),
         BuilderDescriptorState.Stable,
         "The relative turn speed modifier",
         null
      );
      this.requireFeature(Feature.AnyPosition);
      return this;
   }

   public double getSpread() {
      return this.spread;
   }

   public boolean isDeflection() {
      return this.deflection;
   }

   public double getHitProbability() {
      return this.hitProbability;
   }

   public double getRelativeTurnSpeed(@Nonnull BuilderSupport support) {
      return this.relativeTurnSpeed.get(support.getExecutionContext());
   }
}
