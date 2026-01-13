package com.google.crypto.tink.config.internal;

final class TinkFipsStatus {
   public static boolean useOnlyFips() {
      return false;
   }

   private TinkFipsStatus() {
   }
}
