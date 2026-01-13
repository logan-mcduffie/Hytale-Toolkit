package com.google.common.flogger;

import com.google.common.flogger.backend.Metadata;
import com.google.common.flogger.util.Checks;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

final class LogSiteStats {
   private static final LogSiteMap<LogSiteStats> map = new LogSiteMap<LogSiteStats>() {
      protected LogSiteStats initialValue() {
         return new LogSiteStats();
      }
   };
   private final AtomicLong invocationCount = new AtomicLong();
   private final AtomicLong lastTimestampNanos = new AtomicLong();
   private final AtomicInteger skippedLogStatements = new AtomicInteger();

   static LogSiteStats.RateLimitPeriod newRateLimitPeriod(int n, TimeUnit unit) {
      return new LogSiteStats.RateLimitPeriod(n, unit);
   }

   static LogSiteStats getStatsForKey(LogSiteKey logSiteKey, Metadata metadata) {
      return map.get(logSiteKey, metadata);
   }

   boolean incrementAndCheckInvocationCount(int rateLimitCount) {
      return this.invocationCount.getAndIncrement() % rateLimitCount == 0L;
   }

   boolean checkLastTimestamp(long timestampNanos, LogSiteStats.RateLimitPeriod period) {
      long lastNanos = this.lastTimestampNanos.get();
      long deadlineNanos = lastNanos + period.toNanos();
      if (deadlineNanos >= 0L && (timestampNanos >= deadlineNanos || lastNanos == 0L) && this.lastTimestampNanos.compareAndSet(lastNanos, timestampNanos)) {
         period.setSkipCount(this.skippedLogStatements.getAndSet(0));
         return true;
      } else {
         this.skippedLogStatements.incrementAndGet();
         return false;
      }
   }

   static final class RateLimitPeriod {
      private final int n;
      private final TimeUnit unit;
      private int skipCount = -1;

      private RateLimitPeriod(int n, TimeUnit unit) {
         if (n <= 0) {
            throw new IllegalArgumentException("time period must be positive: " + n);
         } else {
            this.n = n;
            this.unit = Checks.checkNotNull(unit, "time unit");
         }
      }

      private long toNanos() {
         return this.unit.toNanos(this.n);
      }

      private void setSkipCount(int skipCount) {
         this.skipCount = skipCount;
      }

      @Override
      public String toString() {
         StringBuilder out = new StringBuilder().append(this.n).append(' ').append(this.unit);
         if (this.skipCount > 0) {
            out.append(" [skipped: ").append(this.skipCount).append(']');
         }

         return out.toString();
      }

      @Override
      public int hashCode() {
         return this.n * 37 ^ this.unit.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         if (!(obj instanceof LogSiteStats.RateLimitPeriod)) {
            return false;
         } else {
            LogSiteStats.RateLimitPeriod that = (LogSiteStats.RateLimitPeriod)obj;
            return this.n == that.n && this.unit == that.unit;
         }
      }
   }
}
