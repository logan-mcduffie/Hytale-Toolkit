package com.google.crypto.tink.daead;

import com.google.crypto.tink.internal.TinkBugException;

public final class PredefinedDeterministicAeadParameters {
   public static final AesSivParameters AES256_SIV = TinkBugException.exceptionIsBug(
      () -> AesSivParameters.builder().setKeySizeBytes(64).setVariant(AesSivParameters.Variant.TINK).build()
   );

   private PredefinedDeterministicAeadParameters() {
   }
}
