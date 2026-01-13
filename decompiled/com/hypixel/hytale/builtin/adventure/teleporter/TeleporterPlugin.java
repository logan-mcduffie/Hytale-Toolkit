package com.hypixel.hytale.builtin.adventure.teleporter;

import com.hypixel.hytale.builtin.adventure.teleporter.component.Teleporter;
import com.hypixel.hytale.builtin.adventure.teleporter.interaction.server.TeleporterInteraction;
import com.hypixel.hytale.builtin.adventure.teleporter.page.TeleporterSettingsPageSupplier;
import com.hypixel.hytale.builtin.adventure.teleporter.system.CreateWarpWhenTeleporterPlacedSystem;
import com.hypixel.hytale.builtin.teleport.TeleportPlugin;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.OpenCustomUIInteraction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TeleporterPlugin extends JavaPlugin {
   private static TeleporterPlugin instance;
   private ComponentType<ChunkStore, Teleporter> teleporterComponentType;

   public static TeleporterPlugin get() {
      return instance;
   }

   public TeleporterPlugin(@Nonnull JavaPluginInit init) {
      super(init);
      instance = this;
   }

   @Override
   protected void setup() {
      this.teleporterComponentType = this.getChunkStoreRegistry().registerComponent(Teleporter.class, "Teleporter", Teleporter.CODEC);
      this.getChunkStoreRegistry().registerSystem(new TeleporterPlugin.TeleporterOwnedWarpRefChangeSystem());
      this.getChunkStoreRegistry().registerSystem(new TeleporterPlugin.TeleporterOwnedWarpRefSystem());
      this.getChunkStoreRegistry().registerSystem(new CreateWarpWhenTeleporterPlacedSystem());
      this.getCodecRegistry(Interaction.CODEC).register("Teleporter", TeleporterInteraction.class, TeleporterInteraction.CODEC);
      this.getCodecRegistry(OpenCustomUIInteraction.PAGE_CODEC)
         .register("Teleporter", TeleporterSettingsPageSupplier.class, TeleporterSettingsPageSupplier.CODEC);
   }

   public ComponentType<ChunkStore, Teleporter> getTeleporterComponentType() {
      return this.teleporterComponentType;
   }

   private static class TeleporterOwnedWarpRefChangeSystem extends RefChangeSystem<ChunkStore, Teleporter> {
      @Nonnull
      @Override
      public ComponentType<ChunkStore, Teleporter> componentType() {
         return Teleporter.getComponentType();
      }

      public void onComponentAdded(
         @Nonnull Ref<ChunkStore> ref, @Nonnull Teleporter component, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
      }

      public void onComponentSet(
         @Nonnull Ref<ChunkStore> ref,
         @Nullable Teleporter oldComponent,
         @Nonnull Teleporter newComponent,
         @Nonnull Store<ChunkStore> store,
         @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
         String ownedWarp = oldComponent.getOwnedWarp();
         if (ownedWarp != null && !ownedWarp.isEmpty() && !ownedWarp.equals(newComponent.getOwnedWarp())) {
            TeleportPlugin.get().getWarps().remove(ownedWarp.toLowerCase());
            TeleportPlugin.get().saveWarps();
            oldComponent.setOwnedWarp(null);
         }
      }

      public void onComponentRemoved(
         @Nonnull Ref<ChunkStore> ref, @Nonnull Teleporter component, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
         String ownedWarp = component.getOwnedWarp();
         if (ownedWarp != null && !ownedWarp.isEmpty()) {
            TeleportPlugin.get().getWarps().remove(ownedWarp.toLowerCase());
            TeleportPlugin.get().saveWarps();
            component.setOwnedWarp(null);
         }
      }

      @Nonnull
      @Override
      public Query<ChunkStore> getQuery() {
         return Query.any();
      }
   }

   private static class TeleporterOwnedWarpRefSystem extends RefSystem<ChunkStore> {
      public static final ComponentType<ChunkStore, Teleporter> COMPONENT_TYPE = Teleporter.getComponentType();

      @Override
      public void onEntityAdded(
         @Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
         switch (reason) {
            case LOAD:
               Teleporter component = commandBuffer.getComponent(ref, COMPONENT_TYPE);
               String ownedWarp = component.getOwnedWarp();
               if (ownedWarp != null && !ownedWarp.isEmpty() && !TeleportPlugin.get().getWarps().containsKey(ownedWarp.toLowerCase())) {
               }
            case SPAWN:
         }
      }

      @Override
      public void onEntityRemove(
         @Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
         if (reason == RemoveReason.REMOVE) {
            Teleporter component = commandBuffer.getComponent(ref, COMPONENT_TYPE);
            String ownedWarp = component.getOwnedWarp();
            if (ownedWarp != null && !ownedWarp.isEmpty()) {
               TeleportPlugin.get().getWarps().remove(ownedWarp.toLowerCase());
               TeleportPlugin.get().saveWarps();
               component.setOwnedWarp(null);
            }
         }
      }

      @Override
      public Query<ChunkStore> getQuery() {
         return COMPONENT_TYPE;
      }
   }
}
