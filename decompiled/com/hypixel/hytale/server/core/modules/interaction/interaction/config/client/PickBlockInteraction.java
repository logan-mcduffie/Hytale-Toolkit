package com.hypixel.hytale.server.core.modules.interaction.interaction.config.client;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.Interaction;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class PickBlockInteraction extends SimpleBlockInteraction {
   @Nonnull
   public static final BuilderCodec<PickBlockInteraction> CODEC = BuilderCodec.builder(
         PickBlockInteraction.class, PickBlockInteraction::new, SimpleBlockInteraction.CODEC
      )
      .documentation("Performs a 'block pick', moving a the target block to the user's hand if they have it in their inventory or are in creative.")
      .build();

   @Nonnull
   @Override
   public WaitForDataFrom getWaitForDataFrom() {
      return WaitForDataFrom.Client;
   }

   @Override
   protected void interactWithBlock(
      @NonNullDecl World world,
      @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
      @NonNullDecl InteractionType type,
      @NonNullDecl InteractionContext context,
      @NullableDecl ItemStack itemInHand,
      @NonNullDecl Vector3i targetBlock,
      @NonNullDecl CooldownHandler cooldownHandler
   ) {
   }

   @Override
   protected void simulateInteractWithBlock(
      @NonNullDecl InteractionType type,
      @NonNullDecl InteractionContext context,
      @NullableDecl ItemStack itemInHand,
      @NonNullDecl World world,
      @NonNullDecl Vector3i targetBlock
   ) {
   }

   @Nonnull
   @Override
   protected Interaction generatePacket() {
      return new com.hypixel.hytale.protocol.PickBlockInteraction();
   }

   @Override
   public boolean needsRemoteSync() {
      return true;
   }

   @Nonnull
   @Override
   public String toString() {
      return "PickBlockInteraction{} " + super.toString();
   }
}
