package io.sentry;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SystemOutLogger implements ILogger {
   @Override
   public void log(@NotNull SentryLevel level, @NotNull String message, @Nullable Object... args) {
      System.out.println(String.format("%s: %s", level, String.format(message, args)));
   }

   @Override
   public void log(@NotNull SentryLevel level, @NotNull String message, @Nullable Throwable throwable) {
      if (throwable == null) {
         this.log(level, message);
      } else {
         System.out.println(String.format("%s: %s\n%s", level, String.format(message, throwable.toString()), this.captureStackTrace(throwable)));
      }
   }

   @Override
   public void log(@NotNull SentryLevel level, @Nullable Throwable throwable, @NotNull String message, @Nullable Object... args) {
      if (throwable == null) {
         this.log(level, message, args);
      } else {
         System.out.println(String.format("%s: %s \n %s\n%s", level, String.format(message, args), throwable.toString(), this.captureStackTrace(throwable)));
      }
   }

   @Override
   public boolean isEnabled(@Nullable SentryLevel level) {
      return true;
   }

   @NotNull
   private String captureStackTrace(@NotNull Throwable throwable) {
      StringWriter stringWriter = new StringWriter();
      PrintWriter printWriter = new PrintWriter(stringWriter);
      throwable.printStackTrace(printWriter);
      return stringWriter.toString();
   }
}
