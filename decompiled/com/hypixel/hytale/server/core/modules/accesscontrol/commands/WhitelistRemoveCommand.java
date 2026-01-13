package com.hypixel.hytale.server.core.modules.accesscontrol.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.modules.accesscontrol.provider.HytaleWhitelistProvider;
import com.hypixel.hytale.server.core.util.AuthUtil;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public class WhitelistRemoveCommand extends AbstractAsyncCommand {
   @Nonnull
   private final HytaleWhitelistProvider whitelistProvider;
   @Nonnull
   private final RequiredArg<String> usernameArg = this.withRequiredArg("username", "server.commands.whitelist.remove.username.desc", ArgTypes.STRING);

   public WhitelistRemoveCommand(@Nonnull HytaleWhitelistProvider whitelistProvider) {
      super("remove", "server.commands.whitelist.remove.desc");
      this.whitelistProvider = whitelistProvider;
   }

   @Nonnull
   @Override
   protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
      String username = this.usernameArg.get(context);
      return AuthUtil.lookupUuid(username).thenAccept(uuid -> {
         if (this.whitelistProvider.modify(list -> list.remove(uuid))) {
            context.sendMessage(Message.translation("server.modules.whitelist.removalSuccess").param("uuid", uuid.toString()));
         } else {
            context.sendMessage(Message.translation("server.modules.whitelist.uuidNotWhitelisted").param("uuid", uuid.toString()));
         }
      }).exceptionally(ex -> {
         context.sendMessage(Message.translation("server.modules.ban.lookupFailed").param("name", username));
         ex.printStackTrace();
         return null;
      });
   }
}
