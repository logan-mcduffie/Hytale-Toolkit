package com.hypixel.hytale.server.core.modules.singleplayer.commands;

import com.hypixel.hytale.protocol.packets.serveraccess.Access;
import com.hypixel.hytale.server.core.Constants;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.modules.singleplayer.SingleplayerModule;
import com.hypixel.hytale.server.core.util.message.MessageFormat;
import javax.annotation.Nonnull;

public abstract class PlayCommandBase extends CommandBase {
   @Nonnull
   private final SingleplayerModule singleplayerModule;
   @Nonnull
   private final Access commandAccess;
   @Nonnull
   private final OptionalArg<Boolean> enabledArg = this.withOptionalArg("enabled", "server.commands.play.enabled.desc", ArgTypes.BOOLEAN);

   protected PlayCommandBase(@Nonnull String name, @Nonnull String description, @Nonnull SingleplayerModule singleplayerModule, @Nonnull Access commandAccess) {
      super(name, description);
      this.singleplayerModule = singleplayerModule;
      this.commandAccess = commandAccess;
   }

   @Override
   protected void executeSync(@Nonnull CommandContext context) {
      if (!Constants.SINGLEPLAYER) {
         context.sendMessage(Message.translation("server.commands.play.singleplayerOnly").param("commandAccess", this.commandAccess.toString()));
      } else {
         Access access = SingleplayerModule.get().getAccess();
         if (!this.enabledArg.provided(context)) {
            if (access == this.commandAccess) {
               this.singleplayerModule.requestServerAccess(Access.Private);
               context.sendMessage(Message.translation("server.commands.play.accessDisabled").param("commandAccess", this.commandAccess.toString()));
            } else {
               this.singleplayerModule.requestServerAccess(this.commandAccess);
               context.sendMessage(Message.translation("server.commands.play.accessEnabled").param("commandAccess", this.commandAccess.toString()));
            }
         } else {
            boolean enabled = this.enabledArg.get(context);
            if (!enabled && access == this.commandAccess) {
               this.singleplayerModule.requestServerAccess(Access.Private);
               context.sendMessage(Message.translation("server.commands.play.accessDisabled").param("commandAccess", this.commandAccess.toString()));
            } else if (enabled && access != this.commandAccess) {
               this.singleplayerModule.requestServerAccess(this.commandAccess);
               context.sendMessage(Message.translation("server.commands.play.accessEnabled").param("commandAccess", this.commandAccess.toString()));
            } else {
               context.sendMessage(
                  Message.translation("server.commands.play.accessAlreadyToggled")
                     .param("commandAccess", this.commandAccess.toString())
                     .param("enabled", MessageFormat.enabled(enabled))
               );
            }
         }
      }
   }
}
