package com.hypixel.hytale.server.core.command.commands.debug.server;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class ServerStatsGcCommand extends CommandBase {
   @Nonnull
   private static final Message MESSAGE_COMMANDS_SERVER_STATS_GC_USAGE_INFO = Message.translation("server.commands.server.stats.gc.usageInfo");

   public ServerStatsGcCommand() {
      super("gc", "server.commands.server.stats.gc.desc");
   }

   @Override
   protected void executeSync(@Nonnull CommandContext context) {
      for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
         context.sendMessage(
            MESSAGE_COMMANDS_SERVER_STATS_GC_USAGE_INFO.param("name", garbageCollectorMXBean.getName())
               .param("poolNames", Arrays.toString((Object[])garbageCollectorMXBean.getMemoryPoolNames()))
               .param("collectionCount", garbageCollectorMXBean.getCollectionCount())
               .param("collectionTime", garbageCollectorMXBean.getCollectionTime())
         );
      }
   }
}
