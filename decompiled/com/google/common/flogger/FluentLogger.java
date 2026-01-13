package com.google.common.flogger;

import com.google.common.flogger.backend.LoggerBackend;
import com.google.common.flogger.backend.Platform;
import com.google.common.flogger.parser.DefaultPrintfMessageParser;
import com.google.common.flogger.parser.MessageParser;
import com.google.errorprone.annotations.CheckReturnValue;
import java.util.logging.Level;

@CheckReturnValue
public final class FluentLogger extends AbstractLogger<FluentLogger.Api> {
   static final FluentLogger.NoOp NO_OP = new FluentLogger.NoOp();

   public static FluentLogger forEnclosingClass() {
      String loggingClass = Platform.getCallerFinder().findLoggingClass(FluentLogger.class);
      return new FluentLogger(Platform.getBackend(loggingClass));
   }

   FluentLogger(LoggerBackend backend) {
      super(backend);
   }

   public FluentLogger.Api at(Level level) {
      boolean isLoggable = this.isLoggable(level);
      boolean isForced = Platform.shouldForceLogging(this.getName(), level, isLoggable);
      return (FluentLogger.Api)(!isLoggable && !isForced ? NO_OP : new FluentLogger.Context(level, isForced));
   }

   public interface Api extends LoggingApi<FluentLogger.Api> {
   }

   final class Context extends LogContext<FluentLogger, FluentLogger.Api> implements FluentLogger.Api {
      private Context(Level level, boolean isForced) {
         super(level, isForced);
      }

      protected FluentLogger getLogger() {
         return FluentLogger.this;
      }

      protected FluentLogger.Api api() {
         return this;
      }

      protected FluentLogger.Api noOp() {
         return FluentLogger.NO_OP;
      }

      @Override
      protected MessageParser getMessageParser() {
         return DefaultPrintfMessageParser.getInstance();
      }
   }

   private static final class NoOp extends LoggingApi.NoOp<FluentLogger.Api> implements FluentLogger.Api {
      private NoOp() {
      }
   }
}
