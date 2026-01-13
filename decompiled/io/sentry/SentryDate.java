package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SentryDate implements Comparable<SentryDate> {
   public abstract long nanoTimestamp();

   public long laterDateNanosTimestampByDiff(@Nullable SentryDate otherDate) {
      return otherDate != null && this.compareTo(otherDate) < 0 ? otherDate.nanoTimestamp() : this.nanoTimestamp();
   }

   public long diff(@NotNull SentryDate otherDate) {
      return this.nanoTimestamp() - otherDate.nanoTimestamp();
   }

   public final boolean isBefore(@NotNull SentryDate otherDate) {
      return this.diff(otherDate) < 0L;
   }

   public final boolean isAfter(@NotNull SentryDate otherDate) {
      return this.diff(otherDate) > 0L;
   }

   public int compareTo(@NotNull SentryDate otherDate) {
      return Long.valueOf(this.nanoTimestamp()).compareTo(otherDate.nanoTimestamp());
   }
}
