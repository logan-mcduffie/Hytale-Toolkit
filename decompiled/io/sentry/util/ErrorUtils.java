package io.sentry.util;

import io.sentry.FilterString;
import io.sentry.SentryEvent;
import io.sentry.protocol.Message;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class ErrorUtils {
   @Internal
   public static boolean isIgnored(@Nullable List<FilterString> ignoredErrors, @NotNull SentryEvent event) {
      if (event != null && ignoredErrors != null && !ignoredErrors.isEmpty()) {
         Set<String> possibleMessages = new HashSet<>();
         Message eventMessage = event.getMessage();
         if (eventMessage != null) {
            String stringMessage = eventMessage.getMessage();
            if (stringMessage != null) {
               possibleMessages.add(stringMessage);
            }

            String formattedMessage = eventMessage.getFormatted();
            if (formattedMessage != null) {
               possibleMessages.add(formattedMessage);
            }
         }

         Throwable throwable = event.getThrowable();
         if (throwable != null) {
            possibleMessages.add(throwable.toString());
         }

         for (FilterString filter : ignoredErrors) {
            if (possibleMessages.contains(filter.getFilterString())) {
               return true;
            }
         }

         for (FilterString filterx : ignoredErrors) {
            for (String message : possibleMessages) {
               if (filterx.matches(message)) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }
}
