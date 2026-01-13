package org.bouncycastle.crypto.encodings;

import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;

public class ISO9796d1Encoding implements AsymmetricBlockCipher {
   private static final BigInteger SIXTEEN = BigInteger.valueOf(16L);
   private static final BigInteger SIX = BigInteger.valueOf(6L);
   private static byte[] shadows = new byte[]{14, 3, 5, 8, 9, 4, 2, 15, 0, 13, 11, 6, 7, 10, 12, 1};
   private static byte[] inverse = new byte[]{8, 15, 6, 1, 5, 2, 11, 12, 3, 4, 13, 10, 14, 9, 0, 7};
   private AsymmetricBlockCipher engine;
   private boolean forEncryption;
   private int bitSize;
   private int padBits = 0;
   private BigInteger modulus;

   public ISO9796d1Encoding(AsymmetricBlockCipher var1) {
      this.engine = var1;
   }

   public AsymmetricBlockCipher getUnderlyingCipher() {
      return this.engine;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      RSAKeyParameters var3 = null;
      if (var2 instanceof ParametersWithRandom) {
         ParametersWithRandom var4 = (ParametersWithRandom)var2;
         var3 = (RSAKeyParameters)var4.getParameters();
      } else {
         var3 = (RSAKeyParameters)var2;
      }

      this.engine.init(var1, var2);
      this.modulus = var3.getModulus();
      this.bitSize = this.modulus.bitLength();
      this.forEncryption = var1;
   }

   @Override
   public int getInputBlockSize() {
      int var1 = this.engine.getInputBlockSize();
      return this.forEncryption ? (var1 + 1) / 2 : var1;
   }

   @Override
   public int getOutputBlockSize() {
      int var1 = this.engine.getOutputBlockSize();
      return this.forEncryption ? var1 : (var1 + 1) / 2;
   }

   public void setPadBits(int var1) {
      if (var1 > 7) {
         throw new IllegalArgumentException("padBits > 7");
      } else {
         this.padBits = var1;
      }
   }

   public int getPadBits() {
      return this.padBits;
   }

   @Override
   public byte[] processBlock(byte[] var1, int var2, int var3) throws InvalidCipherTextException {
      return this.forEncryption ? this.encodeBlock(var1, var2, var3) : this.decodeBlock(var1, var2, var3);
   }

   private byte[] encodeBlock(byte[] var1, int var2, int var3) throws InvalidCipherTextException {
      byte[] var4 = new byte[(this.bitSize + 7) / 8];
      int var5 = this.padBits + 1;
      int var6 = var3;
      int var7 = (this.bitSize + 13) / 16;

      for (int var8 = 0; var8 < var7; var8 += var6) {
         if (var8 > var7 - var6) {
            System.arraycopy(var1, var2 + var3 - (var7 - var8), var4, var4.length - var7, var7 - var8);
         } else {
            System.arraycopy(var1, var2, var4, var4.length - (var8 + var6), var6);
         }
      }

      for (int var10 = var4.length - 2 * var7; var10 != var4.length; var10 += 2) {
         byte var9 = var4[var4.length - var7 + var10 / 2];
         var4[var10] = (byte)(shadows[(var9 & 255) >>> 4] << 4 | shadows[var9 & 15]);
         var4[var10 + 1] = var9;
      }

      var4[var4.length - 2 * var6] = (byte)(var4[var4.length - 2 * var6] ^ var5);
      var4[var4.length - 1] = (byte)(var4[var4.length - 1] << 4 | 6);
      int var11 = 8 - (this.bitSize - 1) % 8;
      byte var12 = 0;
      if (var11 != 8) {
         var4[0] = (byte)(var4[0] & 255 >>> var11);
         var4[0] = (byte)(var4[0] | 128 >>> var11);
      } else {
         var4[0] = 0;
         var4[1] = (byte)(var4[1] | 128);
         var12 = 1;
      }

      return this.engine.processBlock(var4, var12, var4.length - var12);
   }

   private byte[] decodeBlock(byte[] var1, int var2, int var3) throws InvalidCipherTextException {
      byte[] var4 = this.engine.processBlock(var1, var2, var3);
      int var5 = 1;
      int var6 = (this.bitSize + 13) / 16;
      BigInteger var7 = new BigInteger(1, var4);
      BigInteger var8;
      if (var7.mod(SIXTEEN).equals(SIX)) {
         var8 = var7;
      } else {
         if (!this.modulus.subtract(var7).mod(SIXTEEN).equals(SIX)) {
            throw new InvalidCipherTextException("resulting integer iS or (modulus - iS) is not congruent to 6 mod 16");
         }

         var8 = this.modulus.subtract(var7);
      }

      var4 = convertOutputDecryptOnly(var8);
      if ((var4[var4.length - 1] & 15) != 6) {
         throw new InvalidCipherTextException("invalid forcing byte in block");
      } else {
         var4[var4.length - 1] = (byte)((var4[var4.length - 1] & 255) >>> 4 | inverse[(var4[var4.length - 2] & 255) >> 4] << 4);
         var4[0] = (byte)(shadows[(var4[1] & 255) >>> 4] << 4 | shadows[var4[1] & 15]);
         boolean var9 = false;
         int var10 = 0;

         for (int var11 = var4.length - 1; var11 >= var4.length - 2 * var6; var11 -= 2) {
            int var12 = shadows[(var4[var11] & 255) >>> 4] << 4 | shadows[var4[var11] & 15];
            if (((var4[var11 - 1] ^ var12) & 0xFF) != 0) {
               if (var9) {
                  throw new InvalidCipherTextException("invalid tsums in block");
               }

               var9 = true;
               var5 = (var4[var11 - 1] ^ var12) & 0xFF;
               var10 = var11 - 1;
            }
         }

         var4[var10] = 0;
         byte[] var14 = new byte[(var4.length - var10) / 2];

         for (int var15 = 0; var15 < var14.length; var15++) {
            var14[var15] = var4[2 * var15 + var10 + 1];
         }

         this.padBits = var5 - 1;
         return var14;
      }
   }

   private static byte[] convertOutputDecryptOnly(BigInteger var0) {
      byte[] var1 = var0.toByteArray();
      if (var1[0] == 0) {
         byte[] var2 = new byte[var1.length - 1];
         System.arraycopy(var1, 1, var2, 0, var2.length);
         return var2;
      } else {
         return var1;
      }
   }
}
