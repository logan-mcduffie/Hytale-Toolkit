package com.nimbusds.jose.util;

import java.util.Date;

@Deprecated
public class DateUtils {
   public static long toSecondsSinceEpoch(Date date) {
      return date.getTime() / 1000L;
   }

   public static Date fromSecondsSinceEpoch(long time) {
      return new Date(time * 1000L);
   }

   public static boolean isAfter(Date date, Date reference, long maxClockSkewSeconds) {
      return new Date(date.getTime() + maxClockSkewSeconds * 1000L).after(reference);
   }

   public static boolean isBefore(Date date, Date reference, long maxClockSkewSeconds) {
      return new Date(date.getTime() - maxClockSkewSeconds * 1000L).before(reference);
   }

   private DateUtils() {
   }
}
