package io.sentry;

import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryNanotimeDate extends SentryDate {
   @NotNull
   private final Date date;
   private final long nanos;

   public SentryNanotimeDate() {
      this(DateUtils.getCurrentDateTime(), System.nanoTime());
   }

   public SentryNanotimeDate(@NotNull Date date, long nanos) {
      this.date = date;
      this.nanos = nanos;
   }

   @Override
   public long diff(@NotNull SentryDate otherDate) {
      if (otherDate instanceof SentryNanotimeDate) {
         SentryNanotimeDate otherNanoDate = (SentryNanotimeDate)otherDate;
         return this.nanos - otherNanoDate.nanos;
      } else {
         return super.diff(otherDate);
      }
   }

   @Override
   public long nanoTimestamp() {
      return DateUtils.dateToNanos(this.date);
   }

   @Override
   public long laterDateNanosTimestampByDiff(@Nullable SentryDate otherDate) {
      if (otherDate != null && otherDate instanceof SentryNanotimeDate) {
         SentryNanotimeDate otherNanoDate = (SentryNanotimeDate)otherDate;
         return this.compareTo(otherDate) < 0 ? this.nanotimeDiff(this, otherNanoDate) : this.nanotimeDiff(otherNanoDate, this);
      } else {
         return super.laterDateNanosTimestampByDiff(otherDate);
      }
   }

   @Override
   public int compareTo(@NotNull SentryDate otherDate) {
      if (otherDate instanceof SentryNanotimeDate) {
         SentryNanotimeDate otherNanoDate = (SentryNanotimeDate)otherDate;
         long thisDateMillis = this.date.getTime();
         long otherDateMillis = otherNanoDate.date.getTime();
         return thisDateMillis == otherDateMillis
            ? Long.valueOf(this.nanos).compareTo(otherNanoDate.nanos)
            : Long.valueOf(thisDateMillis).compareTo(otherDateMillis);
      } else {
         return super.compareTo(otherDate);
      }
   }

   private long nanotimeDiff(@NotNull SentryNanotimeDate earlierDate, @NotNull SentryNanotimeDate laterDate) {
      long nanoDiff = laterDate.nanos - earlierDate.nanos;
      return earlierDate.nanoTimestamp() + nanoDiff;
   }
}
