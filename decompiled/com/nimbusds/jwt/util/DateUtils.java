package com.nimbusds.jwt.util;

import java.util.Date;

public class DateUtils {
   public static Date nowWithSecondsPrecision() {
      return fromSecondsSinceEpoch(toSecondsSinceEpoch(new Date()));
   }

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

   public static boolean isWithin(Date date, Date reference, long maxClockSkewSeconds) {
      long minTime = reference.getTime() - maxClockSkewSeconds * 1000L;
      long maxTime = reference.getTime() + maxClockSkewSeconds * 1000L;
      return date.getTime() > minTime && date.getTime() < maxTime;
   }

   private DateUtils() {
   }
}
