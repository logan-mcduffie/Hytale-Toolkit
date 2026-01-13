package com.google.crypto.tink.subtle;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.prf.HmacPrfKey;
import com.google.crypto.tink.prf.Prf;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Immutable
@AccessesPartialKey
public final class PrfHmacJce implements Prf {
   public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_REQUIRES_BORINGCRYPTO;
   static final int MIN_KEY_SIZE_IN_BYTES = 16;
   private final ThreadLocal<Mac> localMac = new ThreadLocal<Mac>() {
      protected Mac initialValue() {
         try {
            Mac mac = EngineFactory.MAC.getInstance(PrfHmacJce.this.algorithm);
            mac.init(PrfHmacJce.this.key);
            return mac;
         } catch (GeneralSecurityException var2) {
            throw new IllegalStateException(var2);
         }
      }
   };
   private final String algorithm;
   private final Key key;
   private final int maxOutputLength;

   public PrfHmacJce(String algorithm, Key key) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use HMAC in FIPS-mode, as BoringCrypto module is not available.");
      } else {
         this.algorithm = algorithm;
         this.key = key;
         if (key.getEncoded().length < 16) {
            throw new InvalidAlgorithmParameterException("key size too small, need at least 16 bytes");
         } else {
            switch (algorithm) {
               case "HMACSHA1":
                  this.maxOutputLength = 20;
                  break;
               case "HMACSHA224":
                  this.maxOutputLength = 28;
                  break;
               case "HMACSHA256":
                  this.maxOutputLength = 32;
                  break;
               case "HMACSHA384":
                  this.maxOutputLength = 48;
                  break;
               case "HMACSHA512":
                  this.maxOutputLength = 64;
                  break;
               default:
                  throw new NoSuchAlgorithmException("unknown Hmac algorithm: " + algorithm);
            }

            this.localMac.get();
         }
      }
   }

   public static Prf create(HmacPrfKey key) throws GeneralSecurityException {
      return new PrfHmacJce("HMAC" + key.getParameters().getHashType(), new SecretKeySpec(key.getKeyBytes().toByteArray(InsecureSecretKeyAccess.get()), "HMAC"));
   }

   @Override
   public byte[] compute(byte[] data, int outputLength) throws GeneralSecurityException {
      if (outputLength > this.maxOutputLength) {
         throw new InvalidAlgorithmParameterException("tag size too big");
      } else {
         this.localMac.get().update(data);
         return Arrays.copyOf(this.localMac.get().doFinal(), outputLength);
      }
   }

   public int getMaxOutputLength() {
      return this.maxOutputLength;
   }
}
