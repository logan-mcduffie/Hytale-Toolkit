package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public abstract class UpdateStatus {
   public static final class NewRelease extends UpdateStatus {
      @NotNull
      private final UpdateInfo info;

      public NewRelease(@NotNull UpdateInfo info) {
         this.info = info;
      }

      @NotNull
      public UpdateInfo getInfo() {
         return this.info;
      }

      @Override
      public String toString() {
         return "UpdateStatus.NewRelease{info=" + this.info + '}';
      }
   }

   public static final class NoNetwork extends UpdateStatus {
      @NotNull
      private final String message;

      public NoNetwork(@NotNull String message) {
         this.message = message;
      }

      @NotNull
      public String getMessage() {
         return this.message;
      }

      @Override
      public String toString() {
         return "UpdateStatus.NoNetwork{message='" + this.message + '\'' + '}';
      }
   }

   public static final class UpToDate extends UpdateStatus {
      private static final UpdateStatus.UpToDate INSTANCE = new UpdateStatus.UpToDate();

      private UpToDate() {
      }

      public static UpdateStatus.UpToDate getInstance() {
         return INSTANCE;
      }

      @Override
      public String toString() {
         return "UpdateStatus.UpToDate{}";
      }
   }

   public static final class UpdateError extends UpdateStatus {
      @NotNull
      private final String message;

      public UpdateError(@NotNull String message) {
         this.message = message;
      }

      @NotNull
      public String getMessage() {
         return this.message;
      }

      @Override
      public String toString() {
         return "UpdateStatus.UpdateError{message='" + this.message + '\'' + '}';
      }
   }
}
