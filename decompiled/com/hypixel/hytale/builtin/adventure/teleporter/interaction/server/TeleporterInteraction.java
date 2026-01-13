package com.hypixel.hytale.builtin.adventure.teleporter.interaction.server;

import com.hypixel.hytale.builtin.adventure.teleporter.component.Teleporter;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.PendingTeleport;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TeleporterInteraction extends SimpleBlockInteraction {
   @Nonnull
   public static final BuilderCodec<TeleporterInteraction> CODEC = BuilderCodec.builder(
         TeleporterInteraction.class, TeleporterInteraction::new, SimpleBlockInteraction.CODEC
      )
      .appendInherited(
         new KeyedCodec<>("Particle", Codec.STRING),
         (interaction, s) -> interaction.particle = s,
         interaction -> interaction.particle,
         (interaction, parent) -> interaction.particle = parent.particle
      )
      .documentation("The particle to play on the entity when teleporting.")
      .add()
      .build();
   @Nullable
   private String particle;

   @Nonnull
   @Override
   public WaitForDataFrom getWaitForDataFrom() {
      return WaitForDataFrom.Server;
   }

   @Override
   protected void interactWithBlock(
      @Nonnull World world,
      @Nonnull CommandBuffer<EntityStore> commandBuffer,
      @Nonnull InteractionType type,
      @Nonnull InteractionContext context,
      @Nullable ItemStack itemInHand,
      @Nonnull Vector3i targetBlock,
      @Nonnull CooldownHandler cooldownHandler
   ) {
      ChunkStore chunkStore = world.getChunkStore();
      long chunkIndex = ChunkUtil.indexChunkFromBlock(targetBlock.getX(), targetBlock.getZ());
      BlockComponentChunk blockComponentChunk = chunkStore.getChunkComponent(chunkIndex, BlockComponentChunk.getComponentType());
      if (blockComponentChunk != null) {
         int blockIndex = ChunkUtil.indexBlockInColumn(targetBlock.x, targetBlock.y, targetBlock.z);
         Ref<ChunkStore> blockRef = blockComponentChunk.getEntityReference(blockIndex);
         if (blockRef != null && blockRef.isValid()) {
            BlockModule.BlockStateInfo blockStateInfoComponent = blockRef.getStore().getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());
            if (blockStateInfoComponent != null) {
               Ref<ChunkStore> chunkRef = blockStateInfoComponent.getChunkRef();
               if (chunkRef != null || chunkRef.isValid()) {
                  Teleporter teleporter = chunkStore.getStore().getComponent(blockRef, Teleporter.getComponentType());
                  if (teleporter != null) {
                     Ref<EntityStore> ref = context.getEntity();
                     Player playerComponent = commandBuffer.getComponent(ref, Player.getComponentType());
                     if (playerComponent == null || !playerComponent.isWaitingForClientReady()) {
                        Archetype<EntityStore> archetype = commandBuffer.getArchetype(ref);
                        if (!archetype.contains(Teleport.getComponentType()) && !archetype.contains(PendingTeleport.getComponentType())) {
                           if (!teleporter.isValid()) {
                              WorldChunk worldChunkComponent = chunkRef.getStore().getComponent(chunkRef, WorldChunk.getComponentType());

                              assert worldChunkComponent != null;

                              BlockType blockType = worldChunkComponent.getBlockType(targetBlock.x, targetBlock.y, targetBlock.z);
                              String currentState = blockType.getStateForBlock(blockType);
                              if (!"default".equals(currentState)) {
                                 BlockType variantBlockType = blockType.getBlockForState("default");
                                 if (variantBlockType != null) {
                                    worldChunkComponent.setBlockInteractionState(targetBlock.x, targetBlock.y, targetBlock.z, variantBlockType, "default", true);
                                 }
                              }
                           }

                           TransformComponent transformComponent = commandBuffer.getComponent(ref, TransformComponent.getComponentType());

                           assert transformComponent != null;

                           Teleport teleportComponent = teleporter.toTeleport(transformComponent.getPosition(), transformComponent.getRotation(), targetBlock);
                           if (teleportComponent != null) {
                              commandBuffer.addComponent(ref, Teleport.getComponentType(), teleportComponent);
                              if (this.particle != null) {
                                 Vector3d particlePosition = transformComponent.getPosition();
                                 SpatialResource<Ref<EntityStore>, EntityStore> playerSpatialResource = commandBuffer.getResource(
                                    EntityModule.get().getPlayerSpatialResourceType()
                                 );
                                 ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
                                 playerSpatialResource.getSpatialStructure().collect(particlePosition, 75.0, results);
                                 ParticleUtil.spawnParticleEffect(this.particle, particlePosition, results, commandBuffer);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   protected void simulateInteractWithBlock(
      @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull World world, @Nonnull Vector3i targetBlock
   ) {
   }
}
