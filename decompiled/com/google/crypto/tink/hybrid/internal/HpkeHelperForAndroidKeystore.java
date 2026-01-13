package com.google.crypto.tink.hybrid.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.hybrid.HpkeParameters;
import com.google.crypto.tink.hybrid.HpkePublicKey;
import com.google.crypto.tink.subtle.EllipticCurves;
import java.security.GeneralSecurityException;

public final class HpkeHelperForAndroidKeystore {
   private static final byte[] EMPTY_ASSOCIATED_DATA = new byte[0];
   private final HpkeKem kem;
   private final HpkeKdf kdf;
   private final HpkeAead aead;
   private final byte[] publicKeyByteArray;

   private HpkeHelperForAndroidKeystore(HpkeKem kem, HpkeKdf kdf, HpkeAead aead, byte[] publicKeyByteArray) {
      this.kem = kem;
      this.kdf = kdf;
      this.aead = aead;
      this.publicKeyByteArray = publicKeyByteArray;
   }

   @AccessesPartialKey
   public static HpkeHelperForAndroidKeystore create(HpkePublicKey receiverPublicKey) throws GeneralSecurityException {
      HpkeParameters parameters = receiverPublicKey.getParameters();
      validateParameters(parameters);
      HpkeKem kem = HpkePrimitiveFactory.createKem(parameters.getKemId());
      HpkeKdf kdf = HpkePrimitiveFactory.createKdf(parameters.getKdfId());
      HpkeAead aead = HpkePrimitiveFactory.createAead(parameters.getAeadId());
      return new HpkeHelperForAndroidKeystore(kem, kdf, aead, receiverPublicKey.getPublicKeyBytes().toByteArray());
   }

   private static void validateParameters(HpkeParameters parameters) throws GeneralSecurityException {
      if (!parameters.getKemId().equals(HpkeParameters.KemId.DHKEM_P256_HKDF_SHA256)) {
         throw new GeneralSecurityException("HpkeHelperForAndroidKeystore currently only supports DHKEM_P256_HKDF_SHA256.");
      } else if (!parameters.getKdfId().equals(HpkeParameters.KdfId.HKDF_SHA256)) {
         throw new GeneralSecurityException("HpkeHelperForAndroidKeystore currently only supports HKDF_SHA256.");
      } else if (!parameters.getAeadId().equals(HpkeParameters.AeadId.AES_128_GCM) && !parameters.getAeadId().equals(HpkeParameters.AeadId.AES_256_GCM)) {
         throw new GeneralSecurityException("HpkeHelperForAndroidKeystore currently only supports AES_128_GCM and AES_256_GCM.");
      } else if (!parameters.getVariant().equals(HpkeParameters.Variant.NO_PREFIX)) {
         throw new GeneralSecurityException("HpkeHelperForAndroidKeystore currently only supports Variant.NO_PREFIX");
      }
   }

   public byte[] decryptUnauthenticatedWithEncapsulatedKeyAndP256SharedSecret(
      byte[] encapsulatedKey, byte[] dhSharedSecret, byte[] ciphertext, int ciphertextOffset, byte[] contextInfo
   ) throws GeneralSecurityException {
      byte[] info = contextInfo;
      if (contextInfo == null) {
         info = new byte[0];
      }

      byte[] sharedSecret = NistCurvesHpkeKem.fromCurve(EllipticCurves.CurveType.NIST_P256)
         .deriveKemSharedSecret(dhSharedSecret, encapsulatedKey, this.publicKeyByteArray);
      HpkeContext context = HpkeContext.createContext(HpkeUtil.BASE_MODE, encapsulatedKey, sharedSecret, this.kem, this.kdf, this.aead, info);
      return context.open(ciphertext, ciphertextOffset, EMPTY_ASSOCIATED_DATA);
   }
}
