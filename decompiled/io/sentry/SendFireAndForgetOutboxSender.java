package io.sentry;

import io.sentry.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class SendFireAndForgetOutboxSender implements SendCachedEnvelopeFireAndForgetIntegration.SendFireAndForgetFactory {
   @NotNull
   private final SendCachedEnvelopeFireAndForgetIntegration.SendFireAndForgetDirPath sendFireAndForgetDirPath;

   public SendFireAndForgetOutboxSender(@NotNull SendCachedEnvelopeFireAndForgetIntegration.SendFireAndForgetDirPath sendFireAndForgetDirPath) {
      this.sendFireAndForgetDirPath = Objects.requireNonNull(sendFireAndForgetDirPath, "SendFireAndForgetDirPath is required");
   }

   @Nullable
   @Override
   public SendCachedEnvelopeFireAndForgetIntegration.SendFireAndForget create(@NotNull IScopes scopes, @NotNull SentryOptions options) {
      Objects.requireNonNull(scopes, "Scopes are required");
      Objects.requireNonNull(options, "SentryOptions is required");
      String dirPath = this.sendFireAndForgetDirPath.getDirPath();
      if (dirPath != null && this.hasValidPath(dirPath, options.getLogger())) {
         OutboxSender outboxSender = new OutboxSender(
            scopes, options.getEnvelopeReader(), options.getSerializer(), options.getLogger(), options.getFlushTimeoutMillis(), options.getMaxQueueSize()
         );
         return this.processDir(outboxSender, dirPath, options.getLogger());
      } else {
         options.getLogger().log(SentryLevel.ERROR, "No outbox dir path is defined in options.");
         return null;
      }
   }
}
