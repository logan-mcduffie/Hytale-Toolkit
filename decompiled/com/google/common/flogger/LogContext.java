package com.google.common.flogger;

import com.google.common.flogger.backend.LogData;
import com.google.common.flogger.backend.Metadata;
import com.google.common.flogger.backend.Platform;
import com.google.common.flogger.backend.TemplateContext;
import com.google.common.flogger.context.Tags;
import com.google.common.flogger.parser.MessageParser;
import com.google.common.flogger.util.CallerFinder;
import com.google.common.flogger.util.Checks;
import com.google.errorprone.annotations.CheckReturnValue;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

@CheckReturnValue
public abstract class LogContext<LOGGER extends AbstractLogger<API>, API extends LoggingApi<API>> implements LoggingApi<API>, LogData {
   private static final String LITERAL_VALUE_MESSAGE = new String();
   private final Level level;
   private final long timestampNanos;
   private LogContext.MutableMetadata metadata = null;
   private LogSite logSite = null;
   private TemplateContext templateContext = null;
   private Object[] args = null;

   protected LogContext(Level level, boolean isForced) {
      this(level, isForced, Platform.getCurrentTimeNanos());
   }

   protected LogContext(Level level, boolean isForced, long timestampNanos) {
      this.level = Checks.checkNotNull(level, "level");
      this.timestampNanos = timestampNanos;
      if (isForced) {
         this.addMetadata(LogContext.Key.WAS_FORCED, Boolean.TRUE);
      }
   }

   protected abstract API api();

   protected abstract LOGGER getLogger();

   protected abstract API noOp();

   protected abstract MessageParser getMessageParser();

   @Override
   public final Level getLevel() {
      return this.level;
   }

   @Deprecated
   @Override
   public final long getTimestampMicros() {
      return TimeUnit.NANOSECONDS.toMicros(this.timestampNanos);
   }

   @Override
   public final long getTimestampNanos() {
      return this.timestampNanos;
   }

   @Override
   public final String getLoggerName() {
      return this.getLogger().getBackend().getLoggerName();
   }

   @Override
   public final LogSite getLogSite() {
      if (this.logSite == null) {
         throw new IllegalStateException("cannot request log site information prior to postProcess()");
      } else {
         return this.logSite;
      }
   }

   @Override
   public final TemplateContext getTemplateContext() {
      return this.templateContext;
   }

   @Override
   public final Object[] getArguments() {
      if (this.templateContext == null) {
         throw new IllegalStateException("cannot get arguments unless a template context exists");
      } else {
         return this.args;
      }
   }

   @Override
   public final Object getLiteralArgument() {
      if (this.templateContext != null) {
         throw new IllegalStateException("cannot get literal argument if a template context exists");
      } else {
         return this.args[0];
      }
   }

   @Override
   public final boolean wasForced() {
      return this.metadata != null && Boolean.TRUE.equals(this.metadata.findValue(LogContext.Key.WAS_FORCED));
   }

   @Override
   public final Metadata getMetadata() {
      return (Metadata)(this.metadata != null ? this.metadata : Metadata.empty());
   }

   protected final <T> void addMetadata(MetadataKey<T> key, T value) {
      if (this.metadata == null) {
         this.metadata = new LogContext.MutableMetadata();
      }

      this.metadata.addValue(key, value);
   }

   protected final void removeMetadata(MetadataKey<?> key) {
      if (this.metadata != null) {
         this.metadata.removeAllValues(key);
      }
   }

   protected boolean postProcess(@NullableDecl LogSiteKey logSiteKey) {
      if (this.metadata != null) {
         if (logSiteKey != null) {
            Integer rateLimitCount = this.metadata.findValue(LogContext.Key.LOG_EVERY_N);
            LogSiteStats.RateLimitPeriod rateLimitPeriod = this.metadata.findValue(LogContext.Key.LOG_AT_MOST_EVERY);
            LogSiteStats stats = LogSiteStats.getStatsForKey(logSiteKey, this.metadata);
            if (rateLimitCount != null && !stats.incrementAndCheckInvocationCount(rateLimitCount)) {
               return false;
            }

            if (rateLimitPeriod != null && !stats.checkLastTimestamp(this.getTimestampNanos(), rateLimitPeriod)) {
               return false;
            }
         }

         StackSize stackSize = this.metadata.findValue(LogContext.Key.CONTEXT_STACK_SIZE);
         if (stackSize != null) {
            this.removeMetadata(LogContext.Key.CONTEXT_STACK_SIZE);
            LogSiteStackTrace context = new LogSiteStackTrace(
               this.getMetadata().findValue(LogContext.Key.LOG_CAUSE),
               stackSize,
               CallerFinder.getStackForCallerOf(LogContext.class, new Throwable(), stackSize.getMaxDepth(), 1)
            );
            this.addMetadata(LogContext.Key.LOG_CAUSE, context);
         }
      }

      return true;
   }

