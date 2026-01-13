package com.google.crypto.tink.subtle;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.Ed25519;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.signature.Ed25519Parameters;
import com.google.crypto.tink.signature.Ed25519PublicKey;
import com.google.crypto.tink.signature.internal.Ed25519VerifyJce;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.Arrays;

@Immutable
public final class Ed25519Verify implements PublicKeyVerify {
   public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS;
   public static final int PUBLIC_KEY_LEN = 32;
   public static final int SIGNATURE_LEN = 64;
   private final com.google.crypto.tink.util.Bytes publicKey;
   private final byte[] outputPrefix;
   private final byte[] messageSuffix;

   @AccessesPartialKey
   public static PublicKeyVerify create(Ed25519PublicKey key) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use Ed25519 in FIPS-mode.");
      } else {
         try {
            return Ed25519VerifyJce.create(key);
         } catch (GeneralSecurityException var2) {
            return new Ed25519Verify(
               key.getPublicKeyBytes().toByteArray(),
               key.getOutputPrefix().toByteArray(),
               key.getParameters().getVariant().equals(Ed25519Parameters.Variant.LEGACY) ? new byte[]{0} : new byte[0]
            );
         }
      }
   }

   public Ed25519Verify(final byte[] publicKey) {
      this(publicKey, new byte[0], new byte[0]);
   }

   private Ed25519Verify(final byte[] publicKey, final byte[] outputPrefix, final byte[] messageSuffix) {
      if (!FIPS.isCompatible()) {
         throw new IllegalStateException(new GeneralSecurityException("Can not use Ed25519 in FIPS-mode."));
      } else if (publicKey.length != 32) {
         throw new IllegalArgumentException(String.format("Given public key's length is not %s.", 32));
      } else {
         this.publicKey = com.google.crypto.tink.util.Bytes.copyFrom(publicKey);
         this.outputPrefix = outputPrefix;
         this.messageSuffix = messageSuffix;
         Ed25519.init();
      }
   }

   private void noPrefixVerify(byte[] signature, byte[] data) throws GeneralSecurityException {
      if (signature.length != 64) {
         throw new GeneralSecurityException(String.format("The length of the signature is not %s.", 64));
      } else if (!Ed25519.verify(data, signature, this.publicKey.toByteArray())) {
         throw new GeneralSecurityException("Signature check failed.");
      }
   }

   @Override
   public void verify(final byte[] signature, final byte[] data) throws GeneralSecurityException {
      if (this.outputPrefix.length == 0 && this.messageSuffix.length == 0) {
         this.noPrefixVerify(signature, data);
      } else if (!Util.isPrefix(this.outputPrefix, signature)) {
         throw new GeneralSecurityException("Invalid signature (output prefix mismatch)");
      } else {
         byte[] dataCopy = data;
         if (this.messageSuffix.length != 0) {
            dataCopy = Bytes.concat(data, this.messageSuffix);
         }

         byte[] signatureNoPrefix = Arrays.copyOfRange(signature, this.outputPrefix.length, signature.length);
         this.noPrefixVerify(signatureNoPrefix, dataCopy);
      }
   }
}
