package com.google.crypto.tink.subtle;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.HybridDecrypt;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.hybrid.EciesPrivateKey;
import com.google.crypto.tink.hybrid.internal.EciesDemHelper;
import com.google.crypto.tink.internal.BigIntegerEncoding;
import com.google.crypto.tink.internal.Util;
import java.security.GeneralSecurityException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.EllipticCurve;
import java.util.Arrays;

public final class EciesAeadHkdfHybridDecrypt implements HybridDecrypt {
   private final ECPrivateKey recipientPrivateKey;
   private final EciesHkdfRecipientKem recipientKem;
   private final String hkdfHmacAlgo;
   private final byte[] hkdfSalt;
   private final EllipticCurves.PointFormatType ecPointFormat;
   private final EciesDemHelper.Dem dem;
   private final byte[] outputPrefix;

   private EciesAeadHkdfHybridDecrypt(
      final ECPrivateKey recipientPrivateKey,
      final byte[] hkdfSalt,
      String hkdfHmacAlgo,
      EllipticCurves.PointFormatType ecPointFormat,
      EciesDemHelper.Dem dem,
      byte[] outputPrefix
   ) {
      this.recipientPrivateKey = recipientPrivateKey;
      this.recipientKem = new EciesHkdfRecipientKem(recipientPrivateKey);
      this.hkdfSalt = hkdfSalt;
      this.hkdfHmacAlgo = hkdfHmacAlgo;
      this.ecPointFormat = ecPointFormat;
      this.dem = dem;
      this.outputPrefix = outputPrefix;
   }

   @AccessesPartialKey
   public static HybridDecrypt create(EciesPrivateKey key) throws GeneralSecurityException {
      EllipticCurves.CurveType curveType = EciesAeadHkdfHybridEncrypt.CURVE_TYPE_CONVERTER.toProtoEnum(key.getParameters().getCurveType());
      ECPrivateKey recipientPrivateKey = EllipticCurves.getEcPrivateKey(
         curveType, BigIntegerEncoding.toBigEndianBytes(key.getNistPrivateKeyValue().getBigInteger(InsecureSecretKeyAccess.get()))
      );
      byte[] hkdfSalt = new byte[0];
      if (key.getParameters().getSalt() != null) {
         hkdfSalt = key.getParameters().getSalt().toByteArray();
      }

      return new EciesAeadHkdfHybridDecrypt(
         recipientPrivateKey,
         hkdfSalt,
         EciesAeadHkdfHybridEncrypt.toHmacAlgo(key.getParameters().getHashType()),
         EciesAeadHkdfHybridEncrypt.POINT_FORMAT_TYPE_CONVERTER.toProtoEnum(key.getParameters().getNistCurvePointFormat()),
         EciesDemHelper.getDem(key.getParameters()),
         key.getOutputPrefix().toByteArray()
      );
   }

   @Override
   public byte[] decrypt(final byte[] ciphertext, final byte[] contextInfo) throws GeneralSecurityException {
      if (!Util.isPrefix(this.outputPrefix, ciphertext)) {
         throw new GeneralSecurityException("Invalid ciphertext (output prefix mismatch)");
      } else {
         int prefixSize = this.outputPrefix.length;
         EllipticCurve curve = this.recipientPrivateKey.getParams().getCurve();
         int headerSize = EllipticCurves.encodingSizeInBytes(curve, this.ecPointFormat);
         if (ciphertext.length < prefixSize + headerSize) {
            throw new GeneralSecurityException("ciphertext too short");
         } else {
            byte[] kemBytes = Arrays.copyOfRange(ciphertext, prefixSize, prefixSize + headerSize);
            byte[] symmetricKey = this.recipientKem
               .generateKey(kemBytes, this.hkdfHmacAlgo, this.hkdfSalt, contextInfo, this.dem.getSymmetricKeySizeInBytes(), this.ecPointFormat);
            return this.dem.decrypt(symmetricKey, ciphertext, prefixSize + headerSize);
         }
      }
   }
}
