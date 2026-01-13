package com.google.crypto.tink.subtle;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

public final class EciesHkdfSenderKem {
   private final ECPublicKey recipientPublicKey;

   public EciesHkdfSenderKem(final ECPublicKey recipientPublicKey) {
      this.recipientPublicKey = recipientPublicKey;
   }

   public EciesHkdfSenderKem.KemKey generateKey(
      String hmacAlgo, final byte[] hkdfSalt, final byte[] hkdfInfo, int keySizeInBytes, EllipticCurves.PointFormatType pointFormat
   ) throws GeneralSecurityException {
      KeyPair ephemeralKeyPair = EllipticCurves.generateKeyPair(this.recipientPublicKey.getParams());
      ECPublicKey ephemeralPublicKey = (ECPublicKey)ephemeralKeyPair.getPublic();
      ECPrivateKey ephemeralPrivateKey = (ECPrivateKey)ephemeralKeyPair.getPrivate();
      byte[] sharedSecret = EllipticCurves.computeSharedSecret(ephemeralPrivateKey, this.recipientPublicKey);
      byte[] kemBytes = EllipticCurves.pointEncode(ephemeralPublicKey.getParams().getCurve(), pointFormat, ephemeralPublicKey.getW());
      byte[] symmetricKey = Hkdf.computeEciesHkdfSymmetricKey(kemBytes, sharedSecret, hmacAlgo, hkdfSalt, hkdfInfo, keySizeInBytes);
      return new EciesHkdfSenderKem.KemKey(kemBytes, symmetricKey);
   }

   public static final class KemKey {
      private final com.google.crypto.tink.util.Bytes kemBytes;
      private final com.google.crypto.tink.util.Bytes symmetricKey;

      public KemKey(final byte[] kemBytes, final byte[] symmetricKey) {
         if (kemBytes == null) {
            throw new NullPointerException("KemBytes must be non-null");
         } else if (symmetricKey == null) {
            throw new NullPointerException("symmetricKey must be non-null");
         } else {
            this.kemBytes = com.google.crypto.tink.util.Bytes.copyFrom(kemBytes);
            this.symmetricKey = com.google.crypto.tink.util.Bytes.copyFrom(symmetricKey);
         }
      }

      public byte[] getKemBytes() {
         return this.kemBytes.toByteArray();
      }

      public byte[] getSymmetricKey() {
         return this.symmetricKey.toByteArray();
      }
   }
}
