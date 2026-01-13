package io.sentry.transport;

import io.sentry.DateUtils;
import io.sentry.Hint;
import io.sentry.ILogger;
import io.sentry.RequestDetails;
import io.sentry.SentryDate;
import io.sentry.SentryDateProvider;
import io.sentry.SentryEnvelope;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.UncaughtExceptionHandlerIntegration;
import io.sentry.cache.IEnvelopeCache;
import io.sentry.clientreport.DiscardReason;
import io.sentry.hints.Cached;
import io.sentry.hints.DiskFlushNotification;
import io.sentry.hints.Enqueable;
import io.sentry.hints.Retryable;
import io.sentry.hints.SubmissionResult;
import io.sentry.util.HintUtils;
import io.sentry.util.LogUtils;
import io.sentry.util.Objects;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AsyncHttpTransport implements ITransport {
   @NotNull
   private final QueuedThreadPoolExecutor executor;
   @NotNull
   private final IEnvelopeCache envelopeCache;
   @NotNull
   private final SentryOptions options;
   @NotNull
   private final RateLimiter rateLimiter;
   @NotNull
   private final ITransportGate transportGate;
   @NotNull
   private final HttpConnection connection;
   @Nullable
   private volatile Runnable currentRunnable = null;

   public AsyncHttpTransport(
      @NotNull SentryOptions options, @NotNull RateLimiter rateLimiter, @NotNull ITransportGate transportGate, @NotNull RequestDetails requestDetails
   ) {
      this(
         initExecutor(options.getMaxQueueSize(), options.getEnvelopeDiskCache(), options.getLogger(), options.getDateProvider()),
         options,
         rateLimiter,
         transportGate,
         new HttpConnection(options, requestDetails, rateLimiter)
      );
   }

   public AsyncHttpTransport(
      @NotNull QueuedThreadPoolExecutor executor,
      @NotNull SentryOptions options,
      @NotNull RateLimiter rateLimiter,
      @NotNull ITransportGate transportGate,
      @NotNull HttpConnection httpConnection
   ) {
      this.executor = Objects.requireNonNull(executor, "executor is required");
      this.envelopeCache = Objects.requireNonNull(options.getEnvelopeDiskCache(), "envelopeCache is required");
      this.options = Objects.requireNonNull(options, "options is required");
      this.rateLimiter = Objects.requireNonNull(rateLimiter, "rateLimiter is required");
      this.transportGate = Objects.requireNonNull(transportGate, "transportGate is required");
      this.connection = Objects.requireNonNull(httpConnection, "httpConnection is required");
   }

   @Override
   public void send(@NotNull SentryEnvelope envelope, @NotNull Hint hint) throws IOException {
      IEnvelopeCache currentEnvelopeCache = this.envelopeCache;
      boolean cached = false;
      if (HintUtils.hasType(hint, Cached.class)) {
         currentEnvelopeCache = NoOpEnvelopeCache.getInstance();
         cached = true;
         this.options.getLogger().log(SentryLevel.DEBUG, "Captured Envelope is already cached");
      }

      SentryEnvelope filteredEnvelope = this.rateLimiter.filter(envelope, hint);
      if (filteredEnvelope == null) {
         if (cached) {
            this.envelopeCache.discard(envelope);
         }
      } else {
         SentryEnvelope envelopeThatMayIncludeClientReport;
         if (HintUtils.hasType(hint, UncaughtExceptionHandlerIntegration.UncaughtExceptionHint.class)) {
            envelopeThatMayIncludeClientReport = this.options.getClientReportRecorder().attachReportToEnvelope(filteredEnvelope);
         } else {
            envelopeThatMayIncludeClientReport = filteredEnvelope;
         }

         Future<?> future = this.executor.submit(new AsyncHttpTransport.EnvelopeSender(envelopeThatMayIncludeClientReport, hint, currentEnvelopeCache));
         if (future != null && future.isCancelled()) {
            this.options.getClientReportRecorder().recordLostEnvelope(DiscardReason.QUEUE_OVERFLOW, envelopeThatMayIncludeClientReport);
         } else {
            HintUtils.runIfHasType(hint, Enqueable.class, enqueable -> {
               enqueable.markEnqueued();
               this.options.getLogger().log(SentryLevel.DEBUG, "Envelope enqueued");
            });
         }
      }
   }

   @Override
   public void flush(long timeoutMillis) {
      this.executor.waitTillIdle(timeoutMillis);
   }

   private static QueuedThreadPoolExecutor initExecutor(
      int maxQueueSize, @NotNull IEnvelopeCache envelopeCache, @NotNull ILogger logger, @NotNull SentryDateProvider dateProvider
   ) {
      RejectedExecutionHandler storeEvents = (r, executor) -> {
         if (r instanceof AsyncHttpTransport.EnvelopeSender) {
            AsyncHttpTransport.EnvelopeSender envelopeSender = (AsyncHttpTransport.EnvelopeSender)r;
            if (!HintUtils.hasType(envelopeSender.hint, Cached.class)) {
               envelopeCache.storeEnvelope(envelopeSender.envelope, envelopeSender.hint);
            }

            markHintWhenSendingFailed(envelopeSender.hint, true);
            logger.log(SentryLevel.WARNING, "Envelope rejected");
         }
      };
      return new QueuedThreadPoolExecutor(1, maxQueueSize, new AsyncHttpTransport.AsyncConnectionThreadFactory(), storeEvents, logger, dateProvider);
   }

   @NotNull
   @Override
   public RateLimiter getRateLimiter() {
      return this.rateLimiter;
   }

   @Override
   public boolean isHealthy() {
      boolean anyRateLimitActive = this.rateLimiter.isAnyRateLimitActive();
      boolean didRejectRecently = this.executor.didRejectRecently();
      return !anyRateLimitActive && !didRejectRecently;
   }

   @Override
   public void close() throws IOException {
      this.close(false);
   }

   @Override
   public void close(boolean isRestarting) throws IOException {
      this.rateLimiter.close();
      this.executor.shutdown();
      this.options.getLogger().log(SentryLevel.DEBUG, "Shutting down");

      try {
         if (!isRestarting) {
            long timeout = this.options.getFlushTimeoutMillis();
            if (!this.executor.awaitTermination(timeout, TimeUnit.MILLISECONDS)) {
               this.options
                  .getLogger()
                  .log(SentryLevel.WARNING, "Failed to shutdown the async connection async sender  within " + timeout + " ms. Trying to force it now.");
               this.executor.shutdownNow();
               if (this.currentRunnable != null) {
                  this.executor.getRejectedExecutionHandler().rejectedExecution(this.currentRunnable, this.executor);
               }
            }
         }
      } catch (InterruptedException var4) {
         this.options.getLogger().log(SentryLevel.DEBUG, "Thread interrupted while closing the connection.");
         Thread.currentThread().interrupt();
      }
   }

   private static void markHintWhenSendingFailed(@NotNull Hint hint, boolean retry) {
      HintUtils.runIfHasType(hint, SubmissionResult.class, result -> result.setResult(false));
      HintUtils.runIfHasType(hint, Retryable.class, retryable -> retryable.setRetry(retry));
   }

   private static final class AsyncConnectionThreadFactory implements ThreadFactory {
      private int cnt;

      private AsyncConnectionThreadFactory() {
      }

      @NotNull
      @Override
      public Thread newThread(@NotNull Runnable r) {
         Thread ret = new Thread(r, "SentryAsyncConnection-" + this.cnt++);
         ret.setDaemon(true);
         return ret;
      }
   }

   private final class EnvelopeSender implements Runnable {
      @NotNull
      private final SentryEnvelope envelope;
      @NotNull
      private final Hint hint;
      @NotNull
      private final IEnvelopeCache envelopeCache;
      private final TransportResult failedResult = TransportResult.error();

      EnvelopeSender(@NotNull SentryEnvelope envelope, @NotNull Hint hint, @NotNull IEnvelopeCache envelopeCache) {
         this.envelope = Objects.requireNonNull(envelope, "Envelope is required.");
         this.hint = hint;
         this.envelopeCache = Objects.requireNonNull(envelopeCache, "EnvelopeCache is required.");
      }

      @Override
      public void run() {
         AsyncHttpTransport.this.currentRunnable = this;
         TransportResult result = this.failedResult;

         try {
            result = this.flush();
            AsyncHttpTransport.this.options.getLogger().log(SentryLevel.DEBUG, "Envelope flushed");
         } catch (Throwable var7) {
            AsyncHttpTransport.this.options.getLogger().log(SentryLevel.ERROR, var7, "Envelope submission failed");
            throw var7;
         } finally {
            TransportResult finalResult = result;
            HintUtils.runIfHasType(this.hint, SubmissionResult.class, submissionResult -> {
               AsyncHttpTransport.this.options.getLogger().log(SentryLevel.DEBUG, "Marking envelope submission result: %s", finalResult.isSuccess());
               submissionResult.setResult(finalResult.isSuccess());
            });
            AsyncHttpTransport.this.currentRunnable = null;
         }
      }

      @NotNull
      private TransportResult flush() {
         TransportResult result = this.failedResult;
         this.envelope.getHeader().setSentAt(null);
         boolean cached = this.envelopeCache.storeEnvelope(this.envelope, this.hint);
         HintUtils.runIfHasType(this.hint, DiskFlushNotification.class, diskFlushNotification -> {
            if (diskFlushNotification.isFlushable(this.envelope.getHeader().getEventId())) {
               diskFlushNotification.markFlushed();
               AsyncHttpTransport.this.options.getLogger().log(SentryLevel.DEBUG, "Disk flush envelope fired");
            } else {
               AsyncHttpTransport.this.options.getLogger().log(SentryLevel.DEBUG, "Not firing envelope flush as there's an ongoing transaction");
            }
         });
         if (AsyncHttpTransport.this.transportGate.isConnected()) {
            SentryEnvelope envelopeWithClientReport = AsyncHttpTransport.this.options.getClientReportRecorder().attachReportToEnvelope(this.envelope);

            try {
               SentryDate now = AsyncHttpTransport.this.options.getDateProvider().now();
               envelopeWithClientReport.getHeader().setSentAt(DateUtils.nanosToDate(now.nanoTimestamp()));
               result = AsyncHttpTransport.this.connection.send(envelopeWithClientReport);
               if (!result.isSuccess()) {
                  String message = "The transport failed to send the envelope with response code " + result.getResponseCode();
                  AsyncHttpTransport.this.options.getLogger().log(SentryLevel.ERROR, message);
                  if (result.getResponseCode() >= 400 && result.getResponseCode() != 429 && !cached) {
                     HintUtils.runIfDoesNotHaveType(
                        this.hint,
                        Retryable.class,
                        hint -> AsyncHttpTransport.this.options
                           .getClientReportRecorder()
                           .recordLostEnvelope(DiscardReason.NETWORK_ERROR, envelopeWithClientReport)
                     );
                  }

                  throw new IllegalStateException(message);
               }

               this.envelopeCache.discard(this.envelope);
            } catch (IOException var6) {
               HintUtils.runIfHasType(this.hint, Retryable.class, retryable -> retryable.setRetry(true), (hint, clazz) -> {
                  if (!cached) {
                     LogUtils.logNotInstanceOf(clazz, hint, AsyncHttpTransport.this.options.getLogger());
                     AsyncHttpTransport.this.options.getClientReportRecorder().recordLostEnvelope(DiscardReason.NETWORK_ERROR, envelopeWithClientReport);
                  }
               });
               throw new IllegalStateException("Sending the event failed.", var6);
            }
         } else {
            HintUtils.runIfHasType(this.hint, Retryable.class, retryable -> retryable.setRetry(true), (hint, clazz) -> {
               if (!cached) {
                  LogUtils.logNotInstanceOf(clazz, hint, AsyncHttpTransport.this.options.getLogger());
                  AsyncHttpTransport.this.options.getClientReportRecorder().recordLostEnvelope(DiscardReason.NETWORK_ERROR, this.envelope);
               }
            });
         }

         return result;
      }
   }
}
