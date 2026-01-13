package com.google.common.flogger.backend;

public abstract class LogMessageFormatter {
   public String format(LogData logData, MetadataProcessor metadata) {
      return this.append(logData, metadata, new StringBuilder()).toString();
   }

   public abstract StringBuilder append(LogData var1, MetadataProcessor var2, StringBuilder var3);
}
