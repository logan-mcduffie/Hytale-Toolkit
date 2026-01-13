package io.netty.util.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public final class ThrowableUtil {
   private ThrowableUtil() {
   }

   public static <T extends Throwable> T unknownStackTrace(T cause, Class<?> clazz, String method) {
      cause.setStackTrace(new StackTraceElement[]{new StackTraceElement(clazz.getName(), method, null, -1)});
      return cause;
   }

   @Deprecated
   public static String stackTraceToString(Throwable cause) {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      PrintStream pout = new PrintStream(out);
      cause.printStackTrace(pout);
      pout.flush();

      String var3;
      try {
         var3 = out.toString();
      } finally {
         try {
            out.close();
         } catch (IOException var10) {
         }
      }

      return var3;
   }

   @Deprecated
   public static boolean haveSuppressed() {
      return true;
   }

   public static void addSuppressed(Throwable target, Throwable suppressed) {
      if (suppressed != null) {
         target.addSuppressed(suppressed);
      }
   }

   public static void addSuppressedAndClear(Throwable target, List<Throwable> suppressed) {
      addSuppressed(target, suppressed);
      suppressed.clear();
   }

   public static void addSuppressed(Throwable target, List<Throwable> suppressed) {
      for (Throwable t : suppressed) {
         addSuppressed(target, t);
      }
   }

   public static Throwable[] getSuppressed(Throwable source) {
      return source.getSuppressed();
   }
}
