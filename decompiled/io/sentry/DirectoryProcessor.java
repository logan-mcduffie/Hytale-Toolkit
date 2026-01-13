package io.sentry;

import io.sentry.hints.Cached;
import io.sentry.hints.Enqueable;
import io.sentry.hints.Flushable;
import io.sentry.hints.Retryable;
import io.sentry.hints.SubmissionResult;
import io.sentry.transport.RateLimiter;
import io.sentry.util.HintUtils;
import java.io.File;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

abstract class DirectoryProcessor {
   private static final long ENVELOPE_PROCESSING_DELAY = 100L;
   @NotNull
   private final IScopes scopes;
   @NotNull
   private final ILogger logger;
   private final long flushTimeoutMillis;
   private final Queue<String> processedEnvelopes;

   DirectoryProcessor(@NotNull IScopes scopes, @NotNull ILogger logger, long flushTimeoutMillis, int maxQueueSize) {
      this.scopes = scopes;
      this.logger = logger;
      this.flushTimeoutMillis = flushTimeoutMillis;
      this.processedEnvelopes = SynchronizedQueue.synchronizedQueue(new CircularFifoQueue<>(maxQueueSize));
   }

   public void processDirectory(@NotNull File directory) {
      try {
         this.logger.log(SentryLevel.DEBUG, "Processing dir. %s", directory.getAbsolutePath());
         File[] filteredListFiles = directory.listFiles((d, name) -> this.isRelevantFileName(name));
         if (filteredListFiles == null) {
            this.logger.log(SentryLevel.ERROR, "Cache dir %s is null or is not a directory.", directory.getAbsolutePath());
            return;
         }

         this.logger.log(SentryLevel.DEBUG, "Processing %d items from cache dir %s", filteredListFiles.length, directory.getAbsolutePath());

         for (File file : filteredListFiles) {
            if (!file.isFile()) {
               this.logger.log(SentryLevel.DEBUG, "File %s is not a File.", file.getAbsolutePath());
            } else {
               String filePath = file.getAbsolutePath();
               if (this.processedEnvelopes.contains(filePath)) {
                  this.logger.log(SentryLevel.DEBUG, "File '%s' has already been processed so it will not be processed again.", filePath);
               } else {
                  RateLimiter rateLimiter = this.scopes.getRateLimiter();
                  if (rateLimiter != null && rateLimiter.isActiveForCategory(DataCategory.All)) {
                     this.logger.log(SentryLevel.INFO, "DirectoryProcessor, rate limiting active.");
                     return;
                  }

                  this.logger.log(SentryLevel.DEBUG, "Processing file: %s", filePath);
                  DirectoryProcessor.SendCachedEnvelopeHint cachedHint = new DirectoryProcessor.SendCachedEnvelopeHint(
                     this.flushTimeoutMillis, this.logger, filePath, this.processedEnvelopes
                  );
                  Hint hint = HintUtils.createWithTypeCheckHint(cachedHint);
                  this.processFile(file, hint);
                  Thread.sleep(100L);
               }
            }
         }
      } catch (Throwable var11) {
         this.logger.log(SentryLevel.ERROR, var11, "Failed processing '%s'", directory.getAbsolutePath());
      }
   }

   protected abstract void processFile(@NotNull File var1, @NotNull Hint var2);

   protected abstract boolean isRelevantFileName(String var1);

   private static final class SendCachedEnvelopeHint implements Cached, Retryable, SubmissionResult, Flushable, Enqueable {
      boolean retry = false;
      boolean succeeded = false;
      private final CountDownLatch latch;
      private final long flushTimeoutMillis;
      @NotNull
      private final ILogger logger;
      @NotNull
      private final String filePath;
      @NotNull
      private final Queue<String> processedEnvelopes;

      public SendCachedEnvelopeHint(long flushTimeoutMillis, @NotNull ILogger logger, @NotNull String filePath, @NotNull Queue<String> processedEnvelopes) {
         this.flushTimeoutMillis = flushTimeoutMillis;
         this.filePath = filePath;
         this.processedEnvelopes = processedEnvelopes;
         this.latch = new CountDownLatch(1);
         this.logger = logger;
      }

      @Override
      public boolean isRetry() {
         return this.retry;
      }

      @Override
      public void setRetry(boolean retry) {
         this.retry = retry;
      }

      @Override
      public boolean waitFlush() {
         try {
            return this.latch.await(this.flushTimeoutMillis, TimeUnit.MILLISECONDS);
         } catch (InterruptedException var2) {
            Thread.currentThread().interrupt();
            this.logger.log(SentryLevel.ERROR, "Exception while awaiting on lock.", var2);
            return false;
         }
      }

      @Override
      public void setResult(boolean succeeded) {
         this.succeeded = succeeded;
         this.latch.countDown();
      }

      @Override
      public boolean isSuccess() {
         return this.succeeded;
      }

      @Override
      public void markEnqueued() {
         this.processedEnvelopes.add(this.filePath);
      }
   }
}
