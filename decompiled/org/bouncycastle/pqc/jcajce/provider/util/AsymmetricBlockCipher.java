package org.bouncycastle.pqc.jcajce.provider.util;

import java.io.ByteArrayOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import org.bouncycastle.crypto.CryptoServicesRegistrar;

public abstract class AsymmetricBlockCipher extends CipherSpiExt {
   protected AlgorithmParameterSpec paramSpec;
   protected ByteArrayOutputStream buf = new ByteArrayOutputStream();
   protected int maxPlainTextSize;
   protected int cipherTextSize;

   @Override
   public final int getBlockSize() {
      return this.opMode == 1 ? this.maxPlainTextSize : this.cipherTextSize;
   }

   @Override
   public final byte[] getIV() {
      return null;
   }

   @Override
   public final int getOutputSize(int var1) {
      int var2 = var1 + this.buf.size();
      int var3 = this.getBlockSize();
      if (var2 > var3) {
         return 0;
      } else {
         return this.opMode == 1 ? this.cipherTextSize : this.maxPlainTextSize;
      }
   }

   @Override
   public final AlgorithmParameterSpec getParameters() {
      return this.paramSpec;
   }

   public final void initEncrypt(Key var1) throws InvalidKeyException {
      try {
         this.initEncrypt(var1, null, CryptoServicesRegistrar.getSecureRandom());
      } catch (InvalidAlgorithmParameterException var3) {
         throw new InvalidParameterException("This cipher needs algorithm parameters for initialization (cannot be null).");
      }
   }

   public final void initEncrypt(Key var1, SecureRandom var2) throws InvalidKeyException {
      try {
         this.initEncrypt(var1, null, var2);
      } catch (InvalidAlgorithmParameterException var4) {
         throw new InvalidParameterException("This cipher needs algorithm parameters for initialization (cannot be null).");
      }
   }

   public final void initEncrypt(Key var1, AlgorithmParameterSpec var2) throws InvalidKeyException, InvalidAlgorithmParameterException {
      this.initEncrypt(var1, var2, CryptoServicesRegistrar.getSecureRandom());
   }

   @Override
   public final void initEncrypt(Key var1, AlgorithmParameterSpec var2, SecureRandom var3) throws InvalidKeyException, InvalidAlgorithmParameterException {
      this.opMode = 1;
      this.initCipherEncrypt(var1, var2, var3);
   }

   public final void initDecrypt(Key var1) throws InvalidKeyException {
      try {
         this.initDecrypt(var1, null);
      } catch (InvalidAlgorithmParameterException var3) {
         throw new InvalidParameterException("This cipher needs algorithm parameters for initialization (cannot be null).");
      }
   }

   @Override
   public final void initDecrypt(Key var1, AlgorithmParameterSpec var2) throws InvalidKeyException, InvalidAlgorithmParameterException {
      this.opMode = 2;
      this.initCipherDecrypt(var1, var2);
   }

   @Override
   public final byte[] update(byte[] var1, int var2, int var3) {
      if (var3 != 0) {
         this.buf.write(var1, var2, var3);
      }

      return new byte[0];
   }

   @Override
   public final int update(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      this.update(var1, var2, var3);
      return 0;
   }

   @Override
   public final byte[] doFinal(byte[] var1, int var2, int var3) throws IllegalBlockSizeException, BadPaddingException {
      this.checkLength(var3);
      this.update(var1, var2, var3);
      byte[] var4 = this.buf.toByteArray();
      this.buf.reset();
      switch (this.opMode) {
         case 1:
            return this.messageEncrypt(var4);
         case 2:
            return this.messageDecrypt(var4);
         default:
            return null;
      }
   }

   @Override
   public final int doFinal(byte[] var1, int var2, int var3, byte[] var4, int var5) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
      if (var4.length < this.getOutputSize(var3)) {
         throw new ShortBufferException("Output buffer too short.");
      } else {
         byte[] var6 = this.doFinal(var1, var2, var3);
         System.arraycopy(var6, 0, var4, var5, var6.length);
         return var6.length;
      }
   }

   @Override
   protected final void setMode(String var1) {
   }

   @Override
   protected final void setPadding(String var1) {
   }

   protected void checkLength(int var1) throws IllegalBlockSizeException {
      int var2 = var1 + this.buf.size();
      if (this.opMode == 1) {
         if (var2 > this.maxPlainTextSize) {
            throw new IllegalBlockSizeException(
               "The length of the plaintext (" + var2 + " bytes) is not supported by the cipher (max. " + this.maxPlainTextSize + " bytes)."
            );
         }
      } else if (this.opMode == 2 && var2 != this.cipherTextSize) {
         throw new IllegalBlockSizeException("Illegal ciphertext length (expected " + this.cipherTextSize + " bytes, was " + var2 + " bytes).");
      }
   }

   protected abstract void initCipherEncrypt(Key var1, AlgorithmParameterSpec var2, SecureRandom var3) throws InvalidKeyException, InvalidAlgorithmParameterException;

   protected abstract void initCipherDecrypt(Key var1, AlgorithmParameterSpec var2) throws InvalidKeyException, InvalidAlgorithmParameterException;

   protected abstract byte[] messageEncrypt(byte[] var1) throws IllegalBlockSizeException, BadPaddingException;

   protected abstract byte[] messageDecrypt(byte[] var1) throws IllegalBlockSizeException, BadPaddingException;
}
