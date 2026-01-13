package org.bouncycastle.pqc.crypto.mldsa;

import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.util.Arrays;

class MLDSAEngine {
   private final SecureRandom random;
   final SHAKEDigest shake256Digest = new SHAKEDigest(256);
   public static final int DilithiumN = 256;
   public static final int DilithiumQ = 8380417;
   public static final int DilithiumQinv = 58728449;
   public static final int DilithiumD = 13;
   public static final int SeedBytes = 32;
   public static final int CrhBytes = 64;
   public static final int RndBytes = 32;
   public static final int TrBytes = 64;
   public static final int DilithiumPolyT1PackedBytes = 320;
   public static final int DilithiumPolyT0PackedBytes = 416;
   private final int DilithiumPolyVecHPackedBytes;
   private final int DilithiumPolyZPackedBytes;
   private final int DilithiumPolyW1PackedBytes;
   private final int DilithiumPolyEtaPackedBytes;
   private final int DilithiumK;
   private final int DilithiumL;
   private final int DilithiumEta;
   private final int DilithiumTau;
   private final int DilithiumBeta;
   private final int DilithiumGamma1;
   private final int DilithiumGamma2;
   private final int DilithiumOmega;
   private final int DilithiumCTilde;
   private final int CryptoPublicKeyBytes;
   private final int CryptoBytes;
   private final int PolyUniformGamma1NBlocks;
   private final Symmetric symmetric;

   protected Symmetric GetSymmetric() {
      return this.symmetric;
   }

   int getDilithiumPolyZPackedBytes() {
      return this.DilithiumPolyZPackedBytes;
   }

   int getDilithiumPolyW1PackedBytes() {
      return this.DilithiumPolyW1PackedBytes;
   }

   int getDilithiumPolyEtaPackedBytes() {
      return this.DilithiumPolyEtaPackedBytes;
   }

   int getDilithiumK() {
      return this.DilithiumK;
   }

   int getDilithiumL() {
      return this.DilithiumL;
   }

   int getDilithiumEta() {
      return this.DilithiumEta;
   }

   int getDilithiumTau() {
      return this.DilithiumTau;
   }

   int getDilithiumBeta() {
      return this.DilithiumBeta;
   }

   int getDilithiumGamma1() {
      return this.DilithiumGamma1;
   }

   int getDilithiumGamma2() {
      return this.DilithiumGamma2;
   }

   int getDilithiumOmega() {
      return this.DilithiumOmega;
   }

   int getDilithiumCTilde() {
      return this.DilithiumCTilde;
   }

   int getCryptoPublicKeyBytes() {
      return this.CryptoPublicKeyBytes;
   }

   int getPolyUniformGamma1NBlocks() {
      return this.PolyUniformGamma1NBlocks;
   }

