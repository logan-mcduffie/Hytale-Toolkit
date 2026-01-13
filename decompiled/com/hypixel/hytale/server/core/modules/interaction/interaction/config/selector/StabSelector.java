package com.hypixel.hytale.server.core.modules.interaction.interaction.config.selector;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.function.consumer.TriIntConsumer;
import com.hypixel.hytale.math.hitdetection.HitDetectionExecutor;
import com.hypixel.hytale.math.hitdetection.LineOfSightProvider;
import com.hypixel.hytale.math.hitdetection.projection.OrthogonalProjectionProvider;
import com.hypixel.hytale.math.hitdetection.view.DirectionViewProvider;
import com.hypixel.hytale.math.iterator.BlockIterator;
import com.hypixel.hytale.math.matrix.Matrix4d;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.HashUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector4d;
import com.hypixel.hytale.server.core.asset.type.blockhitbox.BlockBoundingBoxes;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.modules.debug.DebugUtils;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.none.SelectInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.accessor.LocalCachedChunkAccessor;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public class StabSelector extends SelectorType {
   public static final BuilderCodec<StabSelector> CODEC = BuilderCodec.builder(StabSelector.class, StabSelector::new, BASE_CODEC)
      .documentation("A selector  that stabs in a straight line over a given period of time.")
      .<Double>appendInherited(
         new KeyedCodec<>("StartDistance", Codec.DOUBLE),
         (selector, value) -> selector.startDistance = value,
         selector -> selector.startDistance,
         (selector, parent) -> selector.startDistance = parent.startDistance
      )
      .documentation("The distance from the entity that the selector starts its search from.")
      .add()
      .<Double>appendInherited(
         new KeyedCodec<>("EndDistance", Codec.DOUBLE),
         (selector, value) -> selector.endDistance = value,
         selector -> selector.endDistance,
         (selector, parent) -> selector.endDistance = parent.endDistance
      )
      .documentation("The distance from the entity that the selector ends its search at.")
      .add()
      .<Double>appendInherited(
         new KeyedCodec<>("ExtendTop", Codec.DOUBLE),
         (selector, value) -> selector.extendTop = value,
         selector -> selector.extendTop,
         (selector, parent) -> selector.extendTop = parent.extendTop
      )
      .documentation("The amount to extend the top of the selector by.")
      .add()
      .<Double>appendInherited(
         new KeyedCodec<>("ExtendBottom", Codec.DOUBLE),
         (selector, value) -> selector.extendBottom = value,
         selector -> selector.extendBottom,
         (selector, parent) -> selector.extendBottom = parent.extendBottom
      )
      .documentation("The amount to extend the bottom of the selector by.")
      .add()
      .<Double>appendInherited(
         new KeyedCodec<>("ExtendLeft", Codec.DOUBLE),
         (selector, value) -> selector.extendLeft = value,
         selector -> selector.extendLeft,
         (selector, parent) -> selector.extendLeft = parent.extendLeft
      )
      .documentation("The amount to extend the left side of the selector by.")
      .add()
      .<Double>appendInherited(
         new KeyedCodec<>("ExtendRight", Codec.DOUBLE),
         (selector, value) -> selector.extendRight = value,
         selector -> selector.extendRight,
         (selector, parent) -> selector.extendRight = parent.extendRight
      )
      .documentation("The amount to extend the right side of the selector by.")
      .add()
      .<Double>appendInherited(
         new KeyedCodec<>("YawOffset", Codec.DOUBLE),
         (selector, value) -> selector.yawOffset = Math.toRadians(value),
         selector -> Math.toDegrees(selector.yawOffset),
         (selector, parent) -> selector.yawOffset = parent.yawOffset
      )
      .documentation("The yaw rotation offset in degrees for this selector")
      .add()
      .<Double>appendInherited(
         new KeyedCodec<>("PitchOffset", Codec.DOUBLE),
         (selector, value) -> selector.pitchOffset = Math.toRadians(value),
         selector -> Math.toDegrees(selector.pitchOffset),
         (selector, parent) -> selector.pitchOffset = parent.pitchOffset
      )
      .documentation("The pitch rotation offset in degrees for this selector")
      .add()
      .<Double>appendInherited(
         new KeyedCodec<>("RollOffset", Codec.DOUBLE),
         (selector, value) -> selector.rollOffset = Math.toRadians(value),
         selector -> Math.toDegrees(selector.rollOffset),
         (selector, parent) -> selector.rollOffset = parent.rollOffset
      )
      .documentation("The roll rotation offset in degrees for this selector")
      .add()
      .<Boolean>appendInherited(
         new KeyedCodec<>("TestLineOfSight", Codec.BOOLEAN),
         (selector, value) -> selector.testLineOfSight = value,
         selector -> selector.testLineOfSight,
         (selector, parent) -> selector.testLineOfSight = parent.testLineOfSight
      )
      .documentation("Whether to test for line of sight between the user and the target before counting a hit")
      .add()
      .build();
   protected double extendTop = 1.0;
   protected double extendBottom = 1.0;
   protected double extendLeft = 1.0;
   protected double extendRight = 1.0;
   protected double yawOffset;
   protected double pitchOffset;
   protected double rollOffset;
   protected double startDistance = 0.01;
   protected double endDistance;
   protected boolean testLineOfSight;

   @Nonnull
   @Override
   public Selector newSelector() {
      return new StabSelector.RuntimeSelector();
   }

   public com.hypixel.hytale.protocol.Selector toPacket() {
      com.hypixel.hytale.protocol.StabSelector selector = new com.hypixel.hytale.protocol.StabSelector();
      selector.extendTop = (float)this.extendTop;
      selector.extendBottom = (float)this.extendBottom;
      selector.extendLeft = (float)this.extendLeft;
      selector.extendRight = (float)this.extendRight;
      selector.yawOffset = (float)this.yawOffset;
      selector.pitchOffset = (float)this.pitchOffset;
      selector.rollOffset = (float)this.rollOffset;
      selector.startDistance = (float)this.startDistance;
      selector.endDistance = (float)this.endDistance;
      selector.testLineOfSight = this.testLineOfSight;
      return selector;
   }

   private class RuntimeSelector implements Selector {
      @Nonnull
      protected HitDetectionExecutor executor = new HitDetectionExecutor();
      @Nonnull
      protected Matrix4d modelMatrix = new Matrix4d();
      @Nonnull
      protected OrthogonalProjectionProvider projectionProvider = new OrthogonalProjectionProvider();
      @Nonnull
      protected DirectionViewProvider viewProvider = new DirectionViewProvider();
      protected float lastTime = 0.0F;
      protected double runTimeDeltaPercentageSum;

      @Override
      public void tick(@Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Ref<EntityStore> attacker, float time, float runTime) {
         float yOffset = commandBuffer.getComponent(attacker, ModelComponent.getComponentType()).getModel().getEyeHeight(attacker, commandBuffer);
         Vector3d position = commandBuffer.getComponent(attacker, TransformComponent.getComponentType()).getPosition();
         HeadRotation look = commandBuffer.getComponent(attacker, HeadRotation.getComponentType());
         double posX = position.getX();
         double posY = position.getY() + yOffset;
         double posZ = position.getZ();
         float delta = time - this.lastTime;
         this.lastTime = time;
         float runTimeDeltaPercentage = delta / runTime;
         double distanceDiff = StabSelector.this.endDistance - StabSelector.this.startDistance;
         double deltaStartDistance = this.runTimeDeltaPercentageSum * distanceDiff + StabSelector.this.startDistance;
         double deltaEndDistance = (this.runTimeDeltaPercentageSum + runTimeDeltaPercentage) * distanceDiff + StabSelector.this.startDistance;
         this.projectionProvider
            .setNear(deltaStartDistance)
            .setFar(deltaEndDistance)
            .setLeft(StabSelector.this.extendLeft)
            .setRight(StabSelector.this.extendRight)
            .setBottom(StabSelector.this.extendBottom)
            .setTop(StabSelector.this.extendTop)
            .setRotation(StabSelector.this.yawOffset, StabSelector.this.pitchOffset, StabSelector.this.rollOffset);
         this.viewProvider.setPosition(posX, posY, posZ).setDirection(look.getRotation().getYaw(), look.getRotation().getPitch());
         this.executor.setOrigin(posX, posY, posZ).setProjectionProvider(this.projectionProvider).setViewProvider(this.viewProvider);
         if (StabSelector.this.testLineOfSight) {
            this.executor
               .setLineOfSightProvider(
                  (fromX, fromY, fromZ, toX, toY, toZ) -> {
                     LocalCachedChunkAccessor localAccessor = LocalCachedChunkAccessor.atWorldCoords(
                        commandBuffer.getStore().getExternalData().getWorld(), (int)fromX, (int)fromZ, (int)(StabSelector.this.endDistance + 1.0)
                     );
                     return BlockIterator.iterateFromTo(fromX, fromY, fromZ, toX, toY, toZ, (x, y, z, px, py, pz, qx, qy, qz, accessor) -> {
                        int blockId = accessor.getBlock(x, y, z);
                        return blockId == 0;
                     }, localAccessor);
                  }
               );
         } else {
            this.executor.setLineOfSightProvider(LineOfSightProvider.DEFAULT_TRUE);
         }

         if (SelectInteraction.SHOW_VISUAL_DEBUG) {
            Matrix4d tmp = new Matrix4d();
            Matrix4d matrix = new Matrix4d();
            matrix.identity()
               .translate(posX, posY, posZ)
               .rotateAxis(-look.getRotation().getYaw(), 0.0, 1.0, 0.0, tmp)
               .rotateAxis(-look.getRotation().getPitch(), 1.0, 0.0, 0.0, tmp);
            Vector3f color = new Vector3f(
               (float)HashUtil.random(attacker.getIndex(), this.hashCode(), 10L),
               (float)HashUtil.random(attacker.getIndex(), this.hashCode(), 11L),
               (float)HashUtil.random(attacker.getIndex(), this.hashCode(), 12L)
            );
            DebugUtils.addFrustum(commandBuffer.getExternalData().getWorld(), matrix, this.projectionProvider.getMatrix(), color, 5.0F, true);
         }

         this.runTimeDeltaPercentageSum += runTimeDeltaPercentage;
      }

      @Override
      public void selectTargetEntities(
         @Nonnull CommandBuffer<EntityStore> commandBuffer,
         @Nonnull Ref<EntityStore> attacker,
         @Nonnull BiConsumer<Ref<EntityStore>, Vector4d> consumer,
         Predicate<Ref<EntityStore>> filter
      ) {
         Selector.selectNearbyEntities(commandBuffer, attacker, StabSelector.this.endDistance + 3.0, entity -> {
            BoundingBox hitboxComponent = commandBuffer.getComponent(entity, BoundingBox.getComponentType());
            if (hitboxComponent != null) {
               Box hitbox = hitboxComponent.getBoundingBox();
               TransformComponent transform = commandBuffer.getComponent(entity, TransformComponent.getComponentType());
               this.modelMatrix.identity().translate(transform.getPosition()).translate(hitbox.getMin()).scale(hitbox.width(), hitbox.height(), hitbox.depth());
               if (this.executor.test(HitDetectionExecutor.CUBE_QUADS, this.modelMatrix)) {
                  consumer.accept(entity, this.executor.getHitLocation());
               }
            }
         }, filter);
      }

      @Override
      public void selectTargetBlocks(@Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Ref<EntityStore> attacker, @Nonnull TriIntConsumer consumer) {
         Selector.selectNearbyBlocks(commandBuffer, attacker, StabSelector.this.startDistance + StabSelector.this.endDistance, (x, y, z) -> {
            World world = commandBuffer.getStore().getExternalData().getWorld();
            WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(x, z));
            if (chunk != null) {
               BlockType blockType = chunk.getBlockType(x, y, z);
               if (blockType != BlockType.EMPTY) {
                  int rotation = chunk.getRotationIndex(x, y, z);
                  Box[] hitboxes = BlockBoundingBoxes.getAssetMap().getAsset(blockType.getHitboxTypeIndex()).get(rotation).getDetailBoxes();

                  for (int i = 0; i < hitboxes.length; i++) {
                     Box hitbox = hitboxes[i];
                     this.modelMatrix.identity().translate(x, y, z).translate(hitbox.getMin()).scale(hitbox.width(), hitbox.height(), hitbox.depth());
                     if (this.executor.test(HitDetectionExecutor.CUBE_QUADS, this.modelMatrix)) {
                        consumer.accept(x, y, z);
                        break;
                     }
                  }
               }
            }
         });
      }
   }
}
