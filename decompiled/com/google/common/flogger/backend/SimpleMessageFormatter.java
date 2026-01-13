package com.google.common.flogger.backend;

import com.google.common.flogger.LogContext;
import com.google.common.flogger.MetadataKey;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public final class SimpleMessageFormatter {
   private static final Set<MetadataKey<?>> DEFAULT_KEYS_TO_IGNORE = Collections.singleton(LogContext.Key.LOG_CAUSE);
   private static final MetadataHandler<MetadataKey.KeyValueHandler> DEFAULT_HANDLER = MetadataKeyValueHandlers.getDefaultHandler(DEFAULT_KEYS_TO_IGNORE);
   private static final LogMessageFormatter DEFAULT_FORMATTER = new LogMessageFormatter() {
      @Override
      public StringBuilder append(LogData logData, MetadataProcessor metadata, StringBuilder out) {
         return SimpleMessageFormatter.appendFormatted(logData, metadata, SimpleMessageFormatter.DEFAULT_HANDLER, out);
      }

      @Override
      public String format(LogData logData, MetadataProcessor metadata) {
         return SimpleMessageFormatter.format(logData, metadata);
      }
   };

   public static LogMessageFormatter getDefaultFormatter() {
      return DEFAULT_FORMATTER;
   }

   public static StringBuilder appendFormatted(
      LogData logData, MetadataProcessor metadata, MetadataHandler<MetadataKey.KeyValueHandler> metadataHandler, StringBuilder buffer
   ) {
      BaseMessageFormatter.appendFormattedMessage(logData, buffer);
      return appendContext(metadata, metadataHandler, buffer);
   }

   public static StringBuilder appendContext(
      MetadataProcessor metadataProcessor, MetadataHandler<MetadataKey.KeyValueHandler> metadataHandler, StringBuilder buffer
   ) {
      KeyValueFormatter kvf = new KeyValueFormatter("[CONTEXT ", " ]", buffer);
      metadataProcessor.process(metadataHandler, kvf);
      kvf.done();
      return buffer;
   }

   public static String getLiteralLogMessage(LogData logData) {
      return MessageUtils.safeToString(logData.getLiteralArgument());
   }

   public static boolean mustBeFormatted(LogData logData, MetadataProcessor metadata, Set<MetadataKey<?>> keysToIgnore) {
      return logData.getTemplateContext() != null || metadata.keyCount() > keysToIgnore.size() || !keysToIgnore.containsAll(metadata.keySet());
   }

   private static String format(LogData logData, MetadataProcessor metadata) {
      return mustBeFormatted(logData, metadata, DEFAULT_KEYS_TO_IGNORE)
         ? appendFormatted(logData, metadata, DEFAULT_HANDLER, new StringBuilder()).toString()
         : getLiteralLogMessage(logData);
   }

   @Deprecated
   public static void format(LogData logData, SimpleMessageFormatter.SimpleLogHandler receiver) {
      MetadataProcessor metadata = MetadataProcessor.forScopeAndLogSite(Metadata.empty(), logData.getMetadata());
      receiver.handleFormattedLogMessage(logData.getLevel(), format(logData, metadata), metadata.getSingleValue(LogContext.Key.LOG_CAUSE));
   }

   @Deprecated
   static void format(LogData logData, SimpleMessageFormatter.SimpleLogHandler receiver, SimpleMessageFormatter.Option option) {
      switch (option) {
         case WITH_LOG_SITE:
            StringBuilder buffer = new StringBuilder();
            if (MessageUtils.appendLogSite(logData.getLogSite(), buffer)) {
               buffer.append(" ");
            }

            String message = appendFormatted(logData, MetadataProcessor.forScopeAndLogSite(Metadata.empty(), logData.getMetadata()), DEFAULT_HANDLER, buffer)
               .toString();
            Throwable cause = logData.getMetadata().findValue(LogContext.Key.LOG_CAUSE);
            receiver.handleFormattedLogMessage(logData.getLevel(), message, cause);
            break;
         case DEFAULT:
            format(logData, receiver);
      }
   }

   private SimpleMessageFormatter() {
   }

   @Deprecated
   static enum Option {
      DEFAULT,
      WITH_LOG_SITE;
   }

   @Deprecated
   public interface SimpleLogHandler {
      void handleFormattedLogMessage(Level var1, String var2, @NullableDecl Throwable var3);
   }
}
