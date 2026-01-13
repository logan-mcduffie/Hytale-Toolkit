package io.netty.util.internal.logging;

public interface InternalLogger {
   String name();

   boolean isTraceEnabled();

   void trace(String var1);

   void trace(String var1, Object var2);

   void trace(String var1, Object var2, Object var3);

   void trace(String var1, Object... var2);

   void trace(String var1, Throwable var2);

   void trace(Throwable var1);

   boolean isDebugEnabled();

   void debug(String var1);

   void debug(String var1, Object var2);

   void debug(String var1, Object var2, Object var3);

   void debug(String var1, Object... var2);

   void debug(String var1, Throwable var2);

   void debug(Throwable var1);

   boolean isInfoEnabled();

   void info(String var1);

   void info(String var1, Object var2);

   void info(String var1, Object var2, Object var3);

   void info(String var1, Object... var2);

   void info(String var1, Throwable var2);

   void info(Throwable var1);

   boolean isWarnEnabled();

   void warn(String var1);

   void warn(String var1, Object var2);

   void warn(String var1, Object... var2);

   void warn(String var1, Object var2, Object var3);

   void warn(String var1, Throwable var2);

   void warn(Throwable var1);

   boolean isErrorEnabled();

   void error(String var1);

   void error(String var1, Object var2);

   void error(String var1, Object var2, Object var3);

   void error(String var1, Object... var2);

   void error(String var1, Throwable var2);

   void error(Throwable var1);

   boolean isEnabled(InternalLogLevel var1);

   void log(InternalLogLevel var1, String var2);

   void log(InternalLogLevel var1, String var2, Object var3);

   void log(InternalLogLevel var1, String var2, Object var3, Object var4);

   void log(InternalLogLevel var1, String var2, Object... var3);

   void log(InternalLogLevel var1, String var2, Throwable var3);

   void log(InternalLogLevel var1, Throwable var2);
}
