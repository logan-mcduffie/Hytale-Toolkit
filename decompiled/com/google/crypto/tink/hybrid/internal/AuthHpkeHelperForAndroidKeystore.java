package com.google.crypto.tink.hybrid.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.hybrid.HpkeParameters;
import com.google.crypto.tink.hybrid.HpkePublicKey;
import com.google.crypto.tink.subtle.Bytes;
import com.google.crypto.tink.subtle.EllipticCurves;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.spec.ECPoint;

@Immutable
public final class AuthHpkeHelperForAndroidKeystore {
   private static final byte[] EMPTY_ASSOCIATED_DATA = new byte[0];
   private final HpkeKem kem;
   private final HpkeKdf kdf;
   private final HpkeAead aead;
   private final byte[] ourPublicKeyByteArray;
   private final byte[] theirPublicKeyByteArray;

   private AuthHpkeHelperForAndroidKeystore(HpkeKem kem, HpkeKdf kdf, HpkeAead aead, byte[] ourPublicKeyByteArray, byte[] theirPublicKeyByteArray) {
      this.kem = kem;
      this.kdf = kdf;
      this.aead = aead;
      this.ourPublicKeyByteArray = ourPublicKeyByteArray;
      this.theirPublicKeyByteArray = theirPublicKeyByteArray;
   }

   @AccessesPartialKey
   public static AuthHpkeHelperForAndroidKeystore create(HpkePublicKey ourPublicKey, HpkePublicKey theirPublicKey) throws GeneralSecurityException {
      if (!ourPublicKey.getParameters().equals(theirPublicKey.getParameters())) {
         throw new GeneralSecurityException("ourPublicKey.getParameters() must be equal to theirPublicKey.getParameters()");
      } else {
         HpkeParameters parameters = ourPublicKey.getParameters();
         validateParameters(parameters);
         HpkeKem kem = HpkePrimitiveFactory.createKem(parameters.getKemId());
         HpkeKdf kdf = HpkePrimitiveFactory.createKdf(parameters.getKdfId());
         HpkeAead aead = HpkePrimitiveFactory.createAead(parameters.getAeadId());
         return new AuthHpkeHelperForAndroidKeystore(
            kem, kdf, aead, ourPublicKey.getPublicKeyBytes().toByteArray(), theirPublicKey.getPublicKeyBytes().toByteArray()
         );
      }
   }

   private static void validateParameters(HpkeParameters parameters) throws GeneralSecurityException {
      if (!parameters.getKemId().equals(HpkeParameters.KemId.DHKEM_P256_HKDF_SHA256)) {
         throw new GeneralSecurityException("AuthHpkeHelperForAndroidKeystore currently only supports KemId.DHKEM_P256_HKDF_SHA256.");
      } else if (!parameters.getKdfId().equals(HpkeParameters.KdfId.HKDF_SHA256)) {
         throw new GeneralSecurityException("AuthHpkeHelperForAndroidKeystore currently only supports KdfId.HKDF_SHA256.");
      } else if (!parameters.getAeadId().equals(HpkeParameters.AeadId.AES_128_GCM) && !parameters.getAeadId().equals(HpkeParameters.AeadId.AES_256_GCM)) {
         throw new GeneralSecurityException("AuthHpkeHelperForAndroidKeystore currently only supports AeadId.AES_128_GCM and AeadId.AES_256_GCM.");
      } else if (!parameters.getVariant().equals(HpkeParameters.Variant.NO_PREFIX)) {
         throw new GeneralSecurityException("AuthHpkeHelperForAndroidKeystore currently only supports Variant.NO_PREFIX");
      }
   }

   public byte[] decryptAuthenticatedWithEncapsulatedKeyAndP256SharedSecret(
      byte[] encapsulatedKey, byte[] dhSharedSecret1, byte[] dhSharedSecret2, byte[] ciphertext, int ciphertextOffset, byte[] info
   ) throws GeneralSecurityException {
      byte[] dhSharedSecret = Bytes.concat(dhSharedSecret1, dhSharedSecret2);
      byte[] derivedSharedSecret = NistCurvesHpkeKem.fromCurve(EllipticCurves.CurveType.NIST_P256)
         .deriveKemSharedSecret(dhSharedSecret, encapsulatedKey, this.ourPublicKeyByteArray, this.theirPublicKeyByteArray);
      HpkeContext context = HpkeContext.createContext(HpkeUtil.AUTH_MODE, encapsulatedKey, derivedSharedSecret, this.kem, this.kdf, this.aead, info);
      return context.open(ciphertext, ciphertextOffset, EMPTY_ASSOCIATED_DATA);
   }

   public byte[] encryptAuthenticatedWithEncapsulatedKeyAndP256SharedSecret(
      ECPoint emphemeralPublicKey, byte[] dhSharedSecret1, byte[] dhSharedSecret2, byte[] plaintext, byte[] contextInfo
   ) throws GeneralSecurityException {
      byte[] emphemeralPublicKeyByteArray = EllipticCurves.pointEncode(
         EllipticCurves.CurveType.NIST_P256, EllipticCurves.PointFormatType.UNCOMPRESSED, emphemeralPublicKey
      );
      byte[] dhSharedSecret = Bytes.concat(dhSharedSecret1, dhSharedSecret2);
      byte[] derivedSharedSecret = NistCurvesHpkeKem.fromCurve(EllipticCurves.CurveType.NIST_P256)
         .deriveKemSharedSecret(dhSharedSecret, emphemeralPublicKeyByteArray, this.theirPublicKeyByteArray, this.ourPublicKeyByteArray);
      HpkeContext context = HpkeContext.createContext(
         HpkeUtil.AUTH_MODE, emphemeralPublicKeyByteArray, derivedSharedSecret, this.kem, this.kdf, this.aead, contextInfo
      );
      return Bytes.concat(emphemeralPublicKeyByteArray, context.seal(plaintext, EMPTY_ASSOCIATED_DATA));
   }
}
