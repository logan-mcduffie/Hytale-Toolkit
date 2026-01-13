package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.util.Arrays;

public class MLKEMPublicKeyParameters extends MLKEMKeyParameters {
   final byte[] t;
   final byte[] rho;

   static byte[] getEncoded(byte[] var0, byte[] var1) {
      return Arrays.concatenate(var0, var1);
   }

   public MLKEMPublicKeyParameters(MLKEMParameters var1, byte[] var2, byte[] var3) {
      super(false, var1);
      MLKEMEngine var4 = var1.getEngine();
      if (var2.length != var4.getKyberPolyVecBytes()) {
         throw new IllegalArgumentException("'t' has invalid length");
      } else if (var3.length != 32) {
         throw new IllegalArgumentException("'rho' has invalid length");
      } else {
         this.t = Arrays.clone(var2);
         this.rho = Arrays.clone(var3);
         if (!var4.checkModulus(this.t)) {
            throw new IllegalArgumentException("Modulus check failed for ML-KEM public key");
         }
      }
   }

   public MLKEMPublicKeyParameters(MLKEMParameters var1, byte[] var2) {
      super(false, var1);
      MLKEMEngine var3 = var1.getEngine();
      if (var2.length != var3.getKyberIndCpaPublicKeyBytes()) {
         throw new IllegalArgumentException("'encoding' has invalid length");
      } else {
         this.t = Arrays.copyOfRange(var2, 0, var2.length - 32);
         this.rho = Arrays.copyOfRange(var2, var2.length - 32, var2.length);
         if (!var3.checkModulus(this.t)) {
            throw new IllegalArgumentException("Modulus check failed for ML-KEM public key");
         }
      }
   }

   public byte[] getEncoded() {
      return getEncoded(this.t, this.rho);
   }

   public byte[] getRho() {
      return Arrays.clone(this.rho);
   }

   public byte[] getT() {
      return Arrays.clone(this.t);
   }
}
