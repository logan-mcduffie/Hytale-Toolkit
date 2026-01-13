package com.google.crypto.tink;

import com.google.errorprone.annotations.CheckReturnValue;
import com.google.errorprone.annotations.Immutable;

@CheckReturnValue
@Immutable
public final class InsecureSecretKeyAccess {
   private InsecureSecretKeyAccess() {
   }

   public static SecretKeyAccess get() {
      return SecretKeyAccess.instance();
   }
}
