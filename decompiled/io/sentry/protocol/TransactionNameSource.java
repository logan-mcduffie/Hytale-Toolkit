package io.sentry.protocol;

import java.util.Locale;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public enum TransactionNameSource {
   CUSTOM,
   URL,
   ROUTE,
   VIEW,
   COMPONENT,
   TASK;

   public String apiName() {
      return this.name().toLowerCase(Locale.ROOT);
   }
}
