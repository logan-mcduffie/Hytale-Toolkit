package org.bouncycastle.pqc.jcajce.provider.util;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

public abstract class CipherSpiExt extends CipherSpi {
   public static final int ENCRYPT_MODE = 1;
   public static final int DECRYPT_MODE = 2;
   protected int opMode;

   @Override
   protected final void engineInit(int var1, Key var2, SecureRandom var3) throws InvalidKeyException {
      try {
         this.engineInit(var1, var2, (AlgorithmParameterSpec)null, var3);
      } catch (InvalidAlgorithmParameterException var5) {
         throw new InvalidParameterException(var5.getMessage());
      }
   }

   @Override
   protected final void engineInit(int var1, Key var2, AlgorithmParameters var3, SecureRandom var4) throws InvalidKeyException, InvalidAlgorithmParameterException {
      if (var3 == null) {
         this.engineInit(var1, var2, var4);
      } else {
         Object var5 = null;
         this.engineInit(var1, var2, (AlgorithmParameterSpec)var5, var4);
      }
   }

   @Override
   protected void engineInit(int var1, Key var2, AlgorithmParameterSpec var3, SecureRandom var4) throws InvalidKeyException, InvalidAlgorithmParameterException {
      if (var3 != null && !(var3 instanceof AlgorithmParameterSpec)) {
         throw new InvalidAlgorithmParameterException();
      } else if (var2 != null && var2 instanceof Key) {
         this.opMode = var1;
         if (var1 == 1) {
            this.initEncrypt(var2, var3, var4);
         } else if (var1 == 2) {
            this.initDecrypt(var2, var3);
         }
      } else {
         throw new InvalidKeyException();
      }
   }

   @Override
   protected final byte[] engineDoFinal(byte[] var1, int var2, int var3) throws IllegalBlockSizeException, BadPaddingException {
      return this.doFinal(var1, var2, var3);
   }

   @Override
   protected final int engineDoFinal(byte[] var1, int var2, int var3, byte[] var4, int var5) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
      return this.doFinal(var1, var2, var3, var4, var5);
   }

   @Override
   protected final int engineGetBlockSize() {
      return this.getBlockSize();
   }

   @Override
   protected final int engineGetKeySize(Key var1) throws InvalidKeyException {
      if (!(var1 instanceof Key)) {
         throw new InvalidKeyException("Unsupported key.");
      } else {
         return this.getKeySize(var1);
      }
   }

   @Override
   protected final byte[] engineGetIV() {
      return this.getIV();
   }

   @Override
   protected final int engineGetOutputSize(int var1) {
      return this.getOutputSize(var1);
   }

   @Override
   protected final AlgorithmParameters engineGetParameters() {
      return null;
   }

   @Override
   protected final void engineSetMode(String var1) throws NoSuchAlgorithmException {
      this.setMode(var1);
   }

   @Override
   protected final void engineSetPadding(String var1) throws NoSuchPaddingException {
      this.setPadding(var1);
   }

   @Override
   protected final byte[] engineUpdate(byte[] var1, int var2, int var3) {
      return this.update(var1, var2, var3);
   }

   @Override
   protected final int engineUpdate(byte[] var1, int var2, int var3, byte[] var4, int var5) throws ShortBufferException {
      return this.update(var1, var2, var3, var4, var5);
   }

   public abstract void initEncrypt(Key var1, AlgorithmParameterSpec var2, SecureRandom var3) throws InvalidKeyException, InvalidAlgorithmParameterException;

   public abstract void initDecrypt(Key var1, AlgorithmParameterSpec var2) throws InvalidKeyException, InvalidAlgorithmParameterException;

   public abstract String getName();

   public abstract int getBlockSize();

   public abstract int getOutputSize(int var1);

   public abstract int getKeySize(Key var1) throws InvalidKeyException;

   public abstract AlgorithmParameterSpec getParameters();

   public abstract byte[] getIV();

   protected abstract void setMode(String var1) throws NoSuchAlgorithmException;

   protected abstract void setPadding(String var1) throws NoSuchPaddingException;

   public final byte[] update(byte[] var1) {
      return this.update(var1, 0, var1.length);
   }

   public abstract byte[] update(byte[] var1, int var2, int var3);

   public abstract int update(byte[] var1, int var2, int var3, byte[] var4, int var5) throws ShortBufferException;

   public final byte[] doFinal() throws IllegalBlockSizeException, BadPaddingException {
      return this.doFinal(null, 0, 0);
   }

   public final byte[] doFinal(byte[] var1) throws IllegalBlockSizeException, BadPaddingException {
      return this.doFinal(var1, 0, var1.length);
   }

   public abstract byte[] doFinal(byte[] var1, int var2, int var3) throws IllegalBlockSizeException, BadPaddingException;

   public abstract int doFinal(byte[] var1, int var2, int var3, byte[] var4, int var5) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException;
}
