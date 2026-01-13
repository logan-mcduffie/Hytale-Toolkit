package io.sentry.transport;

import io.sentry.DataCategory;
import io.sentry.Hint;
import io.sentry.ISentryLifecycleToken;
import io.sentry.SentryEnvelope;
import io.sentry.SentryEnvelopeItem;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.clientreport.DiscardReason;
import io.sentry.hints.DiskFlushNotification;
import io.sentry.hints.Retryable;
import io.sentry.hints.SubmissionResult;
import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.HintUtils;
import io.sentry.util.StringUtils;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RateLimiter implements Closeable {
   private static final int HTTP_RETRY_AFTER_DEFAULT_DELAY_MILLIS = 60000;
   @NotNull
   private final ICurrentDateProvider currentDateProvider;
   @NotNull
   private final SentryOptions options;
   @NotNull
   private final Map<DataCategory, Date> sentryRetryAfterLimit = new ConcurrentHashMap<>();
   @NotNull
   private final List<RateLimiter.IRateLimitObserver> rateLimitObservers = new CopyOnWriteArrayList<>();
   @Nullable
   private Timer timer = null;
   @NotNull
   private final AutoClosableReentrantLock timerLock = new AutoClosableReentrantLock();

   public RateLimiter(@NotNull ICurrentDateProvider currentDateProvider, @NotNull SentryOptions options) {
      this.currentDateProvider = currentDateProvider;
      this.options = options;
   }

   public RateLimiter(@NotNull SentryOptions options) {
      this(CurrentDateProvider.getInstance(), options);
   }

   @Nullable
   public SentryEnvelope filter(@NotNull SentryEnvelope envelope, @NotNull Hint hint) {
      List<SentryEnvelopeItem> dropItems = null;

      for (SentryEnvelopeItem item : envelope.getItems()) {
         if (this.isRetryAfter(item.getHeader().getType().getItemType())) {
            if (dropItems == null) {
               dropItems = new ArrayList<>();
            }

            dropItems.add(item);
            this.options.getClientReportRecorder().recordLostEnvelopeItem(DiscardReason.RATELIMIT_BACKOFF, item);
         }
      }

      if (dropItems != null) {
         this.options.getLogger().log(SentryLevel.WARNING, "%d envelope items will be dropped due rate limiting.", dropItems.size());
         List<SentryEnvelopeItem> toSend = new ArrayList<>();

         for (SentryEnvelopeItem itemx : envelope.getItems()) {
            if (!dropItems.contains(itemx)) {
               toSend.add(itemx);
            }
         }

         if (toSend.isEmpty()) {
            this.options.getLogger().log(SentryLevel.WARNING, "Envelope discarded due all items rate limited.");
            this.markHintWhenSendingFailed(hint, false);
            return null;
         } else {
            return new SentryEnvelope(envelope.getHeader(), toSend);
         }
      } else {
         return envelope;
      }
   }

   public boolean isActiveForCategory(@NotNull DataCategory dataCategory) {
      Date currentDate = new Date(this.currentDateProvider.getCurrentTimeMillis());
      Date dateAllCategories = this.sentryRetryAfterLimit.get(DataCategory.All);
      if (dateAllCategories != null && !currentDate.after(dateAllCategories)) {
         return true;
      } else if (DataCategory.Unknown.equals(dataCategory)) {
         return false;
      } else {
         Date dateCategory = this.sentryRetryAfterLimit.get(dataCategory);
         return dateCategory != null ? !currentDate.after(dateCategory) : false;
      }
   }

   public boolean isAnyRateLimitActive() {
      Date currentDate = new Date(this.currentDateProvider.getCurrentTimeMillis());

      for (DataCategory dataCategory : this.sentryRetryAfterLimit.keySet()) {
         Date dateCategory = this.sentryRetryAfterLimit.get(dataCategory);
         if (dateCategory != null && !currentDate.after(dateCategory)) {
            return true;
         }
      }

      return false;
   }

   private void markHintWhenSendingFailed(@NotNull Hint hint, boolean retry) {
      HintUtils.runIfHasType(hint, SubmissionResult.class, result -> result.setResult(false));
      HintUtils.runIfHasType(hint, Retryable.class, retryable -> retryable.setRetry(retry));
      HintUtils.runIfHasType(hint, DiskFlushNotification.class, diskFlushNotification -> {
         diskFlushNotification.markFlushed();
         this.options.getLogger().log(SentryLevel.DEBUG, "Disk flush envelope fired due to rate limit");
      });
   }

   private boolean isRetryAfter(@NotNull String itemType) {
      for (DataCategory category : this.getCategoryFromItemType(itemType)) {
         if (this.isActiveForCategory(category)) {
            return true;
         }
      }

      return false;
   }

   @NotNull
   private List<DataCategory> getCategoryFromItemType(@NotNull String itemType) {
      switch (itemType) {
         case "event":
            return Collections.singletonList(DataCategory.Error);
         case "session":
            return Collections.singletonList(DataCategory.Session);
         case "attachment":
            return Collections.singletonList(DataCategory.Attachment);
         case "profile":
            return Collections.singletonList(DataCategory.Profile);
         case "profile_chunk":
            return Arrays.asList(DataCategory.ProfileChunkUi, DataCategory.ProfileChunk);
         case "transaction":
            return Collections.singletonList(DataCategory.Transaction);
         case "check_in":
            return Collections.singletonList(DataCategory.Monitor);
         case "replay_video":
            return Collections.singletonList(DataCategory.Replay);
         case "feedback":
            return Collections.singletonList(DataCategory.Feedback);
         case "log":
            return Collections.singletonList(DataCategory.LogItem);
         case "span":
            return Collections.singletonList(DataCategory.Span);
         case "trace_metric":
            return Collections.singletonList(DataCategory.TraceMetric);
         default:
            return Collections.singletonList(DataCategory.Unknown);
      }
   }

   public void updateRetryAfterLimits(@Nullable String sentryRateLimitHeader, @Nullable String retryAfterHeader, int errorCode) {
      if (sentryRateLimitHeader != null) {
         for (String limit : sentryRateLimitHeader.split(",", -1)) {
            limit = limit.replace(" ", "");
            String[] rateLimit = limit.split(":", -1);
            if (rateLimit.length > 0) {
               String retryAfter = rateLimit[0];
               long retryAfterMillis = this.parseRetryAfterOrDefault(retryAfter);
               if (rateLimit.length > 1) {
                  String allCategories = rateLimit[1];
                  Date date = new Date(this.currentDateProvider.getCurrentTimeMillis() + retryAfterMillis);
                  if (allCategories != null && !allCategories.isEmpty()) {
                     String[] categories = allCategories.split(";", -1);

                     for (String catItem : categories) {
                        DataCategory dataCategory = DataCategory.Unknown;

                        try {
                           String catItemCapitalized = StringUtils.camelCase(catItem);
                           if (catItemCapitalized != null) {
                              dataCategory = DataCategory.valueOf(catItemCapitalized);
                           } else {
                              this.options.getLogger().log(SentryLevel.ERROR, "Couldn't capitalize: %s", catItem);
                           }
                        } catch (IllegalArgumentException var21) {
                           this.options.getLogger().log(SentryLevel.INFO, var21, "Unknown category: %s", catItem);
                        }

                        if (!DataCategory.Unknown.equals(dataCategory)) {
                           this.applyRetryAfterOnlyIfLonger(dataCategory, date);
                        }
                     }
                  } else {
                     this.applyRetryAfterOnlyIfLonger(DataCategory.All, date);
                  }
               }
            }
         }
      } else if (errorCode == 429) {
         long retryAfterMillis = this.parseRetryAfterOrDefault(retryAfterHeader);
         Date date = new Date(this.currentDateProvider.getCurrentTimeMillis() + retryAfterMillis);
         this.applyRetryAfterOnlyIfLonger(DataCategory.All, date);
      }
   }

   private void applyRetryAfterOnlyIfLonger(@NotNull DataCategory dataCategory, @NotNull Date date) {
      Date oldDate = this.sentryRetryAfterLimit.get(dataCategory);
      if (oldDate == null || date.after(oldDate)) {
         this.sentryRetryAfterLimit.put(dataCategory, date);
         this.notifyRateLimitObservers();
         ISentryLifecycleToken ignored = this.timerLock.acquire();

         try {
            if (this.timer == null) {
               this.timer = new Timer(true);
            }

            this.timer.schedule(new TimerTask() {
               @Override
               public void run() {
                  RateLimiter.this.notifyRateLimitObservers();
               }
            }, date);
         } catch (Throwable var8) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (ignored != null) {
            ignored.close();
         }
      }
   }

   private long parseRetryAfterOrDefault(@Nullable String retryAfterHeader) {
      long retryAfterMillis = 60000L;
      if (retryAfterHeader != null) {
         try {
            retryAfterMillis = (long)(Double.parseDouble(retryAfterHeader) * 1000.0);
         } catch (NumberFormatException var5) {
         }
      }

      return retryAfterMillis;
   }

   private void notifyRateLimitObservers() {
      for (RateLimiter.IRateLimitObserver observer : this.rateLimitObservers) {
         observer.onRateLimitChanged(this);
      }
   }

   public void addRateLimitObserver(@NotNull RateLimiter.IRateLimitObserver observer) {
      this.rateLimitObservers.add(observer);
   }

   public void removeRateLimitObserver(@NotNull RateLimiter.IRateLimitObserver observer) {
      this.rateLimitObservers.remove(observer);
   }

   @Override
   public void close() throws IOException {
      ISentryLifecycleToken ignored = this.timerLock.acquire();

      try {
         if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
         }
      } catch (Throwable var5) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (ignored != null) {
         ignored.close();
      }

      this.rateLimitObservers.clear();
   }

   public interface IRateLimitObserver {
      void onRateLimitChanged(@NotNull RateLimiter var1);
   }
}
