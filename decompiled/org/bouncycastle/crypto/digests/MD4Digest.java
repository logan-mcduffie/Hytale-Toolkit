package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class MD4Digest extends GeneralDigest {
   private static final int DIGEST_LENGTH = 16;
   private int H1;
   private int H2;
   private int H3;
   private int H4;
   private int[] X = new int[16];
   private int xOff;
   private static final int S11 = 3;
   private static final int S12 = 7;
   private static final int S13 = 11;
   private static final int S14 = 19;
   private static final int S21 = 3;
   private static final int S22 = 5;
   private static final int S23 = 9;
   private static final int S24 = 13;
   private static final int S31 = 3;
   private static final int S32 = 9;
   private static final int S33 = 11;
   private static final int S34 = 15;

   public MD4Digest() {
      this(CryptoServicePurpose.ANY);
   }

   public MD4Digest(CryptoServicePurpose var1) {
      super(var1);
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, 64, var1));
      this.reset();
   }

   public MD4Digest(MD4Digest var1) {
      super(var1.purpose);
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, 64, this.purpose));
      this.copyIn(var1);
   }

   private void copyIn(MD4Digest var1) {
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
      return "MD4";
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
      return var1 & var2 | var1 & var3 | var2 & var3;
   }

   private int H(int var1, int var2, int var3) {
      return var1 ^ var2 ^ var3;
   }

   @Override
   protected void processBlock() {
      int var1 = this.H1;
      int var2 = this.H2;
      int var3 = this.H3;
      int var4 = this.H4;
      var1 = this.rotateLeft(var1 + this.F(var2, var3, var4) + this.X[0], 3);
      var4 = this.rotateLeft(var4 + this.F(var1, var2, var3) + this.X[1], 7);
      var3 = this.rotateLeft(var3 + this.F(var4, var1, var2) + this.X[2], 11);
      var2 = this.rotateLeft(var2 + this.F(var3, var4, var1) + this.X[3], 19);
      var1 = this.rotateLeft(var1 + this.F(var2, var3, var4) + this.X[4], 3);
      var4 = this.rotateLeft(var4 + this.F(var1, var2, var3) + this.X[5], 7);
      var3 = this.rotateLeft(var3 + this.F(var4, var1, var2) + this.X[6], 11);
      var2 = this.rotateLeft(var2 + this.F(var3, var4, var1) + this.X[7], 19);
      var1 = this.rotateLeft(var1 + this.F(var2, var3, var4) + this.X[8], 3);
      var4 = this.rotateLeft(var4 + this.F(var1, var2, var3) + this.X[9], 7);
      var3 = this.rotateLeft(var3 + this.F(var4, var1, var2) + this.X[10], 11);
      var2 = this.rotateLeft(var2 + this.F(var3, var4, var1) + this.X[11], 19);
      var1 = this.rotateLeft(var1 + this.F(var2, var3, var4) + this.X[12], 3);
      var4 = this.rotateLeft(var4 + this.F(var1, var2, var3) + this.X[13], 7);
      var3 = this.rotateLeft(var3 + this.F(var4, var1, var2) + this.X[14], 11);
      var2 = this.rotateLeft(var2 + this.F(var3, var4, var1) + this.X[15], 19);
      var1 = this.rotateLeft(var1 + this.G(var2, var3, var4) + this.X[0] + 1518500249, 3);
      var4 = this.rotateLeft(var4 + this.G(var1, var2, var3) + this.X[4] + 1518500249, 5);
      var3 = this.rotateLeft(var3 + this.G(var4, var1, var2) + this.X[8] + 1518500249, 9);
      var2 = this.rotateLeft(var2 + this.G(var3, var4, var1) + this.X[12] + 1518500249, 13);
      var1 = this.rotateLeft(var1 + this.G(var2, var3, var4) + this.X[1] + 1518500249, 3);
      var4 = this.rotateLeft(var4 + this.G(var1, var2, var3) + this.X[5] + 1518500249, 5);
      var3 = this.rotateLeft(var3 + this.G(var4, var1, var2) + this.X[9] + 1518500249, 9);
      var2 = this.rotateLeft(var2 + this.G(var3, var4, var1) + this.X[13] + 1518500249, 13);
      var1 = this.rotateLeft(var1 + this.G(var2, var3, var4) + this.X[2] + 1518500249, 3);
      var4 = this.rotateLeft(var4 + this.G(var1, var2, var3) + this.X[6] + 1518500249, 5);
      var3 = this.rotateLeft(var3 + this.G(var4, var1, var2) + this.X[10] + 1518500249, 9);
      var2 = this.rotateLeft(var2 + this.G(var3, var4, var1) + this.X[14] + 1518500249, 13);
      var1 = this.rotateLeft(var1 + this.G(var2, var3, var4) + this.X[3] + 1518500249, 3);
      var4 = this.rotateLeft(var4 + this.G(var1, var2, var3) + this.X[7] + 1518500249, 5);
      var3 = this.rotateLeft(var3 + this.G(var4, var1, var2) + this.X[11] + 1518500249, 9);
      var2 = this.rotateLeft(var2 + this.G(var3, var4, var1) + this.X[15] + 1518500249, 13);
      var1 = this.rotateLeft(var1 + this.H(var2, var3, var4) + this.X[0] + 1859775393, 3);
      var4 = this.rotateLeft(var4 + this.H(var1, var2, var3) + this.X[8] + 1859775393, 9);
      var3 = this.rotateLeft(var3 + this.H(var4, var1, var2) + this.X[4] + 1859775393, 11);
      var2 = this.rotateLeft(var2 + this.H(var3, var4, var1) + this.X[12] + 1859775393, 15);
      var1 = this.rotateLeft(var1 + this.H(var2, var3, var4) + this.X[2] + 1859775393, 3);
      var4 = this.rotateLeft(var4 + this.H(var1, var2, var3) + this.X[10] + 1859775393, 9);
      var3 = this.rotateLeft(var3 + this.H(var4, var1, var2) + this.X[6] + 1859775393, 11);
      var2 = this.rotateLeft(var2 + this.H(var3, var4, var1) + this.X[14] + 1859775393, 15);
      var1 = this.rotateLeft(var1 + this.H(var2, var3, var4) + this.X[1] + 1859775393, 3);
      var4 = this.rotateLeft(var4 + this.H(var1, var2, var3) + this.X[9] + 1859775393, 9);
      var3 = this.rotateLeft(var3 + this.H(var4, var1, var2) + this.X[5] + 1859775393, 11);
      var2 = this.rotateLeft(var2 + this.H(var3, var4, var1) + this.X[13] + 1859775393, 15);
      var1 = this.rotateLeft(var1 + this.H(var2, var3, var4) + this.X[3] + 1859775393, 3);
      var4 = this.rotateLeft(var4 + this.H(var1, var2, var3) + this.X[11] + 1859775393, 9);
      var3 = this.rotateLeft(var3 + this.H(var4, var1, var2) + this.X[7] + 1859775393, 11);
      var2 = this.rotateLeft(var2 + this.H(var3, var4, var1) + this.X[15] + 1859775393, 15);
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
      return new MD4Digest(this);
   }

   @Override
   public void reset(Memoable var1) {
      MD4Digest var2 = (MD4Digest)var1;
      this.copyIn(var2);
   }

   @Override
   protected CryptoServiceProperties cryptoServiceProperties() {
      return Utils.getDefaultProperties(this, this.purpose);
   }
}
