package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class SHA1Digest extends GeneralDigest implements EncodableDigest {
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

   public SHA1Digest() {
      this(CryptoServicePurpose.ANY);
   }

   public SHA1Digest(CryptoServicePurpose var1) {
      super(var1);
      CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
      this.reset();
   }

   public SHA1Digest(SHA1Digest var1) {
      super(var1);
      CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
      this.copyIn(var1);
   }

   public SHA1Digest(byte[] var1) {
      super(var1);
      CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
      this.H1 = Pack.bigEndianToInt(var1, 16);
      this.H2 = Pack.bigEndianToInt(var1, 20);
      this.H3 = Pack.bigEndianToInt(var1, 24);
      this.H4 = Pack.bigEndianToInt(var1, 28);
      this.H5 = Pack.bigEndianToInt(var1, 32);
      this.xOff = Pack.bigEndianToInt(var1, 36);

      for (int var2 = 0; var2 != this.xOff; var2++) {
         this.X[var2] = Pack.bigEndianToInt(var1, 40 + var2 * 4);
      }
   }

   private void copyIn(SHA1Digest var1) {
      this.H1 = var1.H1;
      this.H2 = var1.H2;
      this.H3 = var1.H3;
      this.H4 = var1.H4;
      this.H5 = var1.H5;
      System.arraycopy(var1.X, 0, this.X, 0, var1.X.length);
      this.xOff = var1.xOff;
   }

   @Override
   public String getAlgorithmName() {
      return "SHA-1";
   }

   @Override
   public int getDigestSize() {
      return 20;
   }

   @Override
   protected void processWord(byte[] var1, int var2) {
      this.X[this.xOff] = Pack.bigEndianToInt(var1, var2);
      if (++this.xOff == 16) {
         this.processBlock();
      }
   }

   @Override
   protected void processLength(long var1) {
      if (this.xOff > 14) {
         this.processBlock();
      }

      this.X[14] = (int)(var1 >>> 32);
      this.X[15] = (int)var1;
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
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
   public void reset() {
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
   protected void processBlock() {
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

   @Override
   public Memoable copy() {
      return new SHA1Digest(this);
   }

   @Override
   public void reset(Memoable var1) {
      SHA1Digest var2 = (SHA1Digest)var1;
      super.copyIn(var2);
      this.copyIn(var2);
   }

   @Override
   public byte[] getEncodedState() {
      byte[] var1 = new byte[40 + this.xOff * 4 + 1];
      super.populateState(var1);
      Pack.intToBigEndian(this.H1, var1, 16);
      Pack.intToBigEndian(this.H2, var1, 20);
      Pack.intToBigEndian(this.H3, var1, 24);
      Pack.intToBigEndian(this.H4, var1, 28);
      Pack.intToBigEndian(this.H5, var1, 32);
      Pack.intToBigEndian(this.xOff, var1, 36);

      for (int var2 = 0; var2 != this.xOff; var2++) {
         Pack.intToBigEndian(this.X[var2], var1, 40 + var2 * 4);
      }

      var1[var1.length - 1] = (byte)this.purpose.ordinal();
      return var1;
   }

   @Override
   protected CryptoServiceProperties cryptoServiceProperties() {
      return Utils.getDefaultProperties(this, 128, this.purpose);
   }
}
