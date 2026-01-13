package com.google.common.flogger.backend;

import com.google.common.flogger.FluentLogger;
import com.google.common.flogger.StackSize;
import com.google.common.flogger.context.ContextDataProvider;
import com.google.common.flogger.context.LogLevelMap;
import com.google.common.flogger.context.ScopedLoggingContext;
import com.google.common.flogger.context.Tags;
import java.util.concurrent.atomic.AtomicBoolean;

public final class NoOpContextDataProvider extends ContextDataProvider {
   private static final ContextDataProvider NO_OP_INSTANCE = new NoOpContextDataProvider();
   private final ScopedLoggingContext noOpContext = new NoOpContextDataProvider.NoOpScopedLoggingContext();

   public static final ContextDataProvider getInstance() {
      return NO_OP_INSTANCE;
   }

   @Override
   public ScopedLoggingContext getContextApiSingleton() {
      return this.noOpContext;
   }

   @Override
   public String toString() {
      return "No-op Provider";
   }

   private static final class NoOpScopedLoggingContext extends ScopedLoggingContext implements ScopedLoggingContext.LoggingContextCloseable {
      private final AtomicBoolean haveWarned = new AtomicBoolean();

      private NoOpScopedLoggingContext() {
      }

      private void logWarningOnceOnly() {
         if (this.haveWarned.compareAndSet(false, true)) {
            NoOpContextDataProvider.NoOpScopedLoggingContext.LazyLogger.logger
               .atWarning()
               .withStackTrace(StackSize.SMALL)
               .log(
                  "Scoped logging contexts are disabled; no context data provider was installed.\nTo enable scoped logging contexts in your application, see the site-specific Platform class used to configure logging behaviour.\nDefault Platform: com.google.common.flogger.backend.system.DefaultPlatform"
               );
         }
      }

      @Override
      public ScopedLoggingContext.Builder newContext() {
         return new ScopedLoggingContext.Builder() {
            @Override
            public ScopedLoggingContext.LoggingContextCloseable install() {
               NoOpScopedLoggingContext.this.logWarningOnceOnly();
               return NoOpScopedLoggingContext.this;
            }
         };
      }

      @Override
      public boolean addTags(Tags tags) {
         this.logWarningOnceOnly();
         return false;
      }

      @Override
      public boolean applyLogLevelMap(LogLevelMap m) {
         this.logWarningOnceOnly();
         return false;
      }

      @Override
      public void close() {
      }

      private static final class LazyLogger {
         private static final FluentLogger logger = FluentLogger.forEnclosingClass();
      }
   }
}
