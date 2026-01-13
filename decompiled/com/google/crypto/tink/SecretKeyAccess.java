package com.google.crypto.tink;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

@CheckReturnValue
@Immutable
public final class SecretKeyAccess {
   private static final SecretKeyAccess INSTANCE = new SecretKeyAccess();

   private SecretKeyAccess() {
   }

   static SecretKeyAccess instance() {
      return INSTANCE;
   }

   @CanIgnoreReturnValue
   public static SecretKeyAccess requireAccess(@Nullable SecretKeyAccess access) throws GeneralSecurityException {
      if (access == null) {
         throw new GeneralSecurityException("SecretKeyAccess is required");
      } else {
         return access;
      }
   }
}
