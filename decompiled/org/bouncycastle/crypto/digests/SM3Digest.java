package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class SM3Digest extends GeneralDigest {
   private static final int DIGEST_LENGTH = 32;
   private static final int BLOCK_SIZE = 16;
   private int[] V = new int[8];
   private int[] inwords = new int[16];
   private int xOff;
   private int[] W = new int[68];
   private static final int[] T = new int[64];

   public SM3Digest() {
      this(CryptoServicePurpose.ANY);
   }

   public SM3Digest(CryptoServicePurpose var1) {
      super(var1);
      CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
      this.reset();
   }

   public SM3Digest(SM3Digest var1) {
      super(var1);
      CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
      this.copyIn(var1);
   }

   private void copyIn(SM3Digest var1) {
      System.arraycopy(var1.V, 0, this.V, 0, this.V.length);
      System.arraycopy(var1.inwords, 0, this.inwords, 0, this.inwords.length);
      this.xOff = var1.xOff;
   }

   @Override
   public String getAlgorithmName() {
      return "SM3";
   }

   @Override
   public int getDigestSize() {
      return 32;
   }

   @Override
   public Memoable copy() {
      return new SM3Digest(this);
   }

   @Override
   public void reset(Memoable var1) {
      SM3Digest var2 = (SM3Digest)var1;
      super.copyIn(var2);
      this.copyIn(var2);
   }

   @Override
   public void reset() {
      super.reset();
      this.V[0] = 1937774191;
      this.V[1] = 1226093241;
      this.V[2] = 388252375;
      this.V[3] = -628488704;
      this.V[4] = -1452330820;
      this.V[5] = 372324522;
      this.V[6] = -477237683;
      this.V[7] = -1325724082;
      this.xOff = 0;
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      this.finish();
      Pack.intToBigEndian(this.V, var1, var2);
      this.reset();
      return 32;
   }

   @Override
   protected void processWord(byte[] var1, int var2) {
      this.inwords[this.xOff++] = Pack.bigEndianToInt(var1, var2);
      if (this.xOff >= 16) {
         this.processBlock();
      }
   }

   @Override
   protected void processLength(long var1) {
      if (this.xOff > 14) {
         this.inwords[this.xOff] = 0;
         this.xOff++;
         this.processBlock();
      }

      while (this.xOff < 14) {
         this.inwords[this.xOff] = 0;
         this.xOff++;
      }

      this.inwords[this.xOff++] = (int)(var1 >>> 32);
      this.inwords[this.xOff++] = (int)var1;
   }

   private int P0(int var1) {
      int var2 = var1 << 9 | var1 >>> 23;
      int var3 = var1 << 17 | var1 >>> 15;
      return var1 ^ var2 ^ var3;
   }

   private int P1(int var1) {
      int var2 = var1 << 15 | var1 >>> 17;
      int var3 = var1 << 23 | var1 >>> 9;
      return var1 ^ var2 ^ var3;
   }

   private int FF0(int var1, int var2, int var3) {
      return var1 ^ var2 ^ var3;
   }

   private int FF1(int var1, int var2, int var3) {
      return var1 & var2 | var1 & var3 | var2 & var3;
   }

   private int GG0(int var1, int var2, int var3) {
      return var1 ^ var2 ^ var3;
   }

   private int GG1(int var1, int var2, int var3) {
      return var1 & var2 | ~var1 & var3;
   }

   @Override
   protected void processBlock() {
      for (int var1 = 0; var1 < 16; var1++) {
         this.W[var1] = this.inwords[var1];
      }

      for (int var18 = 16; var18 < 68; var18++) {
         int var2 = this.W[var18 - 3];
         int var3 = var2 << 15 | var2 >>> 17;
         int var4 = this.W[var18 - 13];
         int var5 = var4 << 7 | var4 >>> 25;
         this.W[var18] = this.P1(this.W[var18 - 16] ^ this.W[var18 - 9] ^ var3) ^ var5 ^ this.W[var18 - 6];
      }

      int var19 = this.V[0];
      int var20 = this.V[1];
      int var21 = this.V[2];
      int var22 = this.V[3];
      int var23 = this.V[4];
      int var6 = this.V[5];
      int var7 = this.V[6];
      int var8 = this.V[7];

      for (int var9 = 0; var9 < 16; var9++) {
         int var10 = var19 << 12 | var19 >>> 20;
         int var11 = var10 + var23 + T[var9];
         int var12 = var11 << 7 | var11 >>> 25;
         int var13 = var12 ^ var10;
         int var14 = this.W[var9];
         int var15 = var14 ^ this.W[var9 + 4];
         int var16 = this.FF0(var19, var20, var21) + var22 + var13 + var15;
         int var17 = this.GG0(var23, var6, var7) + var8 + var12 + var14;
         var22 = var21;
         var21 = var20 << 9 | var20 >>> 23;
         var20 = var19;
         var19 = var16;
         var8 = var7;
         var7 = var6 << 19 | var6 >>> 13;
         var6 = var23;
         var23 = this.P0(var17);
      }

      for (int var24 = 16; var24 < 64; var24++) {
         int var25 = var19 << 12 | var19 >>> 20;
         int var26 = var25 + var23 + T[var24];
         int var27 = var26 << 7 | var26 >>> 25;
         int var28 = var27 ^ var25;
         int var29 = this.W[var24];
         int var30 = var29 ^ this.W[var24 + 4];
         int var31 = this.FF1(var19, var20, var21) + var22 + var28 + var30;
         int var32 = this.GG1(var23, var6, var7) + var8 + var27 + var29;
         var22 = var21;
         var21 = var20 << 9 | var20 >>> 23;
         var20 = var19;
         var19 = var31;
         var8 = var7;
         var7 = var6 << 19 | var6 >>> 13;
         var6 = var23;
         var23 = this.P0(var32);
      }

      this.V[0] = this.V[0] ^ var19;
      this.V[1] = this.V[1] ^ var20;
      this.V[2] = this.V[2] ^ var21;
      this.V[3] = this.V[3] ^ var22;
      this.V[4] = this.V[4] ^ var23;
      this.V[5] = this.V[5] ^ var6;
      this.V[6] = this.V[6] ^ var7;
      this.V[7] = this.V[7] ^ var8;
      this.xOff = 0;
   }

   @Override
   protected CryptoServiceProperties cryptoServiceProperties() {
      return Utils.getDefaultProperties(this, 256, this.purpose);
   }

   static {
      for (int var0 = 0; var0 < 16; var0++) {
         int var1 = 2043430169;
         T[var0] = var1 << var0 | var1 >>> 32 - var0;
      }

      for (int var3 = 16; var3 < 64; var3++) {
         int var4 = var3 % 32;
         int var2 = 2055708042;
         T[var3] = var2 << var4 | var2 >>> 32 - var4;
      }
   }
}
