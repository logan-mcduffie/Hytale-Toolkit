package com.google.common.flogger.backend;

import java.util.logging.Level;

public abstract class LoggerBackend {
   public abstract String getLoggerName();

   public abstract boolean isLoggable(Level var1);

   public abstract void log(LogData var1);

   public abstract void handleError(RuntimeException var1, LogData var2);
}
