package com.google.crypto.tink.subtle;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.Ed25519;
import com.google.crypto.tink.signature.Ed25519Parameters;
import com.google.crypto.tink.signature.Ed25519PrivateKey;
import com.google.crypto.tink.signature.internal.Ed25519SignJce;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public final class Ed25519Sign implements PublicKeySign {
   public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS;
   public static final int SECRET_KEY_LEN = 32;
   private final byte[] hashedPrivateKey;
   private final byte[] publicKey;
   private final byte[] outputPrefix;
   private final byte[] messageSuffix;

   @AccessesPartialKey
   public static PublicKeySign create(Ed25519PrivateKey key) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use Ed25519 in FIPS-mode.");
      } else {
         try {
            return Ed25519SignJce.create(key);
         } catch (GeneralSecurityException var2) {
            return new Ed25519Sign(
               key.getPrivateKeyBytes().toByteArray(InsecureSecretKeyAccess.get()),
               key.getOutputPrefix().toByteArray(),
               key.getParameters().getVariant().equals(Ed25519Parameters.Variant.LEGACY) ? new byte[]{0} : new byte[0]
            );
         }
      }
   }

   private Ed25519Sign(final byte[] privateKey, final byte[] outputPrefix, final byte[] messageSuffix) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use Ed25519 in FIPS-mode.");
      } else if (privateKey.length != 32) {
         throw new IllegalArgumentException(String.format("Given private key's length is not %s", 32));
      } else {
         this.hashedPrivateKey = Ed25519.getHashedScalar(privateKey);
         this.publicKey = Ed25519.scalarMultWithBaseToBytes(this.hashedPrivateKey);
         this.outputPrefix = outputPrefix;
         this.messageSuffix = messageSuffix;
      }
   }

   public Ed25519Sign(final byte[] privateKey) throws GeneralSecurityException {
      this(privateKey, new byte[0], new byte[0]);
   }

   private byte[] noPrefixSign(final byte[] data) throws GeneralSecurityException {
      return Ed25519.sign(data, this.publicKey, this.hashedPrivateKey);
   }

   @Override
   public byte[] sign(final byte[] data) throws GeneralSecurityException {
      byte[] signature;
      if (this.messageSuffix.length == 0) {
         signature = this.noPrefixSign(data);
      } else {
         signature = this.noPrefixSign(Bytes.concat(data, this.messageSuffix));
      }

      return this.outputPrefix.length == 0 ? signature : Bytes.concat(this.outputPrefix, signature);
   }

   public static final class KeyPair {
      private final byte[] publicKey;
      private final byte[] privateKey;

      private KeyPair(final byte[] publicKey, final byte[] privateKey) {
         this.publicKey = publicKey;
         this.privateKey = privateKey;
      }

      public byte[] getPublicKey() {
         return Arrays.copyOf(this.publicKey, this.publicKey.length);
      }

      public byte[] getPrivateKey() {
         return Arrays.copyOf(this.privateKey, this.privateKey.length);
      }

      public static Ed25519Sign.KeyPair newKeyPair() throws GeneralSecurityException {
         return newKeyPairFromSeed(Random.randBytes(32));
      }

      public static Ed25519Sign.KeyPair newKeyPairFromSeed(byte[] secretSeed) throws GeneralSecurityException {
         if (secretSeed.length != 32) {
            throw new IllegalArgumentException(String.format("Given secret seed length is not %s", 32));
         } else {
            byte[] publicKey = Ed25519.scalarMultWithBaseToBytes(Ed25519.getHashedScalar(secretSeed));
            return new Ed25519Sign.KeyPair(publicKey, secretSeed);
         }
      }
   }
}
