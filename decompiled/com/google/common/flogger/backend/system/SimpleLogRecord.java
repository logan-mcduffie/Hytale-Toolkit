package com.google.common.flogger.backend.system;

import com.google.common.flogger.LogContext;
import com.google.common.flogger.backend.LogData;
import com.google.common.flogger.backend.Metadata;

public final class SimpleLogRecord extends AbstractLogRecord {
   public static SimpleLogRecord create(LogData data, Metadata scope) {
      return new SimpleLogRecord(data, scope);
   }

   @Deprecated
   public static SimpleLogRecord create(LogData data) {
      return create(data, Metadata.empty());
   }

   public static SimpleLogRecord error(RuntimeException error, LogData data, Metadata scope) {
      return new SimpleLogRecord(error, data, scope);
   }

   @Deprecated
   public static SimpleLogRecord error(RuntimeException error, LogData data) {
      return error(error, data, Metadata.empty());
   }

   private SimpleLogRecord(LogData data, Metadata scope) {
      super(data, scope);
      this.setThrown(this.getMetadataProcessor().getSingleValue(LogContext.Key.LOG_CAUSE));
      this.getMessage();
   }

   private SimpleLogRecord(RuntimeException error, LogData data, Metadata scope) {
      super(error, data, scope);
   }
}
