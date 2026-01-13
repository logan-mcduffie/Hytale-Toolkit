package com.google.crypto.tink.internal;

import java.util.concurrent.atomic.AtomicReference;

public final class MutableMonitoringRegistry {
   private static final MutableMonitoringRegistry GLOBAL_INSTANCE = new MutableMonitoringRegistry();
   private static final MutableMonitoringRegistry.DoNothingClient DO_NOTHING_CLIENT = new MutableMonitoringRegistry.DoNothingClient();
   private final AtomicReference<MonitoringClient> monitoringClient = new AtomicReference<>();

   public static MutableMonitoringRegistry globalInstance() {
      return GLOBAL_INSTANCE;
   }

   public synchronized void clear() {
      this.monitoringClient.set(null);
   }

   public synchronized void registerMonitoringClient(MonitoringClient client) {
      if (this.monitoringClient.get() != null) {
         throw new IllegalStateException("a monitoring client has already been registered");
      } else {
         this.monitoringClient.set(client);
      }
   }

   public MonitoringClient getMonitoringClient() {
      MonitoringClient client = this.monitoringClient.get();
      return (MonitoringClient)(client == null ? DO_NOTHING_CLIENT : client);
   }

   private static class DoNothingClient implements MonitoringClient {
      private DoNothingClient() {
      }

      @Override
      public MonitoringClient.Logger createLogger(KeysetHandleInterface keysetInfo, MonitoringAnnotations annotations, String primitive, String api) {
         return MonitoringUtil.DO_NOTHING_LOGGER;
      }
   }
}
