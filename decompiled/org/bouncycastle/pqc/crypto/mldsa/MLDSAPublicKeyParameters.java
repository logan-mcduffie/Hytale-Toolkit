package org.bouncycastle.pqc.crypto.mldsa;

import org.bouncycastle.util.Arrays;

public class MLDSAPublicKeyParameters extends MLDSAKeyParameters {
   final byte[] rho;
   final byte[] t1;

   static byte[] getEncoded(byte[] var0, byte[] var1) {
      return Arrays.concatenate(var0, var1);
   }

   public MLDSAPublicKeyParameters(MLDSAParameters var1, byte[] var2) {
      super(false, var1);
      this.rho = Arrays.copyOfRange(var2, 0, 32);
      this.t1 = Arrays.copyOfRange(var2, 32, var2.length);
      if (this.t1.length == 0) {
         throw new IllegalArgumentException("encoding too short");
      }
   }

   public MLDSAPublicKeyParameters(MLDSAParameters var1, byte[] var2, byte[] var3) {
      super(false, var1);
      if (var2 == null) {
         throw new NullPointerException("rho cannot be null");
      } else if (var3 == null) {
         throw new NullPointerException("t1 cannot be null");
      } else {
         this.rho = Arrays.clone(var2);
         this.t1 = Arrays.clone(var3);
      }
   }

   public byte[] getEncoded() {
      return getEncoded(this.rho, this.t1);
   }

   public byte[] getRho() {
      return Arrays.clone(this.rho);
   }

   public byte[] getT1() {
      return Arrays.clone(this.t1);
   }
}
