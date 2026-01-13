package com.hypixel.hytale.server.core.modules.accesscontrol.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.modules.accesscontrol.ban.InfiniteBan;
import com.hypixel.hytale.server.core.modules.accesscontrol.provider.HytaleBanProvider;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.util.AuthUtil;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public class BanCommand extends AbstractAsyncCommand {
   @Nonnull
   private final HytaleBanProvider banProvider;
   @Nonnull
   private final RequiredArg<String> usernameArg = this.withRequiredArg("username", "server.commands.ban.username.desc", ArgTypes.STRING);
   @Nonnull
   private final OptionalArg<String> reasonArg = this.withOptionalArg("reason", "server.commands.ban.reason.desc", ArgTypes.STRING);

   public BanCommand(@Nonnull HytaleBanProvider banProvider) {
      super("ban", "server.commands.ban.desc");
      this.setUnavailableInSingleplayer(true);
      this.banProvider = banProvider;
   }

   @Nonnull
   @Override
   protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
      String username = this.usernameArg.get(context);
      String rawInput = context.getInputString();
      int usernameIndex = rawInput.indexOf(username);
      String reason;
      if (usernameIndex != -1 && usernameIndex + username.length() < rawInput.length()) {
         String afterUsername = rawInput.substring(usernameIndex + username.length()).trim();
         reason = afterUsername.isEmpty() ? "No reason." : afterUsername;
      } else {
         reason = "No reason.";
      }

      return AuthUtil.lookupUuid(username).thenCompose(uuid -> {
         if (this.banProvider.hasBan(uuid)) {
            context.sendMessage(Message.translation("server.modules.ban.alreadyBanned").param("name", username));
            return CompletableFuture.completedFuture(null);
         } else {
            InfiniteBan ban = new InfiniteBan(uuid, context.sender().getUuid(), Instant.now(), reason);
            this.banProvider.modify(banMap -> {
               banMap.put(uuid, ban);
               return true;
            });
            PlayerRef player = Universe.get().getPlayer(uuid);
            if (player != null) {
               CompletableFuture<Optional<String>> disconnectReason = ban.getDisconnectReason(uuid);
               return disconnectReason.whenComplete((string, disconnectEx) -> {
                  Optional<String> optional = (Optional<String>)string;
                  if (disconnectEx != null) {
                     context.sendMessage(Message.translation("server.modules.ban.failedDisconnectReason").param("name", username));
                     disconnectEx.printStackTrace();
                  }

                  if (string == null || !string.isPresent()) {
                     optional = Optional.of("Failed to get disconnect reason.");
                  }

                  player.getPacketHandler().disconnect(optional.get());
                  context.sendMessage(Message.translation("server.modules.ban.bannedWithReason").param("name", username).param("reason", reason));
               }).thenApply(v -> null);
            } else {
               context.sendMessage(Message.translation("server.modules.ban.bannedWithReason").param("name", username).param("reason", reason));
               return CompletableFuture.completedFuture(null);
            }
         }
      });
   }
}
