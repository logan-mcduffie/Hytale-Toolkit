package com.google.crypto.tink.subtle;

import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.signature.EcdsaPublicKey;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.interfaces.ECPublicKey;

@Immutable
public final class EcdsaVerifyJce implements PublicKeyVerify {
   public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_REQUIRES_BORINGCRYPTO;
   private final PublicKeyVerify verifier;

   public static PublicKeyVerify create(EcdsaPublicKey key) throws GeneralSecurityException {
      return com.google.crypto.tink.signature.internal.EcdsaVerifyJce.create(key);
   }

   public EcdsaVerifyJce(final ECPublicKey publicKey, Enums.HashType hash, EllipticCurves.EcdsaEncoding encoding) throws GeneralSecurityException {
      this.verifier = new com.google.crypto.tink.signature.internal.EcdsaVerifyJce(publicKey, hash, encoding);
   }

   @Override
   public void verify(final byte[] signature, final byte[] data) throws GeneralSecurityException {
      this.verifier.verify(signature, data);
   }
}
