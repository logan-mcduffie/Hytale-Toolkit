package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.SavableDigest;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class SHA256Digest extends GeneralDigest implements SavableDigest {
   private static final int DIGEST_LENGTH = 32;
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

   public static SavableDigest newInstance() {
      return new SHA256Digest();
   }

   public static SavableDigest newInstance(CryptoServicePurpose var0) {
      return new SHA256Digest(var0);
   }

   public static SavableDigest newInstance(Digest var0) {
      if (var0 instanceof SHA256Digest) {
         return new SHA256Digest((SHA256Digest)var0);
      } else {
         throw new IllegalArgumentException("receiver digest not available for input type " + (var0 != null ? var0.getClass().getName() : "null"));
      }
   }

   public static SavableDigest newInstance(byte[] var0) {
      return new SHA256Digest(var0);
   }

   public SHA256Digest() {
      this(CryptoServicePurpose.ANY);
   }

   public SHA256Digest(CryptoServicePurpose var1) {
      super(var1);
      CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
      this.reset();
   }

   public SHA256Digest(SHA256Digest var1) {
      super(var1);
      this.copyIn(var1);
   }

   private void copyIn(SHA256Digest var1) {
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

   public SHA256Digest(byte[] var1) {
      super(var1);
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
      return "SHA-256";
   }

   @Override
   public int getDigestSize() {
      return 32;
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
      Pack.intToBigEndian(this.H8, var1, var2 + 28);
      this.reset();
      return 32;
   }

   @Override
   public void reset() {
      super.reset();
      this.H1 = 1779033703;
      this.H2 = -1150833019;
      this.H3 = 1013904242;
      this.H4 = -1521486534;
      this.H5 = 1359893119;
      this.H6 = -1694144372;
      this.H7 = 528734635;
      this.H8 = 1541459225;
      this.xOff = 0;

      for (int var1 = 0; var1 != this.X.length; var1++) {
         this.X[var1] = 0;
      }
   }

   @Override
   protected void processBlock() {
      for (int var1 = 16; var1 <= 63; var1++) {
         this.X[var1] = Theta1(this.X[var1 - 2]) + this.X[var1 - 7] + Theta0(this.X[var1 - 15]) + this.X[var1 - 16];
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
         int var26 = var8 + Sum1(var5) + Ch(var5, var6, var7) + K[var9] + this.X[var9];
         int var18 = var4 + var26;
         int var27 = var26 + Sum0(var11) + Maj(var11, var2, var3);
         int var24 = var7 + Sum1(var18) + Ch(var18, var5, var6) + K[++var9] + this.X[var9];
         int var16 = var3 + var24;
         int var25 = var24 + Sum0(var27) + Maj(var27, var11, var2);
         int var22 = var6 + Sum1(var16) + Ch(var16, var18, var5) + K[++var9] + this.X[var9];
         int var14 = var2 + var22;
         int var23 = var22 + Sum0(var25) + Maj(var25, var27, var11);
         var5 += Sum1(var14) + Ch(var14, var16, var18) + K[++var9] + this.X[var9];
         var11 += var5;
         var5 += Sum0(var23) + Maj(var23, var25, var27);
         int var19 = var18 + Sum1(var11) + Ch(var11, var14, var16) + K[++var9] + this.X[var9];
         var8 = var27 + var19;
         var4 = var19 + Sum0(var5) + Maj(var5, var23, var25);
         int var17 = var16 + Sum1(var8) + Ch(var8, var11, var14) + K[++var9] + this.X[var9];
         var7 = var25 + var17;
         var3 = var17 + Sum0(var4) + Maj(var4, var5, var23);
         int var15 = var14 + Sum1(var7) + Ch(var7, var8, var11) + K[++var9] + this.X[var9];
         var6 = var23 + var15;
         var2 = var15 + Sum0(var3) + Maj(var3, var4, var5);
         var11 += Sum1(var6) + Ch(var6, var7, var8) + K[++var9] + this.X[var9];
         var5 += var11;
         var11 += Sum0(var2) + Maj(var2, var3, var4);
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

   private static int Ch(int var0, int var1, int var2) {
      return var0 & var1 ^ ~var0 & var2;
   }

   private static int Maj(int var0, int var1, int var2) {
      return var0 & var1 | var2 & (var0 ^ var1);
   }

   private static int Sum0(int var0) {
      return (var0 >>> 2 | var0 << 30) ^ (var0 >>> 13 | var0 << 19) ^ (var0 >>> 22 | var0 << 10);
   }

   private static int Sum1(int var0) {
      return (var0 >>> 6 | var0 << 26) ^ (var0 >>> 11 | var0 << 21) ^ (var0 >>> 25 | var0 << 7);
   }

   private static int Theta0(int var0) {
      return (var0 >>> 7 | var0 << 25) ^ (var0 >>> 18 | var0 << 14) ^ var0 >>> 3;
   }

   private static int Theta1(int var0) {
      return (var0 >>> 17 | var0 << 15) ^ (var0 >>> 19 | var0 << 13) ^ var0 >>> 10;
   }

   @Override
   public Memoable copy() {
      return new SHA256Digest(this);
   }

   @Override
   public void reset(Memoable var1) {
      SHA256Digest var2 = (SHA256Digest)var1;
      this.copyIn(var2);
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
      return Utils.getDefaultProperties(this, 256, this.purpose);
   }
}
