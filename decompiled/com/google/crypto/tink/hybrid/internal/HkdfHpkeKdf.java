package com.google.crypto.tink.hybrid.internal;

import com.google.crypto.tink.subtle.EngineFactory;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Immutable
final class HkdfHpkeKdf implements HpkeKdf {
   private final String macAlgorithm;

   HkdfHpkeKdf(String macAlgorithm) {
      this.macAlgorithm = macAlgorithm;
   }

   private byte[] extract(final byte[] ikm, final byte[] salt) throws GeneralSecurityException {
      Mac mac = EngineFactory.MAC.getInstance(this.macAlgorithm);
      if (salt != null && salt.length != 0) {
         mac.init(new SecretKeySpec(salt, this.macAlgorithm));
      } else {
         mac.init(new SecretKeySpec(new byte[mac.getMacLength()], this.macAlgorithm));
      }

      return mac.doFinal(ikm);
   }

   private byte[] expand(final byte[] prk, final byte[] info, int length) throws GeneralSecurityException {
      Mac mac = EngineFactory.MAC.getInstance(this.macAlgorithm);
      if (length > 255 * mac.getMacLength()) {
         throw new GeneralSecurityException("size too large");
      } else {
         byte[] result = new byte[length];
         int ctr = 1;
         int pos = 0;
         mac.init(new SecretKeySpec(prk, this.macAlgorithm));
         byte[] digest = new byte[0];

         while (true) {
            mac.update(digest);
            mac.update(info);
            mac.update((byte)ctr);
            digest = mac.doFinal();
            if (pos + digest.length >= length) {
               System.arraycopy(digest, 0, result, pos, length - pos);
               return result;
            }

            System.arraycopy(digest, 0, result, pos, digest.length);
            pos += digest.length;
            ctr++;
         }
      }
   }

   @Override
   public byte[] labeledExtract(byte[] salt, byte[] ikm, String ikmLabel, byte[] suiteId) throws GeneralSecurityException {
      return this.extract(HpkeUtil.labelIkm(ikmLabel, ikm, suiteId), salt);
   }

   @Override
   public byte[] labeledExpand(byte[] prk, byte[] info, String infoLabel, byte[] suiteId, int length) throws GeneralSecurityException {
      return this.expand(prk, HpkeUtil.labelInfo(infoLabel, info, suiteId, length), length);
   }

   @Override
   public byte[] extractAndExpand(byte[] salt, byte[] ikm, String ikmLabel, byte[] info, String infoLabel, byte[] suiteId, int length) throws GeneralSecurityException {
      byte[] prk = this.extract(HpkeUtil.labelIkm(ikmLabel, ikm, suiteId), salt);
      return this.expand(prk, HpkeUtil.labelInfo(infoLabel, info, suiteId, length), length);
   }

   @Override
   public byte[] getKdfId() throws GeneralSecurityException {
      String var1 = this.macAlgorithm;
      switch (var1) {
         case "HmacSha256":
            return HpkeUtil.HKDF_SHA256_KDF_ID;
         case "HmacSha384":
            return HpkeUtil.HKDF_SHA384_KDF_ID;
         case "HmacSha512":
            return HpkeUtil.HKDF_SHA512_KDF_ID;
         default:
            throw new GeneralSecurityException("Could not determine HPKE KDF ID");
      }
   }

   int getMacLength() throws GeneralSecurityException {
      return Mac.getInstance(this.macAlgorithm).getMacLength();
   }
}
