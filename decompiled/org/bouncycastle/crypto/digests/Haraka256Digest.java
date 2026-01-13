package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;

public class Haraka256Digest extends HarakaBase {
   private final byte[] buffer;
   private int off;
   private final CryptoServicePurpose purpose;

   private void mix256(byte[][] var1, byte[][] var2) {
      System.arraycopy(var1[0], 0, var2[0], 0, 4);
      System.arraycopy(var1[1], 0, var2[0], 4, 4);
      System.arraycopy(var1[0], 4, var2[0], 8, 4);
      System.arraycopy(var1[1], 4, var2[0], 12, 4);
      System.arraycopy(var1[0], 8, var2[1], 0, 4);
      System.arraycopy(var1[1], 8, var2[1], 4, 4);
      System.arraycopy(var1[0], 12, var2[1], 8, 4);
      System.arraycopy(var1[1], 12, var2[1], 12, 4);
   }

   private int haraka256256(byte[] var1, byte[] var2, int var3) {
      byte[][] var4 = new byte[2][16];
      byte[][] var5 = new byte[2][16];
      System.arraycopy(var1, 0, var4[0], 0, 16);
      System.arraycopy(var1, 16, var4[1], 0, 16);
      var4[0] = aesEnc(var4[0], RC[0]);
      var4[1] = aesEnc(var4[1], RC[1]);
      var4[0] = aesEnc(var4[0], RC[2]);
      var4[1] = aesEnc(var4[1], RC[3]);
      this.mix256(var4, var5);
      var4[0] = aesEnc(var5[0], RC[4]);
      var4[1] = aesEnc(var5[1], RC[5]);
      var4[0] = aesEnc(var4[0], RC[6]);
      var4[1] = aesEnc(var4[1], RC[7]);
      this.mix256(var4, var5);
      var4[0] = aesEnc(var5[0], RC[8]);
      var4[1] = aesEnc(var5[1], RC[9]);
      var4[0] = aesEnc(var4[0], RC[10]);
      var4[1] = aesEnc(var4[1], RC[11]);
      this.mix256(var4, var5);
      var4[0] = aesEnc(var5[0], RC[12]);
      var4[1] = aesEnc(var5[1], RC[13]);
      var4[0] = aesEnc(var4[0], RC[14]);
      var4[1] = aesEnc(var4[1], RC[15]);
      this.mix256(var4, var5);
      var4[0] = aesEnc(var5[0], RC[16]);
      var4[1] = aesEnc(var5[1], RC[17]);
      var4[0] = aesEnc(var4[0], RC[18]);
      var4[1] = aesEnc(var4[1], RC[19]);
      this.mix256(var4, var5);
      Bytes.xor(16, var5[0], 0, var1, 0, var2, var3);
      Bytes.xor(16, var5[1], 0, var1, 16, var2, var3 + 16);
      return 32;
   }

   public Haraka256Digest() {
      this(CryptoServicePurpose.ANY);
   }

   public Haraka256Digest(CryptoServicePurpose var1) {
      this.purpose = var1;
      this.buffer = new byte[32];
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, this.getDigestSize() * 4, var1));
   }

   public Haraka256Digest(Haraka256Digest var1) {
      this.purpose = var1.purpose;
      this.buffer = Arrays.clone(var1.buffer);
      this.off = var1.off;
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, this.getDigestSize() * 4, this.purpose));
   }

   @Override
   public String getAlgorithmName() {
      return "Haraka-256";
   }

   @Override
   public void update(byte var1) {
      if (this.off > 31) {
         throw new IllegalArgumentException("total input cannot be more than 32 bytes");
      } else {
         this.buffer[this.off++] = var1;
      }
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      if (this.off > 32 - var3) {
         throw new IllegalArgumentException("total input cannot be more than 32 bytes");
      } else {
         System.arraycopy(var1, var2, this.buffer, this.off, var3);
         this.off += var3;
      }
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      if (this.off != 32) {
         throw new IllegalStateException("input must be exactly 32 bytes");
      } else if (var1.length - var2 < 32) {
         throw new IllegalArgumentException("output too short to receive digest");
      } else {
         int var3 = this.haraka256256(this.buffer, var1, var2);
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
