package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class RIPEMD320Digest extends GeneralDigest {
   private static final int DIGEST_LENGTH = 40;
   private int H0;
   private int H1;
   private int H2;
   private int H3;
   private int H4;
   private int H5;
   private int H6;
   private int H7;
   private int H8;
   private int H9;
   private int[] X = new int[16];
   private int xOff;

   public RIPEMD320Digest() {
      this(CryptoServicePurpose.ANY);
   }

   public RIPEMD320Digest(CryptoServicePurpose var1) {
      super(var1);
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, 128, var1));
      this.reset();
   }

   public RIPEMD320Digest(RIPEMD320Digest var1) {
      super(var1.purpose);
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, 128, this.purpose));
      this.doCopy(var1);
   }

   private void doCopy(RIPEMD320Digest var1) {
      super.copyIn(var1);
      this.H0 = var1.H0;
      this.H1 = var1.H1;
      this.H2 = var1.H2;
      this.H3 = var1.H3;
      this.H4 = var1.H4;
      this.H5 = var1.H5;
      this.H6 = var1.H6;
      this.H7 = var1.H7;
      this.H8 = var1.H8;
      this.H9 = var1.H9;
      System.arraycopy(var1.X, 0, this.X, 0, var1.X.length);
      this.xOff = var1.xOff;
   }

   @Override
   public String getAlgorithmName() {
      return "RIPEMD320";
   }

   @Override
   public int getDigestSize() {
      return 40;
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
      Pack.intToLittleEndian(this.H0, var1, var2);
      Pack.intToLittleEndian(this.H1, var1, var2 + 4);
      Pack.intToLittleEndian(this.H2, var1, var2 + 8);
      Pack.intToLittleEndian(this.H3, var1, var2 + 12);
      Pack.intToLittleEndian(this.H4, var1, var2 + 16);
      Pack.intToLittleEndian(this.H5, var1, var2 + 20);
      Pack.intToLittleEndian(this.H6, var1, var2 + 24);
      Pack.intToLittleEndian(this.H7, var1, var2 + 28);
      Pack.intToLittleEndian(this.H8, var1, var2 + 32);
      Pack.intToLittleEndian(this.H9, var1, var2 + 36);
      this.reset();
      return 40;
   }

   @Override
   public void reset() {
      super.reset();
      this.H0 = 1732584193;
      this.H1 = -271733879;
      this.H2 = -1732584194;
      this.H3 = 271733878;
      this.H4 = -1009589776;
      this.H5 = 1985229328;
      this.H6 = -19088744;
      this.H7 = -1985229329;
      this.H8 = 19088743;
      this.H9 = 1009589775;
      this.xOff = 0;

      for (int var1 = 0; var1 != this.X.length; var1++) {
         this.X[var1] = 0;
      }
   }

   private int RL(int var1, int var2) {
      return var1 << var2 | var1 >>> 32 - var2;
   }

   private int f1(int var1, int var2, int var3) {
      return var1 ^ var2 ^ var3;
   }

   private int f2(int var1, int var2, int var3) {
      return var1 & var2 | ~var1 & var3;
   }

   private int f3(int var1, int var2, int var3) {
      return (var1 | ~var2) ^ var3;
   }

   private int f4(int var1, int var2, int var3) {
      return var1 & var3 | var2 & ~var3;
   }

   private int f5(int var1, int var2, int var3) {
      return var1 ^ (var2 | ~var3);
   }

   @Override
   protected void processBlock() {
      int var1 = this.H0;
      int var3 = this.H1;
      int var5 = this.H2;
      int var7 = this.H3;
      int var9 = this.H4;
      int var2 = this.H5;
      int var4 = this.H6;
      int var6 = this.H7;
      int var8 = this.H8;
      int var10 = this.H9;
      var1 = this.RL(var1 + this.f1(var3, var5, var7) + this.X[0], 11) + var9;
      var5 = this.RL(var5, 10);
      var9 = this.RL(var9 + this.f1(var1, var3, var5) + this.X[1], 14) + var7;
      var3 = this.RL(var3, 10);
      var7 = this.RL(var7 + this.f1(var9, var1, var3) + this.X[2], 15) + var5;
      var1 = this.RL(var1, 10);
      var5 = this.RL(var5 + this.f1(var7, var9, var1) + this.X[3], 12) + var3;
      var9 = this.RL(var9, 10);
      var3 = this.RL(var3 + this.f1(var5, var7, var9) + this.X[4], 5) + var1;
      var7 = this.RL(var7, 10);
      var1 = this.RL(var1 + this.f1(var3, var5, var7) + this.X[5], 8) + var9;
      var5 = this.RL(var5, 10);
      var9 = this.RL(var9 + this.f1(var1, var3, var5) + this.X[6], 7) + var7;
      var3 = this.RL(var3, 10);
      var7 = this.RL(var7 + this.f1(var9, var1, var3) + this.X[7], 9) + var5;
      var1 = this.RL(var1, 10);
      var5 = this.RL(var5 + this.f1(var7, var9, var1) + this.X[8], 11) + var3;
      var9 = this.RL(var9, 10);
      var3 = this.RL(var3 + this.f1(var5, var7, var9) + this.X[9], 13) + var1;
      var7 = this.RL(var7, 10);
      var1 = this.RL(var1 + this.f1(var3, var5, var7) + this.X[10], 14) + var9;
      var5 = this.RL(var5, 10);
      var9 = this.RL(var9 + this.f1(var1, var3, var5) + this.X[11], 15) + var7;
      var3 = this.RL(var3, 10);
      var7 = this.RL(var7 + this.f1(var9, var1, var3) + this.X[12], 6) + var5;
      var1 = this.RL(var1, 10);
      var5 = this.RL(var5 + this.f1(var7, var9, var1) + this.X[13], 7) + var3;
      var9 = this.RL(var9, 10);
      var3 = this.RL(var3 + this.f1(var5, var7, var9) + this.X[14], 9) + var1;
      var7 = this.RL(var7, 10);
      var1 = this.RL(var1 + this.f1(var3, var5, var7) + this.X[15], 8) + var9;
      var5 = this.RL(var5, 10);
      var2 = this.RL(var2 + this.f5(var4, var6, var8) + this.X[5] + 1352829926, 8) + var10;
      var6 = this.RL(var6, 10);
      var10 = this.RL(var10 + this.f5(var2, var4, var6) + this.X[14] + 1352829926, 9) + var8;
      var4 = this.RL(var4, 10);
      var8 = this.RL(var8 + this.f5(var10, var2, var4) + this.X[7] + 1352829926, 9) + var6;
      var2 = this.RL(var2, 10);
      var6 = this.RL(var6 + this.f5(var8, var10, var2) + this.X[0] + 1352829926, 11) + var4;
      var10 = this.RL(var10, 10);
      var4 = this.RL(var4 + this.f5(var6, var8, var10) + this.X[9] + 1352829926, 13) + var2;
      var8 = this.RL(var8, 10);
      var2 = this.RL(var2 + this.f5(var4, var6, var8) + this.X[2] + 1352829926, 15) + var10;
      var6 = this.RL(var6, 10);
      var10 = this.RL(var10 + this.f5(var2, var4, var6) + this.X[11] + 1352829926, 15) + var8;
      var4 = this.RL(var4, 10);
      var8 = this.RL(var8 + this.f5(var10, var2, var4) + this.X[4] + 1352829926, 5) + var6;
      var2 = this.RL(var2, 10);
      var6 = this.RL(var6 + this.f5(var8, var10, var2) + this.X[13] + 1352829926, 7) + var4;
      var10 = this.RL(var10, 10);
      var4 = this.RL(var4 + this.f5(var6, var8, var10) + this.X[6] + 1352829926, 7) + var2;
      var8 = this.RL(var8, 10);
      var2 = this.RL(var2 + this.f5(var4, var6, var8) + this.X[15] + 1352829926, 8) + var10;
      var6 = this.RL(var6, 10);
      var10 = this.RL(var10 + this.f5(var2, var4, var6) + this.X[8] + 1352829926, 11) + var8;
      var4 = this.RL(var4, 10);
      var8 = this.RL(var8 + this.f5(var10, var2, var4) + this.X[1] + 1352829926, 14) + var6;
      var2 = this.RL(var2, 10);
      var6 = this.RL(var6 + this.f5(var8, var10, var2) + this.X[10] + 1352829926, 14) + var4;
      var10 = this.RL(var10, 10);
      var4 = this.RL(var4 + this.f5(var6, var8, var10) + this.X[3] + 1352829926, 12) + var2;
      var8 = this.RL(var8, 10);
      var2 = this.RL(var2 + this.f5(var4, var6, var8) + this.X[12] + 1352829926, 6) + var10;
      var6 = this.RL(var6, 10);
      var9 = this.RL(var9 + this.f2(var2, var3, var5) + this.X[7] + 1518500249, 7) + var7;
      var3 = this.RL(var3, 10);
      var7 = this.RL(var7 + this.f2(var9, var2, var3) + this.X[4] + 1518500249, 6) + var5;
      int var20 = this.RL(var2, 10);
      var5 = this.RL(var5 + this.f2(var7, var9, var20) + this.X[13] + 1518500249, 8) + var3;
      var9 = this.RL(var9, 10);
      var3 = this.RL(var3 + this.f2(var5, var7, var9) + this.X[1] + 1518500249, 13) + var20;
      var7 = this.RL(var7, 10);
      int var21 = this.RL(var20 + this.f2(var3, var5, var7) + this.X[10] + 1518500249, 11) + var9;
      var5 = this.RL(var5, 10);
      var9 = this.RL(var9 + this.f2(var21, var3, var5) + this.X[6] + 1518500249, 9) + var7;
      var3 = this.RL(var3, 10);
      var7 = this.RL(var7 + this.f2(var9, var21, var3) + this.X[15] + 1518500249, 7) + var5;
      int var22 = this.RL(var21, 10);
      var5 = this.RL(var5 + this.f2(var7, var9, var22) + this.X[3] + 1518500249, 15) + var3;
      var9 = this.RL(var9, 10);
      var3 = this.RL(var3 + this.f2(var5, var7, var9) + this.X[12] + 1518500249, 7) + var22;
      var7 = this.RL(var7, 10);
      int var23 = this.RL(var22 + this.f2(var3, var5, var7) + this.X[0] + 1518500249, 12) + var9;
      var5 = this.RL(var5, 10);
      var9 = this.RL(var9 + this.f2(var23, var3, var5) + this.X[9] + 1518500249, 15) + var7;
      var3 = this.RL(var3, 10);
      var7 = this.RL(var7 + this.f2(var9, var23, var3) + this.X[5] + 1518500249, 9) + var5;
      int var24 = this.RL(var23, 10);
      var5 = this.RL(var5 + this.f2(var7, var9, var24) + this.X[2] + 1518500249, 11) + var3;
      var9 = this.RL(var9, 10);
      var3 = this.RL(var3 + this.f2(var5, var7, var9) + this.X[14] + 1518500249, 7) + var24;
      var7 = this.RL(var7, 10);
      int var25 = this.RL(var24 + this.f2(var3, var5, var7) + this.X[11] + 1518500249, 13) + var9;
      var5 = this.RL(var5, 10);
      var9 = this.RL(var9 + this.f2(var25, var3, var5) + this.X[8] + 1518500249, 12) + var7;
      var3 = this.RL(var3, 10);
      var10 = this.RL(var10 + this.f4(var1, var4, var6) + this.X[6] + 1548603684, 9) + var8;
      var4 = this.RL(var4, 10);
      var8 = this.RL(var8 + this.f4(var10, var1, var4) + this.X[11] + 1548603684, 13) + var6;
      var2 = this.RL(var1, 10);
      var6 = this.RL(var6 + this.f4(var8, var10, var2) + this.X[3] + 1548603684, 15) + var4;
      var10 = this.RL(var10, 10);
      var4 = this.RL(var4 + this.f4(var6, var8, var10) + this.X[7] + 1548603684, 7) + var2;
      var8 = this.RL(var8, 10);
      var2 = this.RL(var2 + this.f4(var4, var6, var8) + this.X[0] + 1548603684, 12) + var10;
      var6 = this.RL(var6, 10);
      var10 = this.RL(var10 + this.f4(var2, var4, var6) + this.X[13] + 1548603684, 8) + var8;
      var4 = this.RL(var4, 10);
      var8 = this.RL(var8 + this.f4(var10, var2, var4) + this.X[5] + 1548603684, 9) + var6;
      var2 = this.RL(var2, 10);
      var6 = this.RL(var6 + this.f4(var8, var10, var2) + this.X[10] + 1548603684, 11) + var4;
      var10 = this.RL(var10, 10);
      var4 = this.RL(var4 + this.f4(var6, var8, var10) + this.X[14] + 1548603684, 7) + var2;
      var8 = this.RL(var8, 10);
      var2 = this.RL(var2 + this.f4(var4, var6, var8) + this.X[15] + 1548603684, 7) + var10;
      var6 = this.RL(var6, 10);
      var10 = this.RL(var10 + this.f4(var2, var4, var6) + this.X[8] + 1548603684, 12) + var8;
      var4 = this.RL(var4, 10);
      var8 = this.RL(var8 + this.f4(var10, var2, var4) + this.X[12] + 1548603684, 7) + var6;
      var2 = this.RL(var2, 10);
      var6 = this.RL(var6 + this.f4(var8, var10, var2) + this.X[4] + 1548603684, 6) + var4;
      var10 = this.RL(var10, 10);
      var4 = this.RL(var4 + this.f4(var6, var8, var10) + this.X[9] + 1548603684, 15) + var2;
      var8 = this.RL(var8, 10);
      var2 = this.RL(var2 + this.f4(var4, var6, var8) + this.X[1] + 1548603684, 13) + var10;
      var6 = this.RL(var6, 10);
      var10 = this.RL(var10 + this.f4(var2, var4, var6) + this.X[2] + 1548603684, 11) + var8;
      var4 = this.RL(var4, 10);
      var7 = this.RL(var7 + this.f3(var9, var25, var4) + this.X[3] + 1859775393, 11) + var5;
      var1 = this.RL(var25, 10);
      var5 = this.RL(var5 + this.f3(var7, var9, var1) + this.X[10] + 1859775393, 13) + var4;
      var9 = this.RL(var9, 10);
      int var90 = this.RL(var4 + this.f3(var5, var7, var9) + this.X[14] + 1859775393, 6) + var1;
      var7 = this.RL(var7, 10);
      var1 = this.RL(var1 + this.f3(var90, var5, var7) + this.X[4] + 1859775393, 7) + var9;
      var5 = this.RL(var5, 10);
      var9 = this.RL(var9 + this.f3(var1, var90, var5) + this.X[9] + 1859775393, 14) + var7;
      int var91 = this.RL(var90, 10);
      var7 = this.RL(var7 + this.f3(var9, var1, var91) + this.X[15] + 1859775393, 9) + var5;
      var1 = this.RL(var1, 10);
      var5 = this.RL(var5 + this.f3(var7, var9, var1) + this.X[8] + 1859775393, 13) + var91;
      var9 = this.RL(var9, 10);
      int var92 = this.RL(var91 + this.f3(var5, var7, var9) + this.X[1] + 1859775393, 15) + var1;
      var7 = this.RL(var7, 10);
      var1 = this.RL(var1 + this.f3(var92, var5, var7) + this.X[2] + 1859775393, 14) + var9;
      var5 = this.RL(var5, 10);
      var9 = this.RL(var9 + this.f3(var1, var92, var5) + this.X[7] + 1859775393, 8) + var7;
      int var93 = this.RL(var92, 10);
      var7 = this.RL(var7 + this.f3(var9, var1, var93) + this.X[0] + 1859775393, 13) + var5;
      var1 = this.RL(var1, 10);
      var5 = this.RL(var5 + this.f3(var7, var9, var1) + this.X[6] + 1859775393, 6) + var93;
      var9 = this.RL(var9, 10);
      int var94 = this.RL(var93 + this.f3(var5, var7, var9) + this.X[13] + 1859775393, 5) + var1;
      var7 = this.RL(var7, 10);
      var1 = this.RL(var1 + this.f3(var94, var5, var7) + this.X[11] + 1859775393, 12) + var9;
      var5 = this.RL(var5, 10);
      var9 = this.RL(var9 + this.f3(var1, var94, var5) + this.X[5] + 1859775393, 7) + var7;
      int var95 = this.RL(var94, 10);
      var7 = this.RL(var7 + this.f3(var9, var1, var95) + this.X[12] + 1859775393, 5) + var5;
      var1 = this.RL(var1, 10);
      var8 = this.RL(var8 + this.f3(var10, var2, var3) + this.X[15] + 1836072691, 9) + var6;
      var2 = this.RL(var2, 10);
      var6 = this.RL(var6 + this.f3(var8, var10, var2) + this.X[5] + 1836072691, 7) + var3;
      var10 = this.RL(var10, 10);
      var4 = this.RL(var3 + this.f3(var6, var8, var10) + this.X[1] + 1836072691, 15) + var2;
      var8 = this.RL(var8, 10);
      var2 = this.RL(var2 + this.f3(var4, var6, var8) + this.X[3] + 1836072691, 11) + var10;
      var6 = this.RL(var6, 10);
      var10 = this.RL(var10 + this.f3(var2, var4, var6) + this.X[7] + 1836072691, 8) + var8;
      var4 = this.RL(var4, 10);
      var8 = this.RL(var8 + this.f3(var10, var2, var4) + this.X[14] + 1836072691, 6) + var6;
      var2 = this.RL(var2, 10);
      var6 = this.RL(var6 + this.f3(var8, var10, var2) + this.X[6] + 1836072691, 6) + var4;
      var10 = this.RL(var10, 10);
      var4 = this.RL(var4 + this.f3(var6, var8, var10) + this.X[9] + 1836072691, 14) + var2;
      var8 = this.RL(var8, 10);
      var2 = this.RL(var2 + this.f3(var4, var6, var8) + this.X[11] + 1836072691, 12) + var10;
      var6 = this.RL(var6, 10);
      var10 = this.RL(var10 + this.f3(var2, var4, var6) + this.X[8] + 1836072691, 13) + var8;
      var4 = this.RL(var4, 10);
      var8 = this.RL(var8 + this.f3(var10, var2, var4) + this.X[12] + 1836072691, 5) + var6;
      var2 = this.RL(var2, 10);
      var6 = this.RL(var6 + this.f3(var8, var10, var2) + this.X[2] + 1836072691, 14) + var4;
      var10 = this.RL(var10, 10);
      var4 = this.RL(var4 + this.f3(var6, var8, var10) + this.X[10] + 1836072691, 13) + var2;
      var8 = this.RL(var8, 10);
      var2 = this.RL(var2 + this.f3(var4, var6, var8) + this.X[0] + 1836072691, 13) + var10;
      var6 = this.RL(var6, 10);
      var10 = this.RL(var10 + this.f3(var2, var4, var6) + this.X[4] + 1836072691, 7) + var8;
      var4 = this.RL(var4, 10);
      var8 = this.RL(var8 + this.f3(var10, var2, var4) + this.X[13] + 1836072691, 5) + var6;
      var2 = this.RL(var2, 10);
      int var160 = this.RL(var6 + this.f4(var7, var9, var1) + this.X[1] + -1894007588, 11) + var95;
      var9 = this.RL(var9, 10);
      var3 = this.RL(var95 + this.f4(var160, var7, var9) + this.X[9] + -1894007588, 12) + var1;
      var7 = this.RL(var7, 10);
      var1 = this.RL(var1 + this.f4(var3, var160, var7) + this.X[11] + -1894007588, 14) + var9;
      int var161 = this.RL(var160, 10);
      var9 = this.RL(var9 + this.f4(var1, var3, var161) + this.X[10] + -1894007588, 15) + var7;
      var3 = this.RL(var3, 10);
      var7 = this.RL(var7 + this.f4(var9, var1, var3) + this.X[0] + -1894007588, 14) + var161;
      var1 = this.RL(var1, 10);
      int var162 = this.RL(var161 + this.f4(var7, var9, var1) + this.X[8] + -1894007588, 15) + var3;
      var9 = this.RL(var9, 10);
      var3 = this.RL(var3 + this.f4(var162, var7, var9) + this.X[12] + -1894007588, 9) + var1;
      var7 = this.RL(var7, 10);
      var1 = this.RL(var1 + this.f4(var3, var162, var7) + this.X[4] + -1894007588, 8) + var9;
      int var163 = this.RL(var162, 10);
      var9 = this.RL(var9 + this.f4(var1, var3, var163) + this.X[13] + -1894007588, 9) + var7;
      var3 = this.RL(var3, 10);
      var7 = this.RL(var7 + this.f4(var9, var1, var3) + this.X[3] + -1894007588, 14) + var163;
      var1 = this.RL(var1, 10);
      int var164 = this.RL(var163 + this.f4(var7, var9, var1) + this.X[7] + -1894007588, 5) + var3;
      var9 = this.RL(var9, 10);
      var3 = this.RL(var3 + this.f4(var164, var7, var9) + this.X[15] + -1894007588, 6) + var1;
      var7 = this.RL(var7, 10);
      var1 = this.RL(var1 + this.f4(var3, var164, var7) + this.X[14] + -1894007588, 8) + var9;
      int var165 = this.RL(var164, 10);
      var9 = this.RL(var9 + this.f4(var1, var3, var165) + this.X[5] + -1894007588, 6) + var7;
      var3 = this.RL(var3, 10);
      var7 = this.RL(var7 + this.f4(var9, var1, var3) + this.X[6] + -1894007588, 5) + var165;
      var1 = this.RL(var1, 10);
      int var166 = this.RL(var165 + this.f4(var7, var9, var1) + this.X[2] + -1894007588, 12) + var3;
      var9 = this.RL(var9, 10);
      var6 = this.RL(var5 + this.f2(var8, var10, var2) + this.X[8] + 2053994217, 15) + var4;
      var10 = this.RL(var10, 10);
      var4 = this.RL(var4 + this.f2(var6, var8, var10) + this.X[6] + 2053994217, 5) + var2;
      var8 = this.RL(var8, 10);
      var2 = this.RL(var2 + this.f2(var4, var6, var8) + this.X[4] + 2053994217, 8) + var10;
      var6 = this.RL(var6, 10);
      var10 = this.RL(var10 + this.f2(var2, var4, var6) + this.X[1] + 2053994217, 11) + var8;
      var4 = this.RL(var4, 10);
      var8 = this.RL(var8 + this.f2(var10, var2, var4) + this.X[3] + 2053994217, 14) + var6;
      var2 = this.RL(var2, 10);
      var6 = this.RL(var6 + this.f2(var8, var10, var2) + this.X[11] + 2053994217, 14) + var4;
      var10 = this.RL(var10, 10);
      var4 = this.RL(var4 + this.f2(var6, var8, var10) + this.X[15] + 2053994217, 6) + var2;
      var8 = this.RL(var8, 10);
      var2 = this.RL(var2 + this.f2(var4, var6, var8) + this.X[0] + 2053994217, 14) + var10;
      var6 = this.RL(var6, 10);
      var10 = this.RL(var10 + this.f2(var2, var4, var6) + this.X[5] + 2053994217, 6) + var8;
      var4 = this.RL(var4, 10);
      var8 = this.RL(var8 + this.f2(var10, var2, var4) + this.X[12] + 2053994217, 9) + var6;
      var2 = this.RL(var2, 10);
      var6 = this.RL(var6 + this.f2(var8, var10, var2) + this.X[2] + 2053994217, 12) + var4;
      var10 = this.RL(var10, 10);
      var4 = this.RL(var4 + this.f2(var6, var8, var10) + this.X[13] + 2053994217, 9) + var2;
      var8 = this.RL(var8, 10);
      var2 = this.RL(var2 + this.f2(var4, var6, var8) + this.X[9] + 2053994217, 12) + var10;
      var6 = this.RL(var6, 10);
      var10 = this.RL(var10 + this.f2(var2, var4, var6) + this.X[7] + 2053994217, 5) + var8;
      var4 = this.RL(var4, 10);
      var8 = this.RL(var8 + this.f2(var10, var2, var4) + this.X[10] + 2053994217, 15) + var6;
      var2 = this.RL(var2, 10);
      var6 = this.RL(var6 + this.f2(var8, var10, var2) + this.X[14] + 2053994217, 8) + var4;
      var10 = this.RL(var10, 10);
      var3 = this.RL(var3 + this.f5(var166, var8, var9) + this.X[4] + -1454113458, 9) + var1;
      int var230 = this.RL(var8, 10);
      var1 = this.RL(var1 + this.f5(var3, var166, var230) + this.X[0] + -1454113458, 15) + var9;
      var5 = this.RL(var166, 10);
      var9 = this.RL(var9 + this.f5(var1, var3, var5) + this.X[5] + -1454113458, 5) + var230;
      var3 = this.RL(var3, 10);
      int var231 = this.RL(var230 + this.f5(var9, var1, var3) + this.X[9] + -1454113458, 11) + var5;
      var1 = this.RL(var1, 10);
      var5 = this.RL(var5 + this.f5(var231, var9, var1) + this.X[7] + -1454113458, 6) + var3;
      var9 = this.RL(var9, 10);
      var3 = this.RL(var3 + this.f5(var5, var231, var9) + this.X[12] + -1454113458, 8) + var1;
      int var232 = this.RL(var231, 10);
      var1 = this.RL(var1 + this.f5(var3, var5, var232) + this.X[2] + -1454113458, 13) + var9;
      var5 = this.RL(var5, 10);
      var9 = this.RL(var9 + this.f5(var1, var3, var5) + this.X[10] + -1454113458, 12) + var232;
      var3 = this.RL(var3, 10);
      int var233 = this.RL(var232 + this.f5(var9, var1, var3) + this.X[14] + -1454113458, 5) + var5;
      var1 = this.RL(var1, 10);
      var5 = this.RL(var5 + this.f5(var233, var9, var1) + this.X[1] + -1454113458, 12) + var3;
      var9 = this.RL(var9, 10);
      var3 = this.RL(var3 + this.f5(var5, var233, var9) + this.X[3] + -1454113458, 13) + var1;
      int var234 = this.RL(var233, 10);
      var1 = this.RL(var1 + this.f5(var3, var5, var234) + this.X[8] + -1454113458, 14) + var9;
      var5 = this.RL(var5, 10);
      var9 = this.RL(var9 + this.f5(var1, var3, var5) + this.X[11] + -1454113458, 11) + var234;
      var3 = this.RL(var3, 10);
      int var235 = this.RL(var234 + this.f5(var9, var1, var3) + this.X[6] + -1454113458, 8) + var5;
      var1 = this.RL(var1, 10);
      var5 = this.RL(var5 + this.f5(var235, var9, var1) + this.X[15] + -1454113458, 5) + var3;
      var9 = this.RL(var9, 10);
      var3 = this.RL(var3 + this.f5(var5, var235, var9) + this.X[13] + -1454113458, 6) + var1;
      int var236 = this.RL(var235, 10);
      var4 = this.RL(var4 + this.f1(var6, var7, var10) + this.X[12], 8) + var2;
      var8 = this.RL(var7, 10);
      var2 = this.RL(var2 + this.f1(var4, var6, var8) + this.X[15], 5) + var10;
      var6 = this.RL(var6, 10);
      var10 = this.RL(var10 + this.f1(var2, var4, var6) + this.X[10], 12) + var8;
      var4 = this.RL(var4, 10);
      var8 = this.RL(var8 + this.f1(var10, var2, var4) + this.X[4], 9) + var6;
      var2 = this.RL(var2, 10);
      var6 = this.RL(var6 + this.f1(var8, var10, var2) + this.X[1], 12) + var4;
      var10 = this.RL(var10, 10);
      var4 = this.RL(var4 + this.f1(var6, var8, var10) + this.X[5], 5) + var2;
      var8 = this.RL(var8, 10);
      var2 = this.RL(var2 + this.f1(var4, var6, var8) + this.X[8], 14) + var10;
      var6 = this.RL(var6, 10);
      var10 = this.RL(var10 + this.f1(var2, var4, var6) + this.X[7], 6) + var8;
      var4 = this.RL(var4, 10);
      var8 = this.RL(var8 + this.f1(var10, var2, var4) + this.X[6], 8) + var6;
      var2 = this.RL(var2, 10);
      var6 = this.RL(var6 + this.f1(var8, var10, var2) + this.X[2], 13) + var4;
      var10 = this.RL(var10, 10);
      var4 = this.RL(var4 + this.f1(var6, var8, var10) + this.X[13], 6) + var2;
      var8 = this.RL(var8, 10);
      var2 = this.RL(var2 + this.f1(var4, var6, var8) + this.X[14], 5) + var10;
      var6 = this.RL(var6, 10);
      var10 = this.RL(var10 + this.f1(var2, var4, var6) + this.X[0], 15) + var8;
      var4 = this.RL(var4, 10);
      var8 = this.RL(var8 + this.f1(var10, var2, var4) + this.X[3], 13) + var6;
      var2 = this.RL(var2, 10);
      var6 = this.RL(var6 + this.f1(var8, var10, var2) + this.X[9], 11) + var4;
      var10 = this.RL(var10, 10);
      var4 = this.RL(var4 + this.f1(var6, var8, var10) + this.X[11], 11) + var2;
      var8 = this.RL(var8, 10);
      this.H0 += var1;
      this.H1 += var3;
      this.H2 += var5;
      this.H3 += var236;
      this.H4 += var10;
      this.H5 += var2;
      this.H6 += var4;
      this.H7 += var6;
      this.H8 += var8;
      this.H9 += var9;
      this.xOff = 0;

      for (int var12 = 0; var12 != this.X.length; var12++) {
         this.X[var12] = 0;
      }
   }

   @Override
   public Memoable copy() {
      return new RIPEMD320Digest(this);
   }

   @Override
   public void reset(Memoable var1) {
      RIPEMD320Digest var2 = (RIPEMD320Digest)var1;
      this.doCopy(var2);
   }

   @Override
   protected CryptoServiceProperties cryptoServiceProperties() {
      return Utils.getDefaultProperties(this, this.purpose);
   }
}
