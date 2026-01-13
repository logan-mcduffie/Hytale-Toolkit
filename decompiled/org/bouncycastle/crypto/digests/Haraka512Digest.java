package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;

public class Haraka512Digest extends HarakaBase {
   private final byte[] buffer;
   private int off;
   private final CryptoServicePurpose purpose;

   public Haraka512Digest() {
      this(CryptoServicePurpose.ANY);
   }

   public Haraka512Digest(CryptoServicePurpose var1) {
      this.purpose = var1;
      this.buffer = new byte[64];
   }

   public Haraka512Digest(Haraka512Digest var1) {
      this.purpose = var1.purpose;
      this.buffer = Arrays.clone(var1.buffer);
      this.off = var1.off;
   }

   private void mix512(byte[][] var1, byte[][] var2) {
      System.arraycopy(var1[0], 12, var2[0], 0, 4);
      System.arraycopy(var1[2], 12, var2[0], 4, 4);
      System.arraycopy(var1[1], 12, var2[0], 8, 4);
      System.arraycopy(var1[3], 12, var2[0], 12, 4);
      System.arraycopy(var1[2], 0, var2[1], 0, 4);
      System.arraycopy(var1[0], 0, var2[1], 4, 4);
      System.arraycopy(var1[3], 0, var2[1], 8, 4);
      System.arraycopy(var1[1], 0, var2[1], 12, 4);
      System.arraycopy(var1[2], 4, var2[2], 0, 4);
      System.arraycopy(var1[0], 4, var2[2], 4, 4);
      System.arraycopy(var1[3], 4, var2[2], 8, 4);
      System.arraycopy(var1[1], 4, var2[2], 12, 4);
      System.arraycopy(var1[0], 8, var2[3], 0, 4);
      System.arraycopy(var1[2], 8, var2[3], 4, 4);
      System.arraycopy(var1[1], 8, var2[3], 8, 4);
      System.arraycopy(var1[3], 8, var2[3], 12, 4);
   }

   private int haraka512256(byte[] var1, byte[] var2, int var3) {
      byte[][] var4 = new byte[4][16];
      byte[][] var5 = new byte[4][16];
      System.arraycopy(var1, 0, var4[0], 0, 16);
      System.arraycopy(var1, 16, var4[1], 0, 16);
      System.arraycopy(var1, 32, var4[2], 0, 16);
      System.arraycopy(var1, 48, var4[3], 0, 16);
      var4[0] = aesEnc(var4[0], RC[0]);
      var4[1] = aesEnc(var4[1], RC[1]);
      var4[2] = aesEnc(var4[2], RC[2]);
      var4[3] = aesEnc(var4[3], RC[3]);
      var4[0] = aesEnc(var4[0], RC[4]);
      var4[1] = aesEnc(var4[1], RC[5]);
      var4[2] = aesEnc(var4[2], RC[6]);
      var4[3] = aesEnc(var4[3], RC[7]);
      this.mix512(var4, var5);
      var4[0] = aesEnc(var5[0], RC[8]);
      var4[1] = aesEnc(var5[1], RC[9]);
      var4[2] = aesEnc(var5[2], RC[10]);
      var4[3] = aesEnc(var5[3], RC[11]);
      var4[0] = aesEnc(var4[0], RC[12]);
      var4[1] = aesEnc(var4[1], RC[13]);
      var4[2] = aesEnc(var4[2], RC[14]);
      var4[3] = aesEnc(var4[3], RC[15]);
      this.mix512(var4, var5);
      var4[0] = aesEnc(var5[0], RC[16]);
      var4[1] = aesEnc(var5[1], RC[17]);
      var4[2] = aesEnc(var5[2], RC[18]);
      var4[3] = aesEnc(var5[3], RC[19]);
      var4[0] = aesEnc(var4[0], RC[20]);
      var4[1] = aesEnc(var4[1], RC[21]);
      var4[2] = aesEnc(var4[2], RC[22]);
      var4[3] = aesEnc(var4[3], RC[23]);
      this.mix512(var4, var5);
      var4[0] = aesEnc(var5[0], RC[24]);
      var4[1] = aesEnc(var5[1], RC[25]);
      var4[2] = aesEnc(var5[2], RC[26]);
      var4[3] = aesEnc(var5[3], RC[27]);
      var4[0] = aesEnc(var4[0], RC[28]);
      var4[1] = aesEnc(var4[1], RC[29]);
      var4[2] = aesEnc(var4[2], RC[30]);
      var4[3] = aesEnc(var4[3], RC[31]);
      this.mix512(var4, var5);
      var4[0] = aesEnc(var5[0], RC[32]);
      var4[1] = aesEnc(var5[1], RC[33]);
      var4[2] = aesEnc(var5[2], RC[34]);
      var4[3] = aesEnc(var5[3], RC[35]);
      var4[0] = aesEnc(var4[0], RC[36]);
      var4[1] = aesEnc(var4[1], RC[37]);
      var4[2] = aesEnc(var4[2], RC[38]);
      var4[3] = aesEnc(var4[3], RC[39]);
      this.mix512(var4, var5);
      Bytes.xor(16, var5[0], 0, var1, 0, var4[0], 0);
      Bytes.xor(16, var5[1], 0, var1, 16, var4[1], 0);
      Bytes.xor(16, var5[2], 0, var1, 32, var4[2], 0);
      Bytes.xor(16, var5[3], 0, var1, 48, var4[3], 0);
      System.arraycopy(var4[0], 8, var2, var3, 8);
      System.arraycopy(var4[1], 8, var2, var3 + 8, 8);
      System.arraycopy(var4[2], 0, var2, var3 + 16, 8);
      System.arraycopy(var4[3], 0, var2, var3 + 24, 8);
      return 32;
   }

   @Override
   public String getAlgorithmName() {
      return "Haraka-512";
   }

   @Override
   public void update(byte var1) {
      if (this.off > 63) {
         throw new IllegalArgumentException("total input cannot be more than 64 bytes");
      } else {
         this.buffer[this.off++] = var1;
      }
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      if (this.off > 64 - var3) {
         throw new IllegalArgumentException("total input cannot be more than 64 bytes");
      } else {
         System.arraycopy(var1, var2, this.buffer, this.off, var3);
         this.off += var3;
      }
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      if (this.off != 64) {
         throw new IllegalStateException("input must be exactly 64 bytes");
      } else if (var1.length - var2 < 32) {
         throw new IllegalArgumentException("output too short to receive digest");
      } else {
         int var3 = this.haraka512256(this.buffer, var1, var2);
         this.reset();
         return var3;
      }
   }

   @Override
   public void reset() {
      this.off = 0;
      Arrays.clear(this.buffer);
   }
}
