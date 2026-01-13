package com.google.crypto.tink.keyderivation.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.internal.MutableKeyDerivationRegistry;
import com.google.crypto.tink.internal.MutablePrimitiveRegistry;
import com.google.crypto.tink.internal.PrimitiveRegistry;
import com.google.crypto.tink.keyderivation.PrfBasedKeyDerivationKey;
import com.google.crypto.tink.subtle.prf.StreamingPrf;
import com.google.errorprone.annotations.Immutable;
import java.io.InputStream;
import java.security.GeneralSecurityException;

@Immutable
public final class PrfBasedKeyDeriver implements KeyDeriver {
   final StreamingPrf prf;
   final PrfBasedKeyDerivationKey key;

   private PrfBasedKeyDeriver(StreamingPrf prf, PrfBasedKeyDerivationKey key) {
      this.prf = prf;
      this.key = key;
   }

   @AccessesPartialKey
   public static KeyDeriver create(PrfBasedKeyDerivationKey key) throws GeneralSecurityException {
      StreamingPrf prf = MutablePrimitiveRegistry.globalInstance().getPrimitive(key.getPrfKey(), StreamingPrf.class);
      PrfBasedKeyDeriver deriver = new PrfBasedKeyDeriver(prf, key);
      Object unused = deriver.deriveKey(new byte[]{1});
      return deriver;
   }

   @AccessesPartialKey
   public static KeyDeriver createWithPrfPrimitiveRegistry(PrimitiveRegistry primitiveRegistry, PrfBasedKeyDerivationKey key) throws GeneralSecurityException {
      StreamingPrf prf = primitiveRegistry.getPrimitive(key.getPrfKey(), StreamingPrf.class);
      PrfBasedKeyDeriver deriver = new PrfBasedKeyDeriver(prf, key);
      Object unused = deriver.deriveKey(new byte[]{1});
      return deriver;
   }

   @AccessesPartialKey
   @Override
   public Key deriveKey(byte[] salt) throws GeneralSecurityException {
      InputStream inputStream = this.prf.computePrf(salt);
      return MutableKeyDerivationRegistry.globalInstance()
         .createKeyFromRandomness(
            this.key.getParameters().getDerivedKeyParameters(), inputStream, this.key.getIdRequirementOrNull(), InsecureSecretKeyAccess.get()
         );
   }
}
