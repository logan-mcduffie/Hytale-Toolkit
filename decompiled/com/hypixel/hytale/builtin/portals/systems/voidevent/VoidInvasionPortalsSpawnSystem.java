package com.hypixel.hytale.builtin.portals.systems.voidevent;

import com.hypixel.hytale.builtin.portals.components.voidevent.VoidEvent;
import com.hypixel.hytale.builtin.portals.components.voidevent.VoidSpawner;
import com.hypixel.hytale.builtin.portals.components.voidevent.config.InvasionPortalConfig;
import com.hypixel.hytale.builtin.portals.components.voidevent.config.VoidEventConfig;
import com.hypixel.hytale.builtin.portals.resources.PortalWorld;
import com.hypixel.hytale.builtin.portals.utils.posqueries.generators.SearchBelow;
import com.hypixel.hytale.builtin.portals.utils.posqueries.generators.SearchCone;
import com.hypixel.hytale.builtin.portals.utils.posqueries.predicates.FitsAPortal;
import com.hypixel.hytale.builtin.portals.utils.posqueries.predicates.NotNearAnyInHashGrid;
import com.hypixel.hytale.builtin.portals.utils.posqueries.predicates.NotNearPointXZ;
import com.hypixel.hytale.builtin.portals.utils.spatial.SpatialHashGrid;
import com.hypixel.hytale.common.util.RandomUtil;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VoidInvasionPortalsSpawnSystem extends DelayedEntitySystem<EntityStore> {
   private static final int MAX_PORTALS = 24;
   private CompletableFuture<Vector3d> findPortalSpawnPos;

   public VoidInvasionPortalsSpawnSystem() {
      super(2.0F);
   }

   @Override
   public void tick(
      float dt,
      int index,
      @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
      @Nonnull Store<EntityStore> store,
      @Nonnull CommandBuffer<EntityStore> commandBuffer
   ) {
      VoidEvent voidEvent = archetypeChunk.getComponent(index, VoidEvent.getComponentType());
      World world = store.getExternalData().getWorld();
      if (this.findPortalSpawnPos == null) {
         SpatialHashGrid<Ref<EntityStore>> spawners = this.cleanupAndGetSpawners(voidEvent);
         if (spawners.size() < 24) {
            this.findPortalSpawnPos = this.findPortalSpawnPosition(world, voidEvent, commandBuffer);
         }
      } else if (this.findPortalSpawnPos.isDone()) {
         Vector3d portalPos;
         try {
            portalPos = this.findPortalSpawnPos.join();
            this.findPortalSpawnPos = null;
         } catch (Throwable var14) {
            HytaleLogger.getLogger().at(Level.SEVERE).withCause(var14).log("Error trying to find a void event spawn position");
            return;
         }

         if (portalPos != null) {
            Holder<EntityStore> voidSpawnerHolder = EntityStore.REGISTRY.newHolder();
            voidSpawnerHolder.addComponent(VoidSpawner.getComponentType(), new VoidSpawner());
            voidSpawnerHolder.addComponent(TransformComponent.getComponentType(), new TransformComponent(portalPos, new Vector3f()));
            Ref<EntityStore> voidSpawner = commandBuffer.addEntity(voidSpawnerHolder, AddReason.SPAWN);
            voidEvent.getVoidSpawners().add(portalPos, voidSpawner);
            VoidEventConfig eventConfig = voidEvent.getConfig(world);
            if (eventConfig == null) {
               HytaleLogger.getLogger().at(Level.WARNING).log("There's a Void Event entity but no void event config in the gameplay config");
            } else {
               InvasionPortalConfig invasionPortalConfig = eventConfig.getInvasionPortalConfig();
               Vector3i portalBlockPos = portalPos.toVector3i();
               world.getChunkAsync(ChunkUtil.indexChunkFromBlock(portalBlockPos.x, portalBlockPos.z)).thenAcceptAsync(chunk -> {
                  BlockType blockType = invasionPortalConfig.getBlockType();
                  chunk.setBlock(portalBlockPos.x, portalBlockPos.y, portalBlockPos.z, blockType, 4);
               }, world);
            }
         }
      }
   }

   private CompletableFuture<Vector3d> findPortalSpawnPosition(World world, VoidEvent voidEvent, CommandBuffer<EntityStore> commandBuffer) {
      PortalWorld portalWorld = commandBuffer.getResource(PortalWorld.getResourceType());
      if (!portalWorld.exists()) {
         return null;
      } else {
         Vector3d spawnPos = portalWorld.getSpawnPoint().getPosition();
         Transform playerTransform = this.findRandomPlayerTransform(world, commandBuffer);
         if (playerTransform == null) {
            return null;
         } else {
            Vector3d origin = playerTransform.getPosition().clone().add(0.0, 5.0, 0.0);
            Vector3d direction = playerTransform.getDirection();
            SpatialHashGrid<Ref<EntityStore>> existingSpawners = voidEvent.getVoidSpawners();
            NotNearAnyInHashGrid noNearbySpawners = new NotNearAnyInHashGrid(existingSpawners, 62.0);
            return CompletableFuture.supplyAsync(
               () -> new SearchCone(direction, 48.0, 64.0, 90.0, 8)
                  .filter(noNearbySpawners)
                  .filter(new NotNearPointXZ(spawnPos, 18.0))
                  .then(new SearchBelow(12))
                  .filter(new FitsAPortal())
                  .execute(world, origin)
                  .orElse(null),
               world
            );
         }
      }
   }

   @Nullable
   private Transform findRandomPlayerTransform(World world, CommandBuffer<EntityStore> commandBuffer) {
      Collection<PlayerRef> playerRefs = world.getPlayerRefs();
      if (playerRefs.isEmpty()) {
         return null;
      } else {
         List<Ref<EntityStore>> players = new ObjectArrayList<>(playerRefs.size());

         for (PlayerRef playerRef : playerRefs) {
            players.add(playerRef.getReference());
         }

         Ref<EntityStore> randomPlayer = RandomUtil.selectRandom(players);
         TransformComponent transformComponent = commandBuffer.getComponent(randomPlayer, TransformComponent.getComponentType());

         assert transformComponent != null;

         return transformComponent.getTransform();
      }
   }

   private SpatialHashGrid<Ref<EntityStore>> cleanupAndGetSpawners(VoidEvent voidEvent) {
      SpatialHashGrid<Ref<EntityStore>> spawners = voidEvent.getVoidSpawners();
      spawners.removeIf(ref -> !ref.isValid());
      return spawners;
   }

   @Nullable
   @Override
   public Query<EntityStore> getQuery() {
      return VoidEvent.getComponentType();
   }
}
