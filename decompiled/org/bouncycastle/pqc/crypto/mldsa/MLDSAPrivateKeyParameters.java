package org.bouncycastle.pqc.crypto.mldsa;

import org.bouncycastle.util.Arrays;

public class MLDSAPrivateKeyParameters extends MLDSAKeyParameters {
   public static final int BOTH = 0;
   public static final int SEED_ONLY = 1;
   public static final int EXPANDED_KEY = 2;
   final byte[] rho;
   final byte[] k;
   final byte[] tr;
   final byte[] s1;
   final byte[] s2;
   final byte[] t0;
   private final byte[] t1;
   private final byte[] seed;
   private final int prefFormat;

   public MLDSAPrivateKeyParameters(MLDSAParameters var1, byte[] var2) {
      this(var1, var2, null);
   }

   public MLDSAPrivateKeyParameters(MLDSAParameters var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6, byte[] var7, byte[] var8) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, null);
   }

   public MLDSAPrivateKeyParameters(
      MLDSAParameters var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6, byte[] var7, byte[] var8, byte[] var9
   ) {
      super(true, var1);
      this.rho = Arrays.clone(var2);
      this.k = Arrays.clone(var3);
      this.tr = Arrays.clone(var4);
      this.s1 = Arrays.clone(var5);
      this.s2 = Arrays.clone(var6);
      this.t0 = Arrays.clone(var7);
      this.t1 = Arrays.clone(var8);
      this.seed = Arrays.clone(var9);
      this.prefFormat = var9 != null ? 0 : 2;
   }

   public MLDSAPrivateKeyParameters(MLDSAParameters var1, byte[] var2, MLDSAPublicKeyParameters var3) {
      super(true, var1);
      MLDSAEngine var4 = var1.getEngine(null);
      if (var2.length == 32) {
         byte[][] var5 = var4.generateKeyPairInternal(var2);
         this.rho = var5[0];
         this.k = var5[1];
         this.tr = var5[2];
         this.s1 = var5[3];
         this.s2 = var5[4];
         this.t0 = var5[5];
         this.t1 = var5[6];
         this.seed = var5[7];
      } else {
         int var7 = 0;
         this.rho = Arrays.copyOfRange(var2, 0, 32);
         var7 += 32;
         this.k = Arrays.copyOfRange(var2, var7, var7 + 32);
         var7 += 32;
         this.tr = Arrays.copyOfRange(var2, var7, var7 + 64);
         var7 += 64;
         int var6 = var4.getDilithiumL() * var4.getDilithiumPolyEtaPackedBytes();
         this.s1 = Arrays.copyOfRange(var2, var7, var7 + var6);
         var7 += var6;
         var6 = var4.getDilithiumK() * var4.getDilithiumPolyEtaPackedBytes();
         this.s2 = Arrays.copyOfRange(var2, var7, var7 + var6);
         var7 += var6;
         var6 = var4.getDilithiumK() * 416;
         this.t0 = Arrays.copyOfRange(var2, var7, var7 + var6);
         var7 += var6;
         this.t1 = var4.deriveT1(this.rho, this.k, this.tr, this.s1, this.s2, this.t0);
         this.seed = null;
      }

      if (var3 != null && !Arrays.constantTimeAreEqual(this.t1, var3.getT1())) {
         throw new IllegalArgumentException("passed in public key does not match private values");
      } else {
         this.prefFormat = this.seed != null ? 0 : 2;
      }
   }

   private MLDSAPrivateKeyParameters(MLDSAPrivateKeyParameters var1, int var2) {
      super(true, var1.getParameters());
      this.rho = var1.rho;
      this.k = var1.k;
      this.tr = var1.tr;
      this.s1 = var1.s1;
      this.s2 = var1.s2;
      this.t0 = var1.t0;
      this.t1 = var1.t1;
      this.seed = var1.seed;
      this.prefFormat = var2;
   }

   public MLDSAPrivateKeyParameters getParametersWithFormat(int var1) {
      if (this.prefFormat == var1) {
         return this;
      } else {
         switch (var1) {
            case 0:
            case 1:
               if (this.seed == null) {
                  throw new IllegalStateException("no seed available");
               }
            case 2:
               return new MLDSAPrivateKeyParameters(this, var1);
            default:
               throw new IllegalArgumentException("unknown format");
         }
      }
   }

   public int getPreferredFormat() {
      return this.prefFormat;
   }

   public byte[] getEncoded() {
      return Arrays.concatenate(new byte[][]{this.rho, this.k, this.tr, this.s1, this.s2, this.t0});
   }

   public byte[] getK() {
      return Arrays.clone(this.k);
   }

   @Deprecated
   public byte[] getPrivateKey() {
      return this.getEncoded();
   }

   public byte[] getPublicKey() {
      return MLDSAPublicKeyParameters.getEncoded(this.rho, this.t1);
   }

   public byte[] getSeed() {
      return Arrays.clone(this.seed);
   }

   public MLDSAPublicKeyParameters getPublicKeyParameters() {
      return this.t1 == null ? null : new MLDSAPublicKeyParameters(this.getParameters(), this.rho, this.t1);
   }

   public byte[] getRho() {
      return Arrays.clone(this.rho);
   }

   public byte[] getS1() {
      return Arrays.clone(this.s1);
   }

   public byte[] getS2() {
      return Arrays.clone(this.s2);
   }

   public byte[] getT0() {
      return Arrays.clone(this.t0);
   }

   public byte[] getT1() {
      return Arrays.clone(this.t1);
   }

   public byte[] getTr() {
      return Arrays.clone(this.tr);
   }
}