   private boolean shouldLog() {
      if (this.logSite == null) {
         this.logSite = Checks.checkNotNull(Platform.getCallerFinder().findLogSite(LogContext.class, 1), "logger backend must not return a null LogSite");
      }

      LogSiteKey logSiteKey = null;
      if (this.logSite != LogSite.INVALID) {
         logSiteKey = this.logSite;
      }

      if (!this.postProcess(logSiteKey)) {
         return false;
      } else {
         Tags tags = Platform.getInjectedTags();
         if (!tags.isEmpty()) {
            this.addMetadata(LogContext.Key.TAGS, tags);
         }

         return true;
      }
   }

   static LogSiteKey specializeLogSiteKeyFromMetadata(LogSiteKey logSiteKey, Metadata metadata) {
      Checks.checkNotNull(logSiteKey, "logSiteKey");
      int n = 0;

      for (int size = metadata.size(); n < size; n++) {
         if (LogContext.Key.LOG_SITE_GROUPING_KEY.equals(metadata.getKey(n))) {
            Object groupByQualifier = metadata.getValue(n);
            if (groupByQualifier instanceof LoggingScope) {
               logSiteKey = ((LoggingScope)groupByQualifier).specialize(logSiteKey);
            } else {
               logSiteKey = SpecializedLogSiteKey.of(logSiteKey, groupByQualifier);
            }
         }
      }

      return logSiteKey;
   }

   private void logImpl(String message, Object... args) {
      this.args = args;

      for (int n = 0; n < args.length; n++) {
         if (args[n] instanceof LazyArg) {
            args[n] = ((LazyArg)args[n]).evaluate();
         }
      }

      if (message != LITERAL_VALUE_MESSAGE) {
         this.templateContext = new TemplateContext(this.getMessageParser(), message);
      }

      this.getLogger().write(this);
   }

   @Override
   public final API withInjectedLogSite(LogSite logSite) {
      if (this.logSite == null && logSite != null) {
         this.logSite = logSite;
      }

      return this.api();
   }

   @Override
   public final API withInjectedLogSite(String internalClassName, String methodName, int encodedLineNumber, @NullableDecl String sourceFileName) {
      return this.withInjectedLogSite(LogSite.injectedLogSite(internalClassName, methodName, encodedLineNumber, sourceFileName));
   }

   @Override
   public final boolean isEnabled() {
      return this.wasForced() || this.getLogger().isLoggable(this.level);
   }

   @Override
   public final <T> API with(MetadataKey<T> key, @NullableDecl T value) {
      Checks.checkNotNull(key, "metadata key");
      if (value != null) {
         this.addMetadata(key, value);
      }

      return this.api();
   }

   @Override
   public final <T> API with(MetadataKey<Boolean> key) {
      return this.with(key, Boolean.TRUE);
   }

   @Override
   public final API withCause(Throwable cause) {
      if (cause != null) {
         this.addMetadata(LogContext.Key.LOG_CAUSE, cause);
      }

      return this.api();
   }

   @Override
   public API withStackTrace(StackSize size) {
      if (Checks.checkNotNull(size, "stack size") != StackSize.NONE) {
         this.addMetadata(LogContext.Key.CONTEXT_STACK_SIZE, size);
      }

      return this.api();
   }

   @Override
   public final API every(int n) {
      if (this.wasForced()) {
         return this.api();
      } else if (n <= 0) {
         throw new IllegalArgumentException("rate limit count must be positive");
      } else {
         if (n > 1) {
            this.addMetadata(LogContext.Key.LOG_EVERY_N, n);
         }

         return this.api();
      }
   }

