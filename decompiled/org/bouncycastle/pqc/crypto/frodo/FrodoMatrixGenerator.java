package org.bouncycastle.pqc.crypto.frodo;

import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

abstract class FrodoMatrixGenerator {
   int n;
   int q;

   public FrodoMatrixGenerator(int var1, int var2) {
      this.n = var1;
      this.q = var2;
   }

   abstract short[] genMatrix(byte[] var1);

   static class Aes128MatrixGenerator extends FrodoMatrixGenerator {
      public Aes128MatrixGenerator(int var1, int var2) {
         super(var1, var2);
      }

      @Override
      short[] genMatrix(byte[] var1) {
         short[] var2 = new short[this.n * this.n];
         byte[] var3 = new byte[16];
         byte[] var4 = new byte[16];
         AESEngine var5 = new AESEngine();
         var5.init(true, new KeyParameter(var1));

         for (int var6 = 0; var6 < this.n; var6++) {
            Pack.shortToLittleEndian((short)var6, var3, 0);

            for (byte var7 = 0; var7 < this.n; var7 += 8) {
               Pack.shortToLittleEndian(var7, var3, 2);
               var5.processBlock(var3, 0, var4, 0);

               for (int var8 = 0; var8 < 8; var8++) {
                  var2[var6 * this.n + var7 + var8] = (short)(Pack.littleEndianToShort(var4, 2 * var8) & this.q - 1);
               }
            }
         }

         return var2;
      }
   }

   static class Shake128MatrixGenerator extends FrodoMatrixGenerator {
      public Shake128MatrixGenerator(int var1, int var2) {
         super(var1, var2);
      }

      @Override
      short[] genMatrix(byte[] var1) {
         short[] var2 = new short[this.n * this.n];
         byte[] var5 = new byte[16 * this.n / 8];
         byte[] var6 = new byte[2 + var1.length];
         System.arraycopy(var1, 0, var6, 2, var1.length);
         SHAKEDigest var7 = new SHAKEDigest(128);

         for (short var3 = 0; var3 < this.n; var3++) {
            Pack.shortToLittleEndian(var3, var6, 0);
            var7.update(var6, 0, var6.length);
            var7.doFinal(var5, 0, var5.length);

            for (short var4 = 0; var4 < this.n; var4++) {
               var2[var3 * this.n + var4] = (short)(Pack.littleEndianToShort(var5, 2 * var4) & this.q - 1);
            }
         }

         return var2;
      }
   }
}
