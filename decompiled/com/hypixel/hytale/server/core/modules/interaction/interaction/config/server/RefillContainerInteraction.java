package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.Interaction;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionSyncData;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RefillContainerInteraction extends SimpleBlockInteraction {
   public static final BuilderCodec<RefillContainerInteraction> CODEC = BuilderCodec.builder(
         RefillContainerInteraction.class, RefillContainerInteraction::new, SimpleBlockInteraction.CODEC
      )
      .documentation("Refills a container item that is currently held.")
      .<Map>appendInherited(
         new KeyedCodec<>("States", new MapCodec<>(RefillContainerInteraction.RefillState.CODEC, HashMap::new)),
         (interaction, value) -> interaction.refillStateMap = value,
         interaction -> interaction.refillStateMap,
         (o, p) -> o.refillStateMap = p.refillStateMap
      )
      .addValidator(Validators.nonNull())
      .add()
      .afterDecode(refillContainerInteraction -> {
         refillContainerInteraction.allowedFluidIds = null;
         refillContainerInteraction.fluidToState = null;
      })
      .build();
   protected Map<String, RefillContainerInteraction.RefillState> refillStateMap;
   @Nullable
   protected int[] allowedFluidIds;
   @Nullable
   protected Int2ObjectMap<String> fluidToState;

   protected int[] getAllowedFluidIds() {
      if (this.allowedFluidIds != null) {
         return this.allowedFluidIds;
      } else {
         this.allowedFluidIds = this.refillStateMap
            .values()
            .stream()
            .map(RefillContainerInteraction.RefillState::getAllowedFluids)
            .flatMap(Arrays::stream)
            .mapToInt(key -> Fluid.getAssetMap().getIndex(key))
            .sorted()
            .toArray();
         return this.allowedFluidIds;
      }
   }

   protected Int2ObjectMap<String> getFluidToState() {
      if (this.fluidToState != null) {
         return this.fluidToState;
      } else {
         this.fluidToState = new Int2ObjectOpenHashMap<>();
         this.refillStateMap.forEach((s, refillState) -> {
            for (String key : refillState.getAllowedFluids()) {
               this.fluidToState.put(Fluid.getAssetMap().getIndex(key), s);
            }
         });
         return this.fluidToState;
      }
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
      Ref<EntityStore> ref = context.getEntity();
      if (EntityUtils.getEntity(ref, commandBuffer) instanceof LivingEntity livingEntity) {
         BlockPosition var24 = context.getClientState().blockPosition;
         InteractionSyncData state = context.getState();
         if (var24 == null) {
            state.state = InteractionState.Failed;
         } else {
            Ref<ChunkStore> section = world.getChunkStore()
               .getChunkSectionReference(ChunkUtil.chunkCoordinate(var24.x), ChunkUtil.chunkCoordinate(var24.y), ChunkUtil.chunkCoordinate(var24.z));
            if (section != null) {
               FluidSection fluidSection = section.getStore().getComponent(section, FluidSection.getComponentType());
               if (fluidSection != null) {
                  int fluidId = fluidSection.getFluidId(var24.x, var24.y, var24.z);
                  int[] allowedBlockIds = this.getAllowedFluidIds();
                  if (allowedBlockIds != null && Arrays.binarySearch(allowedBlockIds, fluidId) < 0) {
                     state.state = InteractionState.Failed;
                  } else {
                     String newState = this.getFluidToState().get(fluidId);
                     if (newState == null) {
                        state.state = InteractionState.Failed;
                     } else {
                        ItemStack current = context.getHeldItem();
                        Item newItemAsset = current.getItem().getItemForState(newState);
                        if (newItemAsset == null) {
                           state.state = InteractionState.Failed;
                        } else {
                           RefillContainerInteraction.RefillState refillState = this.refillStateMap.get(newState);
                           if (newItemAsset.getId().equals(current.getItemId())) {
                              if (refillState != null) {
                                 double newDurability = MathUtil.maxValue(refillState.durability, current.getMaxDurability());
                                 if (newDurability <= current.getDurability()) {
                                    state.state = InteractionState.Failed;
                                    return;
                                 }

                                 ItemStack newItem = current.withIncreasedDurability(newDurability);
                                 ItemStackSlotTransaction transaction = context.getHeldItemContainer().setItemStackForSlot(context.getHeldItemSlot(), newItem);
                                 if (!transaction.succeeded()) {
                                    state.state = InteractionState.Failed;
                                    return;
                                 }

                                 context.setHeldItem(newItem);
                              }
                           } else {
                              ItemStackSlotTransaction removeEmptyTransaction = context.getHeldItemContainer()
                                 .removeItemStackFromSlot(context.getHeldItemSlot(), current, 1);
                              if (!removeEmptyTransaction.succeeded()) {
                                 state.state = InteractionState.Failed;
                                 return;
                              }

                              ItemStack refilledContainer = new ItemStack(newItemAsset.getId(), 1);
                              if (refillState != null && refillState.durability > 0.0) {
                                 refilledContainer = refilledContainer.withDurability(refillState.durability);
                              }

                              if (current.getQuantity() == 1) {
                                 ItemStackSlotTransaction addFilledTransaction = context.getHeldItemContainer()
                                    .setItemStackForSlot(context.getHeldItemSlot(), refilledContainer);
                                 if (!addFilledTransaction.succeeded()) {
                                    state.state = InteractionState.Failed;
                                    return;
                                 }

                                 context.setHeldItem(refilledContainer);
                              } else {
                                 SimpleItemContainer.addOrDropItemStack(
                                    commandBuffer, ref, livingEntity.getInventory().getCombinedHotbarFirst(), refilledContainer
                                 );
                                 context.setHeldItem(context.getHeldItemContainer().getItemStack(context.getHeldItemSlot()));
                              }
                           }

                           if (refillState != null && refillState.getTransformFluid() != null) {
                              int transformedFluid = Fluid.getFluidIdOrUnknown(
                                 refillState.getTransformFluid(), "Unknown fluid %s", refillState.getTransformFluid()
                              );
                              boolean placed = fluidSection.setFluid(
                                 var24.x, var24.y, var24.z, transformedFluid, (byte)Fluid.getAssetMap().getAsset(transformedFluid).getMaxFluidLevel()
                              );
                              if (!placed) {
                                 state.state = InteractionState.Failed;
                              }

                              world.performBlockUpdate(var24.x, var24.y, var24.z);
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

   @Nonnull
   @Override
   protected Interaction generatePacket() {
      return new com.hypixel.hytale.protocol.RefillContainerInteraction();
   }

   @Override
   protected void configurePacket(Interaction packet) {
      super.configurePacket(packet);
      com.hypixel.hytale.protocol.RefillContainerInteraction p = (com.hypixel.hytale.protocol.RefillContainerInteraction)packet;
      p.refillFluids = this.getAllowedFluidIds();
   }

   @Nonnull
   @Override
   public String toString() {
      return "RefillContainerInteraction{refillStateMap="
         + this.refillStateMap
         + ", allowedBlockIds="
         + Arrays.toString(this.allowedFluidIds)
         + ", blockToState="
         + this.fluidToState
         + "} "
         + super.toString();
   }

   protected static class RefillState {
      public static final BuilderCodec<RefillContainerInteraction.RefillState> CODEC = BuilderCodec.builder(
            RefillContainerInteraction.RefillState.class, RefillContainerInteraction.RefillState::new
         )
         .append(
            new KeyedCodec<>("AllowedFluids", new ArrayCodec<>(Codec.STRING, String[]::new)),
            (interaction, value) -> interaction.allowedFluids = value,
            interaction -> interaction.allowedFluids
         )
         .addValidator(Validators.nonNull())
         .add()
         .addField(
            new KeyedCodec<>("TransformFluid", Codec.STRING),
            (interaction, value) -> interaction.transformFluid = value,
            interaction -> interaction.transformFluid
         )
         .addField(new KeyedCodec<>("Durability", Codec.DOUBLE), (interaction, value) -> interaction.durability = value, interaction -> interaction.durability)
         .build();
      protected String[] allowedFluids;
      protected String transformFluid;
      protected double durability = -1.0;

      public String[] getAllowedFluids() {
         return this.allowedFluids;
      }

      public String getTransformFluid() {
         return this.transformFluid;
      }

      public double getDurability() {
         return this.durability;
      }

      @Nonnull
      @Override
      public String toString() {
         return "RefillState{allowedFluids="
            + Arrays.toString((Object[])this.allowedFluids)
            + ", transformFluid='"
            + this.transformFluid
            + "', durability="
            + this.durability
            + "}";
      }
   }
}
