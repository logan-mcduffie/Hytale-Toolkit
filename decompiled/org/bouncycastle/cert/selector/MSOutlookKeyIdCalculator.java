package org.bouncycastle.cert.selector;

import java.io.IOException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.util.Pack;

class MSOutlookKeyIdCalculator {
   static byte[] calculateKeyId(SubjectPublicKeyInfo var0) {
      MSOutlookKeyIdCalculator.SHA1Digest var1 = new MSOutlookKeyIdCalculator.SHA1Digest();
      byte[] var2 = new byte[var1.getDigestSize()];
      byte[] var3 = new byte[0];

      try {
         var3 = var0.getEncoded("DER");
      } catch (IOException var5) {
         return new byte[0];
      }

      var1.update(var3, 0, var3.length);
      var1.doFinal(var2, 0);
      return var2;
   }

   private abstract static class GeneralDigest {
      private static final int BYTE_LENGTH = 64;
      private byte[] xBuf;
      private int xBufOff;
      private long byteCount;

      GeneralDigest() {
         this.xBuf = new byte[4];
         this.xBufOff = 0;
      }

      GeneralDigest(MSOutlookKeyIdCalculator.GeneralDigest var1) {
         this.xBuf = new byte[var1.xBuf.length];
         this.copyIn(var1);
      }

      void copyIn(MSOutlookKeyIdCalculator.GeneralDigest var1) {
         System.arraycopy(var1.xBuf, 0, this.xBuf, 0, var1.xBuf.length);
         this.xBufOff = var1.xBufOff;
         this.byteCount = var1.byteCount;
      }

      void update(byte var1) {
         this.xBuf[this.xBufOff++] = var1;
         if (this.xBufOff == this.xBuf.length) {
            this.processWord(this.xBuf, 0);
            this.xBufOff = 0;
         }

         this.byteCount++;
      }

      void update(byte[] var1, int var2, int var3) {
         while (this.xBufOff != 0 && var3 > 0) {
            this.update(var1[var2]);
            var2++;
            var3--;
         }

         while (var3 > this.xBuf.length) {
            this.processWord(var1, var2);
            var2 += this.xBuf.length;
            var3 -= this.xBuf.length;
            this.byteCount = this.byteCount + this.xBuf.length;
         }

         while (var3 > 0) {
            this.update(var1[var2]);
            var2++;
            var3--;
         }
      }

      void finish() {
         long var1 = this.byteCount << 3;
         this.update((byte)-128);

         while (this.xBufOff != 0) {
            this.update((byte)0);
         }

         this.processLength(var1);
         this.processBlock();
      }

      void reset() {
         this.byteCount = 0L;
         this.xBufOff = 0;

         for (int var1 = 0; var1 < this.xBuf.length; var1++) {
            this.xBuf[var1] = 0;
         }
      }

      abstract void processWord(byte[] var1, int var2);

      abstract void processLength(long var1);

      abstract void processBlock();
   }

   private static class SHA1Digest extends MSOutlookKeyIdCalculator.GeneralDigest {
      private static final int DIGEST_LENGTH = 20;
      private int H1;
      private int H2;
      private int H3;
      private int H4;
      private int H5;
      private int[] X = new int[80];
      private int xOff;
      private static final int Y1 = 1518500249;
      private static final int Y2 = 1859775393;
      private static final int Y3 = -1894007588;
      private static final int Y4 = -899497514;

      SHA1Digest() {
         this.reset();
      }

      String getAlgorithmName() {
         return "SHA-1";
      }

      int getDigestSize() {
         return 20;
      }

      @Override
      void processWord(byte[] var1, int var2) {
         int var3 = var1[var2] << 24;
         var3 |= (var1[++var2] & 255) << 16;
         var3 |= (var1[++var2] & 255) << 8;
         var3 |= var1[++var2] & 255;
         this.X[this.xOff] = var3;
         if (++this.xOff == 16) {
            this.processBlock();
         }
      }

      @Override
      void processLength(long var1) {
         if (this.xOff > 14) {
            this.processBlock();
         }

         this.X[14] = (int)(var1 >>> 32);
         this.X[15] = (int)(var1 & -1L);
      }

      int doFinal(byte[] var1, int var2) {
         this.finish();
         Pack.intToBigEndian(this.H1, var1, var2);
         Pack.intToBigEndian(this.H2, var1, var2 + 4);
         Pack.intToBigEndian(this.H3, var1, var2 + 8);
         Pack.intToBigEndian(this.H4, var1, var2 + 12);
         Pack.intToBigEndian(this.H5, var1, var2 + 16);
         this.reset();
         return 20;
      }

      @Override
      void reset() {
         super.reset();
         this.H1 = 1732584193;
         this.H2 = -271733879;
         this.H3 = -1732584194;
         this.H4 = 271733878;
         this.H5 = -1009589776;
         this.xOff = 0;

         for (int var1 = 0; var1 != this.X.length; var1++) {
            this.X[var1] = 0;
         }
      }

      private int f(int var1, int var2, int var3) {
         return var1 & var2 | ~var1 & var3;
      }

      private int h(int var1, int var2, int var3) {
         return var1 ^ var2 ^ var3;
      }

