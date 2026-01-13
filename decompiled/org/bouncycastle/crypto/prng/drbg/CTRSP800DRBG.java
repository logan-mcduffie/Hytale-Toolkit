package org.bouncycastle.crypto.prng.drbg;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;
import org.bouncycastle.util.encoders.Hex;

public class CTRSP800DRBG implements SP80090DRBG {
   private static final long TDEA_RESEED_MAX = 2147483648L;
   private static final long AES_RESEED_MAX = 140737488355328L;
   private static final int TDEA_MAX_BITS_REQUEST = 4096;
   private static final int AES_MAX_BITS_REQUEST = 262144;
   private EntropySource _entropySource;
   private BlockCipher _engine;
   private int _keySizeInBits;
   private int _seedLength;
   private int _securityStrength;
   private byte[] _Key;
   private byte[] _V;
   private long _reseedCounter = 0L;
   private boolean _isTDEA = false;
   private static final byte[] K_BITS = Hex.decodeStrict("000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F");

   public CTRSP800DRBG(BlockCipher var1, int var2, int var3, EntropySource var4, byte[] var5, byte[] var6) {
      this._entropySource = var4;
      this._engine = var1;
      this._keySizeInBits = var2;
      this._securityStrength = var3;
      this._seedLength = var2 + var1.getBlockSize() * 8;
      this._isTDEA = this.isTDEA(var1);
      if (var3 > 256) {
         throw new IllegalArgumentException("Requested security strength is not supported by the derivation function");
      } else if (this.getMaxSecurityStrength(var1, var2) < var3) {
         throw new IllegalArgumentException("Requested security strength is not supported by block cipher and key size");
      } else if (var4.entropySize() < var3) {
         throw new IllegalArgumentException("Not enough entropy for security strength required");
      } else {
         byte[] var7 = this.getEntropy();
         this.CTR_DRBG_Instantiate_algorithm(var7, var6, var5);
      }
   }

   private void CTR_DRBG_Instantiate_algorithm(byte[] var1, byte[] var2, byte[] var3) {
      byte[] var4 = Arrays.concatenate(var1, var2, var3);
      byte[] var5 = this.Block_Cipher_df(var4, this._seedLength);
      int var6 = this._engine.getBlockSize();
      this._Key = new byte[(this._keySizeInBits + 7) / 8];
      this._V = new byte[var6];
      this.CTR_DRBG_Update(var5, this._Key, this._V);
      this._reseedCounter = 1L;
   }

   private void CTR_DRBG_Update(byte[] var1, byte[] var2, byte[] var3) {
      byte[] var4 = new byte[var1.length];
      byte[] var5 = new byte[this._engine.getBlockSize()];
      int var6 = 0;
      int var7 = this._engine.getBlockSize();
      this._engine.init(true, new KeyParameter(this.expandKey(var2)));

      while (var6 * var7 < var1.length) {
         this.addOneTo(var3);
         this._engine.processBlock(var3, 0, var5, 0);
         int var8 = var4.length - var6 * var7 > var7 ? var7 : var4.length - var6 * var7;
         System.arraycopy(var5, 0, var4, var6 * var7, var8);
         var6++;
      }

      Bytes.xorTo(var1.length, var1, var4);
      System.arraycopy(var4, 0, var2, 0, var2.length);
      System.arraycopy(var4, var2.length, var3, 0, var3.length);
   }

   private void CTR_DRBG_Reseed_algorithm(byte[] var1) {
      byte[] var2 = Arrays.concatenate(this.getEntropy(), var1);
      var2 = this.Block_Cipher_df(var2, this._seedLength);
      this.CTR_DRBG_Update(var2, this._Key, this._V);
      this._reseedCounter = 1L;
   }

   private void addOneTo(byte[] var1) {
      int var2 = 1;

      for (int var3 = 1; var3 <= var1.length; var3++) {
         int var4 = (var1[var1.length - var3] & 255) + var2;
         var2 = var4 > 255 ? 1 : 0;
         var1[var1.length - var3] = (byte)var4;
      }
   }

   private byte[] getEntropy() {
      byte[] var1 = this._entropySource.getEntropy();
      if (var1.length < (this._securityStrength + 7) / 8) {
         throw new IllegalStateException("Insufficient entropy provided by entropy source");
      } else {
         return var1;
      }
   }

   private byte[] Block_Cipher_df(byte[] var1, int var2) {
      int var3 = this._engine.getBlockSize();
      int var4 = var1.length;
      int var5 = var2 / 8;
      int var6 = 8 + var4 + 1;
      int var7 = (var6 + var3 - 1) / var3 * var3;
      byte[] var8 = new byte[var7];
      this.copyIntToByteArray(var8, var4, 0);
      this.copyIntToByteArray(var8, var5, 4);
      System.arraycopy(var1, 0, var8, 8, var4);
      var8[8 + var4] = -128;
      byte[] var9 = new byte[this._keySizeInBits / 8 + var3];
      byte[] var10 = new byte[var3];
      byte[] var11 = new byte[var3];
      int var12 = 0;
      byte[] var13 = new byte[this._keySizeInBits / 8];
      System.arraycopy(K_BITS, 0, var13, 0, var13.length);

      while (var12 * var3 * 8 < this._keySizeInBits + var3 * 8) {
         this.copyIntToByteArray(var11, var12, 0);
         this.BCC(var10, var13, var11, var8);
         int var14 = var9.length - var12 * var3 > var3 ? var3 : var9.length - var12 * var3;
         System.arraycopy(var10, 0, var9, var12 * var3, var14);
         var12++;
      }

      byte[] var18 = new byte[var3];
      System.arraycopy(var9, 0, var13, 0, var13.length);
      System.arraycopy(var9, var13.length, var18, 0, var18.length);
      var9 = new byte[var2 / 8];
      var12 = 0;
      this._engine.init(true, new KeyParameter(this.expandKey(var13)));

      while (var12 * var3 < var9.length) {
         this._engine.processBlock(var18, 0, var18, 0);
         int var15 = var9.length - var12 * var3 > var3 ? var3 : var9.length - var12 * var3;
         System.arraycopy(var18, 0, var9, var12 * var3, var15);
         var12++;
      }

      return var9;
   }

