package com.google.crypto.tink.hybrid.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.hybrid.HpkeParameters;
import com.google.crypto.tink.hybrid.HpkePrivateKey;
import com.google.crypto.tink.util.Bytes;
import java.security.GeneralSecurityException;

public final class HpkeKemKeyFactory {
   @AccessesPartialKey
   public static HpkeKemPrivateKey createPrivate(HpkePrivateKey privateKey) throws GeneralSecurityException {
      HpkeParameters.KemId kemId = privateKey.getParameters().getKemId();
      if (kemId != HpkeParameters.KemId.DHKEM_X25519_HKDF_SHA256
         && kemId != HpkeParameters.KemId.DHKEM_P256_HKDF_SHA256
         && kemId != HpkeParameters.KemId.DHKEM_P384_HKDF_SHA384
         && kemId != HpkeParameters.KemId.DHKEM_P521_HKDF_SHA512) {
         throw new GeneralSecurityException("Unrecognized HPKE KEM identifier");
      } else {
         Bytes convertedPrivateKeyBytes = Bytes.copyFrom(privateKey.getPrivateKeyBytes().toByteArray(InsecureSecretKeyAccess.get()));
         return new HpkeKemPrivateKey(convertedPrivateKeyBytes, privateKey.getPublicKey().getPublicKeyBytes());
      }
   }

   private HpkeKemKeyFactory() {
   }
}
