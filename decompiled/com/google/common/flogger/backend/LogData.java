package com.google.common.flogger.backend;

import com.google.common.flogger.LogSite;
import java.util.logging.Level;

public interface LogData {
   Level getLevel();

   @Deprecated
   long getTimestampMicros();

   long getTimestampNanos();

   String getLoggerName();

   LogSite getLogSite();

   Metadata getMetadata();

   boolean wasForced();

   TemplateContext getTemplateContext();

   Object[] getArguments();

   Object getLiteralArgument();
}
