package com.google.common.flogger.backend.system;

import com.google.common.flogger.AbstractLogger;
import com.google.common.flogger.LogSite;
import com.google.common.flogger.backend.Platform;
import com.google.common.flogger.util.CallerFinder;
import com.google.common.flogger.util.StackBasedLogSite;

public final class StackBasedCallerFinder extends Platform.LogCallerFinder {
   private static final Platform.LogCallerFinder INSTANCE = new StackBasedCallerFinder();

   public static Platform.LogCallerFinder getInstance() {
      return INSTANCE;
   }

   @Override
   public String findLoggingClass(Class<? extends AbstractLogger<?>> loggerClass) {
      StackTraceElement caller = CallerFinder.findCallerOf(loggerClass, new Throwable(), 1);
      if (caller != null) {
         return caller.getClassName();
      } else {
         throw new IllegalStateException("no caller found on the stack for: " + loggerClass.getName());
      }
   }

   @Override
   public LogSite findLogSite(Class<?> loggerApi, int stackFramesToSkip) {
      StackTraceElement caller = CallerFinder.findCallerOf(loggerApi, new Throwable(), stackFramesToSkip + 1);
      return (LogSite)(caller != null ? new StackBasedLogSite(caller) : LogSite.INVALID);
   }

   @Override
   public String toString() {
      return "Default stack-based caller finder";
   }

   private StackBasedCallerFinder() {
   }
}
