package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class MD5Digest extends GeneralDigest implements EncodableDigest {
   private static final int DIGEST_LENGTH = 16;
   private int H1;
   private int H2;
   private int H3;
   private int H4;
   private int[] X = new int[16];
   private int xOff;
   private static final int S11 = 7;
   private static final int S12 = 12;
   private static final int S13 = 17;
   private static final int S14 = 22;
   private static final int S21 = 5;
   private static final int S22 = 9;
   private static final int S23 = 14;
   private static final int S24 = 20;
   private static final int S31 = 4;
   private static final int S32 = 11;
   private static final int S33 = 16;
   private static final int S34 = 23;
   private static final int S41 = 6;
   private static final int S42 = 10;
   private static final int S43 = 15;
   private static final int S44 = 21;

   public MD5Digest() {
      this(CryptoServicePurpose.ANY);
   }

   public MD5Digest(CryptoServicePurpose var1) {
      super(var1);
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, 64, var1));
      this.reset();
   }

   public MD5Digest(byte[] var1) {
      super(var1);
      this.H1 = Pack.bigEndianToInt(var1, 16);
      this.H2 = Pack.bigEndianToInt(var1, 20);
      this.H3 = Pack.bigEndianToInt(var1, 24);
      this.H4 = Pack.bigEndianToInt(var1, 28);
      this.xOff = Pack.bigEndianToInt(var1, 32);

      for (int var2 = 0; var2 != this.xOff; var2++) {
         this.X[var2] = Pack.bigEndianToInt(var1, 36 + var2 * 4);
      }
   }

   public MD5Digest(MD5Digest var1) {
      super(var1);
      this.copyIn(var1);
   }

   private void copyIn(MD5Digest var1) {
      super.copyIn(var1);
      this.H1 = var1.H1;
      this.H2 = var1.H2;
      this.H3 = var1.H3;
      this.H4 = var1.H4;
      System.arraycopy(var1.X, 0, this.X, 0, var1.X.length);
      this.xOff = var1.xOff;
   }

   @Override
   public String getAlgorithmName() {
      return "MD5";
   }

   @Override
   public int getDigestSize() {
      return 16;
   }

   @Override
   protected void processWord(byte[] var1, int var2) {
      this.X[this.xOff++] = Pack.littleEndianToInt(var1, var2);
      if (this.xOff == 16) {
         this.processBlock();
      }
   }

   @Override
   protected void processLength(long var1) {
      if (this.xOff > 14) {
         this.processBlock();
      }

      this.X[14] = (int)(var1 & -1L);
      this.X[15] = (int)(var1 >>> 32);
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      this.finish();
      Pack.intToLittleEndian(this.H1, var1, var2);
      Pack.intToLittleEndian(this.H2, var1, var2 + 4);
      Pack.intToLittleEndian(this.H3, var1, var2 + 8);
      Pack.intToLittleEndian(this.H4, var1, var2 + 12);
      this.reset();
      return 16;
   }

   @Override
   public void reset() {
      super.reset();
      this.H1 = 1732584193;
      this.H2 = -271733879;
      this.H3 = -1732584194;
      this.H4 = 271733878;
      this.xOff = 0;

      for (int var1 = 0; var1 != this.X.length; var1++) {
         this.X[var1] = 0;
      }
   }

   private int rotateLeft(int var1, int var2) {
      return var1 << var2 | var1 >>> 32 - var2;
   }

   private int F(int var1, int var2, int var3) {
      return var1 & var2 | ~var1 & var3;
   }

   private int G(int var1, int var2, int var3) {
      return var1 & var3 | var2 & ~var3;
   }

   private int H(int var1, int var2, int var3) {
      return var1 ^ var2 ^ var3;
   }

   private int K(int var1, int var2, int var3) {
      return var2 ^ (var1 | ~var3);
   }

   @Override
   protected void processBlock() {
      int var1 = this.H1;
      int var2 = this.H2;
      int var3 = this.H3;
      int var4 = this.H4;
      var1 = this.rotateLeft(var1 + this.F(var2, var3, var4) + this.X[0] + -680876936, 7) + var2;
      var4 = this.rotateLeft(var4 + this.F(var1, var2, var3) + this.X[1] + -389564586, 12) + var1;
      var3 = this.rotateLeft(var3 + this.F(var4, var1, var2) + this.X[2] + 606105819, 17) + var4;
      var2 = this.rotateLeft(var2 + this.F(var3, var4, var1) + this.X[3] + -1044525330, 22) + var3;
      var1 = this.rotateLeft(var1 + this.F(var2, var3, var4) + this.X[4] + -176418897, 7) + var2;
      var4 = this.rotateLeft(var4 + this.F(var1, var2, var3) + this.X[5] + 1200080426, 12) + var1;
      var3 = this.rotateLeft(var3 + this.F(var4, var1, var2) + this.X[6] + -1473231341, 17) + var4;
      var2 = this.rotateLeft(var2 + this.F(var3, var4, var1) + this.X[7] + -45705983, 22) + var3;
      var1 = this.rotateLeft(var1 + this.F(var2, var3, var4) + this.X[8] + 1770035416, 7) + var2;
      var4 = this.rotateLeft(var4 + this.F(var1, var2, var3) + this.X[9] + -1958414417, 12) + var1;
      var3 = this.rotateLeft(var3 + this.F(var4, var1, var2) + this.X[10] + -42063, 17) + var4;
      var2 = this.rotateLeft(var2 + this.F(var3, var4, var1) + this.X[11] + -1990404162, 22) + var3;
      var1 = this.rotateLeft(var1 + this.F(var2, var3, var4) + this.X[12] + 1804603682, 7) + var2;
      var4 = this.rotateLeft(var4 + this.F(var1, var2, var3) + this.X[13] + -40341101, 12) + var1;
      var3 = this.rotateLeft(var3 + this.F(var4, var1, var2) + this.X[14] + -1502002290, 17) + var4;
      var2 = this.rotateLeft(var2 + this.F(var3, var4, var1) + this.X[15] + 1236535329, 22) + var3;
      var1 = this.rotateLeft(var1 + this.G(var2, var3, var4) + this.X[1] + -165796510, 5) + var2;
      var4 = this.rotateLeft(var4 + this.G(var1, var2, var3) + this.X[6] + -1069501632, 9) + var1;
      var3 = this.rotateLeft(var3 + this.G(var4, var1, var2) + this.X[11] + 643717713, 14) + var4;
      var2 = this.rotateLeft(var2 + this.G(var3, var4, var1) + this.X[0] + -373897302, 20) + var3;
      var1 = this.rotateLeft(var1 + this.G(var2, var3, var4) + this.X[5] + -701558691, 5) + var2;
      var4 = this.rotateLeft(var4 + this.G(var1, var2, var3) + this.X[10] + 38016083, 9) + var1;
      var3 = this.rotateLeft(var3 + this.G(var4, var1, var2) + this.X[15] + -660478335, 14) + var4;
      var2 = this.rotateLeft(var2 + this.G(var3, var4, var1) + this.X[4] + -405537848, 20) + var3;
      var1 = this.rotateLeft(var1 + this.G(var2, var3, var4) + this.X[9] + 568446438, 5) + var2;
      var4 = this.rotateLeft(var4 + this.G(var1, var2, var3) + this.X[14] + -1019803690, 9) + var1;
      var3 = this.rotateLeft(var3 + this.G(var4, var1, var2) + this.X[3] + -187363961, 14) + var4;
      var2 = this.rotateLeft(var2 + this.G(var3, var4, var1) + this.X[8] + 1163531501, 20) + var3;
      var1 = this.rotateLeft(var1 + this.G(var2, var3, var4) + this.X[13] + -1444681467, 5) + var2;
      var4 = this.rotateLeft(var4 + this.G(var1, var2, var3) + this.X[2] + -51403784, 9) + var1;
      var3 = this.rotateLeft(var3 + this.G(var4, var1, var2) + this.X[7] + 1735328473, 14) + var4;
      var2 = this.rotateLeft(var2 + this.G(var3, var4, var1) + this.X[12] + -1926607734, 20) + var3;
      var1 = this.rotateLeft(var1 + this.H(var2, var3, var4) + this.X[5] + -378558, 4) + var2;
      var4 = this.rotateLeft(var4 + this.H(var1, var2, var3) + this.X[8] + -2022574463, 11) + var1;
      var3 = this.rotateLeft(var3 + this.H(var4, var1, var2) + this.X[11] + 1839030562, 16) + var4;
      var2 = this.rotateLeft(var2 + this.H(var3, var4, var1) + this.X[14] + -35309556, 23) + var3;
      var1 = this.rotateLeft(var1 + this.H(var2, var3, var4) + this.X[1] + -1530992060, 4) + var2;
      var4 = this.rotateLeft(var4 + this.H(var1, var2, var3) + this.X[4] + 1272893353, 11) + var1;
      var3 = this.rotateLeft(var3 + this.H(var4, var1, var2) + this.X[7] + -155497632, 16) + var4;
      var2 = this.rotateLeft(var2 + this.H(var3, var4, var1) + this.X[10] + -1094730640, 23) + var3;
      var1 = this.rotateLeft(var1 + this.H(var2, var3, var4) + this.X[13] + 681279174, 4) + var2;
      var4 = this.rotateLeft(var4 + this.H(var1, var2, var3) + this.X[0] + -358537222, 11) + var1;
      var3 = this.rotateLeft(var3 + this.H(var4, var1, var2) + this.X[3] + -722521979, 16) + var4;
      var2 = this.rotateLeft(var2 + this.H(var3, var4, var1) + this.X[6] + 76029189, 23) + var3;
      var1 = this.rotateLeft(var1 + this.H(var2, var3, var4) + this.X[9] + -640364487, 4) + var2;
      var4 = this.rotateLeft(var4 + this.H(var1, var2, var3) + this.X[12] + -421815835, 11) + var1;
      var3 = this.rotateLeft(var3 + this.H(var4, var1, var2) + this.X[15] + 530742520, 16) + var4;
      var2 = this.rotateLeft(var2 + this.H(var3, var4, var1) + this.X[2] + -995338651, 23) + var3;
      var1 = this.rotateLeft(var1 + this.K(var2, var3, var4) + this.X[0] + -198630844, 6) + var2;
      var4 = this.rotateLeft(var4 + this.K(var1, var2, var3) + this.X[7] + 1126891415, 10) + var1;
      var3 = this.rotateLeft(var3 + this.K(var4, var1, var2) + this.X[14] + -1416354905, 15) + var4;
      var2 = this.rotateLeft(var2 + this.K(var3, var4, var1) + this.X[5] + -57434055, 21) + var3;
      var1 = this.rotateLeft(var1 + this.K(var2, var3, var4) + this.X[12] + 1700485571, 6) + var2;
      var4 = this.rotateLeft(var4 + this.K(var1, var2, var3) + this.X[3] + -1894986606, 10) + var1;
      var3 = this.rotateLeft(var3 + this.K(var4, var1, var2) + this.X[10] + -1051523, 15) + var4;
      var2 = this.rotateLeft(var2 + this.K(var3, var4, var1) + this.X[1] + -2054922799, 21) + var3;
      var1 = this.rotateLeft(var1 + this.K(var2, var3, var4) + this.X[8] + 1873313359, 6) + var2;
      var4 = this.rotateLeft(var4 + this.K(var1, var2, var3) + this.X[15] + -30611744, 10) + var1;
      var3 = this.rotateLeft(var3 + this.K(var4, var1, var2) + this.X[6] + -1560198380, 15) + var4;
      var2 = this.rotateLeft(var2 + this.K(var3, var4, var1) + this.X[13] + 1309151649, 21) + var3;
      var1 = this.rotateLeft(var1 + this.K(var2, var3, var4) + this.X[4] + -145523070, 6) + var2;
      var4 = this.rotateLeft(var4 + this.K(var1, var2, var3) + this.X[11] + -1120210379, 10) + var1;
      var3 = this.rotateLeft(var3 + this.K(var4, var1, var2) + this.X[2] + 718787259, 15) + var4;
      var2 = this.rotateLeft(var2 + this.K(var3, var4, var1) + this.X[9] + -343485551, 21) + var3;
      this.H1 += var1;
      this.H2 += var2;
      this.H3 += var3;
      this.H4 += var4;
      this.xOff = 0;

      for (int var5 = 0; var5 != this.X.length; var5++) {
         this.X[var5] = 0;
      }
   }

   @Override
   public Memoable copy() {
      return new MD5Digest(this);
   }

   @Override
   public void reset(Memoable var1) {
      MD5Digest var2 = (MD5Digest)var1;
      this.copyIn(var2);
   }

   @Override
   public byte[] getEncodedState() {
      byte[] var1 = new byte[36 + this.xOff * 4 + 1];
      super.populateState(var1);
      Pack.intToBigEndian(this.H1, var1, 16);
      Pack.intToBigEndian(this.H2, var1, 20);
      Pack.intToBigEndian(this.H3, var1, 24);
      Pack.intToBigEndian(this.H4, var1, 28);
      Pack.intToBigEndian(this.xOff, var1, 32);

      for (int var2 = 0; var2 != this.xOff; var2++) {
         Pack.intToBigEndian(this.X[var2], var1, 36 + var2 * 4);
      }

      var1[var1.length - 1] = (byte)this.purpose.ordinal();
      return var1;
   }

   @Override
   protected CryptoServiceProperties cryptoServiceProperties() {
      return Utils.getDefaultProperties(this, this.purpose);
   }
}