   private void BCC(byte[] var1, byte[] var2, byte[] var3, byte[] var4) {
      int var5 = this._engine.getBlockSize();
      byte[] var6 = new byte[var5];
      int var7 = var4.length / var5;
      byte[] var8 = new byte[var5];
      this._engine.init(true, new KeyParameter(this.expandKey(var2)));
      this._engine.processBlock(var3, 0, var6, 0);

      for (int var9 = 0; var9 < var7; var9++) {
         Bytes.xor(var5, var6, 0, var4, var9 * var5, var8, 0);
         this._engine.processBlock(var8, 0, var6, 0);
      }

      System.arraycopy(var6, 0, var1, 0, var1.length);
   }

   private void copyIntToByteArray(byte[] var1, int var2, int var3) {
      var1[var3 + 0] = (byte)(var2 >> 24);
      var1[var3 + 1] = (byte)(var2 >> 16);
      var1[var3 + 2] = (byte)(var2 >> 8);
      var1[var3 + 3] = (byte)var2;
   }

   @Override
   public int getBlockSize() {
      return this._V.length * 8;
   }

   @Override
   public int generate(byte[] var1, byte[] var2, boolean var3) {
      if (this._isTDEA) {
         if (this._reseedCounter > 2147483648L) {
            return -1;
         }

         if (Utils.isTooLarge(var1, 512)) {
            throw new IllegalArgumentException("Number of bits per request limited to 4096");
         }
      } else {
         if (this._reseedCounter > 140737488355328L) {
            return -1;
         }

         if (Utils.isTooLarge(var1, 32768)) {
            throw new IllegalArgumentException("Number of bits per request limited to 262144");
         }
      }

      if (var3) {
         this.CTR_DRBG_Reseed_algorithm(var2);
         var2 = null;
      }

      if (var2 != null) {
         var2 = this.Block_Cipher_df(var2, this._seedLength);
         this.CTR_DRBG_Update(var2, this._Key, this._V);
      } else {
         var2 = new byte[this._seedLength / 8];
      }

      byte[] var4 = new byte[this._V.length];
      this._engine.init(true, new KeyParameter(this.expandKey(this._Key)));

      for (int var5 = 0; var5 <= var1.length / var4.length; var5++) {
         int var6 = var1.length - var5 * var4.length > var4.length ? var4.length : var1.length - var5 * this._V.length;
         if (var6 != 0) {
            this.addOneTo(this._V);
            this._engine.processBlock(this._V, 0, var4, 0);
            System.arraycopy(var4, 0, var1, var5 * var4.length, var6);
         }
      }

      this.CTR_DRBG_Update(var2, this._Key, this._V);
      this._reseedCounter++;
      return var1.length * 8;
   }

   @Override
   public void reseed(byte[] var1) {
      this.CTR_DRBG_Reseed_algorithm(var1);
   }

   private boolean isTDEA(BlockCipher var1) {
      return var1.getAlgorithmName().equals("DESede") || var1.getAlgorithmName().equals("TDEA");
   }

   private int getMaxSecurityStrength(BlockCipher var1, int var2) {
      if (this.isTDEA(var1) && var2 == 168) {
         return 112;
      } else {
         return var1.getAlgorithmName().equals("AES") ? var2 : -1;
      }
   }

   byte[] expandKey(byte[] var1) {
      if (this._isTDEA) {
         byte[] var2 = new byte[24];
         this.padKey(var1, 0, var2, 0);
         this.padKey(var1, 7, var2, 8);
         this.padKey(var1, 14, var2, 16);
         return var2;
      } else {
         return var1;
      }
   }

   private void padKey(byte[] var1, int var2, byte[] var3, int var4) {
      var3[var4 + 0] = (byte)(var1[var2 + 0] & 254);
      var3[var4 + 1] = (byte)(var1[var2 + 0] << 7 | (var1[var2 + 1] & 252) >>> 1);
      var3[var4 + 2] = (byte)(var1[var2 + 1] << 6 | (var1[var2 + 2] & 248) >>> 2);
      var3[var4 + 3] = (byte)(var1[var2 + 2] << 5 | (var1[var2 + 3] & 240) >>> 3);
      var3[var4 + 4] = (byte)(var1[var2 + 3] << 4 | (var1[var2 + 4] & 224) >>> 4);
      var3[var4 + 5] = (byte)(var1[var2 + 4] << 3 | (var1[var2 + 5] & 192) >>> 5);
      var3[var4 + 6] = (byte)(var1[var2 + 5] << 2 | (var1[var2 + 6] & 128) >>> 6);
      var3[var4 + 7] = (byte)(var1[var2 + 6] << 1);

      for (int var5 = var4; var5 <= var4 + 7; var5++) {
         byte var6 = var3[var5];
         var3[var5] = (byte)(var6 & 254 | (var6 >> 1 ^ var6 >> 2 ^ var6 >> 3 ^ var6 >> 4 ^ var6 >> 5 ^ var6 >> 6 ^ var6 >> 7 ^ 1) & 1);
      }
   }
}
