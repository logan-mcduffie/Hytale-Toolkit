package com.google.common.flogger.util;

import com.google.common.flogger.LogSite;
import com.google.errorprone.annotations.CheckReturnValue;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

@CheckReturnValue
public final class StackBasedLogSite extends LogSite {
   private final StackTraceElement stackElement;

   public StackBasedLogSite(StackTraceElement stackElement) {
      this.stackElement = Checks.checkNotNull(stackElement, "stack element");
   }

   @Override
   public String getClassName() {
      return this.stackElement.getClassName();
   }

   @Override
   public String getMethodName() {
      return this.stackElement.getMethodName();
   }

   @Override
   public int getLineNumber() {
      return Math.max(this.stackElement.getLineNumber(), 0);
   }

   @Override
   public String getFileName() {
      return this.stackElement.getFileName();
   }

   @Override
   public boolean equals(@NullableDecl Object obj) {
      return obj instanceof StackBasedLogSite && this.stackElement.equals(((StackBasedLogSite)obj).stackElement);
   }

   @Override
   public int hashCode() {
      return this.stackElement.hashCode();
   }
}