   @Override
   public final API atMostEvery(int n, TimeUnit unit) {
      if (this.wasForced()) {
         return this.api();
      } else if (n < 0) {
         throw new IllegalArgumentException("rate limit period cannot be negative");
      } else {
         if (n > 0) {
            this.addMetadata(LogContext.Key.LOG_AT_MOST_EVERY, LogSiteStats.newRateLimitPeriod(n, unit));
         }

         return this.api();
      }
   }

   @Override
   public final void log() {
      if (this.shouldLog()) {
         this.logImpl(LITERAL_VALUE_MESSAGE, "");
      }
   }

   @Override
   public final void log(String msg) {
      if (this.shouldLog()) {
         this.logImpl(LITERAL_VALUE_MESSAGE, msg);
      }
   }

   @Override
   public final void log(String message, @NullableDecl Object p1) {
      if (this.shouldLog()) {
         this.logImpl(message, p1);
      }
   }

   @Override
   public final void log(String message, @NullableDecl Object p1, @NullableDecl Object p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, @NullableDecl Object p1, @NullableDecl Object p2, @NullableDecl Object p3) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2, p3);
      }
   }

   @Override
   public final void log(String message, @NullableDecl Object p1, @NullableDecl Object p2, @NullableDecl Object p3, @NullableDecl Object p4) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2, p3, p4);
      }
   }

   @Override
   public final void log(
      String msg, @NullableDecl Object p1, @NullableDecl Object p2, @NullableDecl Object p3, @NullableDecl Object p4, @NullableDecl Object p5
   ) {
      if (this.shouldLog()) {
         this.logImpl(msg, p1, p2, p3, p4, p5);
      }
   }

   @Override
   public final void log(
      String msg,
      @NullableDecl Object p1,
      @NullableDecl Object p2,
      @NullableDecl Object p3,
      @NullableDecl Object p4,
      @NullableDecl Object p5,
      @NullableDecl Object p6
   ) {
      if (this.shouldLog()) {
         this.logImpl(msg, p1, p2, p3, p4, p5, p6);
      }
   }

   @Override
   public final void log(
      String msg,
      @NullableDecl Object p1,
      @NullableDecl Object p2,
      @NullableDecl Object p3,
      @NullableDecl Object p4,
      @NullableDecl Object p5,
      @NullableDecl Object p6,
      @NullableDecl Object p7
   ) {
      if (this.shouldLog()) {
         this.logImpl(msg, p1, p2, p3, p4, p5, p6, p7);
      }
   }

   @Override
   public final void log(
      String msg,
      @NullableDecl Object p1,
      @NullableDecl Object p2,
      @NullableDecl Object p3,
      @NullableDecl Object p4,
      @NullableDecl Object p5,
      @NullableDecl Object p6,
      @NullableDecl Object p7,
      @NullableDecl Object p8
   ) {
      if (this.shouldLog()) {
         this.logImpl(msg, p1, p2, p3, p4, p5, p6, p7, p8);
      }
   }

   @Override
   public final void log(
      String msg,
      @NullableDecl Object p1,
      @NullableDecl Object p2,
      @NullableDecl Object p3,
      @NullableDecl Object p4,
      @NullableDecl Object p5,
      @NullableDecl Object p6,
      @NullableDecl Object p7,
      @NullableDecl Object p8,
      @NullableDecl Object p9
   ) {
      if (this.shouldLog()) {
         this.logImpl(msg, p1, p2, p3, p4, p5, p6, p7, p8, p9);
      }
   }

   @Override
   public final void log(
      String msg,
      @NullableDecl Object p1,
      @NullableDecl Object p2,
      @NullableDecl Object p3,
      @NullableDecl Object p4,
      @NullableDecl Object p5,
      @NullableDecl Object p6,
      @NullableDecl Object p7,
      @NullableDecl Object p8,
      @NullableDecl Object p9,
      @NullableDecl Object p10
   ) {
      if (this.shouldLog()) {
         this.logImpl(msg, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
      }
   }

   @Override
   public final void log(
      String msg,
      @NullableDecl Object p1,
      @NullableDecl Object p2,
      @NullableDecl Object p3,
      @NullableDecl Object p4,
      @NullableDecl Object p5,
      @NullableDecl Object p6,
      @NullableDecl Object p7,
      @NullableDecl Object p8,
      @NullableDecl Object p9,
      @NullableDecl Object p10,
      Object... rest
   ) {
      if (this.shouldLog()) {
         Object[] params = new Object[rest.length + 10];
         params[0] = p1;
         params[1] = p2;
         params[2] = p3;
         params[3] = p4;
         params[4] = p5;
         params[5] = p6;
         params[6] = p7;
         params[7] = p8;
         params[8] = p9;
         params[9] = p10;
         System.arraycopy(rest, 0, params, 10, rest.length);
         this.logImpl(msg, params);
      }
   }

   @Override
   public final void log(String message, char p1) {
      if (this.shouldLog()) {
         this.logImpl(message, p1);
      }
   }

   @Override
   public final void log(String message, byte p1) {
      if (this.shouldLog()) {
         this.logImpl(message, p1);
      }
   }

   @Override
   public final void log(String message, short p1) {
      if (this.shouldLog()) {
         this.logImpl(message, p1);
      }
   }

   @Override
   public final void log(String message, int p1) {
      if (this.shouldLog()) {
         this.logImpl(message, p1);
      }
   }

   @Override
   public final void log(String message, long p1) {
      if (this.shouldLog()) {
         this.logImpl(message, p1);
      }
   }

   @Override
   public final void log(String message, @NullableDecl Object p1, boolean p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, @NullableDecl Object p1, char p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, @NullableDecl Object p1, byte p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, @NullableDecl Object p1, short p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, @NullableDecl Object p1, int p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, @NullableDecl Object p1, long p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, @NullableDecl Object p1, float p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, @NullableDecl Object p1, double p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, boolean p1, @NullableDecl Object p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, char p1, @NullableDecl Object p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, byte p1, @NullableDecl Object p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, short p1, @NullableDecl Object p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, int p1, @NullableDecl Object p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, long p1, @NullableDecl Object p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, float p1, @NullableDecl Object p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, double p1, @NullableDecl Object p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, boolean p1, boolean p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, char p1, boolean p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, byte p1, boolean p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, short p1, boolean p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, int p1, boolean p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, long p1, boolean p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, float p1, boolean p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, double p1, boolean p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, boolean p1, char p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, char p1, char p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, byte p1, char p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, short p1, char p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, int p1, char p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, long p1, char p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, float p1, char p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, double p1, char p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, boolean p1, byte p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, char p1, byte p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, byte p1, byte p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, short p1, byte p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, int p1, byte p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, long p1, byte p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, float p1, byte p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, double p1, byte p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, boolean p1, short p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, char p1, short p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, byte p1, short p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, short p1, short p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, int p1, short p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, long p1, short p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, float p1, short p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, double p1, short p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, boolean p1, int p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, char p1, int p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, byte p1, int p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, short p1, int p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, int p1, int p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, long p1, int p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, float p1, int p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, double p1, int p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, boolean p1, long p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, char p1, long p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, byte p1, long p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, short p1, long p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, int p1, long p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, long p1, long p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, float p1, long p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, double p1, long p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, boolean p1, float p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, char p1, float p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, byte p1, float p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, short p1, float p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, int p1, float p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, long p1, float p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, float p1, float p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, double p1, float p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, boolean p1, double p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, char p1, double p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, byte p1, double p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, short p1, double p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, int p1, double p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, long p1, double p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, float p1, double p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void log(String message, double p1, double p2) {
      if (this.shouldLog()) {
         this.logImpl(message, p1, p2);
      }
   }

   @Override
   public final void logVarargs(String message, @NullableDecl Object[] params) {
      if (this.shouldLog()) {
         this.logImpl(message, Arrays.copyOf(params, params.length));
      }
   }

   public static final class Key {
      public static final MetadataKey<Throwable> LOG_CAUSE = MetadataKey.single("cause", Throwable.class);
      public static final MetadataKey<Integer> LOG_EVERY_N = MetadataKey.single("ratelimit_count", Integer.class);
      public static final MetadataKey<LogSiteStats.RateLimitPeriod> LOG_AT_MOST_EVERY = MetadataKey.single(
         "ratelimit_period", LogSiteStats.RateLimitPeriod.class
      );
      public static final MetadataKey<Object> LOG_SITE_GROUPING_KEY = new MetadataKey<Object>("group_by", Object.class, true) {
         @Override
         public void emitRepeated(Iterator<Object> keys, MetadataKey.KeyValueHandler out) {
            if (keys.hasNext()) {
               Object first = keys.next();
               if (!keys.hasNext()) {
                  out.handle(this.getLabel(), first);
               } else {
                  StringBuilder buf = new StringBuilder();
                  buf.append('[').append(first);

                  do {
                     buf.append(',').append(keys.next());
                  } while (keys.hasNext());

                  out.handle(this.getLabel(), buf.append(']').toString());
               }
            }
         }
      };
      public static final MetadataKey<Boolean> WAS_FORCED = MetadataKey.single("forced", Boolean.class);
      public static final MetadataKey<Tags> TAGS = new MetadataKey<Tags>("tags", Tags.class, false) {
         public void emit(Tags tags, MetadataKey.KeyValueHandler out) {
            for (Entry<String, ? extends Set<Object>> e : tags.asMap().entrySet()) {
               Set<Object> values = (Set<Object>)e.getValue();
               if (!values.isEmpty()) {
                  for (Object v : e.getValue()) {
                     out.handle(e.getKey(), v);
                  }
               } else {
                  out.handle(e.getKey(), null);
               }
            }
         }
      };
      public static final MetadataKey<StackSize> CONTEXT_STACK_SIZE = MetadataKey.single("stack_size", StackSize.class);

      private Key() {
      }
   }

   static final class MutableMetadata extends Metadata {
      private static final int INITIAL_KEY_VALUE_CAPACITY = 4;
      private Object[] keyValuePairs = new Object[8];
      private int keyValueCount = 0;

      @Override
      public int size() {
         return this.keyValueCount;
      }

      @Override
      public MetadataKey<?> getKey(int n) {
         if (n >= this.keyValueCount) {
            throw new IndexOutOfBoundsException();
         } else {
            return (MetadataKey<?>)this.keyValuePairs[2 * n];
         }
      }

      @Override
      public Object getValue(int n) {
         if (n >= this.keyValueCount) {
            throw new IndexOutOfBoundsException();
         } else {
            return this.keyValuePairs[2 * n + 1];
         }
      }

      private int indexOf(MetadataKey<?> key) {
         for (int index = 0; index < this.keyValueCount; index++) {
            if (this.keyValuePairs[2 * index].equals(key)) {
               return index;
            }
         }

         return -1;
      }

      @NullableDecl
      @Override
      public <T> T findValue(MetadataKey<T> key) {
         int index = this.indexOf(key);
         return index != -1 ? key.cast(this.keyValuePairs[2 * index + 1]) : null;
      }

      <T> void addValue(MetadataKey<T> key, T value) {
         if (!key.canRepeat()) {
            int index = this.indexOf(key);
            if (index != -1) {
               this.keyValuePairs[2 * index + 1] = Checks.checkNotNull(value, "metadata value");
               return;
            }
         }

         if (2 * (this.keyValueCount + 1) > this.keyValuePairs.length) {
            this.keyValuePairs = Arrays.copyOf(this.keyValuePairs, 2 * this.keyValuePairs.length);
         }

         this.keyValuePairs[2 * this.keyValueCount] = Checks.checkNotNull(key, "metadata key");
         this.keyValuePairs[2 * this.keyValueCount + 1] = Checks.checkNotNull(value, "metadata value");
         this.keyValueCount++;
      }

      void removeAllValues(MetadataKey<?> key) {
         int index = this.indexOf(key);
         if (index >= 0) {
            int dest = 2 * index;

            int src;
            for (src = dest + 2; src < 2 * this.keyValueCount; src += 2) {
               Object nextKey = this.keyValuePairs[src];
               if (!nextKey.equals(key)) {
                  this.keyValuePairs[dest] = nextKey;
                  this.keyValuePairs[dest + 1] = this.keyValuePairs[src + 1];
                  dest += 2;
               }
            }

            this.keyValueCount -= src - dest >> 1;

            while (dest < src) {
               this.keyValuePairs[dest++] = null;
            }
         }
      }

      @Override
      public String toString() {
         StringBuilder out = new StringBuilder("Metadata{");

         for (int n = 0; n < this.size(); n++) {
            out.append(" '").append(this.getKey(n)).append("': ").append(this.getValue(n));
         }

         return out.append(" }").toString();
      }
   }
}
