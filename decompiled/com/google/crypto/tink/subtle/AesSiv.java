package com.google.crypto.tink.subtle;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.DeterministicAead;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.daead.AesSivKey;
import com.google.crypto.tink.daead.subtle.DeterministicAeads;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.mac.internal.AesUtil;
import com.google.crypto.tink.prf.AesCmacPrfKey;
import com.google.crypto.tink.prf.AesCmacPrfParameters;
import com.google.crypto.tink.prf.Prf;
import com.google.crypto.tink.util.SecretBytes;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.Arrays;
import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class AesSiv implements DeterministicAead, DeterministicAeads {
   public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS;
   private static final byte[] blockZero = new byte[16];
   private static final byte[] blockOne = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
   private static final int MAX_NUM_ASSOCIATED_DATA = 126;
   private final Prf cmacForS2V;
   private final byte[] aesCtrKey;
   private final byte[] outputPrefix;
   private static final ThreadLocal<Cipher> localAesCtrCipher = new ThreadLocal<Cipher>() {
      protected Cipher initialValue() {
         try {
            return EngineFactory.CIPHER.getInstance("AES/CTR/NoPadding");
         } catch (GeneralSecurityException var2) {
            throw new IllegalStateException(var2);
         }
      }
   };

   @AccessesPartialKey
   public static DeterministicAeads create(AesSivKey key) throws GeneralSecurityException {
      return new AesSiv(key.getKeyBytes().toByteArray(InsecureSecretKeyAccess.get()), key.getOutputPrefix());
   }

   @AccessesPartialKey
   private static Prf createCmac(byte[] key) throws GeneralSecurityException {
      return PrfAesCmac.create(AesCmacPrfKey.create(AesCmacPrfParameters.create(key.length), SecretBytes.copyFrom(key, InsecureSecretKeyAccess.get())));
   }

   private AesSiv(final byte[] key, com.google.crypto.tink.util.Bytes outputPrefix) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use AES-SIV in FIPS-mode.");
      } else if (key.length != 32 && key.length != 64) {
         throw new InvalidKeyException("invalid key size: " + key.length + " bytes; key must have 32 or 64 bytes");
      } else {
         byte[] k1 = Arrays.copyOfRange(key, 0, key.length / 2);
         this.aesCtrKey = Arrays.copyOfRange(key, key.length / 2, key.length);
         this.cmacForS2V = createCmac(k1);
         this.outputPrefix = outputPrefix.toByteArray();
      }
   }

   public AesSiv(final byte[] key) throws GeneralSecurityException {
      this(key, com.google.crypto.tink.util.Bytes.copyFrom(new byte[0]));
   }

   private byte[] s2v(final byte[]... s) throws GeneralSecurityException {
      if (s.length == 0) {
         return this.cmacForS2V.compute(blockOne, 16);
      } else {
         byte[] result = this.cmacForS2V.compute(blockZero, 16);

         for (int i = 0; i < s.length - 1; i++) {
            byte[] currBlock;
            if (s[i] == null) {
               currBlock = new byte[0];
            } else {
               currBlock = s[i];
            }

            result = Bytes.xor(AesUtil.dbl(result), this.cmacForS2V.compute(currBlock, 16));
         }

         byte[] lastBlock = s[s.length - 1];
         if (lastBlock.length >= 16) {
            result = Bytes.xorEnd(lastBlock, result);
         } else {
            result = Bytes.xor(AesUtil.cmacPad(lastBlock), AesUtil.dbl(result));
         }

         return this.cmacForS2V.compute(result, 16);
      }
   }

   private void validateAssociatedDataLength(final int associatedDataLength) throws GeneralSecurityException {
      if (associatedDataLength > 126) {
         throw new GeneralSecurityException("Too many associated datas: " + associatedDataLength + " > " + 126);
      }
   }

   private byte[] encryptInternal(final byte[] plaintext, final byte[]... associatedDatas) throws GeneralSecurityException {
      this.validateAssociatedDataLength(associatedDatas.length);
      if (plaintext.length > Integer.MAX_VALUE - this.outputPrefix.length - 16) {
         throw new GeneralSecurityException("plaintext too long");
      } else {
         Cipher aesCtr = localAesCtrCipher.get();
         byte[][] s = Arrays.copyOf(associatedDatas, associatedDatas.length + 1);
         s[associatedDatas.length] = plaintext;
         byte[] computedIv = this.s2v(s);
         byte[] ivForJavaCrypto = (byte[])computedIv.clone();
         ivForJavaCrypto[8] = (byte)(ivForJavaCrypto[8] & 127);
         ivForJavaCrypto[12] = (byte)(ivForJavaCrypto[12] & 127);
         aesCtr.init(1, new SecretKeySpec(this.aesCtrKey, "AES"), new IvParameterSpec(ivForJavaCrypto));
         int outputSize = this.outputPrefix.length + computedIv.length + plaintext.length;
         byte[] output = Arrays.copyOf(this.outputPrefix, outputSize);
         System.arraycopy(computedIv, 0, output, this.outputPrefix.length, computedIv.length);
         int written = aesCtr.doFinal(plaintext, 0, plaintext.length, output, this.outputPrefix.length + computedIv.length);
         if (written != plaintext.length) {
            throw new GeneralSecurityException("not enough data written");
         } else {
            return output;
         }
      }
   }

   @Override
   public byte[] encryptDeterministicallyWithAssociatedDatas(final byte[] plaintext, final byte[]... associatedDatas) throws GeneralSecurityException {
      return this.encryptInternal(plaintext, associatedDatas);
   }

   @Override
   public byte[] encryptDeterministically(final byte[] plaintext, final byte[] associatedData) throws GeneralSecurityException {
      return this.encryptInternal(plaintext, associatedData);
   }

   private byte[] decryptInternal(final byte[] ciphertext, final byte[]... associatedDatas) throws GeneralSecurityException {
      this.validateAssociatedDataLength(associatedDatas.length);
      if (ciphertext.length < 16 + this.outputPrefix.length) {
         throw new GeneralSecurityException("Ciphertext too short.");
      } else if (!Util.isPrefix(this.outputPrefix, ciphertext)) {
         throw new GeneralSecurityException("Decryption failed (OutputPrefix mismatch).");
      } else {
         Cipher aesCtr = localAesCtrCipher.get();
         byte[] expectedIv = Arrays.copyOfRange(ciphertext, this.outputPrefix.length, 16 + this.outputPrefix.length);
         byte[] ivForJavaCrypto = (byte[])expectedIv.clone();
         ivForJavaCrypto[8] = (byte)(ivForJavaCrypto[8] & 127);
         ivForJavaCrypto[12] = (byte)(ivForJavaCrypto[12] & 127);
         aesCtr.init(2, new SecretKeySpec(this.aesCtrKey, "AES"), new IvParameterSpec(ivForJavaCrypto));
         int offset = 16 + this.outputPrefix.length;
         int ctrCiphertextLen = ciphertext.length - offset;
         byte[] decryptedPt = aesCtr.doFinal(ciphertext, offset, ctrCiphertextLen);
         if (ctrCiphertextLen == 0 && decryptedPt == null && SubtleUtil.isAndroid()) {
            decryptedPt = new byte[0];
         }

         byte[][] s = Arrays.copyOf(associatedDatas, associatedDatas.length + 1);
         s[associatedDatas.length] = decryptedPt;
         byte[] computedIv = this.s2v(s);
         if (Bytes.equal(expectedIv, computedIv)) {
            return decryptedPt;
         } else {
            throw new AEADBadTagException("Integrity check failed.");
         }
      }
   }

   @Override
   public byte[] decryptDeterministicallyWithAssociatedDatas(final byte[] ciphertext, final byte[]... associatedDatas) throws GeneralSecurityException {
      return this.decryptInternal(ciphertext, associatedDatas);
   }

   @Override
   public byte[] decryptDeterministically(final byte[] ciphertext, final byte[] associatedData) throws GeneralSecurityException {
      return this.decryptInternal(ciphertext, associatedData);
   }
}
