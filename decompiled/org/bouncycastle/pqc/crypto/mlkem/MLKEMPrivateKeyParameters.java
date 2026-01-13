package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.util.Arrays;

public class MLKEMPrivateKeyParameters extends MLKEMKeyParameters {
   public static final int BOTH = 0;
   public static final int SEED_ONLY = 1;
   public static final int EXPANDED_KEY = 2;
   final byte[] s;
   final byte[] hpk;
   final byte[] nonce;
   final byte[] t;
   final byte[] rho;
   final byte[] seed;
   private final int prefFormat;

   public MLKEMPrivateKeyParameters(MLKEMParameters var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6) {
      this(var1, var2, var3, var4, var5, var6, null);
   }

   public MLKEMPrivateKeyParameters(MLKEMParameters var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6, byte[] var7) {
      super(true, var1);
      this.s = Arrays.clone(var2);
      this.hpk = Arrays.clone(var3);
      this.nonce = Arrays.clone(var4);
      this.t = Arrays.clone(var5);
      this.rho = Arrays.clone(var6);
      this.seed = Arrays.clone(var7);
      this.prefFormat = 0;
   }

   public MLKEMPrivateKeyParameters(MLKEMParameters var1, byte[] var2) {
      this(var1, var2, null);
   }

   public MLKEMPrivateKeyParameters(MLKEMParameters var1, byte[] var2, MLKEMPublicKeyParameters var3) {
      super(true, var1);
      MLKEMEngine var4 = var1.getEngine();
      if (var2.length == 64) {
         byte[][] var5 = var4.generateKemKeyPairInternal(Arrays.copyOfRange(var2, 0, 32), Arrays.copyOfRange(var2, 32, var2.length));
         this.s = var5[2];
         this.hpk = var5[3];
         this.nonce = var5[4];
         this.t = var5[0];
         this.rho = var5[1];
         this.seed = var5[5];
      } else {
         int var6 = 0;
         this.s = Arrays.copyOfRange(var2, 0, var4.getKyberIndCpaSecretKeyBytes());
         var6 += var4.getKyberIndCpaSecretKeyBytes();
         this.t = Arrays.copyOfRange(var2, var6, var6 + var4.getKyberIndCpaPublicKeyBytes() - 32);
         var6 += var4.getKyberIndCpaPublicKeyBytes() - 32;
         this.rho = Arrays.copyOfRange(var2, var6, var6 + 32);
         var6 += 32;
         this.hpk = Arrays.copyOfRange(var2, var6, var6 + 32);
         var6 += 32;
         this.nonce = Arrays.copyOfRange(var2, var6, var6 + 32);
         this.seed = null;
      }

      if (var3 == null || Arrays.constantTimeAreEqual(this.t, var3.t) && Arrays.constantTimeAreEqual(this.rho, var3.rho)) {
         this.prefFormat = this.seed == null ? 2 : 0;
      } else {
         throw new IllegalArgumentException("passed in public key does not match private values");
      }
   }

   private MLKEMPrivateKeyParameters(MLKEMPrivateKeyParameters var1, int var2) {
      super(true, var1.getParameters());
      this.s = var1.s;
      this.t = var1.t;
      this.rho = var1.rho;
      this.hpk = var1.hpk;
      this.nonce = var1.nonce;
      this.seed = var1.seed;
      this.prefFormat = var2;
   }

   public MLKEMPrivateKeyParameters getParametersWithFormat(int var1) {
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
               return new MLKEMPrivateKeyParameters(this, var1);
            default:
               throw new IllegalArgumentException("unknown format");
         }
      }
   }

   public int getPreferredFormat() {
      return this.prefFormat;
   }

   public byte[] getEncoded() {
      return Arrays.concatenate(new byte[][]{this.s, this.t, this.rho, this.hpk, this.nonce});
   }

   public byte[] getHPK() {
      return Arrays.clone(this.hpk);
   }

   public byte[] getNonce() {
      return Arrays.clone(this.nonce);
   }

   public byte[] getPublicKey() {
      return MLKEMPublicKeyParameters.getEncoded(this.t, this.rho);
   }

   public MLKEMPublicKeyParameters getPublicKeyParameters() {
      return new MLKEMPublicKeyParameters(this.getParameters(), this.t, this.rho);
   }

   public byte[] getRho() {
      return Arrays.clone(this.rho);
   }

   public byte[] getS() {
      return Arrays.clone(this.s);
   }

   public byte[] getT() {
      return Arrays.clone(this.t);
   }

   public byte[] getSeed() {
      return Arrays.clone(this.seed);
   }
}
