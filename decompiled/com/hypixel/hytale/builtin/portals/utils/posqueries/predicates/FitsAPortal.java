package com.hypixel.hytale.builtin.portals.utils.posqueries.predicates;

import com.hypixel.hytale.builtin.portals.utils.posqueries.PositionPredicate;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.modules.collision.WorldUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

public class FitsAPortal implements PositionPredicate {
   private static final int[] THREES = new int[]{-1, 0, 1};

   @Override
   public boolean test(World world, Vector3d point) {
      return check(world, point);
   }

   public static boolean check(World world, Vector3d point) {
      ChunkStore chunkStore = world.getChunkStore();

      for (int x : THREES) {
         for (int z : THREES) {
            for (int y = -1; y <= 3; y++) {
               Vector3i rel = point.toVector3i().add(x, y, z);
               WorldChunk chunk = world.getChunk(ChunkUtil.indexChunkFromBlock(rel.x, rel.z));
               Ref<ChunkStore> chunkRef = chunk.getReference();
               Store<ChunkStore> chunkStoreAccessor = chunkStore.getStore();
               ChunkColumn chunkColumnComponent = chunkStoreAccessor.getComponent(chunkRef, ChunkColumn.getComponentType());
               BlockChunk blockChunkComponent = chunkStoreAccessor.getComponent(chunkRef, BlockChunk.getComponentType());
               int fluidId = WorldUtil.getFluidIdAtPosition(chunkStoreAccessor, chunkColumnComponent, rel.x, rel.y, rel.z);
               if (fluidId != 0) {
                  return false;
               }

               BlockSection blockSection = blockChunkComponent.getSectionAtBlockY(rel.y);
               int blockId = blockSection.get(rel.x, rel.y, rel.z);
               BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
               if (blockType == null) {
                  return false;
               }

               BlockMaterial wanted = y < 0 ? BlockMaterial.Solid : BlockMaterial.Empty;
               if (blockType.getMaterial() != wanted) {
                  return false;
               }
            }
         }
      }

      return true;
   }
}
