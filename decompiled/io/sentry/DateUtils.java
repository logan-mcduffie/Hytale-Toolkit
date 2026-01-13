package io.sentry;

import io.sentry.vendor.gson.internal.bind.util.ISO8601Utils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class DateUtils {
   private DateUtils() {
   }

   @NotNull
   public static Date getCurrentDateTime() {
      Calendar calendar = Calendar.getInstance(ISO8601Utils.TIMEZONE_UTC);
      return calendar.getTime();
   }

   @NotNull
   public static Date getDateTime(@NotNull String timestamp) throws IllegalArgumentException {
      try {
         return ISO8601Utils.parse(timestamp, new ParsePosition(0));
      } catch (ParseException var2) {
         throw new IllegalArgumentException("timestamp is not ISO format " + timestamp);
      }
   }

   @NotNull
   public static Date getDateTimeWithMillisPrecision(@NotNull String timestamp) throws IllegalArgumentException {
      try {
         return getDateTime(new BigDecimal(timestamp).setScale(3, RoundingMode.DOWN).movePointRight(3).longValue());
      } catch (NumberFormatException var2) {
         throw new IllegalArgumentException("timestamp is not millis format " + timestamp);
      }
   }

   @NotNull
   public static String getTimestamp(@NotNull Date date) {
      return ISO8601Utils.format(date, true);
   }

   @NotNull
   public static Date getDateTime(long millis) {
      Calendar calendar = Calendar.getInstance(ISO8601Utils.TIMEZONE_UTC);
      calendar.setTimeInMillis(millis);
      return calendar.getTime();
   }

   public static double millisToSeconds(double millis) {
      return millis / 1000.0;
   }

   public static long millisToNanos(long millis) {
      return millis * 1000000L;
   }

   public static double nanosToMillis(double nanos) {
      return nanos / 1000000.0;
   }

   public static Date nanosToDate(long nanos) {
      Double millis = nanosToMillis(Double.valueOf((double)nanos));
      return getDateTime(millis.longValue());
   }

   @Nullable
   public static Date toUtilDate(@Nullable SentryDate sentryDate) {
      return sentryDate == null ? null : toUtilDateNotNull(sentryDate);
   }

   @NotNull
   public static Date toUtilDateNotNull(@NotNull SentryDate sentryDate) {
      return nanosToDate(sentryDate.nanoTimestamp());
   }

   public static double nanosToSeconds(long nanos) {
      return Double.valueOf((double)nanos) / 1.0E9;
   }

   public static double dateToSeconds(@NotNull Date date) {
      return millisToSeconds(date.getTime());
   }

   public static long dateToNanos(@NotNull Date date) {
      return millisToNanos(date.getTime());
   }

   public static long secondsToNanos(@NotNull long seconds) {
      return seconds * 1000000000L;
   }

   @NotNull
   public static BigDecimal doubleToBigDecimal(@NotNull Double value) {
      return BigDecimal.valueOf(value).setScale(6, RoundingMode.DOWN);
   }
}
