package com.hypixel.hytale.builtin.portals.systems;

import com.hypixel.hytale.builtin.portals.components.PortalDevice;
import com.hypixel.hytale.builtin.portals.components.PortalDeviceConfig;
import com.hypixel.hytale.builtin.portals.utils.BlockTypeUtils;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.AndQuery;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import java.util.UUID;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class PortalInvalidDestinationSystem extends RefSystem<ChunkStore> {
   @Override
   public void onEntityAdded(
      @Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer
   ) {
      if (reason == AddReason.LOAD) {
         World originWorld = store.getExternalData().getWorld();
         PortalDevice portalDevice = commandBuffer.getComponent(ref, PortalDevice.getComponentType());
         BlockModule.BlockStateInfo blockStateInfo = commandBuffer.getComponent(ref, BlockModule.BlockStateInfo.getComponentType());
         World destinationWorld = portalDevice.getDestinationWorld();
         if (destinationWorld == null) {
            originWorld.execute(() -> turnOffPortalBlock(originWorld, portalDevice, blockStateInfo));
         }
      }
   }

   @Override
   public void onEntityRemove(
      @Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer
   ) {
   }

   @Override
   public Query<ChunkStore> getQuery() {
      return Query.and(PortalDevice.getComponentType(), BlockModule.BlockStateInfo.getComponentType());
   }

   public static void turnOffPortalsInWorld(World originWorld, World destinationWorld) {
      UUID destinationWorldUuid = destinationWorld.getWorldConfig().getUuid();
      Store<ChunkStore> store = originWorld.getChunkStore().getStore();
      AndQuery<ChunkStore> entityQuery = Query.and(PortalDevice.getComponentType(), BlockModule.BlockStateInfo.getComponentType());
      store.forEachEntityParallel(entityQuery, (id, archetypeChunk, commandBuffer) -> {
         PortalDevice portalDevice = archetypeChunk.getComponent(id, PortalDevice.getComponentType());
         if (destinationWorldUuid.equals(portalDevice.getDestinationWorldUuid())) {
            BlockModule.BlockStateInfo blockStateInfo = archetypeChunk.getComponent(id, BlockModule.BlockStateInfo.getComponentType());
            originWorld.execute(() -> turnOffPortalBlock(originWorld, portalDevice, blockStateInfo));
         }
      });
   }

   private static void turnOffPortalBlock(World world, PortalDevice portalDevice, BlockModule.BlockStateInfo blockStateInfo) {
      Ref<ChunkStore> chunkRef = blockStateInfo.getChunkRef();
      if (chunkRef != null && chunkRef.isValid()) {
         Store<ChunkStore> store = world.getChunkStore().getStore();
         WorldChunk worldChunk = store.getComponent(chunkRef, WorldChunk.getComponentType());
         if (worldChunk != null) {
            int index = blockStateInfo.getIndex();
            int x = ChunkUtil.xFromBlockInColumn(index);
            int y = ChunkUtil.yFromBlockInColumn(index);
            int z = ChunkUtil.zFromBlockInColumn(index);
            PortalDeviceConfig config = portalDevice.getConfig();
            BlockType blockType = worldChunk.getBlockType(x, y, z);
            BlockType offState = BlockTypeUtils.getBlockForState(blockType, config.getOffState());
            if (offState == null) {
               HytaleLogger.getLogger()
                  .at(Level.WARNING)
                  .log("Couldn't find/set off set for portal block, either " + blockType.getId() + " is misconfigured or the block changed unexpectedly");
            } else {
               worldChunk.setBlockInteractionState(x, y, z, blockType, config.getOffState(), false);
            }
         }
      }
   }
}
