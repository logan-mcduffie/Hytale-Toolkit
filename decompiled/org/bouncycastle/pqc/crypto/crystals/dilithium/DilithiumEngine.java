package org.bouncycastle.pqc.crypto.crystals.dilithium;

import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.util.Arrays;

class DilithiumEngine {
   private final SecureRandom random;
   private final SHAKEDigest shake256Digest = new SHAKEDigest(256);
   public static final int DilithiumN = 256;
   public static final int DilithiumQ = 8380417;
   public static final int DilithiumQinv = 58728449;
   public static final int DilithiumD = 13;
   public static final int DilithiumRootOfUnity = 1753;
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
   private final int DilithiumMode;
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
   private final int CryptoSecretKeyBytes;
   private final int CryptoBytes;
   private final int PolyUniformGamma1NBlocks;
   private final Symmetric symmetric;

   protected Symmetric GetSymmetric() {
      return this.symmetric;
   }

   int getDilithiumPolyVecHPackedBytes() {
      return this.DilithiumPolyVecHPackedBytes;
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

   int getDilithiumMode() {
      return this.DilithiumMode;
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

   int getCryptoSecretKeyBytes() {
      return this.CryptoSecretKeyBytes;
   }

   int getCryptoBytes() {
      return this.CryptoBytes;
   }

   int getPolyUniformGamma1NBlocks() {
      return this.PolyUniformGamma1NBlocks;
   }

   SHAKEDigest getShake256Digest() {
      return this.shake256Digest;
   }

   DilithiumEngine(int var1, SecureRandom var2, boolean var3) {
      this.DilithiumMode = var1;
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

      if (var3) {
         this.symmetric = new Symmetric.AesSymmetric();
      } else {
         this.symmetric = new Symmetric.ShakeSymmetric();
      }

      this.random = var2;
      this.DilithiumPolyVecHPackedBytes = this.DilithiumOmega + this.DilithiumK;
      this.CryptoPublicKeyBytes = 32 + this.DilithiumK * 320;
      this.CryptoSecretKeyBytes = 128
         + this.DilithiumL * this.DilithiumPolyEtaPackedBytes
         + this.DilithiumK * this.DilithiumPolyEtaPackedBytes
         + this.DilithiumK * 416;
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

   public byte[][] generateKeyPairInternal(byte[] var1) {
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
      var8.copyPolyVecL(var9);
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
      return new byte[][]{var14[0], var14[1], var14[2], var14[3], var14[4], var14[5], var13};
   }

   public byte[] signSignatureInternal(byte[] var1, int var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6, byte[] var7, byte[] var8, byte[] var9) {
      byte[] var11 = new byte[this.CryptoBytes + var2];
      byte[] var12 = new byte[64];
      byte[] var13 = new byte[64];
      short var14 = 0;
      PolyVecL var15 = new PolyVecL(this);
      PolyVecL var16 = new PolyVecL(this);
      PolyVecL var17 = new PolyVecL(this);
      PolyVecK var18 = new PolyVecK(this);
      PolyVecK var19 = new PolyVecK(this);
      PolyVecK var20 = new PolyVecK(this);
      PolyVecK var21 = new PolyVecK(this);
      PolyVecK var22 = new PolyVecK(this);
      Poly var23 = new Poly(this);
      PolyVecMatrix var24 = new PolyVecMatrix(this);
      Packing.unpackSecretKey(var18, var15, var19, var6, var7, var8, this);
      this.shake256Digest.update(var5, 0, 64);
      this.shake256Digest.update(var1, 0, var2);
      this.shake256Digest.doFinal(var12, 0, 64);
      byte[] var25 = Arrays.copyOf(var4, 128);
      System.arraycopy(var9, 0, var25, 32, 32);
      System.arraycopy(var12, 0, var25, 64, 64);
      this.shake256Digest.update(var25, 0, 128);
      this.shake256Digest.doFinal(var13, 0, 64);
      var24.expandMatrix(var3);
      var15.polyVecNtt();
      var19.polyVecNtt();
      var18.polyVecNtt();
      int var26 = 0;

      while (var26 < 1000) {
         var26++;
         var16.uniformGamma1(var13, var14++);
         var16.copyPolyVecL(var17);
         var17.polyVecNtt();
         var24.pointwiseMontgomery(var20, var17);
         var20.reduce();
         var20.invNttToMont();
         var20.conditionalAddQ();
         var20.decompose(var21);
         System.arraycopy(var20.packW1(), 0, var11, 0, this.DilithiumK * this.DilithiumPolyW1PackedBytes);
         this.shake256Digest.update(var12, 0, 64);
         this.shake256Digest.update(var11, 0, this.DilithiumK * this.DilithiumPolyW1PackedBytes);
         this.shake256Digest.doFinal(var11, 0, this.DilithiumCTilde);
         var23.challenge(Arrays.copyOfRange(var11, 0, this.DilithiumCTilde));
         var23.polyNtt();
         var17.pointwisePolyMontgomery(var23, var15);
         var17.invNttToMont();
         var17.addPolyVecL(var16);
         var17.reduce();
         if (!var17.checkNorm(this.DilithiumGamma1 - this.DilithiumBeta)) {
            var22.pointwisePolyMontgomery(var23, var19);
            var22.invNttToMont();
            var21.subtract(var22);
            var21.reduce();
            if (!var21.checkNorm(this.DilithiumGamma2 - this.DilithiumBeta)) {
               var22.pointwisePolyMontgomery(var23, var18);
               var22.invNttToMont();
               var22.reduce();
               if (!var22.checkNorm(this.DilithiumGamma2)) {
                  var21.addPolyVecK(var22);
                  var21.conditionalAddQ();
                  int var10 = var22.makeHint(var21, var20);
                  if (var10 <= this.DilithiumOmega) {
                     return Packing.packSignature(var11, var17, var22, this);
                  }
               }
            }
         }
      }

      return null;
   }

   public boolean signVerifyInternal(byte[] var1, int var2, byte[] var3, int var4, byte[] var5, byte[] var6) {
      byte[] var8 = new byte[64];
      byte[] var10 = new byte[this.DilithiumCTilde];
      Poly var11 = new Poly(this);
      PolyVecMatrix var12 = new PolyVecMatrix(this);
      PolyVecL var13 = new PolyVecL(this);
      PolyVecK var14 = new PolyVecK(this);
      PolyVecK var15 = new PolyVecK(this);
      PolyVecK var16 = new PolyVecK(this);
      if (var2 != this.CryptoBytes) {
         return false;
      } else {
         var14 = Packing.unpackPublicKey(var14, var6, this);
         if (!Packing.unpackSignature(var13, var16, var1, this)) {
            return false;
         } else {
            byte[] var9 = Arrays.copyOfRange(var1, 0, this.DilithiumCTilde);
            if (var13.checkNorm(this.getDilithiumGamma1() - this.getDilithiumBeta())) {
               return false;
            } else {
               this.shake256Digest.update(var5, 0, var5.length);
               this.shake256Digest.update(var6, 0, var6.length);
               this.shake256Digest.doFinal(var8, 0, 64);
               this.shake256Digest.update(var8, 0, 64);
               this.shake256Digest.update(var3, 0, var4);
               this.shake256Digest.doFinal(var8, 0);
               var11.challenge(Arrays.copyOfRange(var9, 0, this.DilithiumCTilde));
               var12.expandMatrix(var5);
               var13.polyVecNtt();
               var12.pointwiseMontgomery(var15, var13);
               var11.polyNtt();
               var14.shiftLeft();
               var14.polyVecNtt();
               var14.pointwisePolyMontgomery(var11, var14);
               var15.subtract(var14);
               var15.reduce();
               var15.invNttToMont();
               var15.conditionalAddQ();
               var15.useHint(var15, var16);
               byte[] var7 = var15.packW1();
               SHAKEDigest var17 = new SHAKEDigest(256);
               var17.update(var8, 0, 64);
               var17.update(var7, 0, this.DilithiumK * this.DilithiumPolyW1PackedBytes);
               var17.doFinal(var10, 0, this.DilithiumCTilde);
               return Arrays.constantTimeAreEqual(var9, var10);
            }
         }
      }
   }

   public byte[][] generateKeyPair() {
      byte[] var1 = new byte[32];
      this.random.nextBytes(var1);
      return this.generateKeyPairInternal(var1);
   }

   public byte[] signSignature(byte[] var1, int var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6, byte[] var7, byte[] var8) {
      byte[] var9 = new byte[32];
      if (this.random != null) {
         this.random.nextBytes(var9);
      }

      return this.signSignatureInternal(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public byte[] sign(byte[] var1, int var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6, byte[] var7, byte[] var8) {
      return this.signSignature(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public boolean signVerify(byte[] var1, int var2, byte[] var3, int var4, byte[] var5, byte[] var6) {
      return this.signVerifyInternal(var1, var2, var3, var4, var5, var6);
   }

   public boolean signOpen(byte[] var1, byte[] var2, int var3, byte[] var4, byte[] var5) {
      return this.signVerify(var2, var3, var1, var1.length, var4, var5);
   }
}
