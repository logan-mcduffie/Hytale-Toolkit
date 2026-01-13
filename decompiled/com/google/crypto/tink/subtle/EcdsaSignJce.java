package com.google.crypto.tink.subtle;

import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.signature.EcdsaPrivateKey;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.interfaces.ECPrivateKey;

@Immutable
public final class EcdsaSignJce implements PublicKeySign {
   private final PublicKeySign signer;

   public static PublicKeySign create(EcdsaPrivateKey key) throws GeneralSecurityException {
      return com.google.crypto.tink.signature.internal.EcdsaSignJce.create(key);
   }

   public EcdsaSignJce(final ECPrivateKey privateKey, Enums.HashType hash, EllipticCurves.EcdsaEncoding encoding) throws GeneralSecurityException {
      this.signer = new com.google.crypto.tink.signature.internal.EcdsaSignJce(privateKey, hash, encoding);
   }

   @Override
   public byte[] sign(final byte[] data) throws GeneralSecurityException {
      return this.signer.sign(data);
   }
}
