package com.google.common.flogger.context;

import com.google.common.flogger.LoggingScope;
import com.google.common.flogger.LoggingScopeProvider;
import com.google.common.flogger.util.Checks;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public final class ScopeType implements LoggingScopeProvider {
   public static final ScopeType REQUEST = create("request");
   private final String name;

   public static ScopeType create(String name) {
      return new ScopeType(name);
   }

   private ScopeType(String name) {
      this.name = Checks.checkNotNull(name, "name");
   }

   LoggingScope newScope() {
      return LoggingScope.create(this.name);
   }

   @NullableDecl
   @Override
   public LoggingScope getCurrentScope() {
      return ContextDataProvider.getInstance().getScope(this);
   }
}
