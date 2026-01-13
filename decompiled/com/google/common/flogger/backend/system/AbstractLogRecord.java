package com.google.common.flogger.backend.system;

import com.google.common.flogger.LogSite;
import com.google.common.flogger.backend.LogData;
import com.google.common.flogger.backend.LogMessageFormatter;
import com.google.common.flogger.backend.MessageUtils;
import com.google.common.flogger.backend.Metadata;
import com.google.common.flogger.backend.MetadataProcessor;
import com.google.common.flogger.backend.SimpleMessageFormatter;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public abstract class AbstractLogRecord extends LogRecord {
   private static final Formatter jdkMessageFormatter = new Formatter() {
      @Override
      public String format(LogRecord record) {
         throw new UnsupportedOperationException();
      }
   };
   private static final Object[] NO_PARAMETERS = new Object[0];
   private final LogData data;
   private final MetadataProcessor metadata;

   protected AbstractLogRecord(LogData data, Metadata scope) {
      super(data.getLevel(), null);
      this.data = data;
      this.metadata = MetadataProcessor.forScopeAndLogSite(scope, data.getMetadata());
      LogSite logSite = data.getLogSite();
      this.setSourceClassName(logSite.getClassName());
      this.setSourceMethodName(logSite.getMethodName());
      this.setLoggerName(data.getLoggerName());
      this.setMillis(TimeUnit.NANOSECONDS.toMillis(data.getTimestampNanos()));
      super.setParameters(NO_PARAMETERS);
   }

   protected AbstractLogRecord(RuntimeException error, LogData data, Metadata scope) {
      this(data, scope);
      this.setLevel(data.getLevel().intValue() < Level.WARNING.intValue() ? Level.WARNING : data.getLevel());
      this.setThrown(error);
      StringBuilder errorMsg = new StringBuilder("LOGGING ERROR: ").append(error.getMessage()).append('\n');
      safeAppend(data, errorMsg);
      this.setMessage(errorMsg.toString());
   }

   protected LogMessageFormatter getLogMessageFormatter() {
      return SimpleMessageFormatter.getDefaultFormatter();
   }

   @Override
   public final void setParameters(Object[] parameters) {
      this.getMessage();
      if (parameters == null) {
         parameters = NO_PARAMETERS;
      }

      super.setParameters(parameters);
   }

   @Override
   public final void setMessage(String message) {
      if (message == null) {
         message = "";
      }

      super.setMessage(message);
   }

   @Override
   public final String getMessage() {
      String cachedMessage = super.getMessage();
      if (cachedMessage != null) {
         return cachedMessage;
      } else {
         String formattedMessage = this.getLogMessageFormatter().format(this.data, this.metadata);
         super.setMessage(formattedMessage);
         return formattedMessage;
      }
   }

   public final StringBuilder appendFormattedMessageTo(StringBuilder buffer) {
      String cachedMessage = super.getMessage();
      if (cachedMessage == null) {
         this.getLogMessageFormatter().append(this.data, this.metadata, buffer);
      } else if (this.getParameters().length == 0) {
         buffer.append(cachedMessage);
      } else {
         buffer.append(jdkMessageFormatter.formatMessage(this));
      }

      return buffer;
   }

   public final String getFormattedMessage() {
      return this.getParameters().length == 0 ? this.getMessage() : jdkMessageFormatter.formatMessage(this);
   }

   @Override
   public final void setResourceBundle(ResourceBundle bundle) {
   }

   @Override
   public final void setResourceBundleName(String name) {
   }

   public final LogRecord toMutableLogRecord() {
      LogRecord copy = new LogRecord(this.getLevel(), this.getFormattedMessage());
      copy.setParameters(NO_PARAMETERS);
      copy.setSourceClassName(this.getSourceClassName());
      copy.setSourceMethodName(this.getSourceMethodName());
      copy.setLoggerName(this.getLoggerName());
      copy.setMillis(this.getMillis());
      copy.setThrown(this.getThrown());
      copy.setThreadID(this.getThreadID());
      return copy;
   }

   public final LogData getLogData() {
      return this.data;
   }

   public final MetadataProcessor getMetadataProcessor() {
      return this.metadata;
   }

   @Override
   public String toString() {
      StringBuilder out = new StringBuilder();
      out.append(this.getClass().getSimpleName())
         .append(" {\n  message: ")
         .append(this.getMessage())
         .append("\n  arguments: ")
         .append(this.getParameters() != null ? Arrays.asList(this.getParameters()) : "<none>")
         .append('\n');
      safeAppend(this.getLogData(), out);
      out.append("\n}");
      return out.toString();
   }

   private static void safeAppend(LogData data, StringBuilder out) {
      out.append("  original message: ");
      if (data.getTemplateContext() == null) {
         out.append(MessageUtils.safeToString(data.getLiteralArgument()));
      } else {
         out.append(data.getTemplateContext().getMessage());
         out.append("\n  original arguments:");

         for (Object arg : data.getArguments()) {
            out.append("\n    ").append(MessageUtils.safeToString(arg));
         }
      }

      Metadata metadata = data.getMetadata();
      if (metadata.size() > 0) {
         out.append("\n  metadata:");

         for (int n = 0; n < metadata.size(); n++) {
            out.append("\n    ").append(metadata.getKey(n).getLabel()).append(": ").append(MessageUtils.safeToString(metadata.getValue(n)));
         }
      }

      out.append("\n  level: ").append(MessageUtils.safeToString(data.getLevel()));
      out.append("\n  timestamp (nanos): ").append(data.getTimestampNanos());
      out.append("\n  class: ").append(data.getLogSite().getClassName());
      out.append("\n  method: ").append(data.getLogSite().getMethodName());
      out.append("\n  line number: ").append(data.getLogSite().getLineNumber());
   }
}
