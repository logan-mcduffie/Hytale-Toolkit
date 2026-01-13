package com.hypixel.hytale.server.npc.corecomponents.audiovisual.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.AssetHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSingleValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.asset.ParticleSystemExistsValidator;
import com.hypixel.hytale.server.npc.corecomponents.audiovisual.ActionSpawnParticles;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import javax.annotation.Nonnull;

public class BuilderActionSpawnParticles extends BuilderActionBase {
   protected final AssetHolder particleSystem = new AssetHolder();
   protected double range;
   protected double[] offset;

   @Nonnull
   public ActionSpawnParticles build(@Nonnull BuilderSupport builderSupport) {
      return new ActionSpawnParticles(this, builderSupport);
   }

   @Nonnull
   @Override
   public String getShortDescription() {
      return "Spawn particle system visible within a given range with an offset relative to npc heading";
   }

   @Nonnull
   @Override
   public String getLongDescription() {
      return this.getShortDescription();
   }

   @Nonnull
   @Override
   public BuilderDescriptorState getBuilderDescriptorState() {
      return BuilderDescriptorState.WorkInProgress;
   }

   @Nonnull
   public BuilderActionSpawnParticles readConfig(@Nonnull JsonElement data) {
      this.requireAsset(
         data, "ParticleSystem", this.particleSystem, ParticleSystemExistsValidator.required(), BuilderDescriptorState.Stable, "Particle system to spawn", null
      );
      this.getDouble(
         data, "Range", v -> this.range = v, 75.0, DoubleSingleValidator.greater0(), BuilderDescriptorState.Stable, "Maximum visibility range", null
      );
      this.getVector3d(
         data, "Offset", v -> this.offset = v, null, null, BuilderDescriptorState.Stable, "Offset relative to footpoint in view direction of NPC", null
      );
      return this;
   }

   public String getParticleSystem(@Nonnull BuilderSupport support) {
      return this.particleSystem.get(support.getExecutionContext());
   }

   public double getRange() {
      return this.range;
   }

   public Vector3d getOffset() {
      return createVector3d(this.offset, Vector3d.ZERO::clone);
   }
}
