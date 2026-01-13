package com.google.common.flogger;

import com.google.common.flogger.util.Checks;
import com.google.errorprone.annotations.CheckReturnValue;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

@CheckReturnValue
public abstract class LogSite implements LogSiteKey {
   public static final int UNKNOWN_LINE = 0;
   public static final LogSite INVALID = new LogSite() {
      @Override
      public String getClassName() {
         return "<unknown class>";
      }

      @Override
      public String getMethodName() {
         return "<unknown method>";
      }

      @Override
      public int getLineNumber() {
         return 0;
      }

      @Override
      public String getFileName() {
         return null;
      }
   };

   public abstract String getClassName();

   public abstract String getMethodName();

   public abstract int getLineNumber();

   public abstract String getFileName();

   @Override
   public final String toString() {
      StringBuilder out = new StringBuilder()
         .append("LogSite{ class=")
         .append(this.getClassName())
         .append(", method=")
         .append(this.getMethodName())
         .append(", line=")
         .append(this.getLineNumber());
      if (this.getFileName() != null) {
         out.append(", file=").append(this.getFileName());
      }

      return out.append(" }").toString();
   }

   @Deprecated
   public static LogSite injectedLogSite(String internalClassName, String methodName, int encodedLineNumber, @NullableDecl String sourceFileName) {
      return new LogSite.InjectedLogSite(internalClassName, methodName, encodedLineNumber, sourceFileName);
   }

   private static final class InjectedLogSite extends LogSite {
      private final String internalClassName;
      private final String methodName;
      private final int encodedLineNumber;
      private final String sourceFileName;
      private int hashcode = 0;

      private InjectedLogSite(String internalClassName, String methodName, int encodedLineNumber, String sourceFileName) {
         this.internalClassName = Checks.checkNotNull(internalClassName, "class name");
         this.methodName = Checks.checkNotNull(methodName, "method name");
         this.encodedLineNumber = encodedLineNumber;
         this.sourceFileName = sourceFileName;
      }

      @Override
      public String getClassName() {
         return this.internalClassName.replace('/', '.');
      }

      @Override
      public String getMethodName() {
         return this.methodName;
      }

      @Override
      public int getLineNumber() {
         return this.encodedLineNumber & 65535;
      }

      @Override
      public String getFileName() {
         return this.sourceFileName;
      }

      @Override
      public boolean equals(Object obj) {
         if (!(obj instanceof LogSite.InjectedLogSite)) {
            return false;
         } else {
            LogSite.InjectedLogSite other = (LogSite.InjectedLogSite)obj;
            return this.internalClassName.equals(other.internalClassName)
               && this.methodName.equals(other.methodName)
               && this.encodedLineNumber == other.encodedLineNumber;
         }
      }

      @Override
      public int hashCode() {
         if (this.hashcode == 0) {
            int temp = 157;
            temp = 31 * temp + this.internalClassName.hashCode();
            temp = 31 * temp + this.methodName.hashCode();
            temp = 31 * temp + this.encodedLineNumber;
            this.hashcode = temp;
         }

         return this.hashcode;
      }
   }
}
