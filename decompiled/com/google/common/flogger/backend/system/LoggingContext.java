package com.google.common.flogger.backend.system;

import com.google.common.flogger.context.ContextDataProvider;
import com.google.common.flogger.context.LogLevelMap;
import com.google.common.flogger.context.ScopedLoggingContext;
import com.google.common.flogger.context.Tags;

@Deprecated
public abstract class LoggingContext extends ContextDataProvider {
   private static final ScopedLoggingContext NO_OP_API = new LoggingContext.NoOpScopedLoggingContext();

   @Override
   public ScopedLoggingContext getContextApiSingleton() {
      return NO_OP_API;
   }

   private static final class NoOpScopedLoggingContext extends ScopedLoggingContext implements ScopedLoggingContext.LoggingContextCloseable {
      private NoOpScopedLoggingContext() {
      }

      @Override
      public ScopedLoggingContext.Builder newContext() {
         return new ScopedLoggingContext.Builder() {
            @Override
            public ScopedLoggingContext.LoggingContextCloseable install() {
               return NoOpScopedLoggingContext.this;
            }
         };
      }

      @Override
      public void close() {
      }

      @Override
      public boolean addTags(Tags tags) {
         return false;
      }

      @Override
      public boolean applyLogLevelMap(LogLevelMap m) {
         return false;
      }
   }
}
