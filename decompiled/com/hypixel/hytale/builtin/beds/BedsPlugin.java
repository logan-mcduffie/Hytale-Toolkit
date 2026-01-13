package com.hypixel.hytale.builtin.beds;

import com.hypixel.hytale.builtin.beds.interactions.BedInteraction;
import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSomnolence;
import com.hypixel.hytale.builtin.beds.sleep.components.SleepTracker;
import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSomnolence;
import com.hypixel.hytale.builtin.beds.sleep.systems.player.EnterBedSystem;
import com.hypixel.hytale.builtin.beds.sleep.systems.player.RegisterTrackerSystem;
import com.hypixel.hytale.builtin.beds.sleep.systems.player.UpdateSleepPacketSystem;
import com.hypixel.hytale.builtin.beds.sleep.systems.player.WakeUpOnDismountSystem;
import com.hypixel.hytale.builtin.beds.sleep.systems.world.StartSlumberSystem;
import com.hypixel.hytale.builtin.beds.sleep.systems.world.UpdateWorldSlumberSystem;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class BedsPlugin extends JavaPlugin {
   private static BedsPlugin instance;
   private ComponentType<EntityStore, PlayerSomnolence> playerSomnolenceComponentType;
   private ComponentType<EntityStore, SleepTracker> sleepTrackerComponentType;
   private ResourceType<EntityStore, WorldSomnolence> worldSomnolenceResourceType;

   public static BedsPlugin getInstance() {
      return instance;
   }

   public BedsPlugin(JavaPluginInit init) {
      super(init);
   }

   @Override
   protected void setup() {
      instance = this;
      this.playerSomnolenceComponentType = this.getEntityStoreRegistry().registerComponent(PlayerSomnolence.class, PlayerSomnolence::new);
      this.sleepTrackerComponentType = this.getEntityStoreRegistry().registerComponent(SleepTracker.class, SleepTracker::new);
      this.worldSomnolenceResourceType = this.getEntityStoreRegistry().registerResource(WorldSomnolence.class, WorldSomnolence::new);
      this.getEntityStoreRegistry().registerSystem(new StartSlumberSystem());
      this.getEntityStoreRegistry().registerSystem(new UpdateSleepPacketSystem());
      this.getEntityStoreRegistry().registerSystem(new WakeUpOnDismountSystem());
      this.getEntityStoreRegistry().registerSystem(new RegisterTrackerSystem());
      this.getEntityStoreRegistry().registerSystem(new UpdateWorldSlumberSystem());
      this.getEntityStoreRegistry().registerSystem(new EnterBedSystem());
      Interaction.CODEC.register("Bed", BedInteraction.class, BedInteraction.CODEC);
   }

   public ComponentType<EntityStore, PlayerSomnolence> getPlayerSomnolenceComponentType() {
      return this.playerSomnolenceComponentType;
   }

   public ComponentType<EntityStore, SleepTracker> getSleepTrackerComponentType() {
      return this.sleepTrackerComponentType;
   }

   public ResourceType<EntityStore, WorldSomnolence> getWorldSomnolenceResourceType() {
      return this.worldSomnolenceResourceType;
   }
}
