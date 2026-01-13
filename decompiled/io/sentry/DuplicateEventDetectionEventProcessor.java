package io.sentry;

import io.sentry.util.Objects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DuplicateEventDetectionEventProcessor implements EventProcessor {
   @NotNull
   private final Map<Throwable, Object> capturedObjects = Collections.synchronizedMap(new WeakHashMap<>());
   @NotNull
   private final SentryOptions options;

   public DuplicateEventDetectionEventProcessor(@NotNull SentryOptions options) {
      this.options = Objects.requireNonNull(options, "options are required");
   }

   @Nullable
   @Override
   public SentryEvent process(@NotNull SentryEvent event, @NotNull Hint hint) {
      if (this.options.isEnableDeduplication()) {
         Throwable throwable = event.getThrowable();
         if (throwable != null) {
            if (this.capturedObjects.containsKey(throwable) || containsAnyKey(this.capturedObjects, allCauses(throwable))) {
               this.options.getLogger().log(SentryLevel.DEBUG, "Duplicate Exception detected. Event %s will be discarded.", event.getEventId());
               return null;
            }

            this.capturedObjects.put(throwable, null);
         }
      } else {
         this.options.getLogger().log(SentryLevel.DEBUG, "Event deduplication is disabled.");
      }

      return event;
   }

   private static <T> boolean containsAnyKey(@NotNull Map<T, Object> map, @NotNull List<T> list) {
      for (T entry : list) {
         if (map.containsKey(entry)) {
            return true;
         }
      }

      return false;
   }

   @NotNull
   private static List<Throwable> allCauses(@NotNull Throwable throwable) {
      List<Throwable> causes = new ArrayList<>();

      for (Throwable ex = throwable; ex.getCause() != null; ex = ex.getCause()) {
         causes.add(ex.getCause());
      }

      return causes;
   }

   @Nullable
   @Override
   public Long getOrder() {
      return 1000L;
   }
}