      private int g(int var1, int var2, int var3) {
         return var1 & var2 | var1 & var3 | var2 & var3;
      }

      @Override
      void processBlock() {
         for (int var1 = 16; var1 < 80; var1++) {
            int var2 = this.X[var1 - 3] ^ this.X[var1 - 8] ^ this.X[var1 - 14] ^ this.X[var1 - 16];
            this.X[var1] = var2 << 1 | var2 >>> 31;
         }

         int var8 = this.H1;
         int var13 = this.H2;
         int var3 = this.H3;
         int var4 = this.H4;
         int var5 = this.H5;
         int var6 = 0;

         for (int var7 = 0; var7 < 4; var7++) {
            int var26 = var5 + (var8 << 5 | var8 >>> 27) + this.f(var13, var3, var4) + this.X[var6++] + 1518500249;
            int var14 = var13 << 30 | var13 >>> 2;
            int var22 = var4 + (var26 << 5 | var26 >>> 27) + this.f(var8, var14, var3) + this.X[var6++] + 1518500249;
            var8 = var8 << 30 | var8 >>> 2;
            var3 += (var22 << 5 | var22 >>> 27) + this.f(var26, var8, var14) + this.X[var6++] + 1518500249;
            var5 = var26 << 30 | var26 >>> 2;
            var13 = var14 + (var3 << 5 | var3 >>> 27) + this.f(var22, var5, var8) + this.X[var6++] + 1518500249;
            var4 = var22 << 30 | var22 >>> 2;
            var8 += (var13 << 5 | var13 >>> 27) + this.f(var3, var4, var5) + this.X[var6++] + 1518500249;
            var3 = var3 << 30 | var3 >>> 2;
         }

         for (int var46 = 0; var46 < 4; var46++) {
            int var27 = var5 + (var8 << 5 | var8 >>> 27) + this.h(var13, var3, var4) + this.X[var6++] + 1859775393;
            int var15 = var13 << 30 | var13 >>> 2;
            int var23 = var4 + (var27 << 5 | var27 >>> 27) + this.h(var8, var15, var3) + this.X[var6++] + 1859775393;
            var8 = var8 << 30 | var8 >>> 2;
            var3 += (var23 << 5 | var23 >>> 27) + this.h(var27, var8, var15) + this.X[var6++] + 1859775393;
            var5 = var27 << 30 | var27 >>> 2;
            var13 = var15 + (var3 << 5 | var3 >>> 27) + this.h(var23, var5, var8) + this.X[var6++] + 1859775393;
            var4 = var23 << 30 | var23 >>> 2;
            var8 += (var13 << 5 | var13 >>> 27) + this.h(var3, var4, var5) + this.X[var6++] + 1859775393;
            var3 = var3 << 30 | var3 >>> 2;
         }

         for (int var47 = 0; var47 < 4; var47++) {
            int var28 = var5 + (var8 << 5 | var8 >>> 27) + this.g(var13, var3, var4) + this.X[var6++] + -1894007588;
            int var16 = var13 << 30 | var13 >>> 2;
            int var24 = var4 + (var28 << 5 | var28 >>> 27) + this.g(var8, var16, var3) + this.X[var6++] + -1894007588;
            var8 = var8 << 30 | var8 >>> 2;
            var3 += (var24 << 5 | var24 >>> 27) + this.g(var28, var8, var16) + this.X[var6++] + -1894007588;
            var5 = var28 << 30 | var28 >>> 2;
            var13 = var16 + (var3 << 5 | var3 >>> 27) + this.g(var24, var5, var8) + this.X[var6++] + -1894007588;
            var4 = var24 << 30 | var24 >>> 2;
            var8 += (var13 << 5 | var13 >>> 27) + this.g(var3, var4, var5) + this.X[var6++] + -1894007588;
            var3 = var3 << 30 | var3 >>> 2;
         }

         for (int var48 = 0; var48 <= 3; var48++) {
            int var29 = var5 + (var8 << 5 | var8 >>> 27) + this.h(var13, var3, var4) + this.X[var6++] + -899497514;
            int var17 = var13 << 30 | var13 >>> 2;
            int var25 = var4 + (var29 << 5 | var29 >>> 27) + this.h(var8, var17, var3) + this.X[var6++] + -899497514;
            var8 = var8 << 30 | var8 >>> 2;
            var3 += (var25 << 5 | var25 >>> 27) + this.h(var29, var8, var17) + this.X[var6++] + -899497514;
            var5 = var29 << 30 | var29 >>> 2;
            var13 = var17 + (var3 << 5 | var3 >>> 27) + this.h(var25, var5, var8) + this.X[var6++] + -899497514;
            var4 = var25 << 30 | var25 >>> 2;
            var8 += (var13 << 5 | var13 >>> 27) + this.h(var3, var4, var5) + this.X[var6++] + -899497514;
            var3 = var3 << 30 | var3 >>> 2;
         }

         this.H1 += var8;
         this.H2 += var13;
         this.H3 += var3;
         this.H4 += var4;
         this.H5 += var5;
         this.xOff = 0;

         for (int var49 = 0; var49 < 16; var49++) {
            this.X[var49] = 0;
         }
      }
   }
}
