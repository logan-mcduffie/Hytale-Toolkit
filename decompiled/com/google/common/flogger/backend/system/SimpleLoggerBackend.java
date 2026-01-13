package com.google.common.flogger.backend.system;

import com.google.common.flogger.backend.LogData;
import com.google.common.flogger.backend.Platform;
import java.util.logging.Logger;

public class SimpleLoggerBackend extends AbstractBackend {
   public SimpleLoggerBackend(Logger logger) {
      super(logger);
   }

   @Override
   public void log(LogData data) {
      this.log(SimpleLogRecord.create(data, Platform.getInjectedMetadata()), data.wasForced());
   }

   @Override
   public void handleError(RuntimeException error, LogData badData) {
      this.log(SimpleLogRecord.error(error, badData, Platform.getInjectedMetadata()), badData.wasForced());
   }
}
