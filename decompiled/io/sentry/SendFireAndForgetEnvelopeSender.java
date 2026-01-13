package io.sentry;

import io.sentry.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class SendFireAndForgetEnvelopeSender implements SendCachedEnvelopeFireAndForgetIntegration.SendFireAndForgetFactory {
   @NotNull
   private final SendCachedEnvelopeFireAndForgetIntegration.SendFireAndForgetDirPath sendFireAndForgetDirPath;

   public SendFireAndForgetEnvelopeSender(@NotNull SendCachedEnvelopeFireAndForgetIntegration.SendFireAndForgetDirPath sendFireAndForgetDirPath) {
      this.sendFireAndForgetDirPath = Objects.requireNonNull(sendFireAndForgetDirPath, "SendFireAndForgetDirPath is required");
   }

   @Nullable
   @Override
   public SendCachedEnvelopeFireAndForgetIntegration.SendFireAndForget create(@NotNull IScopes scopes, @NotNull SentryOptions options) {
      Objects.requireNonNull(scopes, "Scopes are required");
      Objects.requireNonNull(options, "SentryOptions is required");
      String dirPath = this.sendFireAndForgetDirPath.getDirPath();
      if (dirPath != null && this.hasValidPath(dirPath, options.getLogger())) {
         EnvelopeSender envelopeSender = new EnvelopeSender(
            scopes, options.getSerializer(), options.getLogger(), options.getFlushTimeoutMillis(), options.getMaxQueueSize()
         );
         return this.processDir(envelopeSender, dirPath, options.getLogger());
      } else {
         options.getLogger().log(SentryLevel.ERROR, "No cache dir path is defined in options.");
         return null;
      }
   }
}
