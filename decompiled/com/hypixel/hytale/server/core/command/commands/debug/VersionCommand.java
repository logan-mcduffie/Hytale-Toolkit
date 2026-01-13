package com.hypixel.hytale.server.core.command.commands.debug;

import com.hypixel.hytale.common.util.java.ManifestUtil;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import javax.annotation.Nonnull;

public class VersionCommand extends CommandBase {
   private static final Message MESSAGE_RESPONSE = Message.translation("server.commands.version.response");
   private static final Message MESSAGE_RESPONSE_WITH_ENV = Message.translation("server.commands.version.response.withEnvironment");

   public VersionCommand() {
      super("version", "Displays version information about the currently running server");
   }

   @Override
   protected void executeSync(@Nonnull CommandContext context) {
      String version = ManifestUtil.getImplementationVersion();
      String patchline = ManifestUtil.getPatchline();
      if ("release".equals(patchline)) {
         context.sendMessage(MESSAGE_RESPONSE.param("version", version).param("patchline", patchline));
      } else {
         context.sendMessage(MESSAGE_RESPONSE_WITH_ENV.param("version", version).param("patchline", patchline).param("environment", "release"));
      }
   }
}
