package com.google.common.flogger;

import com.google.common.flogger.backend.Platform;

public final class LogSites {
   public static LogSite callerOf(Class<?> loggingApi) {
      return Platform.getCallerFinder().findLogSite(loggingApi, 0);
   }

   public static LogSite logSite() {
      return Platform.getCallerFinder().findLogSite(LogSites.class, 0);
   }

   private LogSites() {
   }
}
