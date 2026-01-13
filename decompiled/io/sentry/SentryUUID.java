package io.sentry;

import io.sentry.util.UUIDGenerator;
import io.sentry.util.UUIDStringUtils;

public final class SentryUUID {
   private SentryUUID() {
   }

   public static String generateSentryId() {
      return UUIDStringUtils.toSentryIdString(UUIDGenerator.randomUUID());
   }

   public static String generateSpanId() {
      return UUIDStringUtils.toSentrySpanIdString(UUIDGenerator.randomHalfLengthUUID());
   }
}
