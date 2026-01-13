package com.google.crypto.tink.internal;

public final class TinkBugException extends RuntimeException {
   public TinkBugException(String message) {
      super(message);
   }

   public TinkBugException(String message, Throwable cause) {
      super(message, cause);
   }

   public TinkBugException(Throwable cause) {
      super(cause);
   }

   public static <T> T exceptionIsBug(TinkBugException.ThrowingSupplier<T> t) {
      try {
         return t.get();
      } catch (Exception var2) {
         throw new TinkBugException(var2);
      }
   }

   public static void exceptionIsBug(TinkBugException.ThrowingRunnable v) {
      try {
         v.run();
      } catch (Exception var2) {
         throw new TinkBugException(var2);
      }
   }

   public interface ThrowingRunnable {
      void run() throws Exception;
   }

   public interface ThrowingSupplier<T> {
      T get() throws Exception;
   }
}
