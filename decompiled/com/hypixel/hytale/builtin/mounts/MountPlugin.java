package com.hypixel.hytale.builtin.mounts;

import com.hypixel.hytale.builtin.mounts.commands.MountCommand;
import com.hypixel.hytale.builtin.mounts.interactions.MountInteraction;
import com.hypixel.hytale.builtin.mounts.interactions.SeatingInteraction;
import com.hypixel.hytale.builtin.mounts.interactions.SpawnMinecartInteraction;
import com.hypixel.hytale.builtin.mounts.minecart.MinecartComponent;
import com.hypixel.hytale.builtin.mounts.npc.builders.BuilderActionMount;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interaction.DismountNPC;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.io.ServerManager;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.systems.RoleChangeSystem;
import javax.annotation.Nonnull;

public class MountPlugin extends JavaPlugin {
   private static MountPlugin instance;
   private ComponentType<ChunkStore, BlockMountComponent> blockMountComponentType;
   private ComponentType<EntityStore, NPCMountComponent> mountComponentType;
   private ComponentType<EntityStore, MountedComponent> mountedComponentType;
   private ComponentType<EntityStore, MountedByComponent> mountedByComponentType;
   private ComponentType<EntityStore, MinecartComponent> minecartComponentType;

   public static MountPlugin getInstance() {
      return instance;
   }

   public MountPlugin(@Nonnull JavaPluginInit init) {
      super(init);
   }

   public ComponentType<EntityStore, NPCMountComponent> getMountComponentType() {
      return this.mountComponentType;
   }

   public ComponentType<EntityStore, MountedComponent> getMountedComponentType() {
      return this.mountedComponentType;
   }

   public ComponentType<EntityStore, MountedByComponent> getMountedByComponentType() {
      return this.mountedByComponentType;
   }

   public ComponentType<EntityStore, MinecartComponent> getMinecartComponentType() {
      return this.minecartComponentType;
   }

   @Override
   protected void setup() {
      instance = this;
      this.blockMountComponentType = this.getChunkStoreRegistry().registerComponent(BlockMountComponent.class, BlockMountComponent::new);
      NPCPlugin.get().registerCoreComponentType("Mount", BuilderActionMount::new);
      this.mountComponentType = this.getEntityStoreRegistry().registerComponent(NPCMountComponent.class, "Mount", NPCMountComponent.CODEC);
      this.mountedComponentType = this.getEntityStoreRegistry().registerComponent(MountedComponent.class, () -> {
         throw new UnsupportedOperationException("Mounted component cannot be default constructed");
      });
      this.mountedByComponentType = this.getEntityStoreRegistry().registerComponent(MountedByComponent.class, MountedByComponent::new);
      this.minecartComponentType = this.getEntityStoreRegistry().registerComponent(MinecartComponent.class, "Minecart", MinecartComponent.CODEC);
      this.getEntityStoreRegistry().registerSystem(new NPCMountSystems.OnAdd(this.mountComponentType));
      this.getEntityStoreRegistry().registerSystem(new NPCMountSystems.DismountOnPlayerDeath());
      this.getEntityStoreRegistry().registerSystem(new NPCMountSystems.DismountOnMountDeath());
      this.getEntityStoreRegistry().registerSystem(new MountSystems.TrackerUpdate());
      this.getEntityStoreRegistry().registerSystem(new MountSystems.TrackerRemove());
      this.getEntityStoreRegistry().registerSystem(new MountSystems.RemoveMountedBy());
      this.getEntityStoreRegistry().registerSystem(new MountSystems.RemoveMounted());
      this.getEntityStoreRegistry().registerSystem(new MountSystems.TeleportMountedEntity());
      this.getEntityStoreRegistry().registerSystem(new MountSystems.MountedEntityDeath());
      this.getEntityStoreRegistry().registerSystem(new MountSystems.PlayerMount());
      this.getEntityStoreRegistry().registerSystem(new MountSystems.HandleMountInput());
      this.getEntityStoreRegistry().registerSystem(new MountSystems.TrackedMounted());
      this.getEntityStoreRegistry().registerSystem(new MountSystems.EnsureMinecartComponents());
      this.getEntityStoreRegistry().registerSystem(new MountSystems.OnMinecartHit());
      this.getChunkStoreRegistry().registerSystem(new MountSystems.RemoveBlockSeat());
      ServerManager.get().registerSubPacketHandlers(MountGamePacketHandler::new);
      this.getEventRegistry().register(PlayerDisconnectEvent.class, MountPlugin::onPlayerDisconnect);
      this.getCommandRegistry().registerCommand(new MountCommand());
      Interaction.CODEC.register("SpawnMinecart", SpawnMinecartInteraction.class, SpawnMinecartInteraction.CODEC);
      Interaction.CODEC.register("Mount", MountInteraction.class, MountInteraction.CODEC);
      Interaction.CODEC.register("Seating", SeatingInteraction.class, SeatingInteraction.CODEC);
   }

   public ComponentType<ChunkStore, BlockMountComponent> getBlockMountComponentType() {
      return this.blockMountComponentType;
   }

   private static void onPlayerDisconnect(@Nonnull PlayerDisconnectEvent event) {
      PlayerRef playerRef = event.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      if (ref != null) {
         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         world.execute(() -> {
            if (ref.isValid()) {
               Player playerComponent = store.getComponent(ref, Player.getComponentType());
               if (playerComponent != null) {
                  checkDismountNpc(store, playerComponent);
               }
            }
         });
      }
   }

   public static void checkDismountNpc(@Nonnull ComponentAccessor<EntityStore> store, @Nonnull Player playerComponent) {
      int mountEntityId = playerComponent.getMountEntityId();
      if (mountEntityId != 0) {
         dismountNpc(store, mountEntityId);
      }
   }

   public static void dismountNpc(@Nonnull ComponentAccessor<EntityStore> store, int mountEntityId) {
      Ref<EntityStore> entityReference = store.getExternalData().getRefFromNetworkId(mountEntityId);
      if (entityReference != null && entityReference.isValid()) {
         NPCMountComponent mountComponent = store.getComponent(entityReference, NPCMountComponent.getComponentType());

         assert mountComponent != null;

         resetOriginalMountRole(entityReference, store, mountComponent);
         PlayerRef ownerPlayerRef = mountComponent.getOwnerPlayerRef();
         if (ownerPlayerRef != null) {
            resetOriginalPlayerMovementSettings(ownerPlayerRef, store);
         }
      }
   }

   private static void resetOriginalMountRole(
      @Nonnull Ref<EntityStore> entityReference, @Nonnull ComponentAccessor<EntityStore> store, @Nonnull NPCMountComponent mountComponent
   ) {
      NPCEntity npcComponent = store.getComponent(entityReference, NPCEntity.getComponentType());

      assert npcComponent != null;

      RoleChangeSystem.requestRoleChange(entityReference, npcComponent.getRole(), mountComponent.getOriginalRoleIndex(), false, "Idle", null, store);
      store.removeComponent(entityReference, NPCMountComponent.getComponentType());
   }

   public static void resetOriginalPlayerMovementSettings(@Nonnull PlayerRef playerRef, @Nonnull ComponentAccessor<EntityStore> store) {
      Ref<EntityStore> reference = playerRef.getReference();
      if (reference != null) {
         playerRef.getPacketHandler().write(new DismountNPC());
         MovementManager movementManagerComponent = store.getComponent(reference, MovementManager.getComponentType());

         assert movementManagerComponent != null;

         movementManagerComponent.resetDefaultsAndUpdate(reference, store);
      }
   }
}
