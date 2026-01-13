package com.google.crypto.tink.prf.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.mac.internal.AesUtil;
import com.google.crypto.tink.prf.AesCmacPrfKey;
import com.google.crypto.tink.prf.Prf;
import com.google.crypto.tink.subtle.Bytes;
import com.google.crypto.tink.subtle.EngineFactory;
import com.google.crypto.tink.subtle.Validators;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Immutable
@AccessesPartialKey
public final class PrfAesCmac implements Prf {
   public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS;
   private final SecretKey keySpec;
   private byte[] subKey1;
   private byte[] subKey2;
   private static final ThreadLocal<Cipher> localAesCipher = new ThreadLocal<Cipher>() {
      protected Cipher initialValue() {
         try {
            return EngineFactory.CIPHER.getInstance("AES/ECB/NoPadding");
         } catch (GeneralSecurityException var2) {
            throw new IllegalStateException(var2);
         }
      }
   };

   private static Cipher instance() throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use AES-CMAC in FIPS-mode.");
      } else {
         return localAesCipher.get();
      }
   }

   private PrfAesCmac(final byte[] key) throws GeneralSecurityException {
      Validators.validateAesKeySize(key.length);
      this.keySpec = new SecretKeySpec(key, "AES");
      this.generateSubKeys();
   }

   public static Prf create(AesCmacPrfKey key) throws GeneralSecurityException {
      return new PrfAesCmac(key.getKeyBytes().toByteArray(InsecureSecretKeyAccess.get()));
   }

   static int calcN(int dataLength) {
      return dataLength == 0 ? 1 : (dataLength - 1) / 16 + 1;
   }

   private static void xorBlock(final byte[] x, final byte[] y, int offsetY, byte[] output) {
      for (int i = 0; i < 16; i++) {
         output[i] = (byte)(x[i] ^ y[i + offsetY]);
      }
   }

   @Override
   public byte[] compute(final byte[] data, int outputLength) throws GeneralSecurityException {
      if (outputLength > 16) {
         throw new InvalidAlgorithmParameterException("outputLength too large, max is 16 bytes");
      } else {
         Cipher aes = instance();
         aes.init(1, this.keySpec);
         int n = calcN(data.length);
         boolean flag = n * 16 == data.length;
         byte[] mLast;
         if (flag) {
            mLast = Bytes.xor(data, (n - 1) * 16, this.subKey1, 0, 16);
         } else {
            mLast = Bytes.xor(AesUtil.cmacPad(Arrays.copyOfRange(data, (n - 1) * 16, data.length)), this.subKey2);
         }

         byte[] x = new byte[16];
         byte[] y = new byte[16];

         for (int i = 0; i < n - 1; i++) {
            xorBlock(x, data, i * 16, y);
            int written = aes.doFinal(y, 0, 16, x);
            if (written != 16) {
               throw new IllegalStateException("Cipher didn't write full block");
            }
         }

         xorBlock(x, mLast, 0, y);
         int written = aes.doFinal(y, 0, 16, x);
         if (written != 16) {
            throw new IllegalStateException("Cipher didn't write full block");
         } else {
            return x.length == outputLength ? x : Arrays.copyOf(x, outputLength);
         }
      }
   }

   private void generateSubKeys() throws GeneralSecurityException {
      Cipher aes = instance();
      aes.init(1, this.keySpec);
      byte[] zeroes = new byte[16];
      byte[] l = aes.doFinal(zeroes);
      this.subKey1 = AesUtil.dbl(l);
      this.subKey2 = AesUtil.dbl(this.subKey1);
   }
}
