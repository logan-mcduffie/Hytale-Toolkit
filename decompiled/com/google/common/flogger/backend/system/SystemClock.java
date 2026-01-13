package com.google.common.flogger.backend.system;

import java.util.concurrent.TimeUnit;

public final class SystemClock extends Clock {
   private static final SystemClock INSTANCE = new SystemClock();

   public static SystemClock getInstance() {
      return INSTANCE;
   }

   private SystemClock() {
   }

   @Override
   public long getCurrentTimeNanos() {
      return TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis());
   }

   @Override
   public String toString() {
      return "Default millisecond precision clock";
   }
}
