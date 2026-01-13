package com.google.common.flogger.backend.system;

import com.google.common.flogger.backend.LoggerBackend;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public abstract class AbstractBackend extends LoggerBackend {
   private static volatile boolean cannotUseForcingLogger = false;
   private final Logger logger;

   AbstractBackend(Logger logger) {
      this.logger = logger;
   }

   protected AbstractBackend(String loggingClass) {
      this(Logger.getLogger(loggingClass.replace('$', '.')));
   }

   @Override
   public final String getLoggerName() {
      return this.logger.getName();
   }

   @Override
   public final boolean isLoggable(Level lvl) {
      return this.logger.isLoggable(lvl);
   }

   public final void log(LogRecord record, boolean wasForced) {
      if (wasForced && !this.logger.isLoggable(record.getLevel())) {
         Filter filter = this.logger.getFilter();
         if (filter != null) {
            filter.isLoggable(record);
         }

         if (this.logger.getClass() != Logger.class && !cannotUseForcingLogger) {
            this.forceLoggingViaChildLogger(record);
         } else {
            publish(this.logger, record);
         }
      } else {
         this.logger.log(record);
      }
   }

   private static void publish(Logger logger, LogRecord record) {
      for (Handler handler : logger.getHandlers()) {
         handler.publish(record);
      }

      if (logger.getUseParentHandlers()) {
         logger = logger.getParent();
         if (logger != null) {
            publish(logger, record);
         }
      }
   }

   void forceLoggingViaChildLogger(LogRecord record) {
      Logger forcingLogger = this.getForcingLogger(this.logger);

      try {
         forcingLogger.setLevel(Level.ALL);
      } catch (SecurityException var4) {
         cannotUseForcingLogger = true;
         Logger.getLogger("")
            .log(
               Level.SEVERE,
               "Forcing log statements with Flogger has been partially disabled.\nThe Flogger library cannot modify logger log levels, which is necessary to force log statements. This is likely due to an installed SecurityManager.\nForced log statements will still be published directly to log handlers, but will not be visible to the 'log(LogRecord)' method of Logger subclasses.\n"
            );
         publish(this.logger, record);
         return;
      }

      forcingLogger.log(record);
   }

   Logger getForcingLogger(Logger parent) {
      return Logger.getLogger(parent.getName() + ".__forced__");
   }
}
