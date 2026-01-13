package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class SHA224Digest extends GeneralDigest implements EncodableDigest {
   private static final int DIGEST_LENGTH = 28;
   private int H1;
   private int H2;
   private int H3;
   private int H4;
   private int H5;
   private int H6;
   private int H7;
   private int H8;
   private int[] X = new int[64];
   private int xOff;
   static final int[] K = new int[]{
      1116352408,
      1899447441,
      -1245643825,
      -373957723,
      961987163,
      1508970993,
      -1841331548,
      -1424204075,
      -670586216,
      310598401,
      607225278,
      1426881987,
      1925078388,
      -2132889090,
      -1680079193,
      -1046744716,
      -459576895,
      -272742522,
      264347078,
      604807628,
      770255983,
      1249150122,
      1555081692,
      1996064986,
      -1740746414,
      -1473132947,
      -1341970488,
      -1084653625,
      -958395405,
      -710438585,
      113926993,
      338241895,
      666307205,
      773529912,
      1294757372,
      1396182291,
      1695183700,
      1986661051,
      -2117940946,
      -1838011259,
      -1564481375,
      -1474664885,
      -1035236496,
      -949202525,
      -778901479,
      -694614492,
      -200395387,
      275423344,
      430227734,
      506948616,
      659060556,
      883997877,
      958139571,
      1322822218,
      1537002063,
      1747873779,
      1955562222,
      2024104815,
      -2067236844,
      -1933114872,
      -1866530822,
      -1538233109,
      -1090935817,
      -965641998
   };

   public SHA224Digest() {
      this(CryptoServicePurpose.ANY);
   }

   public SHA224Digest(CryptoServicePurpose var1) {
      super(var1);
      CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
      this.reset();
   }

   public SHA224Digest(SHA224Digest var1) {
      super(var1);
      CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
      this.doCopy(var1);
   }

   private void doCopy(SHA224Digest var1) {
      super.copyIn(var1);
      this.H1 = var1.H1;
      this.H2 = var1.H2;
      this.H3 = var1.H3;
      this.H4 = var1.H4;
      this.H5 = var1.H5;
      this.H6 = var1.H6;
      this.H7 = var1.H7;
      this.H8 = var1.H8;
      System.arraycopy(var1.X, 0, this.X, 0, var1.X.length);
      this.xOff = var1.xOff;
   }

   public SHA224Digest(byte[] var1) {
      super(var1);
      CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
      this.H1 = Pack.bigEndianToInt(var1, 16);
      this.H2 = Pack.bigEndianToInt(var1, 20);
      this.H3 = Pack.bigEndianToInt(var1, 24);
      this.H4 = Pack.bigEndianToInt(var1, 28);
      this.H5 = Pack.bigEndianToInt(var1, 32);
      this.H6 = Pack.bigEndianToInt(var1, 36);
      this.H7 = Pack.bigEndianToInt(var1, 40);
      this.H8 = Pack.bigEndianToInt(var1, 44);
      this.xOff = Pack.bigEndianToInt(var1, 48);

      for (int var2 = 0; var2 != this.xOff; var2++) {
         this.X[var2] = Pack.bigEndianToInt(var1, 52 + var2 * 4);
      }
   }

   @Override
   public String getAlgorithmName() {
      return "SHA-224";
   }

   @Override
   public int getDigestSize() {
      return 28;
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
      this.X[15] = (int)(var1 & -1L);
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      this.finish();
      Pack.intToBigEndian(this.H1, var1, var2);
      Pack.intToBigEndian(this.H2, var1, var2 + 4);
      Pack.intToBigEndian(this.H3, var1, var2 + 8);
      Pack.intToBigEndian(this.H4, var1, var2 + 12);
      Pack.intToBigEndian(this.H5, var1, var2 + 16);
      Pack.intToBigEndian(this.H6, var1, var2 + 20);
      Pack.intToBigEndian(this.H7, var1, var2 + 24);
      this.reset();
      return 28;
   }

   @Override
   public void reset() {
      super.reset();
      this.H1 = -1056596264;
      this.H2 = 914150663;
      this.H3 = 812702999;
      this.H4 = -150054599;
      this.H5 = -4191439;
      this.H6 = 1750603025;
      this.H7 = 1694076839;
      this.H8 = -1090891868;
      this.xOff = 0;

      for (int var1 = 0; var1 != this.X.length; var1++) {
         this.X[var1] = 0;
      }
   }

   @Override
   protected void processBlock() {
      for (int var1 = 16; var1 <= 63; var1++) {
         this.X[var1] = this.Theta1(this.X[var1 - 2]) + this.X[var1 - 7] + this.Theta0(this.X[var1 - 15]) + this.X[var1 - 16];
      }

      int var11 = this.H1;
      int var2 = this.H2;
      int var3 = this.H3;
      int var4 = this.H4;
      int var5 = this.H5;
      int var6 = this.H6;
      int var7 = this.H7;
      int var8 = this.H8;
      int var9 = 0;

      for (int var10 = 0; var10 < 8; var10++) {
         int var26 = var8 + this.Sum1(var5) + this.Ch(var5, var6, var7) + K[var9] + this.X[var9];
         int var18 = var4 + var26;
         int var27 = var26 + this.Sum0(var11) + this.Maj(var11, var2, var3);
         int var24 = var7 + this.Sum1(var18) + this.Ch(var18, var5, var6) + K[++var9] + this.X[var9];
         int var16 = var3 + var24;
         int var25 = var24 + this.Sum0(var27) + this.Maj(var27, var11, var2);
         int var22 = var6 + this.Sum1(var16) + this.Ch(var16, var18, var5) + K[++var9] + this.X[var9];
         int var14 = var2 + var22;
         int var23 = var22 + this.Sum0(var25) + this.Maj(var25, var27, var11);
         var5 += this.Sum1(var14) + this.Ch(var14, var16, var18) + K[++var9] + this.X[var9];
         var11 += var5;
         var5 += this.Sum0(var23) + this.Maj(var23, var25, var27);
         int var19 = var18 + this.Sum1(var11) + this.Ch(var11, var14, var16) + K[++var9] + this.X[var9];
         var8 = var27 + var19;
         var4 = var19 + this.Sum0(var5) + this.Maj(var5, var23, var25);
         int var17 = var16 + this.Sum1(var8) + this.Ch(var8, var11, var14) + K[++var9] + this.X[var9];
         var7 = var25 + var17;
         var3 = var17 + this.Sum0(var4) + this.Maj(var4, var5, var23);
         int var15 = var14 + this.Sum1(var7) + this.Ch(var7, var8, var11) + K[++var9] + this.X[var9];
         var6 = var23 + var15;
         var2 = var15 + this.Sum0(var3) + this.Maj(var3, var4, var5);
         var11 += this.Sum1(var6) + this.Ch(var6, var7, var8) + K[++var9] + this.X[var9];
         var5 += var11;
         var11 += this.Sum0(var2) + this.Maj(var2, var3, var4);
         var9++;
      }

      this.H1 += var11;
      this.H2 += var2;
      this.H3 += var3;
      this.H4 += var4;
      this.H5 += var5;
      this.H6 += var6;
      this.H7 += var7;
      this.H8 += var8;
      this.xOff = 0;

      for (int var35 = 0; var35 < 16; var35++) {
         this.X[var35] = 0;
      }
   }

   private int Ch(int var1, int var2, int var3) {
      return var1 & var2 ^ ~var1 & var3;
   }

   private int Maj(int var1, int var2, int var3) {
      return var1 & var2 ^ var1 & var3 ^ var2 & var3;
   }

   private int Sum0(int var1) {
      return (var1 >>> 2 | var1 << 30) ^ (var1 >>> 13 | var1 << 19) ^ (var1 >>> 22 | var1 << 10);
   }

   private int Sum1(int var1) {
      return (var1 >>> 6 | var1 << 26) ^ (var1 >>> 11 | var1 << 21) ^ (var1 >>> 25 | var1 << 7);
   }

   private int Theta0(int var1) {
      return (var1 >>> 7 | var1 << 25) ^ (var1 >>> 18 | var1 << 14) ^ var1 >>> 3;
   }

   private int Theta1(int var1) {
      return (var1 >>> 17 | var1 << 15) ^ (var1 >>> 19 | var1 << 13) ^ var1 >>> 10;
   }

   @Override
   public Memoable copy() {
      return new SHA224Digest(this);
   }

   @Override
   public void reset(Memoable var1) {
      SHA224Digest var2 = (SHA224Digest)var1;
      this.doCopy(var2);
   }

   @Override
   public byte[] getEncodedState() {
      byte[] var1 = new byte[52 + this.xOff * 4 + 1];
      super.populateState(var1);
      Pack.intToBigEndian(this.H1, var1, 16);
      Pack.intToBigEndian(this.H2, var1, 20);
      Pack.intToBigEndian(this.H3, var1, 24);
      Pack.intToBigEndian(this.H4, var1, 28);
      Pack.intToBigEndian(this.H5, var1, 32);
      Pack.intToBigEndian(this.H6, var1, 36);
      Pack.intToBigEndian(this.H7, var1, 40);
      Pack.intToBigEndian(this.H8, var1, 44);
      Pack.intToBigEndian(this.xOff, var1, 48);

      for (int var2 = 0; var2 != this.xOff; var2++) {
         Pack.intToBigEndian(this.X[var2], var1, 52 + var2 * 4);
      }

      var1[var1.length - 1] = (byte)this.purpose.ordinal();
      return var1;
   }

   @Override
   protected CryptoServiceProperties cryptoServiceProperties() {
      return Utils.getDefaultProperties(this, 192, this.purpose);
   }
}
