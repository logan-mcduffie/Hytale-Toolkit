package io.sentry;

import io.sentry.protocol.SentryRuntime;
import io.sentry.protocol.SentryTransaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class SentryRuntimeEventProcessor implements EventProcessor {
   @Nullable
   private final String javaVersion;
   @Nullable
   private final String javaVendor;

   public SentryRuntimeEventProcessor(@Nullable String javaVersion, @Nullable String javaVendor) {
      this.javaVersion = javaVersion;
      this.javaVendor = javaVendor;
   }

   public SentryRuntimeEventProcessor() {
      this(System.getProperty("java.version"), System.getProperty("java.vendor"));
   }

   @NotNull
   @Override
   public SentryEvent process(@NotNull SentryEvent event, @Nullable Hint hint) {
      return this.process(event);
   }

   @NotNull
   @Override
   public SentryTransaction process(@NotNull SentryTransaction transaction, @Nullable Hint hint) {
      return this.process(transaction);
   }

   @NotNull
   private <T extends SentryBaseEvent> T process(@NotNull T event) {
      if (event.getContexts().getRuntime() == null) {
         event.getContexts().setRuntime(new SentryRuntime());
      }

      SentryRuntime runtime = event.getContexts().getRuntime();
      if (runtime != null && runtime.getName() == null && runtime.getVersion() == null) {
         runtime.setName(this.javaVendor);
         runtime.setVersion(this.javaVersion);
      }

      return event;
   }

   @Nullable
   @Override
   public Long getOrder() {
      return 2000L;
   }
}
