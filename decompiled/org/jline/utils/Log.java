package org.jline.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class Log {
   private static final Logger logger = Logger.getLogger("org.jline");

   public static void trace(Object... messages) {
      log(Level.FINEST, messages);
   }

   public static void trace(Supplier<String> supplier) {
      log(Level.FINEST, supplier);
   }

   public static void debug(Supplier<String> supplier) {
      log(Level.FINE, supplier);
   }

   public static void debug(Object... messages) {
      log(Level.FINE, messages);
   }

   public static void info(Object... messages) {
      log(Level.INFO, messages);
   }

   public static void warn(Object... messages) {
      log(Level.WARNING, messages);
   }

   public static void error(Object... messages) {
      log(Level.SEVERE, messages);
   }

   public static boolean isDebugEnabled() {
      return isEnabled(Level.FINE);
   }

   static void render(PrintStream out, Object message) {
      if (message != null && message.getClass().isArray()) {
         Object[] array = (Object[])message;
         out.print("[");

         for (int i = 0; i < array.length; i++) {
            out.print(array[i]);
            if (i + 1 < array.length) {
               out.print(",");
            }
         }

         out.print("]");
      } else {
         out.print(message);
      }
   }

   static LogRecord createRecord(Level level, Object... messages) {
      Throwable cause = null;
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PrintStream ps = new PrintStream(baos);

      for (int i = 0; i < messages.length; i++) {
         if (i + 1 == messages.length && messages[i] instanceof Throwable) {
            cause = (Throwable)messages[i];
         } else {
            render(ps, messages[i]);
         }
      }

      ps.close();
      LogRecord r = new LogRecord(level, baos.toString());
      r.setThrown(cause);
      return r;
   }

   static LogRecord createRecord(Level level, Supplier<String> message) {
      return new LogRecord(level, message.get());
   }

   static void log(Level level, Supplier<String> message) {
      logr(level, () -> createRecord(level, message));
   }

   static void log(Level level, Object... messages) {
      logr(level, () -> createRecord(level, messages));
   }

   static void logr(Level level, Supplier<LogRecord> record) {
      if (logger.isLoggable(level)) {
         LogRecord tmp = record.get();
         tmp.setLoggerName(logger.getName());
         logger.log(tmp);
      }
   }

   static boolean isEnabled(Level level) {
      return logger.isLoggable(level);
   }
}
