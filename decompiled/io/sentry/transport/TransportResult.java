package io.sentry.transport;

import org.jetbrains.annotations.NotNull;

public abstract class TransportResult {
   @NotNull
   public static TransportResult success() {
      return TransportResult.SuccessTransportResult.INSTANCE;
   }

   @NotNull
   public static TransportResult error(int responseCode) {
      return new TransportResult.ErrorTransportResult(responseCode);
   }

   @NotNull
   public static TransportResult error() {
      return error(-1);
   }

   private TransportResult() {
   }

   public abstract boolean isSuccess();

   public abstract int getResponseCode();

   private static final class ErrorTransportResult extends TransportResult {
      private final int responseCode;

      ErrorTransportResult(int responseCode) {
         this.responseCode = responseCode;
      }

      @Override
      public boolean isSuccess() {
         return false;
      }

      @Override
      public int getResponseCode() {
         return this.responseCode;
      }
   }

   private static final class SuccessTransportResult extends TransportResult {
      static final TransportResult.SuccessTransportResult INSTANCE = new TransportResult.SuccessTransportResult();

      @Override
      public boolean isSuccess() {
         return true;
      }

      @Override
      public int getResponseCode() {
         return -1;
      }
   }
}
