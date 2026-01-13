package io.sentry;

import io.sentry.hints.EventDropReason;
import io.sentry.protocol.SentryException;
import io.sentry.util.HintUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DeduplicateMultithreadedEventProcessor implements EventProcessor {
   @NotNull
   private final Map<String, Long> processedEvents = Collections.synchronizedMap(new HashMap<>());
   @NotNull
   private final SentryOptions options;

   public DeduplicateMultithreadedEventProcessor(@NotNull SentryOptions options) {
      this.options = options;
   }

   @Nullable
   @Override
   public SentryEvent process(@NotNull SentryEvent event, @NotNull Hint hint) {
      if (!HintUtils.hasType(hint, UncaughtExceptionHandlerIntegration.UncaughtExceptionHint.class)) {
         return event;
      } else {
         SentryException exception = event.getUnhandledException();
         if (exception == null) {
            return event;
         } else {
            String type = exception.getType();
            if (type == null) {
               return event;
            } else {
               Long currentEventTid = exception.getThreadId();
               if (currentEventTid == null) {
                  return event;
               } else {
                  Long tid = this.processedEvents.get(type);
                  if (tid != null && !tid.equals(currentEventTid)) {
                     this.options.getLogger().log(SentryLevel.INFO, "Event %s has been dropped due to multi-threaded deduplication", event.getEventId());
                     HintUtils.setEventDropReason(hint, EventDropReason.MULTITHREADED_DEDUPLICATION);
                     return null;
                  } else {
                     this.processedEvents.put(type, currentEventTid);
                     return event;
                  }
               }
            }
         }
      }
   }

   @Nullable
   @Override
   public Long getOrder() {
      return 7000L;
   }
}
