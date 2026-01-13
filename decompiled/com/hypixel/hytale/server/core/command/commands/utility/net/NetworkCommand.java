package com.hypixel.hytale.server.core.command.commands.utility.net;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetPlayerCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.knockback.KnockbackSystems;
import com.hypixel.hytale.server.core.io.netty.LatencySimulationHandler;
import com.hypixel.hytale.server.core.modules.entity.player.KnockbackPredictionSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.netty.channel.Channel;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NetworkCommand extends AbstractCommandCollection {
   public NetworkCommand() {
      super("network", "server.commands.network.desc");
      this.addAliases("net");
      this.addSubCommand(new NetworkCommand.LatencySimulationCommand());
      this.addSubCommand(new NetworkCommand.ServerKnockbackCommand());
      this.addSubCommand(new NetworkCommand.DebugKnockbackCommand());
   }

   static class DebugKnockbackCommand extends CommandBase {
      DebugKnockbackCommand() {
         super("debugknockback", "server.commands.network.debugknockback.desc");
      }

      @Override
      protected void executeSync(@Nonnull CommandContext context) {
         KnockbackPredictionSystems.DEBUG_KNOCKBACK_POSITION = !KnockbackPredictionSystems.DEBUG_KNOCKBACK_POSITION;
         context.sendMessage(
            Message.translation("server.commands.network.knockbackDebugEnabled").param("enabled", KnockbackPredictionSystems.DEBUG_KNOCKBACK_POSITION)
         );
      }
   }

   public static class LatencySimulationCommand extends AbstractCommandCollection {
      public LatencySimulationCommand() {
         super("latencysimulation", "server.commands.latencySimulation.desc");
         this.addAliases("latsim");
         this.addSubCommand(new NetworkCommand.LatencySimulationCommand.Set());
         this.addSubCommand(new NetworkCommand.LatencySimulationCommand.Reset());
      }

      static class Reset extends AbstractTargetPlayerCommand {
         @Nonnull
         private static final Message MESSAGE_COMMANDS_LATENCY_SIMULATION_RESET_SUCCESS = Message.translation("server.commands.latencySimulation.reset.success");

         Reset() {
            super("reset", "server.commands.latencySimulation.reset.desc");
            this.addAliases("clear");
         }

         @Override
         protected void execute(
            @Nonnull CommandContext context,
            @Nullable Ref<EntityStore> sourceRef,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world,
            @Nonnull Store<EntityStore> store
         ) {
            Channel channel = playerRef.getPacketHandler().getChannel();
            LatencySimulationHandler.setLatency(channel, 0L, TimeUnit.MILLISECONDS);
            context.sendMessage(MESSAGE_COMMANDS_LATENCY_SIMULATION_RESET_SUCCESS);
         }
      }

      static class Set extends AbstractTargetPlayerCommand {
         @Nonnull
         private final RequiredArg<Integer> delayArg = this.withRequiredArg("delay", "server.commands.latencySimulation.set.delay.desc", ArgTypes.INTEGER);

         Set() {
            super("set", "server.commands.latencySimulation.set.desc");
         }

         @Override
         protected void execute(
            @Nonnull CommandContext context,
            @Nullable Ref<EntityStore> sourceRef,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world,
            @Nonnull Store<EntityStore> store
         ) {
            int delay = this.delayArg.get(context);
            Channel channel = playerRef.getPacketHandler().getChannel();
            LatencySimulationHandler.setLatency(channel, delay, TimeUnit.MILLISECONDS);
            context.sendMessage(Message.translation("server.commands.latencySimulation.set.success").param("millis", delay));
         }
      }
   }

   static class ServerKnockbackCommand extends CommandBase {
      ServerKnockbackCommand() {
         super("serverknockback", "server.commands.network.serverknockback.desc");
      }

      @Override
      protected void executeSync(@Nonnull CommandContext context) {
         KnockbackSystems.ApplyPlayerKnockback.DO_SERVER_PREDICTION = !KnockbackSystems.ApplyPlayerKnockback.DO_SERVER_PREDICTION;
         context.sendMessage(
            Message.translation("server.commands.network.knockbackServerPredictionEnabled")
               .param("enabled", KnockbackSystems.ApplyPlayerKnockback.DO_SERVER_PREDICTION)
         );
      }
   }
}
