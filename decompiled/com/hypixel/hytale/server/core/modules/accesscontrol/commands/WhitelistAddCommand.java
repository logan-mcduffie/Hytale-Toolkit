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

public class WhitelistAddCommand extends AbstractAsyncCommand {
   @Nonnull
   private final HytaleWhitelistProvider whitelistProvider;
   @Nonnull
   private final RequiredArg<String> usernameArg = this.withRequiredArg("username", "server.commands.whitelist.add.username.desc", ArgTypes.STRING);

   public WhitelistAddCommand(@Nonnull HytaleWhitelistProvider whitelistProvider) {
      super("add", "server.commands.whitelist.add.desc");
      this.whitelistProvider = whitelistProvider;
   }

   @Nonnull
   @Override
   protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
      String username = this.usernameArg.get(context);
      return AuthUtil.lookupUuid(username).thenAccept(uuid -> {
         if (this.whitelistProvider.modify(list -> list.add(uuid))) {
            context.sendMessage(Message.translation("server.modules.whitelist.addSuccess").param("name", username));
         } else {
            context.sendMessage(Message.translation("server.modules.whitelist.alreadyWhitelisted").param("name", username));
         }
      }).exceptionally(ex -> {
         context.sendMessage(Message.translation("server.modules.ban.lookupFailed").param("name", username));
         ex.printStackTrace();
         return null;
      });
   }
}
