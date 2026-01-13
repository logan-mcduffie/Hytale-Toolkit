package com.hypixel.hytale.builtin.portals.resources;

import com.hypixel.hytale.builtin.portals.PortalsPlugin;
import com.hypixel.hytale.builtin.portals.components.voidevent.config.VoidEventConfig;
import com.hypixel.hytale.builtin.portals.integrations.PortalGameplayConfig;
import com.hypixel.hytale.builtin.portals.integrations.PortalRemovalCondition;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.protocol.packets.interface_.PortalDef;
import com.hypixel.hytale.protocol.packets.interface_.PortalState;
import com.hypixel.hytale.protocol.packets.interface_.UpdatePortal;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import com.hypixel.hytale.server.core.asset.type.portalworld.PortalType;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public class PortalWorld implements Resource<EntityStore> {
   private String portalTypeId;
   private int timeLimitSeconds;
   private PortalRemovalCondition worldRemovalCondition;
   private PortalGameplayConfig storedGameplayConfig;
   private Set<UUID> diedInWorld;
   private Set<UUID> seesUi;
   private Transform spawnPoint;
   private Ref<EntityStore> voidEventRef;

   public static ResourceType<EntityStore, PortalWorld> getResourceType() {
      return PortalsPlugin.getInstance().getPortalResourceType();
   }

   public void init(PortalType portalType, int timeLimitSeconds, PortalRemovalCondition removalCondition, PortalGameplayConfig gameplayConfig) {
      this.portalTypeId = portalType.getId();
      this.timeLimitSeconds = timeLimitSeconds;
      this.worldRemovalCondition = removalCondition;
      this.storedGameplayConfig = gameplayConfig;
      this.diedInWorld = Collections.newSetFromMap(new ConcurrentHashMap<>());
      this.seesUi = Collections.newSetFromMap(new ConcurrentHashMap<>());
   }

   public PortalType getPortalType() {
      return this.portalTypeId == null ? null : PortalType.getAssetMap().getAsset(this.portalTypeId);
   }

   public boolean exists() {
      return this.getPortalType() != null;
   }

   public int getTimeLimitSeconds() {
      return this.timeLimitSeconds;
   }

   public double getElapsedSeconds(World world) {
      return this.worldRemovalCondition.getElapsedSeconds(world);
   }

   public double getRemainingSeconds(World world) {
      return this.worldRemovalCondition.getRemainingSeconds(world);
   }

   public void setRemainingSeconds(World world, double seconds) {
      this.worldRemovalCondition.setRemainingSeconds(world, seconds);
   }

   public Set<UUID> getDiedInWorld() {
      return this.diedInWorld;
   }

   public Set<UUID> getSeesUi() {
      return this.seesUi;
   }

   public PortalGameplayConfig getGameplayConfig() {
      GameplayConfig gameplayConfig = this.getPortalType().getGameplayConfig();
      PortalGameplayConfig portalGameplayConfig = gameplayConfig == null ? null : gameplayConfig.getPluginConfig().get(PortalGameplayConfig.class);
      return portalGameplayConfig != null ? portalGameplayConfig : this.storedGameplayConfig;
   }

   public VoidEventConfig getVoidEventConfig() {
      return this.getGameplayConfig().getVoidEvent();
   }

   @Nullable
   public Transform getSpawnPoint() {
      return this.spawnPoint;
   }

   public void setSpawnPoint(Transform spawnPoint) {
      this.spawnPoint = spawnPoint;
   }

   @Nullable
   public Ref<EntityStore> getVoidEventRef() {
      if (this.voidEventRef != null && !this.voidEventRef.isValid()) {
         this.voidEventRef = null;
      }

      return this.voidEventRef;
   }

   public boolean isVoidEventActive() {
      return this.getVoidEventRef() != null;
   }

   public void setVoidEventRef(Ref<EntityStore> voidEventRef) {
      this.voidEventRef = voidEventRef;
   }

   public UpdatePortal createFullPacket(World world) {
      boolean hasBreach = this.getPortalType().isVoidInvasionEnabled();
      int explorationSeconds;
      int breachSeconds;
      if (hasBreach) {
         breachSeconds = this.getGameplayConfig().getVoidEvent().getDurationSeconds();
         explorationSeconds = this.timeLimitSeconds - breachSeconds;
      } else {
         explorationSeconds = this.timeLimitSeconds;
         breachSeconds = 0;
      }

      PortalDef portalDef = new PortalDef(this.getPortalType().getDescription().getDisplayNameKey(), explorationSeconds, breachSeconds);
      return new UpdatePortal(this.createStateForPacket(world), portalDef);
   }

   public UpdatePortal createUpdatePacket(World world) {
      return new UpdatePortal(this.createStateForPacket(world), null);
   }

   private PortalState createStateForPacket(World world) {
      double remainingSeconds = this.worldRemovalCondition.getRemainingSeconds(world);
      int breachSeconds = this.getGameplayConfig().getVoidEvent().getDurationSeconds();
      if (this.getPortalType().isVoidInvasionEnabled() && remainingSeconds > breachSeconds) {
         remainingSeconds -= breachSeconds;
      }

      return new PortalState((int)Math.ceil(remainingSeconds), this.isVoidEventActive());
   }

   @Override
   public Resource<EntityStore> clone() {
      PortalWorld clone = new PortalWorld();
      clone.portalTypeId = this.portalTypeId;
      clone.timeLimitSeconds = this.timeLimitSeconds;
      clone.worldRemovalCondition = this.worldRemovalCondition;
      return clone;
   }
}