   MLDSAEngine(int var1, SecureRandom var2) {
      switch (var1) {
         case 2:
            this.DilithiumK = 4;
            this.DilithiumL = 4;
            this.DilithiumEta = 2;
            this.DilithiumTau = 39;
            this.DilithiumBeta = 78;
            this.DilithiumGamma1 = 131072;
            this.DilithiumGamma2 = 95232;
            this.DilithiumOmega = 80;
            this.DilithiumPolyZPackedBytes = 576;
            this.DilithiumPolyW1PackedBytes = 192;
            this.DilithiumPolyEtaPackedBytes = 96;
            this.DilithiumCTilde = 32;
            break;
         case 3:
            this.DilithiumK = 6;
            this.DilithiumL = 5;
            this.DilithiumEta = 4;
            this.DilithiumTau = 49;
            this.DilithiumBeta = 196;
            this.DilithiumGamma1 = 524288;
            this.DilithiumGamma2 = 261888;
            this.DilithiumOmega = 55;
            this.DilithiumPolyZPackedBytes = 640;
            this.DilithiumPolyW1PackedBytes = 128;
            this.DilithiumPolyEtaPackedBytes = 128;
            this.DilithiumCTilde = 48;
            break;
         case 4:
         default:
            throw new IllegalArgumentException("The mode " + var1 + "is not supported by Crystals Dilithium!");
         case 5:
            this.DilithiumK = 8;
            this.DilithiumL = 7;
            this.DilithiumEta = 2;
            this.DilithiumTau = 60;
            this.DilithiumBeta = 120;
            this.DilithiumGamma1 = 524288;
            this.DilithiumGamma2 = 261888;
            this.DilithiumOmega = 75;
            this.DilithiumPolyZPackedBytes = 640;
            this.DilithiumPolyW1PackedBytes = 128;
            this.DilithiumPolyEtaPackedBytes = 96;
            this.DilithiumCTilde = 64;
      }

      this.symmetric = new Symmetric.ShakeSymmetric();
      this.random = var2;
      this.DilithiumPolyVecHPackedBytes = this.DilithiumOmega + this.DilithiumK;
      this.CryptoPublicKeyBytes = 32 + this.DilithiumK * 320;
      this.CryptoBytes = this.DilithiumCTilde + this.DilithiumL * this.DilithiumPolyZPackedBytes + this.DilithiumPolyVecHPackedBytes;
      if (this.DilithiumGamma1 == 131072) {
         this.PolyUniformGamma1NBlocks = (576 + this.symmetric.stream256BlockBytes - 1) / this.symmetric.stream256BlockBytes;
      } else {
         if (this.DilithiumGamma1 != 524288) {
            throw new RuntimeException("Wrong Dilithium Gamma1!");
         }

         this.PolyUniformGamma1NBlocks = (640 + this.symmetric.stream256BlockBytes - 1) / this.symmetric.stream256BlockBytes;
      }
   }

   byte[][] generateKeyPairInternal(byte[] var1) {
      byte[] var2 = new byte[128];
      byte[] var3 = new byte[64];
      byte[] var4 = new byte[32];
      byte[] var5 = new byte[64];
      byte[] var6 = new byte[32];
      PolyVecMatrix var7 = new PolyVecMatrix(this);
      PolyVecL var8 = new PolyVecL(this);
      PolyVecK var10 = new PolyVecK(this);
      PolyVecK var11 = new PolyVecK(this);
      PolyVecK var12 = new PolyVecK(this);
      this.shake256Digest.update(var1, 0, 32);
      this.shake256Digest.update((byte)this.DilithiumK);
      this.shake256Digest.update((byte)this.DilithiumL);
      this.shake256Digest.doFinal(var2, 0, 128);
      System.arraycopy(var2, 0, var4, 0, 32);
      System.arraycopy(var2, 32, var5, 0, 64);
      System.arraycopy(var2, 96, var6, 0, 32);
      var7.expandMatrix(var4);
      var8.uniformEta(var5, (short)0);
      var10.uniformEta(var5, (short)this.DilithiumL);
      PolyVecL var9 = new PolyVecL(this);
      var8.copyTo(var9);
      var9.polyVecNtt();
      var7.pointwiseMontgomery(var11, var9);
      var11.reduce();
      var11.invNttToMont();
      var11.addPolyVecK(var10);
      var11.conditionalAddQ();
      var11.power2Round(var12);
      byte[] var13 = Packing.packPublicKey(var11, this);
      this.shake256Digest.update(var4, 0, var4.length);
      this.shake256Digest.update(var13, 0, var13.length);
      this.shake256Digest.doFinal(var3, 0, 64);
      byte[][] var14 = Packing.packSecretKey(var4, var3, var6, var12, var8, var10, this);
      return new byte[][]{var14[0], var14[1], var14[2], var14[3], var14[4], var14[5], var13, var1};
   }

