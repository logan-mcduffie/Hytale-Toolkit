package com.google.crypto.tink.hybrid.internal;

import com.google.crypto.tink.subtle.Bytes;
import com.google.crypto.tink.subtle.EllipticCurves;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

@Immutable
final class NistCurvesHpkeKem implements HpkeKem {
   private final EllipticCurves.CurveType curve;
   private final HkdfHpkeKdf hkdf;

   static NistCurvesHpkeKem fromCurve(EllipticCurves.CurveType curve) throws GeneralSecurityException {
      switch (curve) {
         case NIST_P256:
            return new NistCurvesHpkeKem(new HkdfHpkeKdf("HmacSha256"), EllipticCurves.CurveType.NIST_P256);
         case NIST_P384:
            return new NistCurvesHpkeKem(new HkdfHpkeKdf("HmacSha384"), EllipticCurves.CurveType.NIST_P384);
         case NIST_P521:
            return new NistCurvesHpkeKem(new HkdfHpkeKdf("HmacSha512"), EllipticCurves.CurveType.NIST_P521);
         default:
            throw new GeneralSecurityException("invalid curve type: " + curve);
      }
   }

   private NistCurvesHpkeKem(HkdfHpkeKdf hkdf, EllipticCurves.CurveType curve) {
      this.hkdf = hkdf;
      this.curve = curve;
   }

   byte[] deriveKemSharedSecret(byte[] dhSharedSecret, byte[] senderEphemeralPublicKey, byte[] recipientPublicKey) throws GeneralSecurityException {
      byte[] kemContext = Bytes.concat(senderEphemeralPublicKey, recipientPublicKey);
      return this.extractAndExpand(dhSharedSecret, kemContext);
   }

   byte[] deriveKemSharedSecret(byte[] dhSharedSecret, byte[] senderEphemeralPublicKey, byte[] recipientPublicKey, byte[] senderPublicKey) throws GeneralSecurityException {
      byte[] kemContext = Bytes.concat(senderEphemeralPublicKey, recipientPublicKey, senderPublicKey);
      return this.extractAndExpand(dhSharedSecret, kemContext);
   }

   private byte[] extractAndExpand(byte[] dhSharedSecret, byte[] kemContext) throws GeneralSecurityException {
      byte[] kemSuiteID = HpkeUtil.kemSuiteId(this.getKemId());
      return this.hkdf.extractAndExpand(null, dhSharedSecret, "eae_prk", kemContext, "shared_secret", kemSuiteID, this.hkdf.getMacLength());
   }

   HpkeKemEncapOutput encapsulate(byte[] recipientPublicKey, KeyPair senderEphemeralKeyPair) throws GeneralSecurityException {
      ECPublicKey recipientECPublicKey = EllipticCurves.getEcPublicKey(this.curve, EllipticCurves.PointFormatType.UNCOMPRESSED, recipientPublicKey);
      byte[] dhSharedSecret = EllipticCurves.computeSharedSecret((ECPrivateKey)senderEphemeralKeyPair.getPrivate(), recipientECPublicKey);
      byte[] senderPublicKey = EllipticCurves.pointEncode(
         this.curve, EllipticCurves.PointFormatType.UNCOMPRESSED, ((ECPublicKey)senderEphemeralKeyPair.getPublic()).getW()
      );
      byte[] kemSharedSecret = this.deriveKemSharedSecret(dhSharedSecret, senderPublicKey, recipientPublicKey);
      return new HpkeKemEncapOutput(kemSharedSecret, senderPublicKey);
   }

   @Override
   public HpkeKemEncapOutput encapsulate(byte[] recipientPublicKey) throws GeneralSecurityException {
      KeyPair keyPair = EllipticCurves.generateKeyPair(this.curve);
      return this.encapsulate(recipientPublicKey, keyPair);
   }

