package com.hypixel.hytale.builtin.buildertools.tooloperations;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolOnUseInteraction;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockPattern;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class LayersOperation extends ToolOperation {
   private final Vector3i depthDirection;
   private final int layerOneLength;
   private final int layerTwoLength;
   private final boolean enableLayerTwo;
   private final int layerThreeLength;
   private final boolean enableLayerThree;
   private final BlockPattern layerOneBlockPattern;
   private final BlockPattern layerTwoBlockPattern;
   private final BlockPattern layerThreeBlockPattern;
   private final int brushDensity;
   private final int maxDepthNecessary;
   private boolean failed;
   private final int layerTwoDepthEnd;
   private final int layerThreeDepthEnd;

   public LayersOperation(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull Player player,
      @Nonnull BuilderToolOnUseInteraction packet,
      @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      super(ref, packet, componentAccessor);
      HeadRotation headRotationComponent = componentAccessor.getComponent(ref, HeadRotation.getComponentType());

      assert headRotationComponent != null;

      String var6 = (String)this.args.tool().get("aDirection");
      switch (var6) {
         case "Up":
            this.depthDirection = Vector3i.UP;
            break;
         case "Down":
            this.depthDirection = Vector3i.DOWN;
            break;
         case "North":
            this.depthDirection = Vector3i.NORTH;
            break;
         case "South":
            this.depthDirection = Vector3i.SOUTH;
            break;
         case "East":
            this.depthDirection = Vector3i.EAST;
            break;
         case "West":
            this.depthDirection = Vector3i.WEST;
            break;
         case "Camera":
            this.depthDirection = headRotationComponent.getAxisDirection();
            break;
         default:
            this.depthDirection = Vector3i.DOWN;
      }

      this.brushDensity = (Integer)this.args.tool().get("jBrushDensity");
      this.layerOneLength = (Integer)this.args.tool().get("bLayerOneLength");
      this.layerTwoLength = (Integer)this.args.tool().get("eLayerTwoLength");
      this.layerThreeLength = (Integer)this.args.tool().get("hLayerThreeLength");
      this.layerOneBlockPattern = (BlockPattern)this.args.tool().get("cLayerOneMaterial");
      this.layerTwoBlockPattern = (BlockPattern)this.args.tool().get("fLayerTwoMaterial");
      this.layerThreeBlockPattern = (BlockPattern)this.args.tool().get("iLayerThreeMaterial");
      this.enableLayerTwo = (Boolean)this.args.tool().get("dEnableLayerTwo");
      this.enableLayerThree = (Boolean)this.args.tool().get("gEnableLayerThree");
      this.maxDepthNecessary = this.layerOneLength + (this.enableLayerTwo ? this.layerTwoLength : 0) + (this.enableLayerThree ? this.layerThreeLength : 0);
      this.layerTwoDepthEnd = this.layerOneLength + this.layerTwoLength;
      this.layerThreeDepthEnd = this.layerTwoDepthEnd + this.layerThreeLength;
      if (this.enableLayerThree && !this.enableLayerTwo) {
         player.sendMessage(Message.translation("server.builderTools.layerOperation.layerTwoRequired"));
         this.failed = true;
      }
   }

   @Override
   boolean execute0(int x, int y, int z) {
      if (this.failed) {
         return false;
      } else if (this.random.nextInt(100) > this.brushDensity) {
         return true;
      } else {
         int currentBlock = this.edit.getBlock(x, y, z);
         if (currentBlock <= 0) {
            return true;
         } else {
            if (this.depthDirection.x == 1) {
               for (int i = 0; i < this.maxDepthNecessary; i++) {
                  if (this.edit.getBlock(x - i - 1, y, z) <= 0 && this.attemptSetBlock(x, y, z, i)) {
                     return true;
                  }
               }
            } else if (this.depthDirection.x == -1) {
               for (int ix = 0; ix < this.maxDepthNecessary; ix++) {
                  if (this.edit.getBlock(x + ix + 1, y, z) <= 0 && this.attemptSetBlock(x, y, z, ix)) {
                     return true;
                  }
               }
            } else if (this.depthDirection.y == 1) {
               for (int ixx = 0; ixx < this.maxDepthNecessary; ixx++) {
                  if (this.edit.getBlock(x, y - ixx - 1, z) <= 0 && this.attemptSetBlock(x, y, z, ixx)) {
                     return true;
                  }
               }
            } else if (this.depthDirection.y == -1) {
               for (int ixxx = 0; ixxx < this.maxDepthNecessary; ixxx++) {
                  if (this.edit.getBlock(x, y + ixxx + 1, z) <= 0 && this.attemptSetBlock(x, y, z, ixxx)) {
                     return true;
                  }
               }
            } else if (this.depthDirection.z == 1) {
               for (int ixxxx = 0; ixxxx < this.maxDepthNecessary; ixxxx++) {
                  if (this.edit.getBlock(x, y, z - ixxxx - 1) <= 0 && this.attemptSetBlock(x, y, z, ixxxx)) {
                     return true;
                  }
               }
            } else if (this.depthDirection.z == -1) {
               for (int ixxxxx = 0; ixxxxx < this.maxDepthNecessary; ixxxxx++) {
                  if (this.edit.getBlock(x, y, z + ixxxxx + 1) <= 0 && this.attemptSetBlock(x, y, z, ixxxxx)) {
                     return true;
                  }
               }
            }

            return true;
         }
      }
   }

   public boolean attemptSetBlock(int x, int y, int z, int depth) {
      if (depth < this.layerOneLength) {
         this.edit.setBlock(x, y, z, this.layerOneBlockPattern.nextBlock(this.random));
         return true;
      } else if (this.enableLayerTwo && depth < this.layerTwoDepthEnd && !this.layerThreeBlockPattern.isEmpty()) {
         this.edit.setBlock(x, y, z, this.layerTwoBlockPattern.nextBlock(this.random));
         return true;
      } else if (this.enableLayerThree && depth < this.layerThreeDepthEnd && !this.layerThreeBlockPattern.isEmpty()) {
         this.edit.setBlock(x, y, z, this.layerThreeBlockPattern.nextBlock(this.random));
         return true;
      } else {
         return false;
      }
   }
}
