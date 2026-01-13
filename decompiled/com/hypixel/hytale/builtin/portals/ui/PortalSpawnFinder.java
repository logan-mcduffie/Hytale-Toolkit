package com.hypixel.hytale.builtin.portals.ui;

import com.hypixel.hytale.builtin.portals.utils.posqueries.generators.SearchCircular;
import com.hypixel.hytale.builtin.portals.utils.posqueries.predicates.FitsAPortal;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.portalworld.PortalSpawn;
import com.hypixel.hytale.server.core.modules.collision.WorldUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class PortalSpawnFinder {
   @Nullable
   public static Transform computeSpawnTransform(World world, PortalSpawn config) {
      Vector3d spawn = findSpawnByThrowingDarts(world, config);
      if (spawn == null) {
         spawn = findFallbackPositionOnGround(world, config);
         HytaleLogger.getLogger().at(Level.INFO).log("Had to use fallback spawn for portal spawn");
      }

      if (spawn == null) {
         HytaleLogger.getLogger().at(Level.INFO).log("Both dart and fallback spawn finder failed for portal spawn");
         return null;
      } else {
         Vector3f direction = Vector3f.lookAt(spawn).scale(-1.0F);
         direction.setPitch(0.0F);
         direction.setRoll(0.0F);
         return new Transform(spawn.clone().add(0.0, 0.5, 0.0), direction);
      }
   }

   @Nullable
   private static Vector3d findSpawnByThrowingDarts(World world, PortalSpawn config) {
      Vector3d center = config.getCenter().toVector3d();
      center.setY(config.getCheckSpawnY());
      int halfwayThrows = config.getChunkDartThrows() / 2;

      for (int chunkDart = 0; chunkDart < config.getChunkDartThrows(); chunkDart++) {
         Vector3d pointd = new SearchCircular(config.getMinRadius(), config.getMaxRadius(), 1).execute(world, center).orElse(null);
         if (pointd != null) {
            Vector3i point = pointd.toVector3i();
            WorldChunk chunk = world.getChunk(ChunkUtil.indexChunkFromBlock(point.x, point.z));
            BlockType firstBlock = chunk.getBlockType(point.x, point.y, point.z);
            if (firstBlock != null) {
               BlockMaterial firstBlockMat = firstBlock.getMaterial();
               if (firstBlockMat != BlockMaterial.Solid) {
                  boolean checkIfPortalFitsNice = chunkDart < halfwayThrows;
                  Vector3d spawn = findGroundWithinChunk(chunk, config, checkIfPortalFitsNice);
                  if (spawn != null) {
                     HytaleLogger.getLogger().at(Level.INFO).log("Found fragment spawn at " + spawn + " after " + (chunkDart + 1) + " chunk scan(s)");
                     return spawn;
                  }
               }
            }
         }
      }

      return null;
   }

   @Nullable
   private static Vector3d findGroundWithinChunk(WorldChunk chunk, PortalSpawn config, boolean checkIfPortalFitsNice) {
      int chunkBlockX = ChunkUtil.minBlock(chunk.getX());
      int chunkBlockZ = ChunkUtil.minBlock(chunk.getZ());
      ThreadLocalRandom rand = ThreadLocalRandom.current();

      for (int i = 0; i < config.getChecksPerChunk(); i++) {
         int x = chunkBlockX + rand.nextInt(2, 14);
         int z = chunkBlockZ + rand.nextInt(2, 14);
         Vector3d point = findWithGroundBelow(chunk, x, config.getCheckSpawnY(), z, config.getScanHeight(), false);
         if (point != null && (!checkIfPortalFitsNice || FitsAPortal.check(chunk.getWorld(), point))) {
            return point;
         }
      }

      return null;
   }

   @Nullable
   private static Vector3d findWithGroundBelow(WorldChunk chunk, int x, int y, int z, int scanHeight, boolean fluidsAreAcceptable) {
      World world = chunk.getWorld();
      ChunkStore chunkStore = world.getChunkStore();
      Ref<ChunkStore> chunkRef = chunk.getReference();
      Store<ChunkStore> chunkStoreAccessor = chunkStore.getStore();
      ChunkColumn chunkColumnComponent = chunkStoreAccessor.getComponent(chunkRef, ChunkColumn.getComponentType());
      BlockChunk blockChunkComponent = chunkStoreAccessor.getComponent(chunkRef, BlockChunk.getComponentType());

      for (int dy = 0; dy < scanHeight; dy++) {
         PortalSpawnFinder.Material selfMat = getMaterial(chunkStoreAccessor, chunkColumnComponent, blockChunkComponent, x, y - dy, z);
         PortalSpawnFinder.Material belowMat = getMaterial(chunkStoreAccessor, chunkColumnComponent, blockChunkComponent, x, y - dy - 1, z);
         boolean selfValid = selfMat == PortalSpawnFinder.Material.AIR || fluidsAreAcceptable && selfMat == PortalSpawnFinder.Material.FLUID;
         if (!selfValid) {
            break;
         }

         if (belowMat == PortalSpawnFinder.Material.SOLID) {
            return new Vector3d(x, y - dy, z);
         }
      }

      return null;
   }

   private static PortalSpawnFinder.Material getMaterial(
      @Nonnull ComponentAccessor<ChunkStore> chunkStore,
      @Nonnull ChunkColumn chunkColumnComponent,
      @Nonnull BlockChunk blockChunkComponent,
      double x,
      double y,
      double z
   ) {
      int blockX = (int)x;
      int blockY = (int)y;
      int blockZ = (int)z;
      int fluidId = WorldUtil.getFluidIdAtPosition(chunkStore, chunkColumnComponent, blockX, blockY, blockZ);
      if (fluidId != 0) {
         return PortalSpawnFinder.Material.FLUID;
      } else {
         BlockSection blockSection = blockChunkComponent.getSectionAtBlockY(blockY);
         int blockId = blockSection.get(blockX, blockY, blockZ);
         BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
         if (blockType == null) {
            return PortalSpawnFinder.Material.UNKNOWN;
         } else {
            return switch (blockType.getMaterial()) {
               case Solid -> PortalSpawnFinder.Material.SOLID;
               case Empty -> PortalSpawnFinder.Material.AIR;
            };
         }
      }
   }

   @Nullable
   private static Vector3d findFallbackPositionOnGround(World world, PortalSpawn config) {
      Vector3i center = config.getCenter();
      WorldChunk centerChunk = world.getChunk(ChunkUtil.indexChunkFromBlock(center.x, center.z));
      return findWithGroundBelow(centerChunk, 0, 319, 0, 319, true);
   }

   private static enum Material {
      SOLID,
      FLUID,
      AIR,
      UNKNOWN;
   }
}
