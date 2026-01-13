package com.google.common.flogger;

import com.google.common.flogger.util.Checks;

public final class LazyArgs {
   public static <T> LazyArg<T> lazy(LazyArg<T> lambdaOrMethodReference) {
      return Checks.checkNotNull(lambdaOrMethodReference, "lazy arg");
   }

   private LazyArgs() {
   }
}
