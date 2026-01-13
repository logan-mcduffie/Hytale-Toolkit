package io.sentry;

import io.sentry.exception.InvalidSentryTraceHeaderException;
import io.sentry.protocol.SentryId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryTraceHeader {
   public static final String SENTRY_TRACE_HEADER = "sentry-trace";
   @NotNull
   private final SentryId traceId;
   @NotNull
   private final SpanId spanId;
   @Nullable
   private final Boolean sampled;
   private static final Pattern SENTRY_TRACEPARENT_HEADER_REGEX = Pattern.compile("^[ \\t]*([0-9a-f]{32})-([0-9a-f]{16})(-[01])?[ \\t]*$", 2);

   public SentryTraceHeader(@NotNull SentryId traceId, @NotNull SpanId spanId, @Nullable Boolean sampled) {
      this.traceId = traceId;
      this.spanId = spanId;
      this.sampled = sampled;
   }

   public SentryTraceHeader(@NotNull String value) throws InvalidSentryTraceHeaderException {
      Matcher matcher = SENTRY_TRACEPARENT_HEADER_REGEX.matcher(value);
      boolean matchesExist = matcher.matches();
      if (!matchesExist) {
         throw new InvalidSentryTraceHeaderException(value);
      } else {
         this.traceId = new SentryId(matcher.group(1));
         this.spanId = new SpanId(matcher.group(2));
         String sampled = matcher.group(3);
         this.sampled = sampled == null ? null : "1".equals(sampled.substring(1));
      }
   }

   @NotNull
   public String getName() {
      return "sentry-trace";
   }

   @NotNull
   public String getValue() {
      return this.sampled != null
         ? String.format("%s-%s-%s", this.traceId, this.spanId, this.sampled ? "1" : "0")
         : String.format("%s-%s", this.traceId, this.spanId);
   }

   @NotNull
   public SentryId getTraceId() {
      return this.traceId;
   }

   @NotNull
   public SpanId getSpanId() {
      return this.spanId;
   }

   @Nullable
   public Boolean isSampled() {
      return this.sampled;
   }
}
