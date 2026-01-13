package io.sentry;

public final class HttpStatusCodeRange {
   public static final int DEFAULT_MIN = 500;
   public static final int DEFAULT_MAX = 599;
   private final int min;
   private final int max;

   public HttpStatusCodeRange(int min, int max) {
      this.min = min;
      this.max = max;
   }

   public HttpStatusCodeRange(int statusCode) {
      this.min = statusCode;
      this.max = statusCode;
   }

   public boolean isInRange(int statusCode) {
      return statusCode >= this.min && statusCode <= this.max;
   }
}
