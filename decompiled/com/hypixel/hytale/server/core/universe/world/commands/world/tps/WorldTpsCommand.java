package com.hypixel.hytale.server.core.universe.world.commands.world.tps;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class WorldTpsCommand extends AbstractWorldCommand {
   @Nonnull
   private static final Message MESSAGE_COMMANDS_WORLD_TPS_SET_SUCCESS = Message.translation("server.commands.world.tps.set.success");
   @Nonnull
   private static final Message MESSAGE_COMMANDS_WORLD_TPS_SET_INVALID = Message.translation("server.commands.world.tps.set.invalid");
   @Nonnull
   private final RequiredArg<Integer> tickRateArg = this.withRequiredArg("rate", "server.commands.world.tps.rate.desc", ArgTypes.TICK_RATE);

   public WorldTpsCommand() {
      super("tps", "server.commands.world.tps.desc");
      this.addAliases("tickrate");
      this.addSubCommand(new WorldTpsResetCommand());
   }

   @Override
   protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
      int newTickRate = this.tickRateArg.get(context);
      if (newTickRate > 0 && newTickRate <= 2048) {
         world.setTps(newTickRate);
         double newMs = 1000.0 / newTickRate;
         context.sendMessage(
            MESSAGE_COMMANDS_WORLD_TPS_SET_SUCCESS.param("worldName", world.getName()).param("tps", newTickRate).param("ms", String.format("%.2f", newMs))
         );
      } else {
         context.sendMessage(MESSAGE_COMMANDS_WORLD_TPS_SET_INVALID.param("value", newTickRate));
      }
   }
}