   HpkeKemEncapOutput authEncapsulate(byte[] recipientPublicKey, KeyPair senderEphemeralKeyPair, HpkeKemPrivateKey senderPrivateKey) throws GeneralSecurityException {
      ECPublicKey recipientECPublicKey = EllipticCurves.getEcPublicKey(this.curve, EllipticCurves.PointFormatType.UNCOMPRESSED, recipientPublicKey);
      ECPrivateKey privateKey = EllipticCurves.getEcPrivateKey(this.curve, senderPrivateKey.getSerializedPrivate().toByteArray());
      byte[] dhSharedSecret = Bytes.concat(
         EllipticCurves.computeSharedSecret((ECPrivateKey)senderEphemeralKeyPair.getPrivate(), recipientECPublicKey),
         EllipticCurves.computeSharedSecret(privateKey, recipientECPublicKey)
      );
      byte[] senderEphemeralPublicKey = EllipticCurves.pointEncode(
         this.curve, EllipticCurves.PointFormatType.UNCOMPRESSED, ((ECPublicKey)senderEphemeralKeyPair.getPublic()).getW()
      );
      byte[] kemSharedSecret = this.deriveKemSharedSecret(
         dhSharedSecret, senderEphemeralPublicKey, recipientPublicKey, senderPrivateKey.getSerializedPublic().toByteArray()
      );
      return new HpkeKemEncapOutput(kemSharedSecret, senderEphemeralPublicKey);
   }

   @Override
   public HpkeKemEncapOutput authEncapsulate(byte[] recipientPublicKey, HpkeKemPrivateKey senderPrivateKey) throws GeneralSecurityException {
      KeyPair keyPair = EllipticCurves.generateKeyPair(this.curve);
      return this.authEncapsulate(recipientPublicKey, keyPair, senderPrivateKey);
   }

   @Override
   public byte[] decapsulate(byte[] encapsulatedKey, HpkeKemPrivateKey recipientPrivateKey) throws GeneralSecurityException {
      ECPrivateKey privateKey = EllipticCurves.getEcPrivateKey(this.curve, recipientPrivateKey.getSerializedPrivate().toByteArray());
      ECPublicKey publicKey = EllipticCurves.getEcPublicKey(this.curve, EllipticCurves.PointFormatType.UNCOMPRESSED, encapsulatedKey);
      byte[] dhSharedSecret = EllipticCurves.computeSharedSecret(privateKey, publicKey);
      return this.deriveKemSharedSecret(dhSharedSecret, encapsulatedKey, recipientPrivateKey.getSerializedPublic().toByteArray());
   }

   @Override
   public byte[] authDecapsulate(byte[] encapsulatedKey, HpkeKemPrivateKey recipientPrivateKey, byte[] senderPublicKey) throws GeneralSecurityException {
      ECPrivateKey privateKey = EllipticCurves.getEcPrivateKey(this.curve, recipientPrivateKey.getSerializedPrivate().toByteArray());
      ECPublicKey senderEphemeralPublicKey = EllipticCurves.getEcPublicKey(this.curve, EllipticCurves.PointFormatType.UNCOMPRESSED, encapsulatedKey);
      byte[] dhSharedSecret = Bytes.concat(
         EllipticCurves.computeSharedSecret(privateKey, senderEphemeralPublicKey),
         EllipticCurves.computeSharedSecret(privateKey, EllipticCurves.getEcPublicKey(this.curve, EllipticCurves.PointFormatType.UNCOMPRESSED, senderPublicKey))
      );
      return this.deriveKemSharedSecret(dhSharedSecret, encapsulatedKey, recipientPrivateKey.getSerializedPublic().toByteArray(), senderPublicKey);
   }

   @Override
   public byte[] getKemId() throws GeneralSecurityException {
      switch (this.curve) {
         case NIST_P256:
            return HpkeUtil.P256_HKDF_SHA256_KEM_ID;
         case NIST_P384:
            return HpkeUtil.P384_HKDF_SHA384_KEM_ID;
         case NIST_P521:
            return HpkeUtil.P521_HKDF_SHA512_KEM_ID;
         default:
            throw new GeneralSecurityException("Could not determine HPKE KEM ID");
      }
   }
}
