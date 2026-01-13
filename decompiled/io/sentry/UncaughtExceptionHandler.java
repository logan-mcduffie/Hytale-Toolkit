package io.sentry;

import org.jetbrains.annotations.Nullable;

interface UncaughtExceptionHandler {
   java.lang.Thread.UncaughtExceptionHandler getDefaultUncaughtExceptionHandler();

   void setDefaultUncaughtExceptionHandler(@Nullable UncaughtExceptionHandler var1);

   public static final class Adapter implements UncaughtExceptionHandler {
      private static final UncaughtExceptionHandler.Adapter INSTANCE = new UncaughtExceptionHandler.Adapter();

      static UncaughtExceptionHandler getInstance() {
         return INSTANCE;
      }

      private Adapter() {
      }

      @Override
      public java.lang.Thread.UncaughtExceptionHandler getDefaultUncaughtExceptionHandler() {
         return Thread.getDefaultUncaughtExceptionHandler();
      }

      @Override
      public void setDefaultUncaughtExceptionHandler(@Nullable UncaughtExceptionHandler handler) {
         Thread.setDefaultUncaughtExceptionHandler(handler);
      }
   }
}
