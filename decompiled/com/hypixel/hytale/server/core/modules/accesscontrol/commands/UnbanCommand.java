package com.hypixel.hytale.server.core.modules.accesscontrol.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.modules.accesscontrol.provider.HytaleBanProvider;
import com.hypixel.hytale.server.core.util.AuthUtil;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public class UnbanCommand extends AbstractAsyncCommand {
   @Nonnull
   private final HytaleBanProvider banProvider;
   @Nonnull
   private final RequiredArg<String> usernameArg = this.withRequiredArg("username", "server.commands.unban.username.desc", ArgTypes.STRING);

   public UnbanCommand(@Nonnull HytaleBanProvider banProvider) {
      super("unban", "server.commands.unban.desc");
      this.setUnavailableInSingleplayer(true);
      this.banProvider = banProvider;
   }

   @Nonnull
   @Override
   protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
      String username = this.usernameArg.get(context);
      return AuthUtil.lookupUuid(username).thenAccept(uuid -> {
         if (!this.banProvider.hasBan(uuid)) {
            context.sendMessage(Message.translation("server.modules.unban.playerNotBanned").param("name", username));
         } else {
            this.banProvider.modify(map -> map.remove(uuid) != null);
            context.sendMessage(Message.translation("server.modules.unban.success").param("name", username));
         }
      }).exceptionally(ex -> {
         context.sendMessage(Message.translation("server.modules.ban.lookupFailed").param("name", username));
         ex.printStackTrace();
         return null;
      });
   }
}