   byte[] deriveT1(byte[] var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6) {
      PolyVecMatrix var7 = new PolyVecMatrix(this);
      PolyVecL var8 = new PolyVecL(this);
      PolyVecK var10 = new PolyVecK(this);
      PolyVecK var11 = new PolyVecK(this);
      PolyVecK var12 = new PolyVecK(this);
      Packing.unpackSecretKey(var12, var8, var10, var6, var4, var5, this);
      var7.expandMatrix(var1);
      PolyVecL var9 = new PolyVecL(this);
      var8.copyTo(var9);
      var9.polyVecNtt();
      var7.pointwiseMontgomery(var11, var9);
      var11.reduce();
      var11.invNttToMont();
      var11.addPolyVecK(var10);
      var11.conditionalAddQ();
      var11.power2Round(var12);
      return Packing.packPublicKey(var11, this);
   }

   SHAKEDigest getShake256Digest() {
      return new SHAKEDigest(this.shake256Digest);
   }

   void initSign(byte[] var1, boolean var2, byte[] var3) {
      this.shake256Digest.update(var1, 0, 64);
      this.absorbCtx(var2, var3);
   }

   void initVerify(byte[] var1, byte[] var2, boolean var3, byte[] var4) {
      byte[] var5 = new byte[64];
      this.shake256Digest.update(var1, 0, var1.length);
      this.shake256Digest.update(var2, 0, var2.length);
      this.shake256Digest.doFinal(var5, 0, 64);
      this.shake256Digest.update(var5, 0, 64);
      this.absorbCtx(var3, var4);
   }

   void absorbCtx(boolean var1, byte[] var2) {
      if (var2 != null) {
         this.shake256Digest.update((byte)(var1 ? 1 : 0));
         this.shake256Digest.update((byte)var2.length);
         this.shake256Digest.update(var2, 0, var2.length);
      }
   }

   byte[] signInternal(byte[] var1, int var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6, byte[] var7, byte[] var8) {
      SHAKEDigest var9 = new SHAKEDigest(this.shake256Digest);
      var9.update(var1, 0, var2);
      return this.generateSignature(this.generateMu(var9), var9, var3, var4, var5, var6, var7, var8);
   }

   byte[] generateMu(SHAKEDigest var1) {
      byte[] var2 = new byte[64];
      var1.doFinal(var2, 0, 64);
      return var2;
   }

   byte[] generateSignature(byte[] var1, SHAKEDigest var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6, byte[] var7, byte[] var8) {
      byte[] var9 = new byte[this.CryptoBytes];
      byte[] var10 = new byte[64];
      short var11 = 0;
      PolyVecL var12 = new PolyVecL(this);
      PolyVecL var13 = new PolyVecL(this);
      PolyVecL var14 = new PolyVecL(this);
      PolyVecK var15 = new PolyVecK(this);
      PolyVecK var16 = new PolyVecK(this);
      PolyVecK var17 = new PolyVecK(this);
      PolyVecK var18 = new PolyVecK(this);
      PolyVecK var19 = new PolyVecK(this);
      Poly var20 = new Poly(this);
      PolyVecMatrix var21 = new PolyVecMatrix(this);
      Packing.unpackSecretKey(var15, var12, var16, var5, var6, var7, this);
      byte[] var22 = Arrays.copyOf(var4, 128);
      System.arraycopy(var8, 0, var22, 32, 32);
      System.arraycopy(var1, 0, var22, 64, 64);
      var2.update(var22, 0, 128);
      var2.doFinal(var10, 0, 64);
      var21.expandMatrix(var3);
      var12.polyVecNtt();
      var16.polyVecNtt();
      var15.polyVecNtt();
      int var23 = 0;

      while (var23 < 1000) {
         var23++;
         var13.uniformGamma1(var10, var11++);
         var13.copyTo(var14);
         var14.polyVecNtt();
         var21.pointwiseMontgomery(var17, var14);
         var17.reduce();
         var17.invNttToMont();
         var17.conditionalAddQ();
         var17.decompose(var18);
         var17.packW1(this, var9, 0);
         var2.update(var1, 0, 64);
         var2.update(var9, 0, this.DilithiumK * this.DilithiumPolyW1PackedBytes);
         var2.doFinal(var9, 0, this.DilithiumCTilde);
         var20.challenge(var9, 0, this.DilithiumCTilde);
         var20.polyNtt();
         var14.pointwisePolyMontgomery(var20, var12);
         var14.invNttToMont();
         var14.addPolyVecL(var13);
         var14.reduce();
         if (!var14.checkNorm(this.DilithiumGamma1 - this.DilithiumBeta)) {
            var19.pointwisePolyMontgomery(var20, var16);
            var19.invNttToMont();
            var18.subtract(var19);
            var18.reduce();
            if (!var18.checkNorm(this.DilithiumGamma2 - this.DilithiumBeta)) {
               var19.pointwisePolyMontgomery(var20, var15);
               var19.invNttToMont();
               var19.reduce();
               if (!var19.checkNorm(this.DilithiumGamma2)) {
                  var18.addPolyVecK(var19);
                  var18.conditionalAddQ();
                  int var24 = var19.makeHint(var18, var17);
                  if (var24 <= this.DilithiumOmega) {
                     Packing.packSignature(var9, var14, var19, this);
                     return var9;
                  }
               }
            }
         }
      }

      return null;
   }

