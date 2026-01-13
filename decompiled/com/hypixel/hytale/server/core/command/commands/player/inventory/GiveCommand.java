package com.hypixel.hytale.server.core.command.commands.player.inventory;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.permissions.HytalePermissions;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import org.bson.BsonDocument;

public class GiveCommand extends AbstractPlayerCommand {
   @Nonnull
   private static final Message MESSAGE_COMMANDS_GIVE_RECEIVED = Message.translation("server.commands.give.received");
   @Nonnull
   private static final Message MESSAGE_COMMANDS_GIVE_INSUFFICIENT_INV_SPACE = Message.translation("server.commands.give.insufficientInvSpace");
   @Nonnull
   private static final Message MESSAGE_COMMANDS_GIVE_INVALID_METADATA = Message.translation("server.commands.give.invalidMetadata");
   @Nonnull
   private final RequiredArg<Item> itemArg = this.withRequiredArg("item", "server.commands.give.item.desc", ArgTypes.ITEM_ASSET);
   @Nonnull
   private final DefaultArg<Integer> quantityArg = this.withDefaultArg("quantity", "server.commands.give.quantity.desc", ArgTypes.INTEGER, 1, "1");
   @Nonnull
   private final OptionalArg<String> metadataArg = this.withOptionalArg("metadata", "server.commands.give.metadata.desc", ArgTypes.STRING);

   public GiveCommand() {
      super("give", "server.commands.give.desc");
      this.requirePermission(HytalePermissions.fromCommand("give.self"));
      this.addUsageVariant(new GiveCommand.GiveOtherCommand());
      this.addSubCommand(new GiveArmorCommand());
   }

   @Override
   protected void execute(
      @Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world
   ) {
      Player playerComponent = store.getComponent(ref, Player.getComponentType());

      assert playerComponent != null;

      Item item = this.itemArg.get(context);
      Integer quantity = this.quantityArg.get(context);
      BsonDocument metadata = null;
      if (this.metadataArg.provided(context)) {
         String metadataStr = this.metadataArg.get(context);

         try {
            metadata = BsonDocument.parse(metadataStr);
         } catch (Exception var13) {
            context.sendMessage(MESSAGE_COMMANDS_GIVE_INVALID_METADATA.param("error", var13.getMessage()));
            return;
         }
      }

      ItemStackTransaction transaction = playerComponent.getInventory().getCombinedHotbarFirst().addItemStack(new ItemStack(item.getId(), quantity, metadata));
      ItemStack remainder = transaction.getRemainder();
      Message itemNameMessage = Message.translation(item.getTranslationKey());
      if (remainder != null && !remainder.isEmpty()) {
         context.sendMessage(MESSAGE_COMMANDS_GIVE_INSUFFICIENT_INV_SPACE.param("quantity", quantity).param("item", itemNameMessage));
      } else {
         context.sendMessage(MESSAGE_COMMANDS_GIVE_RECEIVED.param("quantity", quantity).param("item", itemNameMessage));
      }
   }

   private static class GiveOtherCommand extends CommandBase {
      @Nonnull
      private static final Message MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD = Message.translation("server.commands.errors.playerNotInWorld");
      @Nonnull
      private static final Message MESSAGE_COMMANDS_GIVE_GAVE = Message.translation("server.commands.give.gave");
      @Nonnull
      private static final Message MESSAGE_COMMANDS_GIVE_INSUFFICIENT_INV_SPACE = Message.translation("server.commands.give.insufficientInvSpace");
      @Nonnull
      private static final Message MESSAGE_COMMANDS_GIVE_INVALID_METADATA = Message.translation("server.commands.give.invalidMetadata");
      @Nonnull
      private final RequiredArg<PlayerRef> playerArg = this.withRequiredArg("player", "server.commands.argtype.player.desc", ArgTypes.PLAYER_REF);
      @Nonnull
      private final RequiredArg<Item> itemArg = this.withRequiredArg("item", "server.commands.give.item.desc", ArgTypes.ITEM_ASSET);
      @Nonnull
      private final DefaultArg<Integer> quantityArg = this.withDefaultArg("quantity", "server.commands.give.quantity.desc", ArgTypes.INTEGER, 1, "1");
      @Nonnull
      private final OptionalArg<String> metadataArg = this.withOptionalArg("metadata", "server.commands.give.metadata.desc", ArgTypes.STRING);

      GiveOtherCommand() {
         super("server.commands.give.other.desc");
         this.requirePermission(HytalePermissions.fromCommand("give.other"));
      }

      @Override
      protected void executeSync(@Nonnull CommandContext context) {
         PlayerRef targetPlayerRef = this.playerArg.get(context);
         Ref<EntityStore> ref = targetPlayerRef.getReference();
         if (ref != null && ref.isValid()) {
            Store<EntityStore> store = ref.getStore();
            World world = store.getExternalData().getWorld();
            world.execute(
               () -> {
                  Player playerComponent = store.getComponent(ref, Player.getComponentType());
                  if (playerComponent == null) {
                     context.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
                  } else {
                     PlayerRef playerRefComponent = store.getComponent(ref, PlayerRef.getComponentType());

                     assert playerRefComponent != null;

                     Item item = this.itemArg.get(context);
                     Integer quantity = this.quantityArg.get(context);
                     BsonDocument metadata = null;
                     if (this.metadataArg.provided(context)) {
                        String metadataStr = this.metadataArg.get(context);

                        try {
                           metadata = BsonDocument.parse(metadataStr);
                        } catch (Exception var13) {
                           context.sendMessage(MESSAGE_COMMANDS_GIVE_INVALID_METADATA.param("error", var13.getMessage()));
                           return;
                        }
                     }

                     ItemStackTransaction transaction = playerComponent.getInventory()
                        .getCombinedHotbarFirst()
                        .addItemStack(new ItemStack(item.getId(), quantity, metadata));
                     ItemStack remainder = transaction.getRemainder();
                     Message itemNameMessage = Message.translation(item.getTranslationKey());
                     if (remainder != null && !remainder.isEmpty()) {
                        context.sendMessage(MESSAGE_COMMANDS_GIVE_INSUFFICIENT_INV_SPACE.param("quantity", quantity).param("item", itemNameMessage));
                     } else {
                        context.sendMessage(
                           MESSAGE_COMMANDS_GIVE_GAVE.param("targetUsername", targetPlayerRef.getUsername())
                              .param("quantity", quantity)
                              .param("item", itemNameMessage)
                        );
                     }
                  }
               }
            );
         } else {
            context.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
         }
      }
   }
}
