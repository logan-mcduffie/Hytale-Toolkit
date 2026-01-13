package com.hypixel.hytale.server.core.command.commands.utility.git;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class UpdateCommand extends AbstractCommandCollection {
   public UpdateCommand() {
      super("update", "server.commands.update.desc");
      this.addSubCommand(new UpdateAssetsCommand());
      this.addSubCommand(new UpdatePrefabsCommand());
   }
}
