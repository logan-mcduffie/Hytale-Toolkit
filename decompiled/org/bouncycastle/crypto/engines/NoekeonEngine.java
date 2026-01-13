package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

public class NoekeonEngine implements BlockCipher {
   private static final int SIZE = 16;
   private static final byte[] roundConstants = new byte[]{-128, 27, 54, 108, -40, -85, 77, -102, 47, 94, -68, 99, -58, -105, 53, 106, -44};
   private final int[] k = new int[4];
   private boolean _initialised = false;
   private boolean _forEncryption;

   @Override
   public String getAlgorithmName() {
      return "Noekeon";
   }

   @Override
   public int getBlockSize() {
      return 16;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (!(var2 instanceof KeyParameter)) {
         throw new IllegalArgumentException("invalid parameter passed to Noekeon init - " + var2.getClass().getName());
      } else {
         KeyParameter var3 = (KeyParameter)var2;
         byte[] var4 = var3.getKey();
         if (var4.length != 16) {
            throw new IllegalArgumentException("Key length not 128 bits.");
         } else {
            Pack.bigEndianToInt(var4, 0, this.k, 0, 4);
            if (!var1) {
               int var5 = this.k[0];
               int var6 = this.k[1];
               int var7 = this.k[2];
               int var8 = this.k[3];
               int var9 = var5 ^ var7;
               var9 ^= Integers.rotateLeft(var9, 8) ^ Integers.rotateLeft(var9, 24);
               int var10 = var6 ^ var8;
               var10 ^= Integers.rotateLeft(var10, 8) ^ Integers.rotateLeft(var10, 24);
               var5 ^= var10;
               var6 ^= var9;
               var7 ^= var10;
               var8 ^= var9;
               this.k[0] = var5;
               this.k[1] = var6;
               this.k[2] = var7;
               this.k[3] = var8;
            }

            this._forEncryption = var1;
            this._initialised = true;
            CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 128, var2, Utils.getPurpose(var1)));
         }
      }
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) {
      if (!this._initialised) {
         throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
      } else if (var2 > var1.length - 16) {
         throw new DataLengthException("input buffer too short");
      } else if (var4 > var3.length - 16) {
         throw new OutputLengthException("output buffer too short");
      } else {
         return this._forEncryption ? this.encryptBlock(var1, var2, var3, var4) : this.decryptBlock(var1, var2, var3, var4);
      }
   }

   @Override
   public void reset() {
   }

   private int encryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      int var5 = Pack.bigEndianToInt(var1, var2);
      int var6 = Pack.bigEndianToInt(var1, var2 + 4);
      int var7 = Pack.bigEndianToInt(var1, var2 + 8);
      int var8 = Pack.bigEndianToInt(var1, var2 + 12);
      int var9 = this.k[0];
      int var10 = this.k[1];
      int var11 = this.k[2];
      int var12 = this.k[3];
      int var13 = 0;

      while (true) {
         var5 ^= roundConstants[var13] & 255;
         int var14 = var5 ^ var7;
         var14 ^= Integers.rotateLeft(var14, 8) ^ Integers.rotateLeft(var14, 24);
         var5 ^= var9;
         var6 ^= var10;
         var7 ^= var11;
         var8 ^= var12;
         int var15 = var6 ^ var8;
         var15 ^= Integers.rotateLeft(var15, 8) ^ Integers.rotateLeft(var15, 24);
         var5 ^= var15;
         var6 ^= var14;
         var7 ^= var15;
         var8 ^= var14;
         if (++var13 > 16) {
            Pack.intToBigEndian(var5, var3, var4);
            Pack.intToBigEndian(var6, var3, var4 + 4);
            Pack.intToBigEndian(var7, var3, var4 + 8);
            Pack.intToBigEndian(var8, var3, var4 + 12);
            return 16;
         }

         var6 = Integers.rotateLeft(var6, 1);
         var7 = Integers.rotateLeft(var7, 5);
         var8 = Integers.rotateLeft(var8, 2);
         var6 ^= var8 | var7;
         int var31 = var5 ^ var7 & ~var6;
         var7 = var8 ^ ~var6 ^ var7 ^ var31;
         var6 ^= var31 | var7;
         var5 = var8 ^ var7 & var6;
         var6 = Integers.rotateLeft(var6, 31);
         var7 = Integers.rotateLeft(var7, 27);
         var8 = Integers.rotateLeft(var31, 30);
      }
   }

   private int decryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      int var5 = Pack.bigEndianToInt(var1, var2);
      int var6 = Pack.bigEndianToInt(var1, var2 + 4);
      int var7 = Pack.bigEndianToInt(var1, var2 + 8);
      int var8 = Pack.bigEndianToInt(var1, var2 + 12);
      int var9 = this.k[0];
      int var10 = this.k[1];
      int var11 = this.k[2];
      int var12 = this.k[3];
      int var13 = 16;

      while (true) {
         int var14 = var5 ^ var7;
         var14 ^= Integers.rotateLeft(var14, 8) ^ Integers.rotateLeft(var14, 24);
         var5 ^= var9;
         var6 ^= var10;
         var7 ^= var11;
         var8 ^= var12;
         int var15 = var6 ^ var8;
         var15 ^= Integers.rotateLeft(var15, 8) ^ Integers.rotateLeft(var15, 24);
         var5 ^= var15;
         var6 ^= var14;
         var7 ^= var15;
         var8 ^= var14;
         var5 ^= roundConstants[var13] & 255;
         if (--var13 < 0) {
            Pack.intToBigEndian(var5, var3, var4);
            Pack.intToBigEndian(var6, var3, var4 + 4);
            Pack.intToBigEndian(var7, var3, var4 + 8);
            Pack.intToBigEndian(var8, var3, var4 + 12);
            return 16;
         }

         var6 = Integers.rotateLeft(var6, 1);
         var7 = Integers.rotateLeft(var7, 5);
         var8 = Integers.rotateLeft(var8, 2);
         var6 ^= var8 | var7;
         int var31 = var5 ^ var7 & ~var6;
         var7 = var8 ^ ~var6 ^ var7 ^ var31;
         var6 ^= var31 | var7;
         var5 = var8 ^ var7 & var6;
         var6 = Integers.rotateLeft(var6, 31);
         var7 = Integers.rotateLeft(var7, 27);
         var8 = Integers.rotateLeft(var31, 30);
      }
   }
}
