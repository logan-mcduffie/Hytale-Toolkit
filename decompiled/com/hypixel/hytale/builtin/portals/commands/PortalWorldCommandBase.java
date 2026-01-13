package com.hypixel.hytale.builtin.portals.commands;

import com.hypixel.hytale.builtin.portals.resources.PortalWorld;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public abstract class PortalWorldCommandBase extends AbstractWorldCommand {
   public PortalWorldCommandBase(String name, String description) {
      super(name, description);
   }

   @Override
   protected final void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
      PortalWorld portalWorld = store.getResource(PortalWorld.getResourceType());
      if (!portalWorld.exists()) {
         context.sendMessage(Message.translation("server.commands.portals.notInPortal"));
      } else {
         this.execute(context, world, portalWorld, store);
      }
   }

   protected abstract void execute(@Nonnull CommandContext var1, @Nonnull World var2, @Nonnull PortalWorld var3, @Nonnull Store<EntityStore> var4);
}
