package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class HC128Engine implements StreamCipher {
   private int[] p = new int[512];
   private int[] q = new int[512];
   private int cnt = 0;
   private byte[] key;
   private byte[] iv;
   private boolean initialised;
   private byte[] buf = new byte[4];
   private int idx = 0;

   private static int f1(int var0) {
      return rotateRight(var0, 7) ^ rotateRight(var0, 18) ^ var0 >>> 3;
   }

   private static int f2(int var0) {
      return rotateRight(var0, 17) ^ rotateRight(var0, 19) ^ var0 >>> 10;
   }

   private int g1(int var1, int var2, int var3) {
      return (rotateRight(var1, 10) ^ rotateRight(var3, 23)) + rotateRight(var2, 8);
   }

   private int g2(int var1, int var2, int var3) {
      return (rotateLeft(var1, 10) ^ rotateLeft(var3, 23)) + rotateLeft(var2, 8);
   }

   private static int rotateLeft(int var0, int var1) {
      return var0 << var1 | var0 >>> -var1;
   }

   private static int rotateRight(int var0, int var1) {
      return var0 >>> var1 | var0 << -var1;
   }

   private int h1(int var1) {
      return this.q[var1 & 0xFF] + this.q[(var1 >> 16 & 0xFF) + 256];
   }

   private int h2(int var1) {
      return this.p[var1 & 0xFF] + this.p[(var1 >> 16 & 0xFF) + 256];
   }

   private static int mod1024(int var0) {
      return var0 & 1023;
   }

   private static int mod512(int var0) {
      return var0 & 511;
   }

   private static int dim(int var0, int var1) {
      return mod512(var0 - var1);
   }

   private int step() {
      int var1 = mod512(this.cnt);
      int var2;
      if (this.cnt < 512) {
         this.p[var1] = this.p[var1] + this.g1(this.p[dim(var1, 3)], this.p[dim(var1, 10)], this.p[dim(var1, 511)]);
         var2 = this.h1(this.p[dim(var1, 12)]) ^ this.p[var1];
      } else {
         this.q[var1] = this.q[var1] + this.g2(this.q[dim(var1, 3)], this.q[dim(var1, 10)], this.q[dim(var1, 511)]);
         var2 = this.h2(this.q[dim(var1, 12)]) ^ this.q[var1];
      }

      this.cnt = mod1024(this.cnt + 1);
      return var2;
   }

   private void init() {
      if (this.key.length != 16) {
         throw new IllegalArgumentException("The key must be 128 bits long");
      } else if (this.iv.length != 16) {
         throw new IllegalArgumentException("The IV must be 128 bits long");
      } else {
         this.idx = 0;
         this.cnt = 0;
         int[] var1 = new int[1280];

         for (int var2 = 0; var2 < 16; var2++) {
            var1[var2 >> 2] = var1[var2 >> 2] | (this.key[var2] & 255) << 8 * (var2 & 3);
         }

         System.arraycopy(var1, 0, var1, 4, 4);

         for (int var3 = 0; var3 < this.iv.length && var3 < 16; var3++) {
            var1[(var3 >> 2) + 8] = var1[(var3 >> 2) + 8] | (this.iv[var3] & 255) << 8 * (var3 & 3);
         }

         System.arraycopy(var1, 8, var1, 12, 4);

         for (int var4 = 16; var4 < 1280; var4++) {
            var1[var4] = f2(var1[var4 - 2]) + var1[var4 - 7] + f1(var1[var4 - 15]) + var1[var4 - 16] + var4;
         }

         System.arraycopy(var1, 256, this.p, 0, 512);
         System.arraycopy(var1, 768, this.q, 0, 512);

         for (int var5 = 0; var5 < 512; var5++) {
            this.p[var5] = this.step();
         }

         for (int var6 = 0; var6 < 512; var6++) {
            this.q[var6] = this.step();
         }

         this.cnt = 0;
      }
   }

   @Override
   public String getAlgorithmName() {
      return "HC-128";
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      if (var2 instanceof ParametersWithIV) {
         this.iv = ((ParametersWithIV)var2).getIV();
         CipherParameters var3 = ((ParametersWithIV)var2).getParameters();
         if (var3 instanceof KeyParameter) {
            this.key = ((KeyParameter)var3).getKey();
            this.init();
            CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 128, var2, Utils.getPurpose(var1)));
            this.initialised = true;
         } else {
            throw new IllegalArgumentException("Invalid parameter passed to HC128 init - " + var2.getClass().getName());
         }
      } else {
         throw new IllegalArgumentException("no IV passed");
      }
   }

   private byte getByte() {
      if (this.idx == 0) {
         int var1 = this.step();
         this.buf[0] = (byte)(var1 & 0xFF);
         var1 >>= 8;
         this.buf[1] = (byte)(var1 & 0xFF);
         var1 >>= 8;
         this.buf[2] = (byte)(var1 & 0xFF);
         var1 >>= 8;
         this.buf[3] = (byte)(var1 & 0xFF);
      }

      byte var5 = this.buf[this.idx];
      this.idx = this.idx + 1 & 3;
      return var5;
   }

   @Override
   public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException {
      if (!this.initialised) {
         throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
      } else if (var2 + var3 > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (var5 + var3 > var4.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         for (int var6 = 0; var6 < var3; var6++) {
            var4[var5 + var6] = (byte)(var1[var2 + var6] ^ this.getByte());
         }

         return var3;
      }
   }

   @Override
   public void reset() {
      this.init();
   }

   @Override
   public byte returnByte(byte var1) {
      return (byte)(var1 ^ this.getByte());
   }
}
