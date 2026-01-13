package com.google.common.flogger.context;

import com.google.common.flogger.LoggingScope;
import com.google.common.flogger.backend.Metadata;
import com.google.common.flogger.backend.Platform;
import java.util.logging.Level;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public abstract class ContextDataProvider {
   public static ContextDataProvider getInstance() {
      return Platform.getContextDataProvider();
   }

   public abstract ScopedLoggingContext getContextApiSingleton();

   public boolean shouldForceLogging(String loggerName, Level level, boolean isEnabledByLevel) {
      return false;
   }

   public Tags getTags() {
      return Tags.empty();
   }

   public Metadata getMetadata() {
      return Metadata.empty();
   }

   @NullableDecl
   public LoggingScope getScope(ScopeType type) {
      return null;
   }
}
