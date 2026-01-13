package com.hypixel.hytale.server.core.permissions.commands.op;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.permissions.HytalePermissions;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;

public class OpAddCommand extends CommandBase {
   @Nonnull
   private static final Message MESSAGE_COMMANDS_OP_ADDED = Message.translation("server.commands.op.added");
   @Nonnull
   private static final Message MESSAGE_COMMANDS_OP_ADDED_TARGET = Message.translation("server.commands.op.added.target");
   @Nonnull
   private static final Message MESSAGE_COMMANDS_OP_ALREADY = Message.translation("server.commands.op.already");
   @Nonnull
   private final RequiredArg<UUID> playerArg = this.withRequiredArg("player", "server.commands.op.add.player.desc", ArgTypes.PLAYER_UUID);

   public OpAddCommand() {
      super("add", "server.commands.op.add.desc");
      this.requirePermission(HytalePermissions.fromCommand("op.add"));
   }

   @Override
   protected void executeSync(@Nonnull CommandContext context) {
      UUID uuid = this.playerArg.get(context);
      PermissionsModule permissionsModule = PermissionsModule.get();
      String opGroup = "OP";
      String rawInput = context.getInput(this.playerArg)[0];
      Message displayMessage = Message.raw(rawInput).bold(true);
      Set<String> groups = permissionsModule.getGroupsForUser(uuid);
      if (groups.contains("OP")) {
         context.sendMessage(MESSAGE_COMMANDS_OP_ALREADY.param("username", displayMessage));
      } else {
         permissionsModule.addUserToGroup(uuid, "OP");
         context.sendMessage(MESSAGE_COMMANDS_OP_ADDED.param("username", displayMessage));
         PlayerRef oppedPlayerRef = Universe.get().getPlayer(uuid);
         if (oppedPlayerRef != null) {
            oppedPlayerRef.sendMessage(MESSAGE_COMMANDS_OP_ADDED_TARGET);
         }
      }
   }
}
