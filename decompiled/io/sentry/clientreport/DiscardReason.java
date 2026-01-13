package io.sentry.clientreport;

public enum DiscardReason {
   QUEUE_OVERFLOW("queue_overflow"),
   CACHE_OVERFLOW("cache_overflow"),
   RATELIMIT_BACKOFF("ratelimit_backoff"),
   NETWORK_ERROR("network_error"),
   SAMPLE_RATE("sample_rate"),
   BEFORE_SEND("before_send"),
   EVENT_PROCESSOR("event_processor"),
   BACKPRESSURE("backpressure");

   private final String reason;

   private DiscardReason(String reason) {
      this.reason = reason;
   }

   public String getReason() {
      return this.reason;
   }
}
