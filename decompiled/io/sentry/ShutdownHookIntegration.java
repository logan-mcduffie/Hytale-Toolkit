package io.sentry;

import io.sentry.util.IntegrationUtils;
import io.sentry.util.Objects;
import java.io.Closeable;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.annotations.VisibleForTesting;

public final class ShutdownHookIntegration implements Integration, Closeable {
   @NotNull
   private final Runtime runtime;
   @Nullable
   private Thread thread;

   @TestOnly
   public ShutdownHookIntegration(@NotNull Runtime runtime) {
      this.runtime = Objects.requireNonNull(runtime, "Runtime is required");
   }

   public ShutdownHookIntegration() {
      this(Runtime.getRuntime());
   }

   @Override
   public void register(@NotNull IScopes scopes, @NotNull SentryOptions options) {
      Objects.requireNonNull(scopes, "Scopes are required");
      Objects.requireNonNull(options, "SentryOptions is required");
      if (options.isEnableShutdownHook()) {
         this.thread = new Thread(() -> scopes.flush(options.getFlushTimeoutMillis()), "sentry-shutdownhook");
         this.handleShutdownInProgress(() -> {
            this.runtime.addShutdownHook(this.thread);
            options.getLogger().log(SentryLevel.DEBUG, "ShutdownHookIntegration installed.");
            IntegrationUtils.addIntegrationToSdkVersion("ShutdownHook");
         });
      } else {
         options.getLogger().log(SentryLevel.INFO, "enableShutdownHook is disabled.");
      }
   }

   @Override
   public void close() throws IOException {
      if (this.thread != null) {
         this.handleShutdownInProgress(() -> this.runtime.removeShutdownHook(this.thread));
      }
   }

   private void handleShutdownInProgress(@NotNull Runnable runnable) {
      try {
         runnable.run();
      } catch (IllegalStateException var4) {
         String message = var4.getMessage();
         if (message == null || !message.equals("Shutdown in progress") && !message.equals("VM already shutting down")) {
            throw var4;
         }
      }
   }

   @VisibleForTesting
   @Nullable
   Thread getHook() {
      return this.thread;
   }
}