   boolean verifyInternalMu(byte[] var1) {
      byte[] var2 = new byte[64];
      this.shake256Digest.doFinal(var2, 0);
      return Arrays.constantTimeAreEqual(var2, var1);
   }

   boolean verifyInternalMuSignature(byte[] var1, byte[] var2, int var3, SHAKEDigest var4, byte[] var5, byte[] var6) {
      byte[] var7 = new byte[Math.max(64 + this.DilithiumK * this.DilithiumPolyW1PackedBytes, this.DilithiumCTilde)];
      System.arraycopy(var1, 0, var7, 0, var1.length);
      return this.doVerifyInternal(var7, var2, var3, var4, var5, var6);
   }

   boolean verifyInternal(byte[] var1, int var2, SHAKEDigest var3, byte[] var4, byte[] var5) {
      byte[] var6 = new byte[Math.max(64 + this.DilithiumK * this.DilithiumPolyW1PackedBytes, this.DilithiumCTilde)];
      var3.doFinal(var6, 0);
      return this.doVerifyInternal(var6, var1, var2, var3, var4, var5);
   }

   private boolean doVerifyInternal(byte[] var1, byte[] var2, int var3, SHAKEDigest var4, byte[] var5, byte[] var6) {
      if (var3 != this.CryptoBytes) {
         return false;
      } else {
         PolyVecK var7 = new PolyVecK(this);
         PolyVecL var8 = new PolyVecL(this);
         if (!Packing.unpackSignature(var8, var7, var2, this)) {
            return false;
         } else if (var8.checkNorm(this.getDilithiumGamma1() - this.getDilithiumBeta())) {
            return false;
         } else {
            Poly var9 = new Poly(this);
            PolyVecMatrix var10 = new PolyVecMatrix(this);
            PolyVecK var11 = new PolyVecK(this);
            PolyVecK var12 = new PolyVecK(this);
            var11 = Packing.unpackPublicKey(var11, var6, this);
            var9.challenge(var2, 0, this.DilithiumCTilde);
            var10.expandMatrix(var5);
            var8.polyVecNtt();
            var10.pointwiseMontgomery(var12, var8);
            var9.polyNtt();
            var11.shiftLeft();
            var11.polyVecNtt();
            var11.pointwisePolyMontgomery(var9, var11);
            var12.subtract(var11);
            var12.reduce();
            var12.invNttToMont();
            var12.conditionalAddQ();
            var12.useHint(var12, var7);
            var12.packW1(this, var1, 64);
            var4.update(var1, 0, 64 + this.DilithiumK * this.DilithiumPolyW1PackedBytes);
            var4.doFinal(var1, 0, this.DilithiumCTilde);
            return Arrays.constantTimeAreEqual(this.DilithiumCTilde, var2, 0, var1, 0);
         }
      }
   }

   byte[][] generateKeyPair() {
      byte[] var1 = new byte[32];
      this.random.nextBytes(var1);
      return this.generateKeyPairInternal(var1);
   }
}
