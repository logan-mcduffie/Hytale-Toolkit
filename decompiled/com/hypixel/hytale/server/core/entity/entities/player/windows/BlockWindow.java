package com.hypixel.hytale.server.core.entity.entities.player.windows;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.protocol.packets.window.WindowType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public abstract class BlockWindow extends Window implements ValidatedWindow {
   private static final float MAX_DISTANCE = 7.0F;
   protected final int x;
   protected final int y;
   protected final int z;
   @Nonnull
   protected BlockType blockType;
   protected final int rotationIndex;
   private double maxDistance = 7.0;
   private double maxDistanceSqr = this.maxDistance * this.maxDistance;

   public BlockWindow(@Nonnull WindowType windowType, int x, int y, int z, int rotationIndex, @Nonnull BlockType blockType) {
      super(windowType);
      this.x = x;
      this.y = y;
      this.z = z;
      this.rotationIndex = rotationIndex;
      this.blockType = blockType;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getZ() {
      return this.z;
   }

   public int getRotationIndex() {
      return this.rotationIndex;
   }

   @Nonnull
   public BlockType getBlockType() {
      return this.blockType;
   }

   public void setMaxDistance(double maxDistance) {
      this.maxDistance = maxDistance;
      this.maxDistanceSqr = maxDistance * maxDistance;
   }

   public double getMaxDistance() {
      return this.maxDistance;
   }

   @Override
   public boolean validate() {
      PlayerRef playerRef = this.getPlayerRef();
      if (playerRef == null) {
         return false;
      } else {
         Ref<EntityStore> ref = playerRef.getReference();
         if (ref == null) {
            return false;
         } else {
            Store<EntityStore> store = ref.getStore();
            World world = store.getExternalData().getWorld();
            TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
            if (transformComponent.getPosition().distanceSquaredTo(this.x, this.y, this.z) > this.maxDistanceSqr) {
               return false;
            } else {
               WorldChunk worldChunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(this.x, this.z));
               if (worldChunk == null) {
                  return false;
               } else {
                  BlockType currentBlockType = worldChunk.getBlockType(this.x, this.y, this.z);
                  if (currentBlockType == null) {
                     return false;
                  } else {
                     Item currentItem = currentBlockType.getItem();
                     return currentItem == null ? false : currentItem.equals(this.blockType.getItem());
                  }
               }
            }
         }
      }
   }
}
