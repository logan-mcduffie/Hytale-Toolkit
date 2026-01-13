package com.google.common.flogger.backend.system;

import com.google.common.flogger.backend.LoggerBackend;
import java.util.logging.Logger;

public final class SimpleBackendFactory extends BackendFactory {
   private static final BackendFactory INSTANCE = new SimpleBackendFactory();

   public static BackendFactory getInstance() {
      return INSTANCE;
   }

   private SimpleBackendFactory() {
   }

   @Override
   public LoggerBackend create(String loggingClass) {
      Logger logger = Logger.getLogger(loggingClass.replace('$', '.'));
      return new SimpleLoggerBackend(logger);
   }

   @Override
   public String toString() {
      return "Default logger backend factory";
   }
}
