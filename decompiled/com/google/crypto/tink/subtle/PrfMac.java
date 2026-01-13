package com.google.crypto.tink.subtle;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.Mac;
import com.google.crypto.tink.mac.AesCmacKey;
import com.google.crypto.tink.mac.AesCmacParameters;
import com.google.crypto.tink.mac.HmacKey;
import com.google.crypto.tink.mac.HmacParameters;
import com.google.crypto.tink.prf.AesCmacPrfKey;
import com.google.crypto.tink.prf.AesCmacPrfParameters;
import com.google.crypto.tink.prf.Prf;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Arrays;
import javax.crypto.spec.SecretKeySpec;

@Immutable
@AccessesPartialKey
public class PrfMac implements Mac {
   private static final byte[] formatVersion = new byte[]{0};
   static final int MIN_TAG_SIZE_IN_BYTES = 10;
   private final Prf wrappedPrf;
   private final int tagSize;
   private final byte[] outputPrefix;
   private final byte[] plaintextLegacySuffix;

   public PrfMac(Prf wrappedPrf, int tagSize) throws GeneralSecurityException {
      this.wrappedPrf = wrappedPrf;
      this.tagSize = tagSize;
      this.outputPrefix = new byte[0];
      this.plaintextLegacySuffix = new byte[0];
      if (tagSize < 10) {
         throw new InvalidAlgorithmParameterException("tag size too small, need at least 10 bytes");
      } else {
         Object unused = wrappedPrf.compute(new byte[0], tagSize);
      }
   }

   private PrfMac(AesCmacKey key) throws GeneralSecurityException {
      this.wrappedPrf = createPrf(key);
      this.tagSize = key.getParameters().getCryptographicTagSizeBytes();
      this.outputPrefix = key.getOutputPrefix().toByteArray();
      if (key.getParameters().getVariant().equals(AesCmacParameters.Variant.LEGACY)) {
         this.plaintextLegacySuffix = Arrays.copyOf(formatVersion, formatVersion.length);
      } else {
         this.plaintextLegacySuffix = new byte[0];
      }
   }

   private PrfMac(HmacKey key) throws GeneralSecurityException {
      this.wrappedPrf = new PrfHmacJce(
         "HMAC" + key.getParameters().getHashType(), new SecretKeySpec(key.getKeyBytes().toByteArray(InsecureSecretKeyAccess.get()), "HMAC")
      );
      this.tagSize = key.getParameters().getCryptographicTagSizeBytes();
      this.outputPrefix = key.getOutputPrefix().toByteArray();
      if (key.getParameters().getVariant().equals(HmacParameters.Variant.LEGACY)) {
         this.plaintextLegacySuffix = Arrays.copyOf(formatVersion, formatVersion.length);
      } else {
         this.plaintextLegacySuffix = new byte[0];
      }
   }

   public static Mac create(AesCmacKey key) throws GeneralSecurityException {
      return new PrfMac(key);
   }

   public static Mac create(HmacKey key) throws GeneralSecurityException {
      return new PrfMac(key);
   }

   @Override
   public byte[] computeMac(byte[] data) throws GeneralSecurityException {
      return this.plaintextLegacySuffix.length > 0
         ? Bytes.concat(this.outputPrefix, this.wrappedPrf.compute(Bytes.concat(data, this.plaintextLegacySuffix), this.tagSize))
         : Bytes.concat(this.outputPrefix, this.wrappedPrf.compute(data, this.tagSize));
   }

   @Override
   public void verifyMac(byte[] mac, byte[] data) throws GeneralSecurityException {
      if (!Bytes.equal(this.computeMac(data), mac)) {
         throw new GeneralSecurityException("invalid MAC");
      }
   }

   @AccessesPartialKey
   private static Prf createPrf(AesCmacKey key) throws GeneralSecurityException {
      return PrfAesCmac.create(AesCmacPrfKey.create(AesCmacPrfParameters.create(key.getParameters().getKeySizeBytes()), key.getAesKey()));
   }
}
