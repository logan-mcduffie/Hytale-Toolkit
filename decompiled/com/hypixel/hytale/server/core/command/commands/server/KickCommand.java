package com.hypixel.hytale.server.core.command.commands.server;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import javax.annotation.Nonnull;

public class KickCommand extends CommandBase {
   @Nonnull
   private static final Message MESSAGE_COMMANDS_KICK_SUCCESS = Message.translation("server.commands.kick.success");
   @Nonnull
   private final RequiredArg<PlayerRef> playerArg = this.withRequiredArg("player", "server.commands.kick.desc", ArgTypes.PLAYER_REF);

   public KickCommand() {
      super("kick", "server.commands.kick.desc");
   }

   @Override
   protected void executeSync(@Nonnull CommandContext context) {
      PlayerRef playerToKick = this.playerArg.get(context);
      playerToKick.getPacketHandler().disconnect("You were kicked.");
      context.sendMessage(MESSAGE_COMMANDS_KICK_SUCCESS.param("username", playerToKick.getUsername()));
   }
}
