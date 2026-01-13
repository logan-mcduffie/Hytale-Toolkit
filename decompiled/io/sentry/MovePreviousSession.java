package io.sentry;

import io.sentry.cache.EnvelopeCache;
import io.sentry.cache.IEnvelopeCache;
import java.io.File;
import org.jetbrains.annotations.NotNull;

final class MovePreviousSession implements Runnable {
   @NotNull
   private final SentryOptions options;

   MovePreviousSession(@NotNull SentryOptions options) {
      this.options = options;
   }

   @Override
   public void run() {
      String cacheDirPath = this.options.getCacheDirPath();
      if (cacheDirPath == null) {
         this.options.getLogger().log(SentryLevel.INFO, "Cache dir is not set, not moving the previous session.");
      } else if (!this.options.isEnableAutoSessionTracking()) {
         this.options.getLogger().log(SentryLevel.DEBUG, "Session tracking is disabled, bailing from previous session mover.");
      } else {
         IEnvelopeCache cache = this.options.getEnvelopeDiskCache();
         if (cache instanceof EnvelopeCache) {
            File currentSessionFile = EnvelopeCache.getCurrentSessionFile(cacheDirPath);
            File previousSessionFile = EnvelopeCache.getPreviousSessionFile(cacheDirPath);
            ((EnvelopeCache)cache).movePreviousSession(currentSessionFile, previousSessionFile);
            ((EnvelopeCache)cache).flushPreviousSession();
         }
      }
   }
}
