package org.bouncycastle.pkix.util;

import java.util.Locale;

public class LocalizedException extends Exception {
   protected ErrorBundle message;
   private Throwable cause;

   public LocalizedException(ErrorBundle var1) {
      super(var1.getText(Locale.getDefault()));
      this.message = var1;
   }

   public LocalizedException(ErrorBundle var1, Throwable var2) {
      super(var1.getText(Locale.getDefault()));
      this.message = var1;
      this.cause = var2;
   }

   public ErrorBundle getErrorMessage() {
      return this.message;
   }

   @Override
   public Throwable getCause() {
      return this.cause;
   }
}
