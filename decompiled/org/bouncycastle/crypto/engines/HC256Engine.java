package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class HC256Engine implements StreamCipher {
   private int[] p = new int[1024];
   private int[] q = new int[1024];
   private int cnt = 0;
   private byte[] key;
   private byte[] iv;
   private boolean initialised;
   private byte[] buf = new byte[4];
   private int idx = 0;

   private int step() {
      int var1 = this.cnt & 1023;
      int var2;
      if (this.cnt < 1024) {
         int var3 = this.p[var1 - 3 & 1023];
         int var4 = this.p[var1 - 1023 & 1023];
         this.p[var1] = this.p[var1] + this.p[var1 - 10 & 1023] + (rotateRight(var3, 10) ^ rotateRight(var4, 23)) + this.q[(var3 ^ var4) & 1023];
         var3 = this.p[var1 - 12 & 1023];
         var2 = this.q[var3 & 0xFF] + this.q[(var3 >> 8 & 0xFF) + 256] + this.q[(var3 >> 16 & 0xFF) + 512] + this.q[(var3 >> 24 & 0xFF) + 768] ^ this.p[var1];
      } else {
         int var6 = this.q[var1 - 3 & 1023];
         int var8 = this.q[var1 - 1023 & 1023];
         this.q[var1] = this.q[var1] + this.q[var1 - 10 & 1023] + (rotateRight(var6, 10) ^ rotateRight(var8, 23)) + this.p[(var6 ^ var8) & 1023];
         var6 = this.q[var1 - 12 & 1023];
         var2 = this.p[var6 & 0xFF] + this.p[(var6 >> 8 & 0xFF) + 256] + this.p[(var6 >> 16 & 0xFF) + 512] + this.p[(var6 >> 24 & 0xFF) + 768] ^ this.q[var1];
      }

      this.cnt = this.cnt + 1 & 2047;
      return var2;
   }

   private void init() {
      if (this.key.length != 32 && this.key.length != 16) {
         throw new IllegalArgumentException("The key must be 128/256 bits long");
      } else if (this.iv.length < 16) {
         throw new IllegalArgumentException("The IV must be at least 128 bits long");
      } else {
         if (this.key.length != 32) {
            byte[] var1 = new byte[32];
            System.arraycopy(this.key, 0, var1, 0, this.key.length);
            System.arraycopy(this.key, 0, var1, 16, this.key.length);
            this.key = var1;
         }

         if (this.iv.length < 32) {
            byte[] var5 = new byte[32];
            System.arraycopy(this.iv, 0, var5, 0, this.iv.length);
            System.arraycopy(this.iv, 0, var5, this.iv.length, var5.length - this.iv.length);
            this.iv = var5;
         }

         this.idx = 0;
         this.cnt = 0;
         int[] var6 = new int[2560];

         for (int var2 = 0; var2 < 32; var2++) {
            var6[var2 >> 2] = var6[var2 >> 2] | (this.key[var2] & 255) << 8 * (var2 & 3);
         }

         for (int var7 = 0; var7 < 32; var7++) {
            var6[(var7 >> 2) + 8] = var6[(var7 >> 2) + 8] | (this.iv[var7] & 255) << 8 * (var7 & 3);
         }

         for (int var8 = 16; var8 < 2560; var8++) {
            int var3 = var6[var8 - 2];
            int var4 = var6[var8 - 15];
            var6[var8] = (rotateRight(var3, 17) ^ rotateRight(var3, 19) ^ var3 >>> 10)
               + var6[var8 - 7]
               + (rotateRight(var4, 7) ^ rotateRight(var4, 18) ^ var4 >>> 3)
               + var6[var8 - 16]
               + var8;
         }

         System.arraycopy(var6, 512, this.p, 0, 1024);
         System.arraycopy(var6, 1536, this.q, 0, 1024);

         for (int var9 = 0; var9 < 4096; var9++) {
            this.step();
         }

         this.cnt = 0;
      }
   }

   @Override
   public String getAlgorithmName() {
      return "HC-256";
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      CipherParameters var3 = var2;
      if (var2 instanceof ParametersWithIV) {
         this.iv = ((ParametersWithIV)var2).getIV();
         var3 = ((ParametersWithIV)var2).getParameters();
      } else {
         this.iv = new byte[0];
      }

      if (var3 instanceof KeyParameter) {
         this.key = ((KeyParameter)var3).getKey();
         this.init();
         CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), this.key.length * 8, var2, Utils.getPurpose(var1)));
         this.initialised = true;
      } else {
         throw new IllegalArgumentException("Invalid parameter passed to HC256 init - " + var2.getClass().getName());
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

   private static int rotateRight(int var0, int var1) {
      return var0 >>> var1 | var0 << -var1;
   }
}
