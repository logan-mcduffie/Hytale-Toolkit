package com.google.common.flogger;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public interface LoggingScopeProvider {
   @NullableDecl
   LoggingScope getCurrentScope();
}
